package lifthrasir.toolkit.steinsgate.archive.model;

import java.util.Date;

/**
 * 댓글
 */
public class CommentVO {
	/** 댓글 번호 */
	private Integer commentNo;
	/** 댓글 부모 번호 외래키 */
	private Integer commentParentNoFk;
	/** 댓글 작성 아이디 */
	private String commentRegisterId;
	/** 댓글 본문 */
	private String commentBody;
	/** 댓글 마지막 수정 날짜 */
	private Date commentLastModifyDate;
	/** 댓글 정렬순서 */
	private Integer commentOrder;
	/** 게시글 번호 외래키 */
	private Integer postNoFk;
	/** 멀티미디어 URL 외래키 */
	private String multimediaUrlFk;
	/** 댓글 답글 여부 YN */
	private String commentReplyYn;
	/** 댓글 작성 닉네임 */
	private String commentRegisterNickname;

	public Integer getCommentNo() {
		return commentNo;
	}

	public void setCommentNo(Integer commentNo) {
		this.commentNo = commentNo;
	}

	public Integer getCommentParentNoFk() {
		return commentParentNoFk;
	}

	public void setCommentParentNoFk(Integer commentParentNoFk) {
		this.commentParentNoFk = commentParentNoFk;
	}

	public String getCommentRegisterId() {
		return commentRegisterId;
	}

	public void setCommentRegisterId(String commentRegisterId) {
		this.commentRegisterId = commentRegisterId;
	}

	public String getCommentBody() {
		return commentBody;
	}

	public void setCommentBody(String commentBody) {
		this.commentBody = commentBody;
	}

	public Date getCommentLastModifyDate() {
		return commentLastModifyDate;
	}

	public void setCommentLastModifyDate(Date commentLastModifyDate) {
		this.commentLastModifyDate = commentLastModifyDate;
	}

	public Integer getCommentOrder() {
		return commentOrder;
	}

	public void setCommentOrder(Integer commentOrder) {
		this.commentOrder = commentOrder;
	}

	public Integer getPostNoFk() {
		return postNoFk;
	}

	public void setPostNoFk(Integer postNoFk) {
		this.postNoFk = postNoFk;
	}

	public String getMultimediaUrlFk() {
		return multimediaUrlFk;
	}

	public void setMultimediaUrlFk(String multimediaUrlFk) {
		this.multimediaUrlFk = multimediaUrlFk;
	}

	public String getCommentReplyYn() {
		return commentReplyYn;
	}

	public void setCommentReplyYn(String commentReplyYn) {
		this.commentReplyYn = commentReplyYn;
	}

	public String getCommentRegisterNickname() {
		return commentRegisterNickname;
	}

	public void setCommentRegisterNickname(String commentRegisterNickname) {
		this.commentRegisterNickname = commentRegisterNickname;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CommentVO [commentNo=").append(commentNo).append(", commentParentNoFk=")
				.append(commentParentNoFk).append(", commentRegisterId=").append(commentRegisterId)
				.append(", commentBody=").append(commentBody).append(", commentLastModifyDate=")
				.append(commentLastModifyDate).append(", commentOrder=").append(commentOrder).append(", postNoFk=")
				.append(postNoFk).append(", multimediaUrlFk=").append(multimediaUrlFk).append(", commentReplyYn=")
				.append(commentReplyYn).append(", commentRegisterNickname=").append(commentRegisterNickname)
				.append("]");
		return builder.toString();
	}
}
