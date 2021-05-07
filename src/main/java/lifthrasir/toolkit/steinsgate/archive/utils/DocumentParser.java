package lifthrasir.toolkit.steinsgate.archive.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import lifthrasir.toolkit.steinsgate.archive.enums.CharsetEnum;
import lifthrasir.toolkit.steinsgate.archive.enums.MultimediaTypeEnum;
import lifthrasir.toolkit.steinsgate.archive.model.CafePostVO;
import lifthrasir.toolkit.steinsgate.archive.model.CommentVO;
import lifthrasir.toolkit.steinsgate.archive.model.GradeVO;
import lifthrasir.toolkit.steinsgate.archive.model.MemberVO;
import lifthrasir.toolkit.steinsgate.archive.model.MenuVO;
import lifthrasir.toolkit.steinsgate.archive.model.MultimediaVO;
import lifthrasir.toolkit.steinsgate.archive.model.PostMemberVO;
import lifthrasir.toolkit.steinsgate.archive.model.PostMultimediaVO;
import lifthrasir.toolkit.steinsgate.archive.model.PostTagVO;
import lifthrasir.toolkit.steinsgate.archive.model.PostVO;
import lifthrasir.toolkit.steinsgate.archive.model.TagVO;

public class DocumentParser {
	/**
	 * 도큐먼트를 파싱해 CafePostVO 형태로 반환합니다.
	 * @param doc
	 * @return
	 */
	public static CafePostVO parseDoc(Document doc) {
		CafePostVO cafePostVO = new CafePostVO();
		Elements postHeader = doc.select(".post_header");
		if(postHeader.isEmpty() == false) {
		// 카페 book 게시글
			MenuVO menuVO = parseBookMenu(doc);
			PostVO postVO = parseBookPost(doc);
			List<TagVO> tagVOList = parseBookTag(doc);
			List<PostTagVO> postTagVOList = getPostTagVOList(postVO.getPostNo(), tagVOList);
			//GradeVO gradeVO = parseGrade(doc);
			List<MemberVO> memberVOList = parseBookMember(doc);
			List<PostMemberVO> postMemberVOList = parseBookPostMember(postVO.getPostNo(), doc);
			List<CommentVO> commentVOList = parseBookComment(doc);
			List<MultimediaVO> multimediaVOList = getMultimediaVOList(
					memberVOList, commentVOList, postVO.getPostBody(), parseBookAttachFile(doc)
			);
			List<PostMultimediaVO> postMultimediaVOList = getPostMultimedialVOList(postVO.getPostNo(), multimediaVOList);
			
			cafePostVO.setMenuVO(menuVO);
			cafePostVO.setPostVO(postVO);
			cafePostVO.setTagVOList(tagVOList);
			cafePostVO.setPostTagVOList(postTagVOList);
			//cafePostVO.setGradeVO(gradeVO);
			cafePostVO.setMemberVOList(memberVOList);
			cafePostVO.setPostMemberVOList(postMemberVOList);
			cafePostVO.setCommentVOList(commentVOList);
			cafePostVO.setMultimediaVOList(multimediaVOList);
			cafePostVO.setPostMultimediaVOList(postMultimediaVOList);
		}else {
		// 일반적인 카페 게시글
			MenuVO menuVO = parseMenu(doc);
			PostVO postVO = parsePost(doc);
			List<TagVO> tagVOList = parseTag(doc);
			List<PostTagVO> postTagVOList = getPostTagVOList(postVO.getPostNo(), tagVOList);
			GradeVO gradeVO = parseGrade(doc);
			List<MemberVO> memberVOList = parseMember(doc);
			List<PostMemberVO> postMemberVOList = parsePostMember(postVO.getPostNo(), doc);
			List<CommentVO> commentVOList = parseComment(doc);
			List<MultimediaVO> multimediaVOList = getMultimediaVOList(
					memberVOList, commentVOList, postVO.getPostBody(), parseAttachFile(doc)
			);
			List<PostMultimediaVO> postMultimediaVOList = getPostMultimedialVOList(postVO.getPostNo(), multimediaVOList);
			
			cafePostVO.setMenuVO(menuVO);
			cafePostVO.setPostVO(postVO);
			cafePostVO.setTagVOList(tagVOList);
			cafePostVO.setPostTagVOList(postTagVOList);
			cafePostVO.setGradeVO(gradeVO);
			cafePostVO.setMemberVOList(memberVOList);
			cafePostVO.setPostMemberVOList(postMemberVOList);
			cafePostVO.setCommentVOList(commentVOList);
			cafePostVO.setMultimediaVOList(multimediaVOList);
			cafePostVO.setPostMultimediaVOList(postMultimediaVOList);
		}
		//LOGGER.info("processor 결과: {}", cafePostVO);
		return cafePostVO;
	}
	
	/**
	 * 게시글-멀티미디어 파싱
	 * @param postNo
	 * @param postMultimediaVOList
	 * @return
	 */
	public static List<PostMultimediaVO> getPostMultimedialVOList(
			Integer postNo, List<MultimediaVO> postMultimediaVOList
	){
		List<PostMultimediaVO> list = new ArrayList<PostMultimediaVO>();
		for(MultimediaVO e : postMultimediaVOList) {
			PostMultimediaVO vo = new PostMultimediaVO();
			vo.setPostNo(postNo);
			vo.setMultimediaUrl(e.getMultimediaUrl());
			list.add(vo);
		}
		return list;
	}
	
	/**
	 * 멀티미디어 파싱
	 * @param memberVOList
	 * @param commentVOList
	 * @param postBody
	 * @param attachFileMap
	 * @return
	 */
	public static List<MultimediaVO> getMultimediaVOList(
			List<MemberVO> memberVOList,
			List<CommentVO> commentVOList,
			String postBody,
			Map<String, String> attachFileMap
	){
		List<MultimediaVO> multimediaVOList = new ArrayList<MultimediaVO>();
		
		// 게시글,댓글 작성자의 프로필 사진
		for(MemberVO memberVO : memberVOList) {
			String profileImageUrl = memberVO.getProfileImageUrl();
			if(StringUtils.hasLength(profileImageUrl) == false) {
				continue;
			}
			
			int qIndex = profileImageUrl.lastIndexOf("?");
			String encodedEucKrFileName = profileImageUrl.substring(
					profileImageUrl.lastIndexOf("/")+1,
					qIndex>0 ? qIndex : profileImageUrl.length()
			);
			String multimediaFileName = null;
			try {
				CharsetEnum e1 = StringUtil.guessCharset2(encodedEucKrFileName);
				multimediaFileName = e1==null ? encodedEucKrFileName : URLDecoder.decode(encodedEucKrFileName, e1.toString());
				if(multimediaFileName.contains("%")) {
					CharsetEnum e2 = StringUtil.guessCharset2(encodedEucKrFileName);
					if(e2!=null) {
						multimediaFileName = URLDecoder.decode(multimediaFileName, e2.toString());	
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			MultimediaVO multimediaVO = new MultimediaVO();
			multimediaVO.setMultimediaUrl(profileImageUrl);
			multimediaVO.setMultimediaTypeFk(MultimediaTypeEnum.PROFILE_IMAGE.getKey());
			multimediaVO.setMultimediaFileName(multimediaFileName);
			
			multimediaVOList.add(multimediaVO);
		}
		
		// 댓글 본문 사진
		for(CommentVO commentVO : commentVOList) {
			String commentBodyImageUrl = commentVO.getMultimediaUrlFk();
			if(StringUtils.hasLength(commentBodyImageUrl) == false) {
				continue;
			}
			
			String encodedEucKrFileName = commentBodyImageUrl.substring(
					commentBodyImageUrl.lastIndexOf("/")+1,
					commentBodyImageUrl.lastIndexOf("?")
			);
			String multimediaFileName = null;
			try {
				CharsetEnum e1 = StringUtil.guessCharset2(encodedEucKrFileName);
				multimediaFileName = e1==null ? encodedEucKrFileName : URLDecoder.decode(encodedEucKrFileName, e1.toString());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			MultimediaVO multimediaVO = new MultimediaVO();
			multimediaVO.setMultimediaUrl(commentBodyImageUrl);
			multimediaVO.setMultimediaTypeFk(MultimediaTypeEnum.COMMENT_IMAGE.getKey());
			multimediaVO.setMultimediaFileName(multimediaFileName);
			
			multimediaVOList.add(multimediaVO);
		}
		
		// 게시글 본문 사진
		if(StringUtils.hasText(postBody)) {
			Pattern pattern = Pattern.compile("<img[^>]*src=[\"']?([^>\"']+)[\"']?[^>]*>");
			Matcher matcher = pattern.matcher(postBody);
			while(matcher.find()) {
				String postBodyImageUrl = matcher.group(1);
				if(StringUtils.hasLength(postBodyImageUrl) == false) {
					continue;
				}
				//LOGGER.info("이미지: {}", postBodyImageUrl);
				// img src안에 원본을 가르키는 src파라미터가 또 있는 경우 사용
				if(postBodyImageUrl.startsWith("%22")) {
					postBodyImageUrl = postBodyImageUrl.substring(3, postBodyImageUrl.lastIndexOf("%22") );
					//LOGGER.info("변경: {}", postBodyImageUrl);
					try {
						postBodyImageUrl = URLDecoder.decode(postBodyImageUrl, "EUC-KR");
						postBodyImageUrl = postBodyImageUrl.substring( postBodyImageUrl.lastIndexOf("http") );
						if(postBodyImageUrl.startsWith("http%3A") || postBodyImageUrl.startsWith("https%3A")) {
							postBodyImageUrl = URLDecoder.decode(postBodyImageUrl, "EUC-KR");
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					//LOGGER.info("디코드: {}", postBodyImageUrl);
				}
				
				int qIndex = postBodyImageUrl.lastIndexOf("?");
				String encodedEucKrFileName = postBodyImageUrl.substring(
						postBodyImageUrl.lastIndexOf("/")+1,
						qIndex>0 ? qIndex : postBodyImageUrl.length()
				);
				String multimediaFileName = null;
				try {
					CharsetEnum e1 = StringUtil.guessCharset2(encodedEucKrFileName);
					multimediaFileName = e1==null ? encodedEucKrFileName : URLDecoder.decode(encodedEucKrFileName, e1.toString());
					if(multimediaFileName.contains("http")) {
						CharsetEnum e2 = StringUtil.guessCharset2(postBodyImageUrl);
						if(e2!=null) {
							postBodyImageUrl = URLDecoder.decode(postBodyImageUrl, e2.toString());
						}
						qIndex = postBodyImageUrl.lastIndexOf("?");
						encodedEucKrFileName = postBodyImageUrl.substring(
								postBodyImageUrl.lastIndexOf("/")+1,
								qIndex>0 ? qIndex : postBodyImageUrl.length()
						);
						CharsetEnum e3 = StringUtil.guessCharset2(encodedEucKrFileName);
						multimediaFileName = e3==null ? encodedEucKrFileName : URLDecoder.decode(encodedEucKrFileName, e3.toString());
					}
					if(multimediaFileName.contains("%")) {
						CharsetEnum e4 = StringUtil.guessCharset2(multimediaFileName);
						multimediaFileName = e4==null ? multimediaFileName : URLDecoder.decode(multimediaFileName, e4.toString());
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				// 너무 긴 url은 skip. 동영상 등에서 암호화된 이미지 링크일 수 있음
				if(StringUtils.hasLength(postBodyImageUrl) && postBodyImageUrl.length()>1000) {
					continue;
				}
				
				// http 라는 단어가 없는 URL은 스킵
				if(StringUtils.hasLength(postBodyImageUrl) && !postBodyImageUrl.contains("http")) {
					continue;
				}
				
				MultimediaVO multimediaVO = new MultimediaVO();
				multimediaVO.setMultimediaUrl(postBodyImageUrl);
				multimediaVO.setMultimediaTypeFk(MultimediaTypeEnum.POST_BODY_IMAGE.getKey());
				multimediaVO.setMultimediaFileName(multimediaFileName);
				
				multimediaVOList.add(multimediaVO);
			}
		}
		
		// 게시글 첨부파일
		if(attachFileMap != null) {
			attachFileMap.forEach(new BiConsumer<String, String>() {
				@Override
				public void accept(String key, String value) {
					MultimediaVO multimediaVO = new MultimediaVO();
					multimediaVO.setMultimediaUrl(value);
					multimediaVO.setMultimediaTypeFk(MultimediaTypeEnum.POST_ATTACHMENT.getKey());
					multimediaVO.setMultimediaFileName(key);
					
					multimediaVOList.add(multimediaVO);
				}
			});
		}
		return multimediaVOList;
	}
	
	/**
	 * book 첨부파일 파싱
	 * @param doc
	 * @return
	 */
	public static Map<String, String> parseBookAttachFile(Document doc){
		Elements attachFileListItem = doc.select("#attachLayer > ul > li");
		if(attachFileListItem.isEmpty()) {
			return null;
		}
		
		Map<String, String> map = new HashMap<String, String>();
		for(Element attachFile : attachFileListItem) {
			String fileName = attachFile.select("span.file_name").get(0).text();
			if(StringUtils.hasLength(fileName)) {
				fileName = fileName.trim();
			}
			
			String fileDownloadUrl = attachFile.select("#attahc > a:first-child").get(0).attr("href");
			if(StringUtils.hasLength(fileDownloadUrl)) {
				fileDownloadUrl = fileDownloadUrl.trim();
			}
			map.put(fileName, fileDownloadUrl);
		}
		return map;
	}
	
	/**
	 * 첨부파일 파싱
	 * @param doc
	 * @return
	 */
	public static Map<String, String> parseAttachFile(Document doc){
		Elements attachFileListItem = doc.select(".AttachFileListItem");
		if(attachFileListItem.isEmpty()) {
			return null;
		}
		
		Map<String, String> map = new HashMap<String, String>();
		for(Element attachFile : attachFileListItem) {
			String fileName = attachFile.select(".file_name .text").get(0).text();
			if(StringUtils.hasLength(fileName)) {
				fileName = fileName.trim();
			}
			
			String fileDownloadUrl = attachFile.select(".file_download a").get(0).attr("href");
			if(StringUtils.hasLength(fileDownloadUrl)) {
				fileDownloadUrl = fileDownloadUrl.trim();
			}
			map.put(fileName, fileDownloadUrl);
		}
		return map;
	}
	
	/**
	 * book 댓글 파싱
	 * @param doc
	 * @return
	 */
	public static List<CommentVO> parseBookComment(Document doc){
		List<CommentVO> commentVOList = new ArrayList<CommentVO>();
		Elements commentList = doc.select("#cmt_list li:not(.board-box-line-dashed)");
		
		// 댓글을 막았거나 없는 경우
		if(commentList.isEmpty()) {
			return commentVOList;
		}
		
		String noneReplyCommentNo = null;
		for(int i=0; i<commentList.size(); i++) {
			Element comment = commentList.get(i);
			
			CommentVO commentVO = new CommentVO();
			
			// 댓글 번호
			String commentNo = comment.select(".comm_cont input[name=\"cmtid\"]").get(0).val();
			
			// 댓글 부모 번호 외래키
			if(comment.hasClass("reply") == false) { // 대댓이 아닌 경우
				noneReplyCommentNo = commentNo;
				commentVO.setCommentReplyYn("N");
			}else if(StringUtils.hasLength(noneReplyCommentNo)){ // 대댓인 경우, 부모댓글아이디 입력
				 commentVO.setCommentParentNoFk(Integer.parseInt(noneReplyCommentNo));
				 commentVO.setCommentReplyYn("Y");
			}
			
			if(StringUtils.hasText(commentNo)) {
				commentVO.setCommentNo(Integer.parseInt(commentNo));
				
				// 댓글 작성 아이디
				String commentRegisterId = comment.select(".comm_cont input[name=\"writerid\"]").get(0).val();
				commentVO.setCommentRegisterId(commentRegisterId);
				
				// 댓글 작성 닉네임
				Elements commentNicknameElements = comment.select("._nickUI");
				if(commentNicknameElements.isEmpty() == false) {
					String commentRegisterNickname = commentNicknameElements.get(0).text();
					if(StringUtils.hasLength(commentRegisterNickname)) {
						commentRegisterNickname = commentRegisterNickname.trim();
					}else {
						commentRegisterNickname = null;
					}
					commentVO.setCommentRegisterNickname(commentRegisterNickname);
				}
				
				// 댓글 본문
				String commentBody = comment.select(".comm_body").get(0).html();
				commentVO.setCommentBody(commentBody);
				
				// 댓글 본문 이미지 URL
				Elements image = comment.select(".comm_cont input[name=\"imageOriginUrl\"]");
				Elements sticker = comment.select(".comm_stck .comm_body img");
				String multimediaUrlFk = null;
				if(image.isEmpty() == false) {
					multimediaUrlFk = image.get(0).val();
				}else if(sticker.isEmpty() == false) {
					multimediaUrlFk = sticker.get(0).attr("src");
				}
				commentVO.setMultimediaUrlFk(multimediaUrlFk);
				
				// 댓글 마지막 수정 날짜
				String commentLastModifyDateText = comment.select(".comm_cont > .h > span.date").get(0).text();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd. HH:mm");
				Date commentLastModifyDate = null;
				if(StringUtils.hasLength(commentLastModifyDateText)) {
					commentLastModifyDateText = commentLastModifyDateText.trim();
					try {
						commentLastModifyDate = sdf.parse(commentLastModifyDateText);
					} catch (java.text.ParseException e) {
						e.printStackTrace();
					}
				}
				commentVO.setCommentLastModifyDate(commentLastModifyDate);
			}
			
			// 게시글 번호 외래키
			Element spiButton = doc.getElementById("spiButton");
			Map<String, String> dataset = spiButton.dataset();
			String postOriginalUrl = dataset.get("url");
			if(StringUtils.hasLength(postOriginalUrl)) {
				postOriginalUrl = postOriginalUrl.trim();
			}
			String postNoFk = postOriginalUrl.substring( postOriginalUrl.lastIndexOf("/")+1 );
			commentVO.setPostNoFk(Integer.parseInt(postNoFk));
			
			// 댓글 정렬순서
			Integer commentOrder = i;
			commentVO.setCommentOrder(commentOrder);
			
			commentVOList.add(commentVO);
		}
		return commentVOList;
	}
	
	/**
	 * 댓글 파싱
	 * @param doc
	 * @return
	 */
	public static List<CommentVO> parseComment(Document doc){
		List<CommentVO> commentVOList = new ArrayList<CommentVO>();
		Elements commentList = doc.select(".comment_list > li");
		
		// 댓글을 막았거나 없는 경우
		if(commentList.isEmpty()) {
			return commentVOList;
		}
		
		String noneReplyCommentNo = null;
		for(int i=0; i<commentList.size(); i++) {
			Element comment = commentList.get(i);
			
			CommentVO commentVO = new CommentVO();
			
			// 댓글 번호
			String commentNo = comment.attr("id");
			
			// 댓글 부모 번호 외래키
			if(comment.hasClass("CommentItem--reply") == false) { // 대댓이 아닌 경우
				noneReplyCommentNo = commentNo;
				commentVO.setCommentReplyYn("N");
			}else if(StringUtils.hasLength(noneReplyCommentNo)){ // 대댓인 경우, 부모댓글아이디 입력
				 commentVO.setCommentParentNoFk(Integer.parseInt(noneReplyCommentNo));
				 commentVO.setCommentReplyYn("Y");
			}
			
			if(StringUtils.hasText(commentNo)) {
				commentVO.setCommentNo(Integer.parseInt(commentNo));
				
				// 댓글 작성 아이디
				String profileUrl = comment.select("a.comment_thumb").get(0).attr("href");
				Map<String, String> params = StringUtil.queryToMap(
						profileUrl.substring( profileUrl.lastIndexOf("?")+1 )
				);
				String commentRegisterId = params.get("memberid");
				commentVO.setCommentRegisterId(commentRegisterId);
				
				// 댓글 작성 닉네임
				Elements commentNicknameElements = comment.select(".comment_nickname");
				if(commentNicknameElements.isEmpty() == false) {
					String commentRegisterNickname = commentNicknameElements.get(0).text().trim();
					commentVO.setCommentRegisterNickname(commentRegisterNickname);
				} 
				
				// 댓글 본문
				String commentBody = comment.select(".comment_text_view").get(0).html();
				commentVO.setCommentBody(commentBody);
				
				// 댓글 본문 이미지 URL
				Elements image = comment.select(".CommentItemImage a.comment_image_link img.image");
				Elements sticker = comment.select(".CommentItemSticker a.comment_sticker_link img.image");
				String multimediaUrlFk = null;
				if(image.isEmpty() == false) {
					multimediaUrlFk = image.get(0).attr("src");
				}else if(sticker.isEmpty() == false) {
					multimediaUrlFk = sticker.get(0).attr("src");
				}
				commentVO.setMultimediaUrlFk(multimediaUrlFk);
				
				// 댓글 마지막 수정 날짜
				String commentLastModifyDateText = comment.select(".comment_info_date").get(0).text();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd. HH:mm");
				Date commentLastModifyDate = null;
				if(StringUtils.hasLength(commentLastModifyDateText)) {
					commentLastModifyDateText = commentLastModifyDateText.trim();
					try {
						commentLastModifyDate = sdf.parse(commentLastModifyDateText);
					} catch (java.text.ParseException e) {
						e.printStackTrace();
					}
				}
				commentVO.setCommentLastModifyDate(commentLastModifyDate);
			}
			
			// 게시글 번호 외래키
			Element spiButton = doc.getElementById("spiButton");
			String postOriginalUrl = null;
			String postNoFk = null;
			if(spiButton == null) { // 공유금지 글인 경우, 좋아요 또는 답글 버튼에서 게시글 번호 추출
				Elements e = doc.select(".ReactionLikeIt");
				if(e.isEmpty()) {
					e = doc.select(".left_area a:nth-child(2)");
					String replyUrl = e.get(0).attr("href");
					postNoFk = replyUrl.substring( replyUrl.lastIndexOf("articles")+9, replyUrl.lastIndexOf("/") );
				}else {
					String cid = e.get(0).dataset().get("cid");
					postNoFk = cid.substring( cid.lastIndexOf("_")+1 );
				}
			}else {
				Map<String, String> dataset = spiButton.dataset();
				postOriginalUrl = dataset.get("url");
				if(StringUtils.hasLength(postOriginalUrl)) {
					postOriginalUrl = postOriginalUrl.trim();
				}
				postNoFk = postOriginalUrl.substring( postOriginalUrl.lastIndexOf("/")+1 );
			}
			commentVO.setPostNoFk(Integer.parseInt(postNoFk));
			
			// 댓글 정렬순서
			Integer commentOrder = i;
			commentVO.setCommentOrder(commentOrder);
			
			commentVOList.add(commentVO);
		}
		return commentVOList;
	}
	
	/**
	 * book 회원 파싱
	 * @param doc
	 * @return
	 */
	public static List<MemberVO> parseBookMember(Document doc) {
		List<MemberVO> memberVOList = new ArrayList<MemberVO>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd. HH:mm");
		
		// 본문 작성자
		// 계정 마지막 활동 날짜(게시글 최종 수정일)
		String writerLastActivityDate = doc.select(".post_header.border-sub > dl > dd.update").get(0).text().trim();
		Elements writerList = doc.select(".post_header.border-sub > dl > dd > a.m-tcol-c");
		if(writerList.isEmpty() == false) {
			for(int i=0; i<writerList.size()-2; i++) {
				Element writerElement = writerList.get(i);
				String onClickString = writerElement.attr("onclick");
				String[] e = onClickString.substring(3, onClickString.lastIndexOf(")")).replaceAll("[' ]", "").split(",");
				
				MemberVO writerMemberVO = new MemberVO();
				writerMemberVO.setMemberId(e[1]); // 아이디
				writerMemberVO.setMemberNickname(e[3]); // 닉네임
				try {
					writerMemberVO.setMemberLastActivityDate(sdf.parse(writerLastActivityDate));
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				// 클럽 아이디 생략
				// 프로필 사진 URL 생략
				// 등급 생략
				memberVOList.add(writerMemberVO);
			}
		}
		
		// 댓글 작성자
		Elements commentAreaList = doc.select("#cmt_list li:not(.board-box-line-dashed)");
		for(Element commentArea : commentAreaList) {
			Elements commentThumbElements = commentArea.select("a.link_profile");
			if(commentThumbElements.isEmpty()) {
				continue;
			}
			String commentProfileUrl = commentThumbElements.get(0).attr("href");
			Map<String, String> commentProfileParams = StringUtil.queryToMap(
					commentProfileUrl.substring( commentProfileUrl.lastIndexOf("?")+1 )
			);
			
			// 아이디
			String commentMemberId = commentProfileParams.get("memberid");
			if(StringUtils.hasLength(commentMemberId)) {
				commentMemberId = commentMemberId.trim();
			}
			
			// 클럽아이디
			String commentClubid = commentProfileParams.get("clubid");
			if(StringUtils.hasLength(commentClubid)) {
				commentClubid = commentClubid.trim();
			}
			
			// 닉네임
			String commentMemberNickname = commentArea.select("a._nickUI").get(0).text();
			if(StringUtils.hasLength(commentMemberNickname)) {
				commentMemberNickname = commentMemberNickname.trim();
			}
			
			// 프로필 사진 URL
			String commentProfileImageUrl = commentThumbElements.select("img").get(0).attr("src");
			
			// 계정 마지막 활동 날짜(댓글 마지막 수정 날짜)
			String commentMemberLastActivityDateText = commentArea.select(".comm_cont > .h > span.date").get(0).text();
			Date commentMemberLastActivityDate = null;
			if(StringUtils.hasLength(commentMemberLastActivityDateText)) {
				commentMemberLastActivityDateText = commentMemberLastActivityDateText.trim();
				try {
					commentMemberLastActivityDate = sdf.parse(commentMemberLastActivityDateText);
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
			}
			
			MemberVO commentMemberVO = new MemberVO();
			commentMemberVO.setMemberId(commentMemberId);
			commentMemberVO.setClubid(commentClubid);
			commentMemberVO.setMemberNickname(commentMemberNickname);
			commentMemberVO.setProfileImageUrl(commentProfileImageUrl);
			commentMemberVO.setMemberLastActivityDate(commentMemberLastActivityDate);
			memberVOList.add(commentMemberVO);
		}
		return memberVOList;
	}
	
	/**
	 * 회원 파싱
	 * @param doc
	 * @return
	 */
	public static List<MemberVO> parseMember(Document doc) {
		List<MemberVO> memberVOList = new ArrayList<MemberVO>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd. HH:mm");
		
		// 본문 작성자
		String profileUrl = doc.select(".WriterInfo > a").get(0).attr("href");
		Map<String, String> params = StringUtil.queryToMap(
				profileUrl.substring( profileUrl.lastIndexOf("?")+1 )
		);
		
		// 아이디
		String memberId = params.get("memberid");
		if(StringUtils.hasLength(memberId)) {
			memberId = memberId.trim();
		}
		
		// 클럽아이디
		String clubid = params.get("clubid");
		if(StringUtils.hasLength(clubid)) {
			clubid = clubid.trim();
		}
		
		// 닉네임
		String memberNickname = doc.select(".nickname").get(0).text();
		if(StringUtils.hasLength(memberNickname)) {
			memberNickname = memberNickname.trim();
		}
		
		// 프로필 사진 URL
		String profileImageUrl = doc.select(".WriterInfo > a > img").get(0).attr("src");
		
		// 등급
		String gradeFk = null;
		Elements nickLevel = doc.select(".nick_level");
		if(nickLevel.isEmpty()==false) {
			gradeFk = nickLevel.get(0).text();
			if(StringUtils.hasLength(gradeFk)) {
				gradeFk = gradeFk.trim();
			}
		}
		
		// 계정 마지막 활동 날짜(게시글 최종 수정일)
		String memberLastActivityDateText = doc.select(".article_info > .date").get(0).text();
		Date memberLastActivityDate = null;
		if(StringUtils.hasLength(memberLastActivityDateText)) {
			memberLastActivityDateText = memberLastActivityDateText.trim();
			try {
				memberLastActivityDate = sdf.parse(memberLastActivityDateText);
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
		}
		
		MemberVO writerMemberVO = new MemberVO();
		writerMemberVO.setMemberId(memberId);
		writerMemberVO.setClubid(clubid);
		writerMemberVO.setMemberNickname(memberNickname);
		writerMemberVO.setProfileImageUrl(profileImageUrl);
		writerMemberVO.setGradeFk(gradeFk);
		writerMemberVO.setMemberLastActivityDate(memberLastActivityDate);
		memberVOList.add(writerMemberVO);
		
		// 댓글 작성자
		Elements commentAreaList = doc.select(".comment_area");
		for(Element commentArea : commentAreaList) {
			Elements commentThumbElements = commentArea.select("a.comment_thumb");
			if(commentThumbElements.isEmpty()) {
				continue;
			}
			String commentProfileUrl = commentThumbElements.get(0).attr("href");
			Map<String, String> commentProfileParams = StringUtil.queryToMap(
					commentProfileUrl.substring( commentProfileUrl.lastIndexOf("?")+1 )
			);
			
			// 아이디
			String commentMemberId = commentProfileParams.get("memberid");
			if(StringUtils.hasLength(commentMemberId)) {
				commentMemberId = commentMemberId.trim();
			}
			
			// 클럽아이디
			String commentClubid = commentProfileParams.get("clubid");
			if(StringUtils.hasLength(commentClubid)) {
				commentClubid = commentClubid.trim();
			}
			
			// 닉네임
			String commentMemberNickname = commentArea.select("a.comment_nickname").get(0).text();
			if(StringUtils.hasLength(commentMemberNickname)) {
				commentMemberNickname = commentMemberNickname.trim();
			}
			
			// 프로필 사진 URL
			String commentProfileImageUrl = commentArea.select("a.comment_thumb > img").get(0).attr("src");
			
			// 계정 마지막 활동 날짜(댓글 마지막 수정 날짜)
			String commentMemberLastActivityDateText = commentArea.select(".comment_info_date").get(0).text();
			Date commentMemberLastActivityDate = null;
			if(StringUtils.hasLength(commentMemberLastActivityDateText)) {
				commentMemberLastActivityDateText = commentMemberLastActivityDateText.trim();
				try {
					commentMemberLastActivityDate = sdf.parse(commentMemberLastActivityDateText);
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
			}
			
			MemberVO commentMemberVO = new MemberVO();
			commentMemberVO.setMemberId(commentMemberId);
			commentMemberVO.setClubid(commentClubid);
			commentMemberVO.setMemberNickname(commentMemberNickname);
			commentMemberVO.setProfileImageUrl(commentProfileImageUrl);
			commentMemberVO.setMemberLastActivityDate(commentMemberLastActivityDate);
			memberVOList.add(commentMemberVO);
		}
		return memberVOList;
	}
	
	/**
	 * book 게시글 작성자 목록 파싱
	 * @param postNo 게시글 번호
	 * @param doc HTML문서
	 * @return
	 */
	public static List<PostMemberVO> parseBookPostMember(Integer postNo, Document doc){
		List<PostMemberVO> postMemberVOList = new ArrayList<PostMemberVO>();
		Elements writerList = doc.select(".post_header.border-sub > dl > dd > a.m-tcol-c");
		if(writerList.isEmpty() == false) {
			for(int i=0; i<writerList.size()-2; i++) {
				Element writerElement = writerList.get(i);
				String onClickString = writerElement.attr("onclick");
				String[] e = onClickString.substring(3, onClickString.lastIndexOf(")")).replaceAll("[' ]", "").split(",");
				//LOGGER.info(e[1] + ", " + e[3]);
				
				PostMemberVO postMemberVO = new PostMemberVO();
				postMemberVO.setPostNo(postNo);
				postMemberVO.setMemberId(e[1]);
				postMemberVOList.add(postMemberVO);
			}
		}
		return postMemberVOList;
	}
	
	/**
	 * 게시글 작성자 목록 파싱
	 * @param postNo 게시글 번호
	 * @param doc HTML문서
	 * @return
	 */
	public static List<PostMemberVO> parsePostMember(Integer postNo, Document doc){
		// 본문 작성자
		String profileUrl = doc.select(".WriterInfo > a").get(0).attr("href");
		Map<String, String> params = StringUtil.queryToMap(
				profileUrl.substring( profileUrl.lastIndexOf("?")+1 )
		);
		
		// 아이디
		String memberId = params.get("memberid");
		if(StringUtils.hasLength(memberId)) {
			memberId = memberId.trim();
		}
		
		PostMemberVO postMemberVO = new PostMemberVO();
		postMemberVO.setPostNo(postNo);
		postMemberVO.setMemberId(memberId);
		
		List<PostMemberVO> postMemberVOList = new ArrayList<PostMemberVO>();
		postMemberVOList.add(postMemberVO);
		
		return postMemberVOList;
	}
	
	/**
	 * 등급 파싱
	 * @param doc
	 * @return
	 */
	public static GradeVO parseGrade(Document doc) {
		Elements nickLevel = doc.select(".nick_level");
		if(nickLevel.isEmpty()) {
			return null;
		}
		String gradeName = nickLevel.get(0).text();
		if(StringUtils.hasLength(gradeName)) {
			gradeName = gradeName.trim();
		}
		GradeVO gradeVO = new GradeVO();
		gradeVO.setGradeName(gradeName);
		return gradeVO;
	}
	
	/**
	 * 게시글-해시태그 파싱
	 * @param doc
	 * @return
	 */
	public static List<PostTagVO> getPostTagVOList(int postNo, List<TagVO> tagVOList){
		List<PostTagVO> postTagVOList = new ArrayList<PostTagVO>();
		for(TagVO tagVO : tagVOList) {
			PostTagVO postTagVO = new PostTagVO();
			postTagVO.setPostNo(postNo);
			postTagVO.setTagName(tagVO.getTagName());
			postTagVOList.add(postTagVO);
		}
		return postTagVOList;
	}
	
	/**
	 * book 해시태그 파싱
	 * @return
	 */
	public static List<TagVO> parseBookTag(Document doc) {
		Elements tagList = doc.select("#tagListArea > a");
		List<TagVO> tagVOList = new ArrayList<TagVO>();
		for(Element tag : tagList) {
			TagVO tagVO = new TagVO();
			tagVO.setTagName(tag.text().substring(1));
			tagVOList.add(tagVO);
		}
		return tagVOList;
	}
	
	/**
	 * 해시태그 파싱
	 * @return
	 */
	public static List<TagVO> parseTag(Document doc) {
		Elements tagList = doc.select(".tag_list > .item > a");
		List<TagVO> tagVOList = new ArrayList<TagVO>();
		for(Element tag : tagList) {
			TagVO tagVO = new TagVO();
			tagVO.setTagName(tag.text().substring(1));
			tagVOList.add(tagVO);
		}
		return tagVOList;
	}
	
	/**
	 * book 게시글(제목,본문,조회수 등) 파싱
	 * @param doc
	 * @return
	 */
	public static PostVO parseBookPost(Document doc) {
		Element spiButton = doc.getElementById("spiButton");
		
		// URL, 게시글 번호
		Map<String, String> dataset = spiButton.dataset();
		String postOriginalUrl = dataset.get("url");
		if(StringUtils.hasLength(postOriginalUrl)) {
			postOriginalUrl = postOriginalUrl.trim();
		}
		String postNo = postOriginalUrl.substring( postOriginalUrl.lastIndexOf("/")+1 );
		//LOGGER.info("processor 게시글번호: {}", postNo);
		
		// 메뉴 아이디
		String menuId = postOriginalUrl.substring(
				postOriginalUrl.lastIndexOf("book"),
				postOriginalUrl.lastIndexOf("/")
		);
		
		// 제목
		Elements titleTextList = doc.select(".post_header > h2.m-tcol-c");
		String postTitle = titleTextList.get(0).text();
		if(StringUtils.hasLength(postTitle)) {
			postTitle = postTitle.trim();
		}
		
		// 공지사항 여부
		String postNoticeYn = "N";
		
		// 작성자 닉네임
		String postRegisterNickname = null;
		Elements writerList = doc.select(".post_header.border-sub > dl > dd > a.m-tcol-c");
		if(writerList.isEmpty() == false) {
			for(int i=0; i<writerList.size()-2; i++) {
				Element writerElement = writerList.get(i);
				String onClickString = writerElement.attr("onclick");
				String[] e = onClickString.substring(3, onClickString.lastIndexOf(")")).replaceAll("[' ]", "").split(",");
				//LOGGER.info(e[1] + ", " + e[3]);
				if(StringUtils.hasLength(e[3])) {
					if(i==0) {
						postRegisterNickname = "";
					}else {
						postRegisterNickname += ",";
					}
					postRegisterNickname += e[3];
				}
			}
		}
		
		// 조회수
		String postHits = doc.select(".reply-box table tbody tr .b.m-tcol-c.reply:nth-child(2)").get(0).text();
		if(StringUtils.hasLength(postHits)) {
			postHits = postHits.trim();
		}
		
		// 최종 수정일
		String postLastModifyDateText = doc.select(".update").get(0).text();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd. HH:mm");
		Date postLastModifyDate = null;
		if(StringUtils.hasLength(postLastModifyDateText)) {
			postLastModifyDateText = postLastModifyDateText.trim();
			try {
				postLastModifyDate = sdf.parse(postLastModifyDateText);
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
		}
		
		// 좋아요 수
		String postLikeCount = null;
		
		// 공유 수
		String postShareCount = null;
		
		// 본문
		String postBody = doc.select(".post-area.m-tcol-c").get(0).html();
		
		// CCL 여부 YN
		String postCclYn = "N";
		Elements cclLink = doc.select(".ccl_box");
		if(cclLink.isEmpty() == false) {
			postCclYn = "Y";
		}
		
		PostVO postVO = new PostVO();
		postVO.setPostOriginalUrl(postOriginalUrl);
		postVO.setPostNo(Integer.parseInt(postNo));
		postVO.setMenuFk(menuId);
		postVO.setPostTitle(postTitle);
		postVO.setPostRegisterNickname(postRegisterNickname);
		postVO.setPostLastModifyDate(postLastModifyDate);
		postVO.setPostHits(Integer.parseInt(StringUtil.removeComma(postHits)));
		postVO.setPostLikeCount(postLikeCount==null ? null : Integer.parseInt(StringUtil.removeComma(postLikeCount)));
		postVO.setPostShareCount(postShareCount == null ? null : Integer.parseInt(postShareCount));
		postVO.setPostBody(postBody);
		postVO.setPostNoticeYn(postNoticeYn);
		postVO.setPostCclYn(postCclYn);
		return postVO;
	}
	
	/**
	 * 게시글(제목,본문,조회수 등) 파싱
	 * @param doc
	 * @return
	 */
	public static PostVO parsePost(Document doc) {
		Element spiButton = doc.getElementById("spiButton");
		
		// URL, 게시글 번호
		String postOriginalUrl = null;
		String postNo = null;
		if(spiButton == null) { // 공유금지 글인 경우, 좋아요 또는 답글 버튼에서 게시글 번호 추출
			Elements e = doc.select(".ReactionLikeIt");
			if(e.isEmpty()) {
				e = doc.select(".left_area a:nth-child(2)");
				String replyUrl = e.get(0).attr("href");
				postNo = replyUrl.substring( replyUrl.lastIndexOf("articles")+9, replyUrl.lastIndexOf("/") );
			}else {
				String cid = e.get(0).dataset().get("cid");
				postNo = cid.substring( cid.lastIndexOf("_")+1 );
			}
		}else {
			Map<String, String> dataset = spiButton.dataset();
			postOriginalUrl = dataset.get("url");
			if(StringUtils.hasLength(postOriginalUrl)) {
				postOriginalUrl = postOriginalUrl.trim();
			}
			postNo = postOriginalUrl.substring( postOriginalUrl.lastIndexOf("/")+1 );
		}
		//LOGGER.info("processor 게시글번호: {}", postNo);
		
		// 메뉴 아이디
		Elements articleTitleList = doc.select(".ArticleTitle > a.link_board");
		String href = articleTitleList.get(0).attr("href");
		String menuId = href.substring( href.lastIndexOf("menuid=")+7);
		if(StringUtils.hasLength(menuId)) {
			menuId = menuId.trim();
		}
		
		// 제목
		Elements titleTextList = doc.select(".title_text");
		String postTitle = titleTextList.get(0).text();
		if(StringUtils.hasLength(postTitle)) {
			postTitle = postTitle.trim();
		}
		
		// 공지사항 여부
		Elements noticeTextList = doc.select("title_notice");
		String postNoticeYn = "N";
		if(noticeTextList.isEmpty() == false) {
			postNoticeYn = "Y";
		}
		
		// 작성자 닉네임
		String postRegisterNickname = null;
		Elements nicknameElements = doc.select(".nickname");
		if(nicknameElements.isEmpty() == false) {
			postRegisterNickname = nicknameElements.get(0).text();
			if(StringUtils.hasLength(postRegisterNickname)) {
				postRegisterNickname = postRegisterNickname.trim();
			}else {
				postRegisterNickname = null;
			}
		}
		
		// 조회수
		String postHitsText = doc.select(".article_info > .count").get(0).text();
		if(StringUtils.hasLength(postHitsText)) {
			postHitsText = postHitsText.trim();
		}
		String postHits = postHitsText.substring( postHitsText.lastIndexOf(" ")+1 );
		
		// 최종 수정일
		String postLastModifyDateText = doc.select(".article_info > .date").get(0).text();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd. HH:mm");
		Date postLastModifyDate = null;
		if(StringUtils.hasLength(postLastModifyDateText)) {
			postLastModifyDateText = postLastModifyDateText.trim();
			try {
				postLastModifyDate = sdf.parse(postLastModifyDateText);
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
		}
		
		// 좋아요 수
		Elements postLikeCountElements = doc.select(".u_cnt._count");
		String postLikeCount = null;
		if(postLikeCountElements.isEmpty() == false) {
			postLikeCount = postLikeCountElements.get(0).text();
			if(StringUtils.hasLength(postLikeCount)) {
				postLikeCount = postLikeCount.trim();
			}
		}
		
		// 공유 수
		Elements countArticleNumList = doc.select(".count_article > .num");
		String postShareCount = null;
		if(countArticleNumList.isEmpty() == false) {
			postShareCount = countArticleNumList.get(0).text();
			if(StringUtils.hasLength(postShareCount)) {
				postShareCount = postShareCount.trim();
			}
		}
		
		// 본문
		String postBody = doc.select(".article_viewer").get(0).html();
		
		// CCL 여부 YN
		String postCclYn = "N";
		Elements cclLink = doc.select("a.ccl_link");
		if(cclLink.isEmpty() == false) {
			postCclYn = "Y";
		}
		
		PostVO postVO = new PostVO();
		postVO.setPostOriginalUrl(postOriginalUrl);
		postVO.setPostNo(Integer.parseInt(postNo));
		postVO.setMenuFk(menuId);
		postVO.setPostTitle(postTitle);
		postVO.setPostRegisterNickname(postRegisterNickname);
		postVO.setPostLastModifyDate(postLastModifyDate);
		postVO.setPostHits(Integer.parseInt(StringUtil.removeComma(postHits)));
		postVO.setPostLikeCount(postLikeCount==null ? null : Integer.parseInt(StringUtil.removeComma(postLikeCount)));
		postVO.setPostShareCount(postShareCount == null ? null : Integer.parseInt(postShareCount));
		postVO.setPostBody(postBody);
		postVO.setPostNoticeYn(postNoticeYn);
		postVO.setPostCclYn(postCclYn);
		return postVO;
	}
	
	/**
	 * book 메뉴(게시판명, 게시판아이디) 파싱
	 * @param doc
	 * @return
	 */
	public static MenuVO parseBookMenu(Document doc) {
		String href = doc.getElementById("linkUrl").attr("href").trim();
		
		String menuId = href.substring( href.lastIndexOf("book"), href.lastIndexOf("/"));
		String menuName = menuId;
		
		MenuVO menuVO = new MenuVO();
		menuVO.setMenuId(menuId);
		menuVO.setMenuName(menuName);
		return menuVO;
	}
	
	/**
	 * 메뉴(게시판명, 게시판아이디) 파싱
	 * @param doc
	 * @return
	 */
	public static MenuVO parseMenu(Document doc) {
		String menuId = null;
		String menuName = null;
		Elements list = doc.select(".ArticleTitle > a.link_board");
		if(list.isEmpty() == false) {
			Element a = list.get(0);
			String href = list.get(0).attr("href");
			
			menuId = href.substring( href.lastIndexOf("menuid=")+7);
			if(StringUtils.hasLength(menuId)) {
				menuId = menuId.trim();
			}
			menuName = a.text();
			if(StringUtils.hasLength(menuName)) {
				menuName = menuName.trim();
			}
		}
		MenuVO menuVO = new MenuVO();
		menuVO.setMenuId(menuId);
		menuVO.setMenuName(menuName);
		return menuVO;
	}
}
