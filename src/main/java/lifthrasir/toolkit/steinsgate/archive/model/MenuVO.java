package lifthrasir.toolkit.steinsgate.archive.model;

/**
 * 메뉴
 */
public class MenuVO {
	/** 메뉴 아이디 */
	private String menuId;
	/** 메뉴 그룹 외래키 */
	private Integer menuGroupFk;
	/** 메뉴 이름 */
	private String menuName;
	/** 메뉴 정렬순서 */
	private Integer menuOrder;
	
	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public Integer getMenuGroupFk() {
		return menuGroupFk;
	}

	public void setMenuGroupFk(Integer menuGroupFk) {
		this.menuGroupFk = menuGroupFk;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public Integer getMenuOrder() {
		return menuOrder;
	}

	public void setMenuOrder(Integer menuOrder) {
		this.menuOrder = menuOrder;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MenuVO [menuId=").append(menuId).append(", menuGroupFk=").append(menuGroupFk)
				.append(", menuName=").append(menuName).append(", menuOrder=").append(menuOrder).append("]");
		return builder.toString();
	}
}
