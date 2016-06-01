package com.tionsoft.test.automation.tmg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.btb.meap.mas.tas.bean.TasBean;
import com.btb.meap.mas.tas.bean.platform.PlatformHeader;
import com.btb.meap.mas.tas.client.message.TasRequest;
import com.btb.meap.mas.tas.client.message.TasResponse;
import com.tionsoft.test.automation.TasTestClient;
import com.tionsoft.test.automation.tools.Utils;

public class TmgMailAutoTestClient extends TasTestClient {
	private String [] mBoxIds;
	private String mailId;
	private String mailSubject;
	private String mailFrom;
	private List<Map<String, Object>> mailList;
	private String attachedFileYN;
	private String [] attachedFileInfo;
	private final Logger logger = LoggerFactory.getLogger("logger");
	
	public String getAttachedFileYN() {
		return attachedFileYN;
	}

	public void setAttachedFileYN(String attachedFileYN) {
		this.attachedFileYN = attachedFileYN;
	}

	@Override
	public void execute(Object...params) throws Exception {
		String messageId = params[0].toString();
		
		initialize("tmg");	
		TasResponse response = tasClient.sendSyncRequest(createTelegram(messageId), TasTestClient.RESPONSE_TIME_OUT);
		
		JSONObject headerJsonObj = JSONObject.fromObject(response.getHeader("Header_JSON", String.class));
		int status = Integer.parseInt(headerJsonObj.get("Status").toString());

		if (((!"FILE00001".equals(messageId)) && (0 != status) ) || ( ("FILE00001".equals(messageId)) &&  (status < 1) ) ) {
			System.out.println(messageId + " - Fail");
			logger.info(messageId + " - Fail");
			throw new Exception(messageId + " - Fail");
		}

		System.out.println( messageId + " - OK");
		logger.info(messageId + " - OK");
		
		JSONObject bodyOutJsonObj = null;
		
		if ("MAIL00026".equals(messageId)) {
			bodyOutJsonObj = JSONObject.fromObject(response.getBody("Out_JSON", String.class));
			
			JSONArray mBoxList = bodyOutJsonObj.getJSONArray("List");
			mBoxIds = new String[mBoxList.size()];
			
			for (int i = 0; i < mBoxList.size(); i++) {
				mBoxIds[i] = ((JSONObject)mBoxList.get(i)).getString("MBox_ID");
			}
		} else if ("MAIL00002".equals(messageId)) {
			bodyOutJsonObj = JSONObject.fromObject(response.getBody("Out_JSON", String.class));
			JSONArray mList = bodyOutJsonObj.getJSONArray("List");
			
			if (mList != null) {
				mailId = ((JSONObject)mList.get(0)).getString("Mail_ID");
				mailSubject = ((JSONObject)mList.get(0)).getString("Mail_Subject");
				mailFrom = ((JSONObject)mList.get(0)).getString("Mail_From");
				attachedFileYN = "N";
				// 첨부파일 존재하는 메일 검색
				for (int i = 0; i < mList.size(); i++) {
					if ("1".equals( ((JSONObject)mList.get(i)).getString("Mail_Status").split("::")[1] )) {
						mailId = ((JSONObject)mList.get(i)).getString("Mail_ID");
						mailSubject = ((JSONObject)mList.get(i)).getString("Mail_Subject");
						setAttachedFileYN("Y");
						break;
					} 
				}

				// mailList 생성 (읽음표시,읽지않음표시,삭제처리할 리스트)
				mailList = Utils.getMailIDList(mList, mailFrom);
			} 

		} else if ("MAIL00003".equals(messageId)) {	
			bodyOutJsonObj = JSONObject.fromObject(response.getBody("Out_JSON", String.class));
			String attachedFiles = bodyOutJsonObj.get("Attached_File").toString();

			if (attachedFiles != null) {
				String attachedFile = attachedFiles.split("::")[0];
				attachedFileInfo = new String[4];
				for (int i = 0; i < 4; i++) {
					attachedFileInfo[i] = attachedFile.split("\\|")[i];
				}
			}													
		}
		
	}
	
	private TasRequest createTelegram(String messageId) throws Exception {
		PlatformHeader ph = createPlatformHeader(messageId);
		TasBean header = createHeader(messageId);
		TasBean body = new TasBean();
		Map<String,Object> bodyMap = null;
		JSONObject json = null;
		TasRequest request = null;
		
		if ("MAIL00001".equals(messageId)) { //전체 메일 개수
			request = new TasRequest(ph, header, null);
		} else if ("MAIL00026".equals(messageId)) {	  //메일함 목록
			request = new TasRequest(ph, header, null);
		} else if ("MAIL00002".equals(messageId)) {  //메일 목록
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("MBox_ID",				mBoxIds[0]);
			bodyMap.put("Req_Page",				deviceConfig.getString("devices.device.body.mail.Req_Page"));
			bodyMap.put("MailItemPerPage",		deviceConfig.getString("devices.device.body.mail.MailItemPerPage"));
			json = JSONObject.fromObject(bodyMap);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		} else if ("MAIL00030".equals(messageId)) {  //메일 임시 저장
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("Mail_To",				deviceConfig.getString("devices.device.body.mail.Mail_To"));
			bodyMap.put("Mail_Cc",				deviceConfig.getString("devices.device.body.mail.Mail_Cc"));
			bodyMap.put("Mail_Hidden_Cc",		deviceConfig.getString("devices.device.body.mail.Mail_Hidden_Cc"));
			bodyMap.put("Mail_Subject",			deviceConfig.getString("devices.device.body.mail.Mail_Subject"));
			bodyMap.put("Mail_Body",			deviceConfig.getString("devices.device.body.mail.Mail_Body"));
			bodyMap.put("Mail_ID",				""); //신규메일 임시저장
			bodyMap.put("Mail_Send_Type",		deviceConfig.getString("devices.device.body.mail.Mail_Send_Type"));
			bodyMap.put("Cal_Action_Type",		"");
			bodyMap.put("Attached_File_Count",	deviceConfig.getString("devices.device.body.mail.Attached_File_Count"));
			json = JSONObject.fromObject(bodyMap);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		} else if ("MAIL00006".equals(messageId)) {  //메일 보내기
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("Mail_To",				deviceConfig.getString("devices.device.body.mail.Mail_To"));
			bodyMap.put("Mail_Cc",				deviceConfig.getString("devices.device.body.mail.Mail_Cc"));
			bodyMap.put("Mail_Hidden_Cc",		deviceConfig.getString("devices.device.body.mail.Mail_Hidden_Cc"));
			bodyMap.put("Mail_Subject",			deviceConfig.getString("devices.device.body.mail.Mail_Subject"));
			bodyMap.put("Mail_Body",			deviceConfig.getString("devices.device.body.mail.Mail_Body"));
			bodyMap.put("Mail_ID",				""); //신규메일
			bodyMap.put("Mail_Send_Type",		deviceConfig.getString("devices.device.body.mail.Mail_Send_Type"));
			bodyMap.put("Cal_Action_Type",		"");
			bodyMap.put("Attached_File_Count",	deviceConfig.getString("devices.device.body.mail.Attached_File_Count"));
			json = JSONObject.fromObject(bodyMap);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		} else if ("MAIL00005".equals(messageId)) {  //메일 검색
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("MBox_ID",				mBoxIds[0]);
			bodyMap.put("Search_Type",			deviceConfig.getString("devices.device.body.mail.Search_Type"));
			bodyMap.put("Search_Keyword",		mailSubject);
			bodyMap.put("Req_Page",				deviceConfig.getString("devices.device.body.mail.Req_Page"));
			bodyMap.put("MailItemPerPage",		deviceConfig.getString("devices.device.body.mail.MailItemPerPage"));
			json = JSONObject.fromObject(bodyMap);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		} else if ("MAIL00003".equals(messageId)) {	  //메일 상세
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("Mail_ID",				mailId);
			json = JSONObject.fromObject(bodyMap);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		} else if ("MAIL00028".equals(messageId)) {	  //읽지 않음 표시 - mailList를 읽지 않음 처리
			json = new JSONObject();
			json.put("Mail_ID_Count", 			mailList.size());
			json.put("List",					mailList);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		} else if ("MAIL00007".equals(messageId)) {  //메일 삭제 - mailList를 삭제 처리
			json = new JSONObject();
			json.put("Mail_ID_Count", 			mailList.size());
			json.put("Delete_Type", 			deviceConfig.getString("devices.device.body.mail.Delete_Type"));
			json.put("List",					mailList);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		} else if ("FILE00001".equals(messageId)) {	 //PDV 변환 요청
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("Domain",					deviceConfig.getString("devices.device.body.file.Domain"));  
			bodyMap.put("FileUsage",				deviceConfig.getString("devices.device.body.file.FileUsage"));				// 게시판 : Public, 메일 : Private
			bodyMap.put("AccountId",				deviceConfig.getString("devices.device.header.AccountId"));
			bodyMap.put("FileId",					attachedFileInfo[2]);					// ATTACH_ID
			bodyMap.put("FileName",					attachedFileInfo[0]); 				// ATTACH_NAME
			bodyMap.put("FileLocation",				attachedFileInfo[3]);			// ATTACH_URL  
			bodyMap.put("FileLocationType",			deviceConfig.getString("devices.device.body.file.FileLocationType"));  		// 1
			bodyMap.put("FileLocationParameter",	deviceConfig.getString("devices.device.body.file.FileLocationParameter"));	// ""
			bodyMap.put("FileSize",					attachedFileInfo[1]);  				// FILE_LENGTH
			bodyMap.put("RequestPage",				deviceConfig.getString("devices.device.body.file.RequestPage"));				// 1
			bodyMap.put("RequestUnit",				deviceConfig.getString("devices.device.body.file.RequestUnit"));				// 2 : 압축파일 반환
			bodyMap.put("Resolution",				deviceConfig.getString("devices.device.body.file.Resolution")); 				// 1 : 기본해상도
			bodyMap.put("WatermarkText",			deviceConfig.getString("devices.device.body.file.WatermarkText"));  			// ""
			bodyMap.put("PageSetup",				deviceConfig.getString("devices.device.body.file.PageSetup"));  				// 0
			bodyMap.put("AffiliateCode",			deviceConfig.getString("devices.device.body.file.AffiliateCode"));  			// ""
			bodyMap.put("Language",					deviceConfig.getString("devices.device.body.file.Language"));  					// ""
			
			json = JSONObject.fromObject(bodyMap);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		}
		
		return request;
	}
	
	private TasBean createHeader(String messageId) throws Exception {
		TasBean header = new TasBean();
		Map<String,Object> headerMap  = new HashMap<String, Object>();
		try {
			headerMap.put("LegacyId",		messageId);
			headerMap.put("AccountId",		deviceConfig.getString("devices.device.header.AccountId"));
			headerMap.put("Min",			deviceConfig.getString("devices.device.header.Min"));
			headerMap.put("Wifi",			deviceConfig.getString("devices.device.header.Wifi"));
			headerMap.put("AuthKey",		getAuthKey());
			headerMap.put("Version",		deviceConfig.getString("devices.device.header.Version"));

			JSONObject headerMapJson = JSONObject.fromObject(headerMap);
			header.setValue("Header_JSON", headerMapJson.toString());
		} catch (Exception e) {
			throw e;  
		}
		
		return header;
	}
}
