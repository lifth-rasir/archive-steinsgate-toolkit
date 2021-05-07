package lifthrasir.toolkit.steinsgate.archive.enums;

public enum CharsetEnum{
	
	UTF_8("UTF-8"),
	EUC_KR("EUC-KR");
	
	private final String key;
	
	private CharsetEnum(String key){
		this.key = key;
	}

	public String getKey(){
		return key;
	}

	@Override
	public String toString() {
		return this.getKey();
	}
}
