package lifthrasir.toolkit.steinsgate.archive.model;

/**
 * 메뉴 그룹
 */
public class MenuGroupVO {
	/** 메뉴 그룹 아이디 */
	private Integer menuGroupId;
	/** 메뉴 그룹 이름 */
	private String menuGropName;

	public Integer getMenuGroupId() {
		return menuGroupId;
	}

	public void setMenuGroupId(Integer menuGroupId) {
		this.menuGroupId = menuGroupId;
	}

	public String getMenuGropName() {
		return menuGropName;
	}

	public void setMenuGropName(String menuGropName) {
		this.menuGropName = menuGropName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MenuGroupVO [menuGroupId=").append(menuGroupId).append(", menuGropName=").append(menuGropName)
				.append("]");
		return builder.toString();
	}
}
