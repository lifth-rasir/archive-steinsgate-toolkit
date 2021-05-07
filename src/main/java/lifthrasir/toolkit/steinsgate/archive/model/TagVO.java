package lifthrasir.toolkit.steinsgate.archive.model;

/**
 * 해시태그
 */
public class TagVO {
	/** 태그 이름 */
	private String tagName;

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TagVO [tagName=").append(tagName).append("]");
		return builder.toString();
	}
}
