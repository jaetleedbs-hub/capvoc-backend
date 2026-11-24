package buttle.apptpl.common.framework.config;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import micoai.corebase.exception.InvalidationException;
import micoai.corebase.util.CoreUtils;
import micoai.corebase.util.CoreUtils.string;

@Slf4j
@Data
@Component
@ConfigurationProperties("env")
public class EnvConfig {

	/** AES256 암호화 키 */
	private String aesKey;

	@Data
	public static class Dir {
		private String coreLog;
		private String upload;

		public String getTempDir() {
			if (string.isBlank(this.getUpload())) {
				throw new InvalidationException("파일 업로드 디렉터리 설정이 없습니다.");
			}

			String tmpDirName = this.getUpload() + File.separator + "_tmp";
			File tmpDir = new File(tmpDirName);
			if (!CoreUtils.file.isDirectory(tmpDir)) {
				tmpDir.mkdirs();
			}

			return tmpDirName;
		}

		public void setCoreLog(String coreLog) {
			if (string.startsWith(coreLog, "~/")) {
				this.coreLog = System.getProperty("user.home") + string.substring(coreLog, 1);
			}
			else {
				this.coreLog = coreLog;
			}
		}

		public void setUpload(String upload) {
			if (string.startsWith(upload, "~/")) {
				this.upload = System.getProperty("user.home") + string.substring(upload, 1);
			}
			else {
				this.upload = upload;
			}
		}
	}


}
