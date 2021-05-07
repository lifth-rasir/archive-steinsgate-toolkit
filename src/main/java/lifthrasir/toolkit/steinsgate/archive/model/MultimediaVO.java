package lifthrasir.toolkit.steinsgate.archive.model;

/**
 * 멀티미디어
 */
public class MultimediaVO {
	/** 멀티미디어 URL */
	private String multimediaUrl;
	/** 멀티미디어 경로 */
	private String multimediaDirectory;
	/** 멀티미디어 파일 이름 */
	private String multimediaFileName;
	/** 멀티미디어 유형 외래키 */
	private String multimediaTypeFk;

	public String getMultimediaUrl() {
		return multimediaUrl;
	}

	public void setMultimediaUrl(String multimediaUrl) {
		this.multimediaUrl = multimediaUrl;
	}

	public String getMultimediaDirectory() {
		return multimediaDirectory;
	}

	public void setMultimediaDirectory(String multimediaDirectory) {
		this.multimediaDirectory = multimediaDirectory;
	}

	public String getMultimediaFileName() {
		return multimediaFileName;
	}

	public void setMultimediaFileName(String multimediaFileName) {
		this.multimediaFileName = multimediaFileName;
	}

	public String getMultimediaTypeFk() {
		return multimediaTypeFk;
	}

	public void setMultimediaTypeFk(String multimediaTypeFk) {
		this.multimediaTypeFk = multimediaTypeFk;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MultimediaVO [multimediaUrl=").append(multimediaUrl).append(", multimediaDirectory=")
				.append(multimediaDirectory).append(", multimediaFileName=").append(multimediaFileName)
				.append(", multimediaTypeFk=").append(multimediaTypeFk).append("]");
		return builder.toString();
	}
}
