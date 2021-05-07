package lifthrasir.toolkit.steinsgate.archive.utils;

import java.io.File;
import java.io.IOException;

/**
 * 출처: https://javaexpert.tistory.com/58
 * 사용법 : File file = new FileRenamePolicy().rename(new File(원하는 파일명));
 */
public class FileRenamePolicy {
	public File rename(File f) { // File f는 원본 파일
		if (createNewFile(f)) {
			return f; // 생성된 f가
		}
		
		// 확장자가 없는 파일 일때 처리
		String name = f.getName();
		String body = null;
		String ext = null;

		int dot = name.lastIndexOf(".");
		if (dot != -1) { // 확장자가 있을때
			body = name.substring(0, dot);
			ext = name.substring(dot);
		} else { // 확장자가 없을때
			body = name;
			ext = "";
		}

		int count = 0;
		// 중복된 파일이 있을때
		do {
			count++;
			String newName = body + "(" + count + ")" + ext;
			f = new File(f.getParent(), newName);
		} while (!createNewFile(f) && count < 9999);
		return f;
	}

	private boolean createNewFile(File f) {
		File folder = new File(f.getParent());
		if(!folder.exists()) {
			folder.mkdirs();
		}
		try {
			return f.createNewFile(); // 존재하는 파일이 아니면
		} catch (IOException ignored) {
			return false;
		}
	}
}
