package lifthrasir.toolkit.steinsgate.archive;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lifthrasir.toolkit.steinsgate.archive.model.TargetInfoVO;
import lifthrasir.toolkit.steinsgate.archive.thread.ArchiveThread;
import lifthrasir.toolkit.steinsgate.archive.utils.OSValidator;

@SpringBootApplication
public class ArchiveSteinsGateToolkitApplication extends JFrame {
	/** 2021y04m22d */
	private static final long serialVersionUID = 3785284568743352940L;
	//private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveSteinsGateToolkitApplication.class);
	public static List<TargetInfoVO> targetInfoList;
	
	private ArchiveThread thread = new ArchiveThread();
	private static SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
	private static JTextArea textArea = new JTextArea();
	private static JButton btnOpenBrowser = new JButton("브라우저 열기");
	private static JButton btnCompleteLogin = new JButton("로그인 완료");
	private static JButton btnStartBackup = new JButton("백업 시작");
	private static JButton btnStopBackup = new JButton("백업 중단");
	
	public static final String savePath = "./archivesteinsgate";
			//Thread.currentThread().getContextClassLoader().getResource(".").getPath() + "archivesteinsgate";
	public static final String baseUrl ="https://cafe.naver.com/stgate";
	public static WebDriver driver;
	
	@Autowired
	private RestTemplateBuilder restTemplateBuilder;
	
	public ArchiveSteinsGateToolkitApplication() {
		initUi();
		initEvent();
		loadTargetInfo();
	}
	
	public static void main(String[] args) {
		System.setProperty("file.encoding","UTF-8");
		try {
			Field charset = Charset.class.getDeclaredField("defaultCharset");
			charset.setAccessible(true);
			charset.set(null, null);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			append("인코딩 설정 실패: " + e.getMessage());
			//e.printStackTrace();
		}
		ConfigurableApplicationContext ctx = new SpringApplicationBuilder(ArchiveSteinsGateToolkitApplication.class)
				//.web(WebApplicationType.NONE)
				.headless(false) // 비윈도우 환경에서 GUI동작 코드를 사용합니까? false
				//.bannerMode(Banner.Mode.OFF)
				.run(args);
		EventQueue.invokeLater(() -> {
			ArchiveSteinsGateToolkitApplication ex = ctx.getBean(ArchiveSteinsGateToolkitApplication.class);
			ex.setVisible(true);
		});
	}
	
	/**
	 * GUI 생성
	 */
	private void initUi() {
		setSize(900, 300);
		setResizable(false);
		setLocationRelativeTo(null);
		setTitle("Archive Steins;Gate Toolkit");
		setFont(new Font("돋움체", Font.PLAIN, 12));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		Dimension panelMaxSize = new Dimension();
		panelMaxSize.setSize(200, 300);
		panel.setMaximumSize(panelMaxSize);
		panel.setBounds(12, 10, 205, 251);
		
		getContentPane().add(panel);
		getContentPane().setLayout(null);
		getContentPane().setFont(new Font("돋움체", Font.PLAIN, 12));
		
		btnCompleteLogin.setEnabled(false);
		btnStartBackup.setEnabled(false);
		btnStopBackup.setEnabled(false);
		
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		panel.add(btnOpenBrowser);
		panel.add(btnCompleteLogin);
		panel.add(btnStartBackup);
		panel.add(btnStopBackup);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(229, 10, 653, 251);
		getContentPane().add(scrollPane);
		
		textArea.setFont(new Font("돋움", Font.PLAIN, 12));
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		append("브라우저 열기 버튼을 누르세요");
		scrollPane.setViewportView(textArea);
	}
	
	/**
	 * 버튼 클릭 이벤트 생성
	 */
	private void initEvent() {
		btnOpenBrowser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnOpenBrowser.setEnabled(false);
				//"src/main/resources/driver/chrome/chromedriver_90.0.4430.24.exe"
				if (OSValidator.isWindows()) {
					append("Windows 환경으로 감지하였습니다. 'chromedriver.exe' 파일을 로딩 합니다.");
					System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
				} else if (OSValidator.isMac()) {
					append("Mac 환경으로 감지되었습니다. 'chromedriver' 파일을 로딩 합니다.");
					System.setProperty("webdriver.chrome.driver", "./chromedriver");
				} else {
					append("지원하지 않는 OS 입니다.");
					return;
				}
				
				try{
					driver = new ChromeDriver();
					driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
					driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
					driver.manage().window().setSize(new org.openqa.selenium.Dimension(1130, 800));
					driver.get("https://naver.com");
					append("현재 접속된 네이버 홈페이지에 카페에 가입한 계정으로 로그인 후, '로그인 완료' 버튼을 누르세요");
					append("창 포커스를 옮겨도 좋지만, 브라우저를 닫거나 창 크기를 조절하지 마세요. 이미지 캡처에 영향을 줍니다.");
					btnCompleteLogin.setEnabled(true);
				}catch(Exception e){
					appendln("드라이버 로딩 실패: " + e.getMessage());
				}
			}
		});
		
		btnCompleteLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				driver.get(baseUrl);
				append("이 툴킷이 위치한 드라이브에 최소 25GB 여유공간 준비 후, '백업 시작' 버튼을 누르세요.");
				btnCompleteLogin.setEnabled(false);
				btnStartBackup.setEnabled(true);
			}
		});
		
		btnStartBackup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				appendln("저장경로 스캔 후, 마지막 게시글 부터 이어 진행합니다. 처음부터 할 경우 예상 소요시간은 30시간25분 입니다.");
				btnStartBackup.setEnabled(false);
				btnStopBackup.setEnabled(true);
				thread.startThread(restTemplateBuilder);
			}
		});
		
		btnStopBackup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnStopBackup.setEnabled(false);
				btnStartBackup.setEnabled(true);
				thread.stopThread();
			}
		});
	}
	
	/**
	 * 백업 완료 시의 동작을 정의합니다.
	 */
	public static void archiveComplte() {
		btnStopBackup.setEnabled(false);
		btnStartBackup.setEnabled(false);
		appendln("백업을 완료 했습니다. 다음의 경로를 확인하세요.\r\n" + savePath.substring(1) + "\r\n\r\nEl Psy Congroo");
	}
	
	/**
	 * 백업 실패 시의 동작을 정의합니다.
	 */
	public static void archiveFail() {
		btnStopBackup.setEnabled(false);
		btnStartBackup.setEnabled(false);
		appendln("백업에 실패 했습니다. 기록을 복사해서 수정 요청 해주세요.");
	}
	
	/**
	 * TextArea를 맨아래로 스크롤 합니다.
	 */
	public static void scrollDown() {
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
	
	/**
	 * TextArea에 timestamp와 메시지를 출력합니다.
	 * @param text
	 * @param isBold
	 */
	public static void append(String text) {
		textArea.append("\r\n");
		textArea.append(sdf.format(new Date()));
		textArea.append(" ");
		textArea.append(text);
		scrollDown();
	}
	
	/**
	 * TextArea에 공백라인을 한 줄 넣은 후, timestamp와 메시지를 출력합니다.
	 * @param text
	 */
	public static void appendln(String text) {
		textArea.append("\r\n");
		append(text);
	}
	
	/**
	 * 백업 대상 게시글 정보를 불러들입니다.
	 */
	private void loadTargetInfo() {
		Reader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/match.json")));
		Gson gson = new Gson();
		targetInfoList = gson.fromJson(reader, new TypeToken<List<TargetInfoVO>>(){}.getType());
		
		Comparator<TargetInfoVO> c = new Comparator<TargetInfoVO>() {
			@Override
			public int compare(TargetInfoVO so1, TargetInfoVO so2) {
				Integer o1 = Integer.parseInt(so1.getPstNo());
				Integer o2 = Integer.parseInt(so2.getPstNo());
				
				if(o1>o2) {
					return 1;
				}else if(o1==o2) {
					return 0;
				}
				return -1;
			}
		};
		targetInfoList.sort(c);
	}
}
