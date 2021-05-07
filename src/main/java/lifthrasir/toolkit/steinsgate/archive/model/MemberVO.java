package lifthrasir.toolkit.steinsgate.archive.model;

import java.util.Date;

/**
 * 회원
 */
public class MemberVO {
	/** 계정 아이디 */
	private String memberId;
	/** 계정 닉네임 */
	private String memberNickname;
	/** 클럽아이디 */
	private String clubid;
	/** 등급 외래키 */
	private String gradeFk;
	/** 프로필 사진 URL */
	private String profileImageUrl;
	/** 계정 마지막 활동 날짜 */
	private Date memberLastActivityDate;

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemberNickname() {
		return memberNickname;
	}

	public void setMemberNickname(String memberNickname) {
		this.memberNickname = memberNickname;
	}

	public String getClubid() {
		return clubid;
	}

	public void setClubid(String clubid) {
		this.clubid = clubid;
	}

	public String getGradeFk() {
		return gradeFk;
	}

	public void setGradeFk(String gradeFk) {
		this.gradeFk = gradeFk;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public Date getMemberLastActivityDate() {
		return memberLastActivityDate;
	}

	public void setMemberLastActivityDate(Date memberLastActivityDate) {
		this.memberLastActivityDate = memberLastActivityDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MemberVO [memberId=").append(memberId).append(", memberNickname=").append(memberNickname)
				.append(", clubid=").append(clubid).append(", gradeFk=").append(gradeFk).append(", profileImageUrl=")
				.append(profileImageUrl).append(", memberLastActivityDate=").append(memberLastActivityDate).append("]");
		return builder.toString();
	}
}
