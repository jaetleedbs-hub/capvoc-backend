package buttle.apptpl.common.util;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.util.vo.MailMessage;
import buttle.apptpl.common.vo.CommonAtchFile;


/**
 * admin-backend 의 메일 발송기.
 *
 * <p><b>현재 호출부는 0건</b>이다(전 리포 재귀 검색). 그래도 desk·admin-batch 와 같은 안전장치를
 * 둔다 — 호출부가 붙는 순간 이 장치가 없으면 그때 사고가 나기 때문이다
 * (W0-6 에서 admin 복사본이 누락돼 N-37 로 다시 조치한 것이 같은 패턴이다).</p>
 */
@Slf4j
@Component("svcMailSender")
public class MailSender {
	@Autowired
    private JavaMailSender javaMailSender;

    /** SMTP 인증 계정. 발신 주소가 아니다. */
    @Value("${spring.mail.username:}")
    private String smtpUsername;

    /** 발신 주소. 비어 있으면 인증 계정으로 폴백. */
    @Value("${app.mail.from:}")
    private String fromAddress;

    /** 발송 사용 여부. false 면 발송하지 않는다. */
    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    /** 수신자 강제 전환 주소. 값이 있으면 원래 수신자를 전부 버리고 이 주소로만 보낸다. */
    @Value("${app.mail.override-to:}")
    private String overrideTo;

    @Value("${file.upload.base-path}")
    private String basePath;

    public boolean send(MailMessage mailMessage) {
        if (!mailEnabled) {
            log.warn("[메일 발송 비활성] app.mail.enabled=false — 발송하지 않는다. subject: {}", mailMessage.getSubject());
            return false;
        }

        String sender = resolveFromAddress();
        if (sender == null) {
            log.error("메일 발신자 주소가 없어 발송할 수 없습니다(app.mail.from 또는 spring.mail.username). subject: {}",
                    mailMessage.getSubject());
            return false;
        }

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true, "UTF-8");
            for (String emlAddr : resolveRecipients(mailMessage)) {
                mimeMessageHelper.addTo(emlAddr);
            }
            mimeMessageHelper.setSubject(mailMessage.getSubject());
            mimeMessageHelper.setText(mailMessage.getMessage(), true);
            mimeMessageHelper.setFrom(sender);

            
            if(!CollectionUtils.isEmpty(mailMessage.getMultipartFile())) {
                for(CommonAtchFile file: mailMessage.getMultipartFile()) {

                    
                    String absolutePath = Paths.get(basePath, file.getFilePathNm()).toString();
                    File attachmentFile = new File(absolutePath);

                    mimeMessageHelper.addAttachment(file.getFileNm(), attachmentFile);
                }
            }

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("메일 발송 실패(메시지 구성 오류) - subject: {}", mailMessage.getSubject(), e);
            return false;
        } catch (MailException e) {
            log.error("메일 발송 실패(SMTP 오류) - subject: {}", mailMessage.getSubject(), e);
            return false;
        }
        return true;
    }

    /** 발신 주소를 정한다. {@code app.mail.from} 우선, 없으면 SMTP 인증 계정. 둘 다 없으면 null. */
    private String resolveFromAddress() {
        if (fromAddress != null && !fromAddress.isBlank()) {
            return fromAddress.trim();
        }
        if (smtpUsername != null && !smtpUsername.isBlank()) {
            return smtpUsername.trim();
        }
        return null;
    }

    /**
     * 실제 수신자를 정한다. {@code app.mail.override-to} 가 있으면 원래 목록을 전부 버리고
     * 그 주소만 반환한다. 호출부의 {@link MailMessage} 는 건드리지 않는다.
     */
    private List<String> resolveRecipients(MailMessage mailMessage) {
        List<String> original = mailMessage.getTo() == null ? List.of() : mailMessage.getTo();
        if (overrideTo == null || overrideTo.isBlank()) {
            return original;
        }
        log.warn("[메일 수신자 전환] app.mail.override-to 설정으로 원래 수신자 {}건 대신 {} 로만 발송한다. subject: {}",
                original.size(), overrideTo, mailMessage.getSubject());
        return List.of(overrideTo);
    }
}
