package lifthrasir.toolkit.steinsgate.archive.model;

/**
 * 게시글-해시태그
 */
public class PostTagVO {
	/** 게시글 번호 */
	private Integer postNo;
	/** 태그 이름 */
	private String tagName;

	public Integer getPostNo() {
		return postNo;
	}

	public void setPostNo(Integer postNo) {
		this.postNo = postNo;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PostTagVO [postNo=").append(postNo).append(", tagName=").append(tagName).append("]");
		return builder.toString();
	}
}
