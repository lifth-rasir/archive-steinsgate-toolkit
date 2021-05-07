package lifthrasir.toolkit.steinsgate.archive.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import lifthrasir.toolkit.steinsgate.archive.enums.CharsetEnum;

public class StringUtil {

	/**
	 * URL쿼리를 Map 형태로 파싱합니다.
	 * @param query
	 * @return
	 */
	public static Map<String, String> queryToMap(String query) {
		Map<String, String> result = new HashMap<String, String>();
		for (String param : query.split("&")) {
			String pair[] = param.split("=");
			if (pair.length > 1) {
				result.put(pair[0], pair[1]);
			} else {
				result.put(pair[0], "");
			}
		}
		return result;
	}
	
	/**
	 * 주어진 문자열에서 콤마 기호를 제거합니다.
	 * @param data
	 * @return
	 */
	public static String removeComma(String data) {
		if(StringUtils.hasLength(data) == false) {
			return data;
		}
		return data.replaceAll("\\,", "");
	}
	
	/**
	 * 주어진 문자열의 바이트 길이를 반환합니다
	 * @param s
	 * @return
	 */
	public static int getEncodedLength(String s) {
		if (s == null) {
			throw new IllegalArgumentException("Cannot calculate UTF-8 length of null string");
		}

		int result = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if ((c >= 0x0001) && (c <= 0x007F)) {
				result++;
			} else if (c > 0x07FF) {
				result += 3;
			} else {
				result += 2;
			}
		}
		return result;
	}
	
	/**
	 * 주어진 문자열의 인코딩을 추측해 디코딩한 후 반환합니다. UTF-8과 EUC-KR이 아닌 경우 원본 문자열을 반환합니다.
	 * @param text
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String guessCharset(String text) {
		if(StringUtils.hasLength(text) == false) {
			return text;
		}
		
		String result = null;
		try {
			String utfLine = URLDecoder.decode(text, CharsetEnum.UTF_8.getKey());
			String euckrLine = URLDecoder.decode(text, CharsetEnum.EUC_KR.getKey());
			Pattern pattern = Pattern.compile("[^óⅡ↘↗øĸo☆〜β１※【】「」『』・／º＿－（）　~ ：:;\\.\\[\\]!@\\#$%<>^&*\\()\\-=+_\\’\\'\\?a-zA-Z0-9가-힇ㄱ-ㅎㅏ-ㅣぁ-ゔァ-ヴー々〆〤一-龥]");
			result = pattern.matcher(utfLine).find() ? (pattern.matcher(euckrLine).find() ? text : euckrLine) : utfLine;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result.trim();
	}
	
	/**
	 * 주어진 텍스트의 인코딩을 추측합니다. UTF-8과 EUC-KR이 아닌 경우 null을 반환합니다.
	 * @param text
	 * @return
	 */
	public static CharsetEnum guessCharset2(String text) {
		if(StringUtils.hasLength(text) == false) {
			return null;
		}
		
		CharsetEnum result = null;
		try {
			String utfLine = URLDecoder.decode(text, CharsetEnum.UTF_8.getKey());
			String euckrLine = URLDecoder.decode(text, CharsetEnum.EUC_KR.getKey());
			Pattern pattern = Pattern.compile("[^óⅡ↘↗øĸo☆〜β１※【】「」『』・／º＿－（）　~ ：:;\\.\\[\\]!@\\#$%<>^&*\\()\\-=+_\\’\\'\\?a-zA-Z0-9가-힇ㄱ-ㅎㅏ-ㅣぁ-ゔァ-ヴー々〆〤一-龥]");
			//System.out.println(utfLine);
			result = pattern.matcher(utfLine).find() ? (pattern.matcher(euckrLine).find() ? null : CharsetEnum.EUC_KR) : CharsetEnum.UTF_8;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
}
