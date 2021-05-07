package lifthrasir.toolkit.steinsgate.archive.enums;

/**
 * 멀티미디어 타입
 */
public enum MultimediaTypeEnum{
	
	/** 댓글 이미지 */
	COMMENT_IMAGE("COMMENT_IMAGE"),
	/** 게시글 본문 이미지 */
	POST_BODY_IMAGE("POST_BODY_IMAGE"),
	/** 게시글 본문 동영상 */
	POST_BODY_MOVIE("POST_BODY_MOVIE"),
	/** 게시글 첨부파일 */
	POST_ATTACHMENT("POST_ATTACHMENT"),
	/** 프로필 이미지 */
	PROFILE_IMAGE("PROFILE_IMAGE");
	
	private final String key;
	
	private MultimediaTypeEnum(String key){
		this.key = key;
	}

	public String getKey(){
		return key;
	}
}
