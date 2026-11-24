package buttle.apptpl.common.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import micoai.corebase.exception.InvalidationException;
import micoai.corebase.util.MimeTypeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import buttle.apptpl.api.common.file.util.AiccFileIdGenerator;
import buttle.apptpl.common.model.common.entity.CommonAtchFileEntity;
import buttle.apptpl.common.model.common.entity.CommonAtchFileGroupEntity;
import buttle.apptpl.common.repository.CommonAtchFileGroupRepository;
import buttle.apptpl.common.repository.CommonAtchFileRepository;
import buttle.apptpl.common.vo.CommonAtchFile;
import buttle.apptpl.common.vo.CommonAtchFileGroup;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 파일 첨부 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileAttachmentService {

    @Value("${file.upload.base-path}")
    private String basePath;

    @Value("${system.code}")
    private String systemCode;

    @Value("${system.company-id}")
    private String companyId;

    private final CommonAtchFileRepository fileRepository;
    private final CommonAtchFileGroupRepository fileGroupRepository;

    // WB-8: 확장자-내용 일치 검증용. admin 은 corebase 를 ComponentScan 하지 않으므로
    // corebase 의 @Component MimeTypeUtils 빈이 자동 등록되지 않는다. 무의존 유틸이라 직접 생성.
    // (초기화 필드라 @RequiredArgsConstructor 생성자에는 포함되지 않는다)
    private final MimeTypeUtils mimeTypeUtils = new MimeTypeUtils();

    private static final int BUFFER_SIZE = 8192;

    /**
     * WB-5: 다운로드 소유권(회사 스코프) 검증. 파일이 속한 그룹의 owner_cmpy_id 가
     * 이 admin 인스턴스의 회사(system.company-id)와 일치해야 한다. null 이면 거부(deny).
     * 공유 DB 에서 타 회사 그룹ID/파일ID 열거로 파일을 받는 IDOR 를 차단한다.
     * (고객 단위/방 멤버십 검증은 후속 — 현재는 회사 스코프)
     */
    private void assertGroupAccess(String atchFileGroupId) {
        String owner = fileGroupRepository.findById(atchFileGroupId)
                .map(CommonAtchFileGroupEntity::getOwnerCmpyId)
                .orElse(null);
        if (owner == null || !owner.equals(this.companyId)) {
            log.warn("WB-5 파일 접근 거부 - group={}, owner={}, requester={}", atchFileGroupId, owner, this.companyId);
            throw new InvalidationException("해당 파일에 접근할 수 없습니다.");
        }
    }

    /**
     * WB-8: 저장된 파일의 실제 내용(매직바이트)이 확장자와 일치하는지 Tika 로 검증한다.
     * 확장자 화이트리스트만으로는 확장자를 위장한 파일을 막지 못한다.
     * 검증 실패 시 이미 기록된 파일을 삭제하고 예외를 던진다(orphan 방지).
     */
    private void verifyMimeType(File savedFile, String filename) {
        if (!mimeTypeUtils.checkMimeType(savedFile, filename)) {
            try {
                Files.deleteIfExists(savedFile.toPath());
            } catch (IOException ignore) {
                log.warn("MIME 불일치 파일 삭제 실패: {}", savedFile.getAbsolutePath());
            }
            throw new InvalidationException(
                String.format("첨부파일(%s)의 내용이 파일 형식과 일치하지 않습니다.", filename));
        }
    }

    /**
     * 다중 파일을 새 그룹으로 업로드
     *
     * @param files 업로드할 파일 목록
     * @param allowedExts 허용 확장자 배열 (예: ["jpg", "png", "pdf"])
     * @param maxSizeBytes 최대 파일 크기 (bytes)
     * @return 업로드된 파일 그룹 정보
     */
    @Transactional
    public CommonAtchFileGroup uploadFiles_toNewGroup(List<MultipartFile> files, String[] allowedExts, long maxSizeBytes) {
        // 파일 개수 검증
        if (files == null || files.isEmpty()) {
            throw new InvalidationException("업로드할 파일을 선택해 주세요.");
        }
        if (files.size() > 50) {
            throw new InvalidationException("파일은 최대 50개까지 업로드 가능합니다.");
        }

        // 파일 크기 및 확장자 검증
        long totalSize = 0;
        for (MultipartFile file : files) {
            // 개별 파일 크기 검증
            if (file.getSize() > maxSizeBytes) {
                String maxSizeMB = String.format("%.0f", maxSizeBytes / (1024.0 * 1024.0));
                throw new InvalidationException("파일 크기는 " + maxSizeMB + "MB를 초과할 수 없습니다: " + file.getOriginalFilename());
            }

            totalSize += file.getSize();

            // 확장자 검증
            // [보안 체크리스트 #27] 종전에는 검사 <전체>가 `contains(".")` 안쪽에 있어
            //  점 없는 파일명이 검증을 통째로 건너뛰고 저장됐다. 이것은 허용목록 방식이므로
            //  확장자가 없는 파일은 애초에 목록에 있을 수 없다 — 통과가 아니라 거부가 맞다.
            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.contains(".")) {
                throw new InvalidationException("확장자가 없는 파일은 업로드할 수 없습니다: " + fileName
                        + " (허용: " + String.join(", ", allowedExts) + ")");
            }
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            boolean isAllowed = false;
            for (String allowedExt : allowedExts) {
                if (ext.equalsIgnoreCase(allowedExt)) {
                    isAllowed = true;
                    break;
                }
            }
            if (!isAllowed) {
                throw new InvalidationException("허용되지 않은 파일 형식입니다: " + fileName + " (허용: " + String.join(", ", allowedExts) + ")");
            }
        }

        // 전체 파일 크기 검증 (1GB)
        if (totalSize > 1024L * 1024 * 1024) {
            throw new InvalidationException("전체 파일 용량이 1GB를 초과할 수 없습니다.");
        }

        // 파일 그룹 생성
        String groupId = AiccFileIdGenerator.generateAdminGroupId();
        LocalDateTime now = LocalDateTime.now();

        CommonAtchFileGroupEntity groupEntity = CommonAtchFileGroupEntity.builder()
                .id(groupId)
                // WB-5: 업로드 시점에 소유 스코프 기록(이 admin 인스턴스의 회사). 신규 업로드는 소유자 확정
                .ownerCmpyId(this.companyId)
                .ownerSubjectType("MBR")
                .build();
        groupEntity.setRgtrId("system");
        groupEntity.setRegDt(now);
        groupEntity.setMdfrId("system");
        groupEntity.setMdfcnDt(now);
        fileGroupRepository.save(groupEntity);

        // 파일 저장
        List<CommonAtchFile> savedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            CommonAtchFile savedFile = saveFile(file, groupId, now);
            savedFiles.add(savedFile);
        }

        return CommonAtchFileGroup.builder()
                .atchFileGroupId(groupId)
                .files(savedFiles)
                .rgtrId("system")
                .regDt(now)
                .mdfrId("system")
                .mdfcnDt(now)
                .build();
    }

    /**
     * 단일 파일 저장
     */
    private CommonAtchFile saveFile(MultipartFile file, String groupId, LocalDateTime now) {
        try {
            // 파일 ID 생성 (형식: ADMIN-UUID)
            String fileId = AiccFileIdGenerator.generateAdminFileId();
            String originalFileName = file.getOriginalFilename();
            //String extension = getFileExtension(originalFileName);

            // 시스템 폴더 + 년월 디렉토리 생성 (aicc-admin/yyyy/MM)
            LocalDate today = LocalDate.now();
            String datePath = today.format(DateTimeFormatter.ofPattern("yyyy/MM"));
            String systemFolder = "/"+this.systemCode;
            String uploadPath = this.basePath;
            Path directoryPath = Paths.get(uploadPath, systemFolder, datePath);
            Files.createDirectories(directoryPath);

            // 파일 저장
            String savedFileName = fileId ;
            Path filePath = directoryPath.resolve(savedFileName);
            file.transferTo(filePath.toFile());

            // WB-8: 저장 후 실제 내용-확장자 일치 검증
            verifyMimeType(filePath.toFile(), originalFileName);

            // DB 저장 (시스템 폴더 포함 상대 경로)
            String relativePath = systemFolder + "/" + datePath + "/" + savedFileName;
            CommonAtchFileEntity entity = CommonAtchFileEntity.builder()
                    .id(fileId)
                    .atchFileGroupId(groupId)
                    .fileNm(originalFileName)
                    .cnType(file.getContentType())
                    .fileSz(BigDecimal.valueOf(file.getSize()))
                    .filePathNm(relativePath)
                    .dwnldCnt(BigDecimal.ZERO)
                    .delYn("N")
                    .build();
            entity.setRgtrId("system");
            entity.setRegDt(now);
            entity.setMdfrId("system");
            entity.setMdfcnDt(now);
            fileRepository.save(entity);

            return CommonAtchFile.builder()
                    .atchFileId(fileId)
                    .atchFileGroupId(groupId)
                    .fileNm(originalFileName)
                    .cnType(file.getContentType())
                    .fileSz(file.getSize())
                    .filePathNm(relativePath)
                    .dwnldCnt(0)
                    .delYn("N")
                    .rgtrId("system")
                    .regDt(now)
                    .mdfrId("system")
                    .mdfcnDt(now)
                    .build();

        } catch (IOException e) {
            log.error("파일 저장 실패: {}", file.getOriginalFilename(), e);
            throw new InvalidationException("파일 저장에 실패했습니다: " + file.getOriginalFilename());
        }
    }

    /**
     * 단일 파일 다운로드
     */
    @Transactional
    public void downloadFile(HttpServletResponse response, String fileId) {
        CommonAtchFileEntity fileEntity = fileRepository.findByIdAndDelYn(fileId, "N")
                .orElseThrow(() -> new InvalidationException("파일을 찾을 수 없습니다."));

        // WB-5: 소유권(회사 스코프) 검증
        assertGroupAccess(fileEntity.getAtchFileGroupId());

        // 다운로드 횟수 증가
        fileEntity.setDwnldCnt(fileEntity.getDwnldCnt().add(BigDecimal.ONE));
        fileRepository.save(fileEntity);

        // 파일 경로 생성 및 다운로드
        String uploadPath = this.basePath;
        Path filePath = Paths.get(uploadPath, fileEntity.getFilePathNm());
        downloadFromLocal(response, filePath, fileEntity.getFileNm(), fileEntity.getCnType());
    }

    /**
     * 그룹 파일 ZIP 다운로드
     */
    @Transactional
    public void downloadGroupFiles(HttpServletResponse response, String groupId, String zipFileName) {
        // WB-5: 소유권(회사 스코프) 검증
        assertGroupAccess(groupId);
        List<CommonAtchFileEntity> files = fileRepository.findByAtchFileGroupIdAndDelYn(groupId, "N");

        if (files.isEmpty()) {
            throw new InvalidationException("다운로드할 파일이 없습니다.");
        }

        String uploadPath = this.basePath;

        try {
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + 
                    new String(zipFileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");

            try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
                for (CommonAtchFileEntity file : files) {
                    Path filePath = Paths.get(uploadPath, file.getFilePathNm());
                    
                    if (!Files.exists(filePath)) {
                        log.warn("파일이 존재하지 않습니다: {}", file.getFilePathNm());
                        continue;
                    }

                    // ZIP 엔트리 추가
                    ZipEntry zipEntry = new ZipEntry(file.getFileNm());
                    zipOut.putNextEntry(zipEntry);

                    // 파일 내용 복사
                    try (InputStream inputStream = Files.newInputStream(filePath)) {
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            zipOut.write(buffer, 0, bytesRead);
                        }
                    }

                    zipOut.closeEntry();

                    // 다운로드 횟수 증가
                    file.setDwnldCnt(file.getDwnldCnt().add(BigDecimal.ONE));
                    fileRepository.save(file);
                }

                zipOut.flush();
            }

        } catch (IOException e) {
            log.error("ZIP 파일 다운로드 실패", e);
            throw new InvalidationException("파일 다운로드에 실패했습니다.");
        }
    }

    /**
     * 여러 파일 ID를 ZIP으로 다운로드
     */
    @Transactional
    public void downloadMultipleFiles(HttpServletResponse response, List<String> fileIds, String zipFileName) {
        // fileIds로 파일 조회 (WB-5: 각 파일의 소유권 회사 스코프 검증)
        List<CommonAtchFileEntity> files = new ArrayList<>();
        for (String fileId : fileIds) {
            CommonAtchFileEntity f = fileRepository.findByIdAndDelYn(fileId, "N").orElse(null);
            if (f == null) continue;
            assertGroupAccess(f.getAtchFileGroupId());
            files.add(f);
        }

        if (files.isEmpty()) {
            throw new InvalidationException("다운로드할 파일이 없습니다.");
        }
        
        String uploadPath = this.basePath;

        try {
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + 
                    new String(zipFileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");

            try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
                for (CommonAtchFileEntity file : files) {
                    Path filePath = Paths.get(uploadPath, file.getFilePathNm());
                    
                    if (!Files.exists(filePath)) {
                        log.warn("파일이 존재하지 않습니다: {}", file.getFilePathNm());
                        continue;
                    }

                    // ZIP 엔트리 추가
                    ZipEntry zipEntry = new ZipEntry(file.getFileNm());
                    zipOut.putNextEntry(zipEntry);

                    // 파일 내용 복사
                    try (InputStream inputStream = Files.newInputStream(filePath)) {
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            zipOut.write(buffer, 0, bytesRead);
                        }
                    }

                    zipOut.closeEntry();

                    // 다운로드 횟수 증가
                    file.setDwnldCnt(file.getDwnldCnt().add(BigDecimal.ONE));
                    fileRepository.save(file);
                }

                zipOut.flush();
            }

        } catch (IOException e) {
            log.error("여러 파일 ZIP 다운로드 실패", e);
            throw new InvalidationException("파일 다운로드에 실패했습니다.");
        }
    }

    /**
     * 그룹의 파일 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CommonAtchFile> getFileList(String groupId) {
        List<CommonAtchFileEntity> entities = fileRepository.findByAtchFileGroupIdAndDelYn(groupId, "N");
        
        List<CommonAtchFile> files = new ArrayList<>();
        for (CommonAtchFileEntity entity : entities) {
            files.add(CommonAtchFile.builder()
                    .atchFileId(entity.getId())
                    .atchFileGroupId(entity.getAtchFileGroupId())
                    .fileNm(entity.getFileNm())
                    .cnType(entity.getCnType())
                    .fileSz(entity.getFileSz().longValue())
                    .filePathNm(entity.getFilePathNm())
                    .dwnldCnt(entity.getDwnldCnt().intValue())
                    .delYn(entity.getDelYn())
                    .rgtrId(entity.getRgtrId())
                    .regDt(entity.getRegDt())
                    .mdfrId(entity.getMdfrId())
                    .mdfcnDt(entity.getMdfcnDt())
                    .build());
        }
        
        return files;
    }

    /**
     * 단일 파일 정보 조회
     * 
     * @param fileId 파일 ID
     * @return 파일 정보 (없으면 null)
     */
    @Transactional(readOnly = true)
    public CommonAtchFile getFileInfo(String fileId) {
        return fileRepository.findByIdAndDelYn(fileId, "N")
                .map(entity -> CommonAtchFile.builder()
                        .atchFileId(entity.getId())
                        .atchFileGroupId(entity.getAtchFileGroupId())
                        .fileNm(entity.getFileNm())
                        .cnType(entity.getCnType())
                        .fileSz(entity.getFileSz() != null ? entity.getFileSz().longValue() : 0L)
                        .filePathNm(entity.getFilePathNm())
                        .dwnldCnt(entity.getDwnldCnt() != null ? entity.getDwnldCnt().intValue() : 0)
                        .delYn(entity.getDelYn())
                        .rgtrId(entity.getRgtrId())
                        .regDt(entity.getRegDt())
                        .mdfrId(entity.getMdfrId())
                        .mdfcnDt(entity.getMdfcnDt())
                        .build())
                .orElse(null);
    }

    /**
     * 파일 그룹 생성 (ID 채번)
     * 
     * @return 생성된 그룹 ID
     */
    @Transactional
    public String createFileGroup() {
        LocalDateTime now = LocalDateTime.now();
        String groupId = AiccFileIdGenerator.generateAdminGroupId();
        
        CommonAtchFileGroupEntity groupEntity = CommonAtchFileGroupEntity.builder()
                .id(groupId)
                // WB-5: 업로드 시점에 소유 스코프 기록(이 admin 인스턴스의 회사). 신규 업로드는 소유자 확정
                .ownerCmpyId(this.companyId)
                .ownerSubjectType("MBR")
                .build();
        groupEntity.setRgtrId("system");
        groupEntity.setRegDt(now);
        groupEntity.setMdfrId("system");
        groupEntity.setMdfcnDt(now);
        fileGroupRepository.save(groupEntity);
        
        log.debug("파일 그룹 생성: {}", groupId);
        return groupId;
    }

    /**
     * 단일 파일 업로드 (그룹 ID 필수)
     * - groupId는 필수이며, 업로드 전에 생성되어 있어야 함
     *
     * @param file 업로드할 파일
     * @param groupId 파일 그룹 ID (필수)
     * @param allowedExts 허용 확장자 배열
     * @param maxSizeBytes 최대 파일 크기
     * @return 업로드된 파일 그룹 정보
     */
    @Transactional
    public CommonAtchFileGroup uploadFile(MultipartFile file, String groupId, 
            String[] allowedExts, long maxSizeBytes) {
        
        // 파일 검증
        if (file == null || file.isEmpty()) {
            throw new InvalidationException("업로드할 파일을 선택해 주세요.");
        }
        
        // groupId 필수 체크
        if (groupId == null || groupId.trim().isEmpty()) {
            throw new InvalidationException("파일 그룹 ID가 필요합니다.");
        }
        
        // 파일 크기 검증
        if (file.getSize() > maxSizeBytes) {
            String maxSizeMB = String.format("%.0f", maxSizeBytes / (1024.0 * 1024.0));
            throw new InvalidationException("파일 크기는 " + maxSizeMB + "MB를 초과할 수 없습니다: " + file.getOriginalFilename());
        }
        
        // 확장자 검증
        // [보안 체크리스트 #27] 점 없는 파일명이 검증을 건너뛰던 자리다(위 검사와 같은 형태였다).
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.contains(".")) {
            throw new InvalidationException("확장자가 없는 파일은 업로드할 수 없습니다: " + fileName
                    + " (허용: " + String.join(", ", allowedExts) + ")");
        }
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        boolean isAllowed = false;
        for (String allowedExt : allowedExts) {
            if (ext.equalsIgnoreCase(allowedExt)) {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed) {
            throw new InvalidationException("허용되지 않은 파일 형식입니다: " + fileName + " (허용: " + String.join(", ", allowedExts) + ")");
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // 기존 그룹 확인
        if (!fileGroupRepository.existsById(groupId)) {
            throw new InvalidationException("존재하지 않는 파일 그룹입니다: " + groupId);
        }
        
        // 그룹 수정일시 업데이트
        fileGroupRepository.findById(groupId).ifPresent(group -> {
            group.setMdfcnDt(now);
            group.setMdfrId("system");
            fileGroupRepository.save(group);
        });
        
        log.debug("기존 파일 그룹에 추가: {}", groupId);
        
        // 파일 저장
        CommonAtchFile savedFile = saveFile(file, groupId, now);
        
        List<CommonAtchFile> savedFiles = new ArrayList<>();
        savedFiles.add(savedFile);
        
        return CommonAtchFileGroup.builder()
                .atchFileGroupId(groupId)
                .files(savedFiles)
                .rgtrId("system")
                .regDt(now)
                .mdfrId("system")
                .mdfcnDt(now)
                .build();
    }

    /**
     * 파일 삭제 (Soft Delete)
     * 
     * @param fileId 삭제할 파일 ID
     */
    @Transactional
    public void deleteFile(String fileId) {
        CommonAtchFileEntity fileEntity = fileRepository.findByIdAndDelYn(fileId, "N")
                .orElseThrow(() -> new InvalidationException("파일을 찾을 수 없습니다: " + fileId));
        
        fileEntity.setDelYn("Y");
        fileEntity.setMdfcnDt(LocalDateTime.now());
        fileEntity.setMdfrId("system");
        fileRepository.save(fileEntity);
        
        log.debug("파일 삭제 (soft delete): {}", fileId);
    }

    /**
     * 그룹 존재 여부 확인
     * 
     * @param groupId 그룹 ID
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean isGroupExists(String groupId) {
        if (groupId == null || groupId.trim().isEmpty()) {
            return false;
        }
        return fileGroupRepository.existsById(groupId);
    }

    /**
     * 로컬 파일 다운로드 (Path 기반)
     * 
     * @param response HTTP 응답 객체
     * @param filePath 다운로드할 파일 경로
     * @param fileName 다운로드 시 사용할 파일명
     * @param contentType 파일 Content-Type
     */
    public void downloadFromLocal(HttpServletResponse response, Path filePath,
                                   String fileName, String contentType) {
        if (!Files.exists(filePath)) {
            throw new InvalidationException("파일이 존재하지 않습니다.");
        }

        try {
            // Response 헤더 설정
            response.setContentType(contentType != null ? contentType : "application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" +
                    new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");
            response.setContentLengthLong(Files.size(filePath));

            // 파일 스트림 전송
            try (InputStream inputStream = Files.newInputStream(filePath);
                 OutputStream outputStream = response.getOutputStream()) {

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }

        } catch (IOException e) {
            log.error("파일 다운로드 실패 - filePath: {}", filePath, e);
            throw new InvalidationException("파일 다운로드에 실패했습니다.");
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
