package lifthrasir.toolkit.steinsgate.archive.model;

/**
 * 게시글-회원
 */
public class PostMemberVO {
	/** 게시글 번호 */
	private Integer postNo;
	/** 계정 아이디 */
	private String memberId;

	public Integer getPostNo() {
		return postNo;
	}

	public void setPostNo(Integer postNo) {
		this.postNo = postNo;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PostMemberVO [postNo=").append(postNo).append(", memberId=").append(memberId).append("]");
		return builder.toString();
	}
}
