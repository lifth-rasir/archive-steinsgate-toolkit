package lifthrasir.toolkit.steinsgate.archive.model;

/**
 * 등급
 */
public class GradeVO {
	/** 등급 이름 */
	private String gradeName;

	public String getGradeName() {
		return gradeName;
	}

	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GradeVO [gradeName=").append(gradeName).append("]");
		return builder.toString();
	}}
