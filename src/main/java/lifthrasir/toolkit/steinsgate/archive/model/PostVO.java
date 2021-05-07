package lifthrasir.toolkit.steinsgate.archive.model;

import java.util.Date;

/**
 * 게시글
 */
public class PostVO {
	/** 게시글 번호 */
	private Integer postNo;
	/** 메뉴 외래키 */
	private String menuFk;
	/** 게시글 제목 */
	private String postTitle;
	/** 게시글 조회수 */
	private Integer postHits;
	/** 게시글 본문 */
	private String postBody;
	/** 게시글 좋아요 수 */
	private Integer postLikeCount;
	/** 게시글 공유 수 */
	private Integer postShareCount;
	/** 게시글 마지막 수정 날짜 */
	private Date postLastModifyDate;
	/** 게시글 원본 URL */
	private String postOriginalUrl;
	/** 게시글 공지사항 여부 YN */
	private String postNoticeYn;
	/** 게시글 CCL 여부 YN */
	private String postCclYn;
	/** 게시글 작성 닉네임 */
	private String postRegisterNickname;
	
	public Integer getPostNo() {
		return postNo;
	}

	public void setPostNo(Integer postNo) {
		this.postNo = postNo;
	}

	public String getMenuFk() {
		return menuFk;
	}

	public void setMenuFk(String menuFk) {
		this.menuFk = menuFk;
	}

	public String getPostTitle() {
		return postTitle;
	}

	public void setPostTitle(String postTitle) {
		this.postTitle = postTitle;
	}

	public Integer getPostHits() {
		return postHits;
	}

	public void setPostHits(Integer postHits) {
		this.postHits = postHits;
	}

	public String getPostBody() {
		return postBody;
	}

	public void setPostBody(String postBody) {
		this.postBody = postBody;
	}
	
	public Integer getPostLikeCount() {
		return postLikeCount;
	}

	public void setPostLikeCount(Integer postLikeCount) {
		this.postLikeCount = postLikeCount;
	}

	public Integer getPostShareCount() {
		return postShareCount;
	}

	public void setPostShareCount(Integer postShareCount) {
		this.postShareCount = postShareCount;
	}

	public Date getPostLastModifyDate() {
		return postLastModifyDate;
	}

	public void setPostLastModifyDate(Date postLastModifyDate) {
		this.postLastModifyDate = postLastModifyDate;
	}

	public String getPostOriginalUrl() {
		return postOriginalUrl;
	}

	public void setPostOriginalUrl(String postOriginalUrl) {
		this.postOriginalUrl = postOriginalUrl;
	}

	public String getPostNoticeYn() {
		return postNoticeYn;
	}

	public void setPostNoticeYn(String postNoticeYn) {
		this.postNoticeYn = postNoticeYn;
	}

	public String getPostCclYn() {
		return postCclYn;
	}

	public void setPostCclYn(String postCclYn) {
		this.postCclYn = postCclYn;
	}

	public String getPostRegisterNickname() {
		return postRegisterNickname;
	}

	public void setPostRegisterNickname(String postRegisterNickname) {
		this.postRegisterNickname = postRegisterNickname;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PostVO [postNo=").append(postNo).append(", menuFk=").append(menuFk).append(", postTitle=")
				.append(postTitle).append(", postHits=").append(postHits).append(", postBody=").append(postBody)
				.append(", postLikeCount=").append(postLikeCount).append(", postShareCount=").append(postShareCount)
				.append(", postLastModifyDate=").append(postLastModifyDate).append(", postOriginalUrl=")
				.append(postOriginalUrl).append(", postNoticeYn=").append(postNoticeYn).append(", postCclYn=")
				.append(postCclYn).append(", postRegisterNickname=").append(postRegisterNickname).append("]");
		return builder.toString();
	}
}
