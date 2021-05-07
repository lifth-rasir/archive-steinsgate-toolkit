package lifthrasir.toolkit.steinsgate.archive.model;

/**
 * 게시글-멀티미디어
 */
public class PostMultimediaVO {
	/** 게시글 번호 */
	private Integer postNo;
	/** 멀티미디어 URL */
	private String multimediaUrl;

	public Integer getPostNo() {
		return postNo;
	}

	public void setPostNo(Integer postNo) {
		this.postNo = postNo;
	}

	public String getMultimediaUrl() {
		return multimediaUrl;
	}

	public void setMultimediaUrl(String multimediaUrl) {
		this.multimediaUrl = multimediaUrl;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PostMultimediaVO [postNo=").append(postNo).append(", multimediaUrl=").append(multimediaUrl)
				.append("]");
		return builder.toString();
	}
}
