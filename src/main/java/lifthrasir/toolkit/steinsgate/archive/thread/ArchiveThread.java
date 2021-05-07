package lifthrasir.toolkit.steinsgate.archive.thread;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;

import lifthrasir.toolkit.steinsgate.archive.ArchiveSteinsGateToolkitApplication;
import lifthrasir.toolkit.steinsgate.archive.model.CafePostVO;
import lifthrasir.toolkit.steinsgate.archive.model.MultimediaVO;
import lifthrasir.toolkit.steinsgate.archive.model.TargetInfoVO;
import lifthrasir.toolkit.steinsgate.archive.utils.DocumentParser;
import lifthrasir.toolkit.steinsgate.archive.utils.FileRenamePolicy;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

public class ArchiveThread implements Runnable {
	
	/** 작업을 수행할 thread */
	private Thread thread;
	private boolean isShutdown = false;
	private ContentInfoUtil util = new ContentInfoUtil();
	private RestTemplateBuilder restTemplateBuilder;

	/** 작업을 수행한다 */
	public void startThread(RestTemplateBuilder restTemplateBuilder) {
		if (thread == null) {
			thread = new Thread(this, "backup thread");
		}
		if (!thread.isAlive()) {
			this.isShutdown = false;
			thread.start();
		}
		this.restTemplateBuilder = restTemplateBuilder;
	}

	/** 컨텍스트 종료 시 thread를 종료시킨다 */
	public void stopThread() {
		this.isShutdown = true;
		try {
			thread.join();
			thread = null;
			append("백업 중단 완료");
		} catch (InterruptedException ie) {
			append("백업 중단 실패(InterruptedException): " + ie.getMessage());
			//ie.printStackTrace();
		}
	}
	
	/** 스레드가 실제로 작업하는 부분 */
	public void run() {
		Thread currentThread = Thread.currentThread();
		List<TargetInfoVO> list = ArchiveSteinsGateToolkitApplication.targetInfoList;
		if(list == null) {
			ArchiveSteinsGateToolkitApplication.archiveFail();
			return;
		}
		
		String lastPostNo = searchLastPostNo();
		
		for(int i=0; i<list.size() && currentThread == thread && this.isShutdown == false; i++) {
			TargetInfoVO vo = list.get(i);
			if(	StringUtils.hasLength(lastPostNo)
				&& ( Integer.parseInt(lastPostNo) > Integer.parseInt(vo.getPstNo()) )
			) {
				continue;
			}
			
			appendln(new StringBuilder()
					.append(i+1)
					.append("/")
					.append(list.size())
					.append(" (")
					.append(Math.round((i+1.0) / list.size() * 100 * 100) / 100.0)
					.append("%")
					.append("), 게시글 번호 ")
					.append(vo.getPstNo())
					.append(" 진입")
					.toString()
			);
			save(vo);
		}
		
		if(this.isShutdown == false) {
			ArchiveSteinsGateToolkitApplication.archiveComplte();
		}
	}
	
	/**
	 * JTextArea에 timestamp와 함께 문자열을 추가합니다.
	 * @param text
	 */
	private void append(String text) {
		ArchiveSteinsGateToolkitApplication.append(text);
	}
	
	/**
	 * JTextArea에 공백라인을 한 줄 넣은 후, timestamp와 함께 문자열을 추가합니다.
	 * @param text
	 */
	private void appendln(String text) {
		ArchiveSteinsGateToolkitApplication.appendln(text);
	}
	
	/**
	 * savePath의 모든 html파일을 읽어들인 후, 가장 값이 큰 파일명을 반환합니다.
	 * 이는 멀티미디어 파일 중에 .html 확장자를 가지는게 없기에 가능한 방식입니다.
	 * @return
	 */
	private String searchLastPostNo() {
		List<File> fileList = null;
		try {
			fileList = htmlFileNameSearch( ArchiveSteinsGateToolkitApplication.savePath );
			if(fileList == null || fileList.size() == 0) {
				append("지난 백업 파일이 조회되지 않았습니다. 신규 백업을 시작합니다.");
				return "";
			}
			
			Comparator<File> c = new Comparator<File>() {
				@Override
				public int compare(File os1, File os2) {
					Integer o1 = Integer.parseInt( os1.getName().substring(os1.getName().lastIndexOf(File.separator) + 1, os1.getName().lastIndexOf(".html")) );
					Integer o2 = Integer.parseInt( os2.getName().substring(os2.getName().lastIndexOf(File.separator) + 1, os2.getName().lastIndexOf(".html")) );
					if(o1>o2) {
						return 1;
					}else if(o1==o2) {
						return 0;
					}
					return -1;
				}
			};
			fileList.sort(c);
		} catch (IOException ioe) {
			append("지난 백업 파일 조회 실패(IOExcetion): " + ioe.getMessage());
			//ioe.printStackTrace();
		} catch(Exception e) {
			append("지난 백업 파일 조회 실패(Exception): " + e.getMessage());
			//e.printStackTrace();
		}
		
		File deleteTargetFile = fileList.get(fileList.size()-1);
		File parentPath = deleteTargetFile.getParentFile();
		try {
			FileUtils.deleteDirectory(parentPath);
		} catch (IOException e) {
			append("지난 백업 파일 제거 실패(IOException): " + e.getMessage());
			//e.printStackTrace();
		}
		
		return (fileList==null || fileList.size()==0) ? "" : fileList.get(fileList.size()-1).getName().substring(0,  fileList.get(fileList.size()-1).getName().lastIndexOf(".html"));
	}
	
	/**
	 * 대상 파일 경로 하위에 있는 모든 .html 확장자 파일을 반환합니다.
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<File> htmlFileNameSearch(String path) throws IOException {
		
		File dir = new File(path);
		File[] fileList = dir.listFiles();
		List<File> list = new ArrayList<File>();
		
		if(fileList == null || fileList.length == 0) {
			return list;
		}
		
		for(int i=0; i<fileList.length; i++) {
			File file = fileList[i];
			
			if(file.isFile()) {
				String fileName = file.getName();
				int extIndex = fileName.lastIndexOf(".html");
				if(StringUtils.hasLength(fileName) && extIndex > 0) {
					list.add(file);
				}
			}else if(file.isDirectory()) {
				list.addAll( htmlFileNameSearch(file.getCanonicalPath().toString()) );
			}
		}
		
		return list;
	}
	
	/**
	 * URL에 접속해 HTML 파일을 저장합니다.
	 * @param targetInfoVO
	 */
	private void save(TargetInfoVO targetInfoVO) {
		WebDriver driver = ArchiveSteinsGateToolkitApplication.driver;
		final String baseUrl = ArchiveSteinsGateToolkitApplication.baseUrl;
		final String savePath = ArchiveSteinsGateToolkitApplication.savePath;
		final String fileNamePattern = "[^óⅡ↘↗øĸo☆〜β１※【】「」『』・／º＿－（）　~ ;\\[\\]!@\\#$%^&\\()\\-=+_\\’\\'a-zA-Z0-9가-힇ㄱ-ㅎㅏ-ㅣぁ-ゔァ-ヴー々〆〤一-龥]";
		
		driver.get(baseUrl + "/" + targetInfoVO.getPstNo());		
		try {Thread.sleep(4 * 1000);} catch (InterruptedException e) {
			append("페이지 로딩 대기 중 오류(InterruptedException): " + e.getMessage());
			//e.printStackTrace();
		} // 페이지 로딩 대기
		try {
			driver.switchTo().defaultContent(); // 상위 프레임으로 전환
			driver.switchTo().frame("cafe_main"); // cafe_main 프레임으로 전환
			
			String html = driver.getPageSource();
			html = html.replaceFirst("<meta name=\\\"robots\\\" content=\\\"noindex, nofollow\\\">", "<meta charset=\\\"UTF-8\\\">");
			
			Document doc = Jsoup.parse(html, "UTF-8");
			
			Elements articleTitleList = doc.select(".ArticleTitle > a.link_board");
			if(articleTitleList.isEmpty() == true) {
				append(targetInfoVO.getPstNo() + " 게시글 백업 실패(삭제 또는 권한 없음)");
				return;
			}
			
			CafePostVO cafePostVO = DocumentParser.parseDoc(doc);
			
			// 파일저장: HTML
			// 전체 경로
			StringBuilder htmlPathBuilder = new StringBuilder(savePath)
					.append(File.separator).append(targetInfoVO.getMnGrpNm().replaceAll(fileNamePattern, "").replaceAll(" ", "_"))
    				.append(File.separator).append(targetInfoVO.getMnNm().replaceAll(fileNamePattern, "").replaceAll(" ", "_"))
					.append(File.separator).append(targetInfoVO.getPstNo()).append("_").append(
							cafePostVO.getPostVO().getPostTitle()
							.replaceAll(fileNamePattern, "")
							.replaceAll(" ", "_")
					).append(File.separator);
			File htmlFile = new File(htmlPathBuilder.toString() + targetInfoVO.getPstNo() + ".html");
			File folder = new File(htmlFile.getParent());
			if(folder.exists() == false) {
				folder.mkdirs();
			}
			htmlFile.createNewFile();
			try(BufferedWriter htmlWriter = new BufferedWriter(new FileWriter(htmlFile));){
				htmlWriter.write(html);
			} catch (Exception e) {
				append("HTML 저장 실패(Exception): " + e.getMessage());
				//e.printStackTrace();
			}
			
			// 파일저장: 브라우저 캡처
			driver.switchTo().defaultContent();
			Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1*000)).takeScreenshot(driver);
			ImageIO.write(screenshot.getImage(), "PNG", new File(htmlPathBuilder.toString() + targetInfoVO.getPstNo() + ".png"));
			driver.switchTo().frame("cafe_main");
			
			// 파일저장: PDF
			generatePdfFromHtml(htmlPathBuilder.toString() + targetInfoVO.getPstNo() + ".pdf");
			
			// 파일저장: 본문 이미지, 첨부파일
			imageDownload(cafePostVO.getMultimediaVOList(), htmlPathBuilder.toString(), targetInfoVO.getPstNo());
			
			append("완료: " + htmlPathBuilder.toString().substring(1));
		}catch (org.openqa.selenium.UnhandledAlertException uae) {
			append("백업 중 오류 발생(UnhandledAlertException): " + uae.getAlertText());
			//uae.printStackTrace();
		}catch (Exception e) {
			append("백업 중 오류 발생(Exception): " + e.getMessage());
			//e.printStackTrace();
		}
	}
	
	/**
	 * 저장된 html을 pdf파일로 변환후 동일한 경로에 저장합니다. 
	 * @param htmlFileNamePath 파일명과 확장자를 포함한 html 파일의 경로
	 */
	private void generatePdfFromHtml(String htmlFileNamePath) {
		// TODO wkhtmltopdf 코드 추가 할 것
	}
	
	/**
	 * 주어진 멀티미디어 목록을 저장합니다.
	 * @param list
	 * @param saveFolder
	 * @param postNo
	 */
	private void imageDownload(final List<MultimediaVO> orgList, final String saveFolder, final String postNo) {
		// 멀티미디어 목록의 중복 제거 처리
		Map<String, MultimediaVO> map = new HashMap<String, MultimediaVO>();
		for(MultimediaVO vo : orgList) {
			map.put(vo.getMultimediaTypeFk() + vo.getMultimediaFileName() + vo.getMultimediaUrl(), vo);
		}
		List<MultimediaVO> list = new ArrayList<MultimediaVO>(map.values());
		
		// 다운로드
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
		HttpEntity<String> entity = new HttpEntity<>(headers);
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		HttpClient httpClient = HttpClientBuilder.create()
				.setRedirectStrategy(new LaxRedirectStrategy())
				.build();
		factory.setHttpClient(httpClient);
		RestTemplate restTemplate = restTemplateBuilder.build();
		restTemplate.setRequestFactory(factory);
		
		for(MultimediaVO vo : list) {
			// 윈도우 경로+파일명 길이 제한 255
			String fileName = vo.getMultimediaFileName();
			fileName = fileName.trim().replaceAll(" ", "_").replaceAll("\\?", "");
			int filePathLength = saveFolder.length();
			if(fileName.length()>250-filePathLength) {
				fileName = fileName.substring(
						fileName.length()-(250-filePathLength)
				);
			}
			
			try {
				ResponseEntity<byte[]> response = restTemplate
						.exchange(vo.getMultimediaUrl(), HttpMethod.GET, entity, byte[].class);
				
				if(response == null || response.getBody() == null) {
					append(postNo + " 게시글 " + vo.getMultimediaTypeFk() + " 다운로드 실패(response or response body is null): " + vo.getMultimediaUrl());
					continue;
				}
				
				// 파일명 파싱
				int extIndex = fileName.lastIndexOf(".");
				String ext = fileName.substring(extIndex>0 ? extIndex+1 : 0);
				if(ext.length() != fileName.length()) {
					// 확장자 있는 경우 그대로 사용
					Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
					Matcher m = pattern.matcher(ext);
					ext = m.find() ? ext.substring(0, m.start()) : ext;
					fileName = fileName.substring(0, extIndex); // dot 제외
				}else {
					// 확장자 없는 경우 mime 분석
					ext = "";
					ContentInfo info = util.findMatch(response.getBody());
					if(info == null) {
						append(postNo + " 게시글 " + vo.getMultimediaTypeFk() + " 확장자 확인 실패(findMatch null): " + vo.getMultimediaFileName());
					}else {
						String[] extensions = info.getFileExtensions();
						if(extensions != null && extensions.length > 0) {
							append(postNo + " 게시글 " + vo.getMultimediaTypeFk() + " 확장자 확인 실패(getFileExtensions null): " + vo.getMultimediaFileName());
						}else{
							ext = extensions[0];
						}
					}
				}
				
				// 유형에 따라 폴더 구분하여 저장
				StringBuilder fileNamePath = new StringBuilder(saveFolder);
				switch(vo.getMultimediaTypeFk()) {
					case "PROFILE_IMAGE":
						fileNamePath.append("프로필"); break;
					case "POST_BODY_IMAGE": case "POST_BODY_MOVIE":
						fileNamePath.append("본문"); break;
					case "COMMENT_IMAGE":
						fileNamePath.append("댓글"); break;
					case "POST_ATTACHMENT":
						fileNamePath.append("첨부"); break;
					default:
						fileNamePath.append("etc"); break;
				}
				fileNamePath.append(File.separator).append(fileName).append(".").append(ext);
				
				// 동일 파일명 있으면 (1)...(2)... 식으로 없을 때까지 증가
				File file = new FileRenamePolicy().rename(new File(fileNamePath.toString()));
				String newPath = file.getPath();
				Path path = Paths.get(newPath);
				byte[] body = response.getBody();
				Files.write(path, body);
				vo.setMultimediaDirectory(file.getPath());
			} catch (HttpClientErrorException.BadRequest hceeB) {
				append(postNo + " 게시글 " + vo.getMultimediaTypeFk() + " 다운로드 실패(BadRequest): " + vo.getMultimediaUrl());
				//hceeB.printStackTrace();
			} catch (HttpClientErrorException.Forbidden hceeF) {
				append(postNo + " 게시글 " + vo.getMultimediaTypeFk() + " 다운로드 실패(Forbidden): " + vo.getMultimediaUrl());
				//hceeF.printStackTrace();
			} catch (HttpClientErrorException.NotFound hceeNf) {
				append(postNo + " 게시글 " + vo.getMultimediaTypeFk() + " 다운로드 실패(NotFound): " + vo.getMultimediaUrl());
				//hceeNf.printStackTrace();
			} catch (ResourceAccessException rae) {
				append(postNo + " 게시글 " + vo.getMultimediaTypeFk() + " 다운로드 실패(사이트에 연결할 수 없음): " + vo.getMultimediaUrl());
				//rae.printStackTrace();
			} catch (IOException ioe) {
				append(postNo + " 게시글 " + vo.getMultimediaTypeFk() + " 다운로드 실패(IOException): " + vo.getMultimediaFileName());
				//ioe.printStackTrace();
			}
		} // end of for()
	}
}
