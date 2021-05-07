package lifthrasir.toolkit.steinsgate.archive.model;

import java.util.List;

/**
 * 네이버 카페 게시글 정보
 */
public class CafePostVO {
	/** 메뉴 그룹 */
	private MenuGroupVO menuGroupVO;
	/** 메뉴 */
	private MenuVO menuVO;
	/** 게시글 */
	private PostVO postVO;
	/** 게시글-해시태그 */
	private List<PostTagVO> postTagVOList;
	/** 해시태그 */
	private List<TagVO> tagVOList;
	/** 등급 */
	private GradeVO gradeVO;
	/** 회원 */
	private List<MemberVO> memberVOList;
	/** 게시글-회원 */
	private List<PostMemberVO> postMemberVOList;
	/** 멀티미디어 */
	private List<MultimediaVO> multimediaVOList;
	/** 게시글-멀티미디어 */
	private List<PostMultimediaVO> postMultimediaVOList;
	/** 댓글 */
	private List<CommentVO> commentVOList;

	public MenuGroupVO getMenuGroupVO() {
		return menuGroupVO;
	}

	public void setMenuGroupVO(MenuGroupVO menuGroupVO) {
		this.menuGroupVO = menuGroupVO;
	}

	public MenuVO getMenuVO() {
		return menuVO;
	}

	public void setMenuVO(MenuVO menuVO) {
		this.menuVO = menuVO;
	}

	public PostVO getPostVO() {
		return postVO;
	}

	public void setPostVO(PostVO postVO) {
		this.postVO = postVO;
	}

	public List<PostTagVO> getPostTagVOList() {
		return postTagVOList;
	}

	public void setPostTagVOList(List<PostTagVO> postTagVOList) {
		this.postTagVOList = postTagVOList;
	}

	public List<TagVO> getTagVOList() {
		return tagVOList;
	}

	public void setTagVOList(List<TagVO> tagVOList) {
		this.tagVOList = tagVOList;
	}

	public GradeVO getGradeVO() {
		return gradeVO;
	}

	public void setGradeVO(GradeVO gradeVO) {
		this.gradeVO = gradeVO;
	}

	public List<MemberVO> getMemberVOList() {
		return memberVOList;
	}

	public void setMemberVOList(List<MemberVO> memberVOList) {
		this.memberVOList = memberVOList;
	}

	public List<PostMemberVO> getPostMemberVOList() {
		return postMemberVOList;
	}

	public void setPostMemberVOList(List<PostMemberVO> postMemberVOList) {
		this.postMemberVOList = postMemberVOList;
	}

	public List<MultimediaVO> getMultimediaVOList() {
		return multimediaVOList;
	}

	public void setMultimediaVOList(List<MultimediaVO> multimediaVOList) {
		this.multimediaVOList = multimediaVOList;
	}

	public List<PostMultimediaVO> getPostMultimediaVOList() {
		return postMultimediaVOList;
	}

	public void setPostMultimediaVOList(List<PostMultimediaVO> postMultimediaVOList) {
		this.postMultimediaVOList = postMultimediaVOList;
	}

	public List<CommentVO> getCommentVOList() {
		return commentVOList;
	}

	public void setCommentVOList(List<CommentVO> commentVOList) {
		this.commentVOList = commentVOList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CafePostVO [menuGroupVO=").append(menuGroupVO).append(", menuVO=").append(menuVO)
				.append(", postVO=").append(postVO).append(", postTagVOList=").append(postTagVOList)
				.append(", tagVOList=").append(tagVOList).append(", gradeVO=").append(gradeVO).append(", memberVOList=")
				.append(memberVOList).append(", multimediaVOList=").append(multimediaVOList)
				.append(", postMultimediaVOList=").append(postMultimediaVOList).append(", commentVOList=")
				.append(commentVOList).append("]");
		return builder.toString();
	}
}
