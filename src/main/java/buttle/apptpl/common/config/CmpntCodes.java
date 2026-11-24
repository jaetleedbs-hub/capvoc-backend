package buttle.apptpl.common.config;

public class CmpntCodes {

	public static String[] imageExts = {"png", "jpg", "jpeg", "gif", "bmp"};

	public static class bannerCd {
		public static final String CD_GROUP = "BANNER_CD";
	}

	public static class popupNtcCd {
		public static final String CD_GROUP = "POPUP_NTC_CD";
	}

	public static class mbrSttsCd {
		public static final String CD_GROUP = "MBR_STTS_CD";

		public static final String NORMAL = "NORMAL";
		public static final String STOP = "STOP";
		public static final String LOCK = "LOCK";
		public static final String WAIT = "WAIT";
	}

	public static class authrtCd {
		public static final String MBR = "ROLE_MBR";
	}
}
