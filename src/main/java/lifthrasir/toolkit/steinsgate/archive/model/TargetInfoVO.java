package lifthrasir.toolkit.steinsgate.archive.model;

import com.google.gson.annotations.SerializedName;

public class TargetInfoVO {
	/** 대메뉴명 */
	@SerializedName("mn_grp_nm")
	private String mnGrpNm;
	/** 소메뉴명 */
	@SerializedName("mn_nm")
	private String mnNm;
	/** 게시글 번호 */
	@SerializedName("pst_no")
	private String pstNo;
	/** 게시글 제목 */
	@SerializedName("pst_title")
	private String pstTitle;

	public String getMnGrpNm() {
		return mnGrpNm;
	}

	public void setMnGrpNm(String mnGrpNm) {
		this.mnGrpNm = mnGrpNm;
	}

	public String getMnNm() {
		return mnNm;
	}

	public void setMnNm(String mnNm) {
		this.mnNm = mnNm;
	}

	public String getPstNo() {
		return pstNo;
	}

	public void setPstNo(String pstNo) {
		this.pstNo = pstNo;
	}

	public String getPstTitle() {
		return pstTitle;
	}

	public void setPstTitle(String pstTitle) {
		this.pstTitle = pstTitle;
	}

	@Override
	public String toString() {
		return "TargetInfoVO [mnGrpNm=" + mnGrpNm + ", mnNm=" + mnNm + ", pstNo=" + pstNo + ", pstTitle=" + pstTitle
				+ "]";
	}
}
