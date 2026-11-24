
package buttle.apptpl.api.common.file.web;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import micoai.corebase.exception.InvalidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import buttle.apptpl.api.common.file.dto.CommonResponseDto;
import buttle.apptpl.api.common.file.dto.FileDeleteReqDto;
import buttle.apptpl.api.common.file.dto.FileDownloadZipReqDto;
import buttle.apptpl.api.common.file.dto.FileInfoDto;
import buttle.apptpl.api.common.file.dto.FileMetaDto;
import buttle.apptpl.api.common.file.dto.FileMetaResDto;
import buttle.apptpl.api.common.file.dto.FileUploadResDto;
import buttle.apptpl.common.adapter.CompanyApiAdapter;
import buttle.apptpl.common.service.FileAttachmentService;
import buttle.apptpl.common.vo.CommonAtchFile;
import buttle.apptpl.common.vo.CommonAtchFileGroup;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 공통 파일 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
public class CommonFileController {

    private final FileAttachmentService fileAttachmentService;
    private final CompanyApiAdapter companyApiAdapter;

    @Value("${file.upload.base-path}")
    private String basePath;

    @Value("${file.upload.allowed-extensions}")
    private String allowedExtsValue;

    @Value("${file.upload.max-size-mb}")
    private int maxSizeMBValue;

    @Value("${system.code}")
    private String systemCode;

    @Value("${system.company-id}")
    private String companyId;

    private static final int BUFFER_SIZE = 8192;

    /**
     * 파일 그룹 ID 채번 (업로드 전 그룹 생성)
     * 
     * POST /api/common/file/group-id
     */
    @PostMapping("/file/group-id")
    public ResponseEntity<CommonResponseDto<String>> generateGroupId() {
        try {
            String groupId = fileAttachmentService.createFileGroup();
            
            CommonResponseDto<String> response = CommonResponseDto.<String>builder()
                    .result("0")
                    .data(groupId)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("파일 그룹 ID 생성 실패", e);
            CommonResponseDto<String> errorResponse = CommonResponseDto.<String>builder()
                    .result("-1")
                    .message("파일 그룹 ID 생성에 실패했습니다.")
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 단일 파일 업로드 (자동 업로드용)
     * - groupId 필수 (업로드 전 생성 필요)
     * 
     * POST /api/common/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<CommonResponseDto<FileUploadResDto>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("groupId") String groupId) {

        try {
            // 파일 검증
            if (file == null || file.isEmpty()) {
                throw new InvalidationException("업로드할 파일을 선택해 주세요.");
            }
            
            // groupId 필수 체크
            if (groupId == null || groupId.trim().isEmpty()) {
                throw new InvalidationException("파일 그룹 ID가 필요합니다.");
            }
            
            String allowedExts = this.allowedExtsValue;
            int maxSizeMB = this.maxSizeMBValue;

            // 확장자 배열 생성
            String[] allowedExtArray = allowedExts.toUpperCase().split(",");
            
            // 파일 업로드 (groupId 필수)
            long maxSizeBytes = maxSizeMB * 1024L * 1024L;
            CommonAtchFileGroup fileGroup = fileAttachmentService.uploadFile(
                    file, groupId, allowedExtArray, maxSizeBytes);

            // 응답 생성
            FileUploadResDto resDto = FileUploadResDto.builder()
                    .atchFileGroupId(fileGroup.getAtchFileGroupId())
                    .uploadCnt(fileGroup.getFiles().size())
                    .savedFileList(new ArrayList<>())
                    .build();

            for (CommonAtchFile uploadedFile : fileGroup.getFiles()) {
                FileInfoDto fileInfo = FileInfoDto.builder()
                        .fileId(uploadedFile.getAtchFileId())
                        .fileName(uploadedFile.getFileNm())
                        .savedFileName(uploadedFile.getFilePathNm())
                        .fileSize(uploadedFile.getFileSz())
                        .contentType(uploadedFile.getCnType())
                        .build();
                resDto.getSavedFileList().add(fileInfo);
            }

            // DTO 응답 형식
            CommonResponseDto<FileUploadResDto> response = CommonResponseDto.<FileUploadResDto>builder()
                    .result("0")
                    .data(resDto)
                    .build();

            return ResponseEntity.ok(response);

        } catch (InvalidationException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            CommonResponseDto<FileUploadResDto> errorResponse = CommonResponseDto.<FileUploadResDto>builder()
                    .result("-1")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 다중 파일 업로드 (수동 업로드용)
     * 
     * POST /api/common/upload-multi
     */
    @PostMapping("/upload-multi")
    public ResponseEntity<CommonResponseDto<FileUploadResDto>> uploadMultipleFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "transdata", required = false) String transdata) {

        try {
            // 파일 목록 생성
            List<MultipartFile> fileList = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    fileList.add(file);
                }
            }

            if (fileList.isEmpty()) {
                throw new InvalidationException("업로드할 파일을 선택해 주세요.");
            }

            String allowedExts = this.allowedExtsValue;
            int maxSizeMB = this.maxSizeMBValue;
            // 확장자 배열 생성
            String[] allowedExtArray = allowedExts.toUpperCase().split(",");
            
            // 파일 업로드
            long maxSizeBytes = maxSizeMB * 1024L * 1024L;
            CommonAtchFileGroup fileGroup = fileAttachmentService.uploadFiles_toNewGroup(
                    fileList, allowedExtArray, maxSizeBytes);

            // 응답 생성
            FileUploadResDto resDto = FileUploadResDto.builder()
                    .atchFileGroupId(fileGroup.getAtchFileGroupId())
                    .uploadCnt(fileGroup.getFiles().size())
                    .savedFileList(new ArrayList<>())
                    .build();

            for (CommonAtchFile file : fileGroup.getFiles()) {
                FileInfoDto fileInfo = FileInfoDto.builder()
                        .fileId(file.getAtchFileId())
                        .fileName(file.getFileNm())
                        .savedFileName(file.getFilePathNm())
                        .fileSize(file.getFileSz())
                        .contentType(file.getCnType())
                        .build();
                resDto.getSavedFileList().add(fileInfo);
            }

            // DTO 응답 형식
            CommonResponseDto<FileUploadResDto> response = CommonResponseDto.<FileUploadResDto>builder()
                    .result("0")
                    .data(resDto)
                    .build();

            return ResponseEntity.ok(response);

        } catch (InvalidationException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            CommonResponseDto<FileUploadResDto> errorResponse = CommonResponseDto.<FileUploadResDto>builder()
                    .result("-1")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 단일 파일 다운로드
     * 
     * GET /api/common/download/{fileId}
     */
    @GetMapping("/download/{fileId}")
    public void downloadFile(@PathVariable(value = "fileId") String fileId, HttpServletResponse response) {
        try {
            fileAttachmentService.downloadFile(response, fileId);
        } catch (Exception e) {
            log.error("파일 다운로드 실패: {}", fileId, e);
            throw new InvalidationException("파일 다운로드에 실패했습니다.");
        }
    }

    /**
     * 그룹 파일 ZIP 다운로드
     * 
     * GET /api/common/download-group/{groupId}
     */
    @GetMapping("/download-group/{groupId}")
    public void downloadGroupFiles(
            @PathVariable(value = "groupId") String groupId,
            @RequestParam(value = "zipFileName", required = false, defaultValue = "files.zip") String zipFileName,
            HttpServletResponse response) {
        try {
            fileAttachmentService.downloadGroupFiles(response, groupId, zipFileName);
        } catch (Exception e) {
            log.error("그룹 파일 다운로드 실패: {}", groupId, e);
            throw new InvalidationException("파일 다운로드에 실패했습니다.");
        }
    }

    /**
     * 여러 파일 ID를 ZIP으로 다운로드
     * 
     * POST /api/common/download-zip
     */
    @PostMapping("/download-zip")
    public void downloadMultipleFilesAsZip(
            @RequestBody FileDownloadZipReqDto reqDto,
            HttpServletResponse response) {
        try {
            if (reqDto.getFileIds() == null || reqDto.getFileIds().isEmpty()) {
                throw new InvalidationException("다운로드할 파일을 선택해주세요.");
            }
            
            String zipFileName = reqDto.getZipFileName() != null ? reqDto.getZipFileName() : "files.zip";
            fileAttachmentService.downloadMultipleFiles(response, reqDto.getFileIds(), zipFileName);
        } catch (Exception e) {
            log.error("여러 파일 ZIP 다운로드 실패", e);
            throw new InvalidationException("파일 다운로드에 실패했습니다.");
        }
    }

    /**
     * 파일 삭제 (Soft Delete)
     * 
     * POST /api/common/file/delete
     */
    @PostMapping("/file/delete")
    public ResponseEntity<CommonResponseDto<Void>> deleteFile(@RequestBody FileDeleteReqDto reqDto) {
        try {
            if (reqDto.getFileId() == null || reqDto.getFileId().trim().isEmpty()) {
                throw new InvalidationException("삭제할 파일 ID를 입력해주세요.");
            }
            
            fileAttachmentService.deleteFile(reqDto.getFileId());
            
            CommonResponseDto<Void> response = CommonResponseDto.<Void>builder()
                    .result("0")
                    .message("파일이 삭제되었습니다.")
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (InvalidationException e) {
            log.error("파일 삭제 실패: {}", e.getMessage());
            CommonResponseDto<Void> errorResponse = CommonResponseDto.<Void>builder()
                    .result("-1")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            log.error("파일 삭제 실패", e);
            CommonResponseDto<Void> errorResponse = CommonResponseDto.<Void>builder()
                    .result("-1")
                    .message("파일 삭제에 실패했습니다.")
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 파일 목록 조회
     * 
     * GET /api/common/files/{groupId}
     */
    @GetMapping("/files/{groupId}")
    public ResponseEntity<CommonResponseDto<List<FileInfoDto>>> getFileList(@PathVariable(value = "groupId") String groupId) {
        try {
            List<CommonAtchFile> files = fileAttachmentService.getFileList(groupId);

            List<FileInfoDto> fileInfoList = new ArrayList<>();
            for (CommonAtchFile file : files) {
                FileInfoDto fileInfo = FileInfoDto.builder()
                        .fileId(file.getAtchFileId())
                        .fileGroupId(file.getAtchFileGroupId())
                        .fileName(file.getFileNm())
                        .fileSize(file.getFileSz())
                        .contentType(file.getCnType())
                        .downloadCnt(file.getDwnldCnt())
                        .regDt(file.getRegDt())
                        .build();
                fileInfoList.add(fileInfo);
            }

            CommonResponseDto<List<FileInfoDto>> response = CommonResponseDto.<List<FileInfoDto>>builder()
                    .result("0")
                    .data(fileInfoList)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("파일 목록 조회 실패: {}", groupId, e);
            CommonResponseDto<List<FileInfoDto>> errorResponse = CommonResponseDto.<List<FileInfoDto>>builder()
                    .result("-1")
                    .message("파일 목록 조회에 실패했습니다.")
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 파일 정보 조회 (파일 ID로 단일 파일 정보 조회)
     * 
     * GET /api/common/file/{fileId}
     */
    @GetMapping("/file/{fileId}")
    public ResponseEntity<CommonResponseDto<FileInfoDto>> getFileInfo(@PathVariable(value = "fileId") String fileId) {
        try {
            CommonAtchFile file = fileAttachmentService.getFileInfo(fileId);
            
            if (file == null) {
                CommonResponseDto<FileInfoDto> errorResponse = CommonResponseDto.<FileInfoDto>builder()
                        .result("-1")
                        .message("파일을 찾을 수 없습니다.")
                        .build();
                return ResponseEntity.badRequest().body(errorResponse);
            }

            FileInfoDto fileInfo = FileInfoDto.builder()
                    .fileId(file.getAtchFileId())
                    .fileGroupId(file.getAtchFileGroupId())
                    .fileName(file.getFileNm())
                    .savedFileName(file.getFilePathNm())
                    .fileSize(file.getFileSz())
                    .contentType(file.getCnType())
                    .downloadCnt(file.getDwnldCnt())
                    .regDt(file.getRegDt())
                    .build();

            CommonResponseDto<FileInfoDto> response = CommonResponseDto.<FileInfoDto>builder()
                    .result("0")
                    .data(fileInfo)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("파일 정보 조회 실패: {}", fileId, e);
            CommonResponseDto<FileInfoDto> errorResponse = CommonResponseDto.<FileInfoDto>builder()
                    .result("-1")
                    .message("파일 정보 조회에 실패했습니다.")
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ============================================================
    // 메타정보 조회 API (Backend 간 통신용)
    // ============================================================

    /**
     * 단일 파일 메타정보 조회 (Backend 간 통신용)
     * 
     * GET /api/common/file/meta/{fileId}
     */
    @GetMapping("/file/meta/{fileId}")
    public ResponseEntity<FileMetaResDto> getFileMeta(@PathVariable(value = "fileId") String fileId) {
        log.debug("파일 메타정보 조회 - fileId: {}", fileId);

        try {
            CommonAtchFile file = fileAttachmentService.getFileInfo(fileId);
            if (file == null) {
                return ResponseEntity.ok(FileMetaResDto.error("파일을 찾을 수 없습니다."));
            }

            FileMetaDto dto = convertToMetaDto(file);
            return ResponseEntity.ok(FileMetaResDto.success(dto));

        } catch (Exception e) {
            log.error("파일 메타정보 조회 실패 - fileId: {}", fileId, e);
            return ResponseEntity.ok(FileMetaResDto.error("파일 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 그룹 파일 메타정보 목록 조회 (Backend 간 통신용)
     * 
     * GET /api/common/files/meta/{groupId}
     */
    @GetMapping("/files/meta/{groupId}")
    public ResponseEntity<FileMetaResDto> getGroupFileMeta(@PathVariable(value = "groupId") String groupId) {
        log.debug("그룹 파일 메타정보 조회 - groupId: {}", groupId);

        try {
            List<CommonAtchFile> files = fileAttachmentService.getFileList(groupId);
            if (files == null || files.isEmpty()) {
                return ResponseEntity.ok(FileMetaResDto.error("파일 그룹을 찾을 수 없습니다."));
            }

            List<FileMetaDto> dtoList = files.stream()
                    .map(this::convertToMetaDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(FileMetaResDto.successList(dtoList));

        } catch (Exception e) {
            log.error("그룹 파일 메타정보 조회 실패 - groupId: {}", groupId, e);
            return ResponseEntity.ok(FileMetaResDto.error("파일 조회 중 오류가 발생했습니다."));
        }
    }

    // ============================================================
    // 외부 파일 API (AICC 파일)
    // ============================================================

    /**
     * AICC 파일 메타정보 조회
     * 
     * GET /api/common/external/meta/{fileId}?cmpyId=xxx
     */
    @GetMapping("/external/meta/{fileId}")
    public ResponseEntity<FileMetaResDto> getExternalFileMeta(
            @PathVariable(value = "fileId") String fileId,
            @RequestParam(value = "cmpyId") String cmpyId) {
        log.debug("외부(AICC) 파일 메타정보 조회 - fileId: {}, cmpyId: {}", fileId, cmpyId);


        try {
            // AICC API 호출
            String endpoint = "/api/admin/files/meta/" + fileId;
            FileMetaResDto response = companyApiAdapter.get(cmpyId, endpoint, null, FileMetaResDto.class);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("외부(AICC) 파일 메타정보 조회 실패 - fileId: {}, cmpyId: {}", fileId, cmpyId, e);
            return ResponseEntity.ok(FileMetaResDto.error("AICC 시스템 연결 실패: " + e.getMessage()));
        }
    }

    /**
     * AICC 그룹 파일 메타정보 조회
     * 
     * GET /api/common/external/files/meta/{groupId}?cmpyId=xxx
     */
    @GetMapping("/external/files/meta/{groupId}")
    public ResponseEntity<FileMetaResDto> getExternalGroupFileMeta(
            @PathVariable(value = "groupId") String groupId,
            @RequestParam(value = "cmpyId") String cmpyId) {
        log.debug("외부(AICC) 그룹 파일 메타정보 조회 - groupId: {}, cmpyId: {}", groupId, cmpyId);

        try {
            // AICC API 호출
            String endpoint = "/api/admin/files/meta/group/" + groupId;
            FileMetaResDto response = companyApiAdapter.get(cmpyId, endpoint, null, FileMetaResDto.class);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("외부(AICC) 그룹 파일 메타정보 조회 실패 - groupId: {}, cmpyId: {}", groupId, cmpyId, e);
            return ResponseEntity.ok(FileMetaResDto.error("AICC 시스템 연결 실패: " + e.getMessage()));
        }
    }

    /**
     * AICC 파일 다운로드 (메타정보 조회 후 로컬 Mount에서 다운로드)
     * 
     * GET /api/common/external/download/{fileId}?cmpyId=xxx
     */
    @GetMapping("/external/download/{fileId}")
    public void downloadExternalFile(
            HttpServletResponse response,
            @PathVariable(value = "fileId") String fileId,
            @RequestParam(value = "cmpyId") String cmpyId) {
        log.debug("외부(AICC) 파일 다운로드 - fileId: {}, cmpyId: {}", fileId, cmpyId);

        // 파일 ID 검증
        // if (!FileIdGenerator.isAiccFile(fileId)) {
        //     throw new InvalidationException("AICC 시스템 파일이 아닙니다.");
        // }

        try {
            // 1. 메타정보 조회
            String endpoint = "/api/admin/files/meta/" + fileId;
            FileMetaResDto metaResponse = companyApiAdapter.get(cmpyId, endpoint, null, FileMetaResDto.class);

            if (!"0".equals(metaResponse.getResult()) || metaResponse.getData() == null) {
                throw new InvalidationException("파일을 찾을 수 없습니다.");
            }

            FileMetaDto meta = metaResponse.getData();

            // 2. 로컬 Mount에서 직접 다운로드
            Path filePath = Paths.get(basePath, meta.getFilePath());
            fileAttachmentService.downloadFromLocal(response, filePath, meta.getFileName(), meta.getContentType());

        } catch (InvalidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("외부(AICC) 파일 다운로드 실패 - fileId: {}, cmpyId: {}", fileId, cmpyId, e);
            throw new InvalidationException("파일 다운로드에 실패했습니다.");
        }
    }

    // ============================================================
    // Private Methods
    // ============================================================

    /**
     * CommonAtchFile을 FileMetaDto로 변환
     */
    private FileMetaDto convertToMetaDto(CommonAtchFile file) {
        return FileMetaDto.builder()
                .fileId(file.getAtchFileId())
                .fileGroupId(file.getAtchFileGroupId())
                .fileName(file.getFileNm())
                .fileSize(file.getFileSz())
                .contentType(file.getCnType())
                .filePath(file.getFilePathNm())
                .downloadCnt(file.getDwnldCnt())
                .regDt(file.getRegDt())
                .systemCode(systemCode)
                .companyId(companyId)
                .build();
    }
}
