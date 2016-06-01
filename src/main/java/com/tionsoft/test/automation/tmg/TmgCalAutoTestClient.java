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

public class TmgCalAutoTestClient extends TasTestClient {
	private String [] docIds;
	private String [] docSubjects;
	private List<Map<String, Object>> calList;
	private final Logger logger = LoggerFactory.getLogger("logger");
	
	@Override
	public void execute(Object...params) throws Exception {
		
		String messageId = params[0].toString();
		
		initialize("tmg");	
		TasResponse response = tasClient.sendSyncRequest(createTelegram(messageId), TasTestClient.RESPONSE_TIME_OUT);
		
		JSONObject headerJsonObj = JSONObject.fromObject(response.getHeader("Header_JSON", String.class));
		String status = headerJsonObj.get("Status").toString();

		if (!"0".equals(status)) {
			System.out.println(messageId + " - Fail");
			logger.info(messageId + " - Fail");
			throw new Exception(messageId + " - Fail");
		}

		System.out.println(messageId + " - OK");
		logger.info(messageId + " - OK");
		
		JSONObject bodyOutJsonObj = null;
		
		if ("CAL000022".equals(messageId)) { 
			bodyOutJsonObj = JSONObject.fromObject(response.getBody("Out_JSON", String.class));
		} else if ("CAL000021".equals(messageId)) { 
			bodyOutJsonObj = JSONObject.fromObject(response.getBody("Out_JSON", String.class));
		} else if ("CAL000011".equals(messageId)) {
			bodyOutJsonObj = JSONObject.fromObject(response.getBody("Out_JSON", String.class));
			JSONArray docIdList = bodyOutJsonObj.getJSONArray("List");
			
			if (docIdList != null) {
				docIds = new String[docIdList.size()];
				docSubjects = new String[docIdList.size()];
				
				for (int i = 0; i < docIdList.size(); i++) {
					docIds[i] = ((JSONObject)docIdList.get(i)).getString("Doc_ID");
					docSubjects[i] = ((JSONObject)docIdList.get(i)).getString("Doc_Subject");
				}

				//삭제할 일정 리스트 추출
				calList = Utils.getCalList(docIdList, deviceConfig.getString("devices.device.body.cal.Doc_Title"));
			}
		} else if ("CAL000014".equals(messageId)) { 
			bodyOutJsonObj = JSONObject.fromObject(response.getBody("Out_JSON", String.class));
		} else if ("CAL000016".equals(messageId)) { 
			bodyOutJsonObj = JSONObject.fromObject(response.getBody("Out_JSON", String.class));
		}
	}
	
	private TasRequest createTelegram(String messageId) throws Exception {
		PlatformHeader ph = createPlatformHeader(messageId);
		TasBean header = createHeader(messageId);
		TasBean body = new TasBean();
		Map<String,Object> bodyMap = null;
		JSONObject json = null;
		TasRequest request = null;
		
		if ("CAL000012".equals(messageId)) {	  //일정 추가/수정
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("Type",							deviceConfig.getString("devices.device.body.cal.Type"));
			bodyMap.put("IsShare",						deviceConfig.getString("devices.device.body.cal.IsShare"));
			bodyMap.put("All_Day_Event",				deviceConfig.getString("devices.device.body.cal.All_Day_Event"));
			bodyMap.put("Start_Day",					deviceConfig.getString("devices.device.body.cal.Start_Day"));
			bodyMap.put("End_Day",						deviceConfig.getString("devices.device.body.cal.End_Day"));
			bodyMap.put("Doc_Title",					deviceConfig.getString("devices.device.body.cal.Doc_Title"));
			bodyMap.put("Doc_Room",						deviceConfig.getString("devices.device.body.cal.Doc_Room"));
			bodyMap.put("Doc_Body",						deviceConfig.getString("devices.device.body.cal.Doc_Body"));
			bodyMap.put("Doc_Alarm",					deviceConfig.getString("devices.device.body.cal.Doc_Alarm"));
			bodyMap.put("Mail_To",						deviceConfig.getString("devices.device.body.cal.Mail_To"));
			json = JSONObject.fromObject(bodyMap);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		} else if ("CAL000022".equals(messageId)) {	  //일정 월별 조회	
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("Standard_Date",				deviceConfig.getString("devices.device.body.cal.Standard_Date"));
			bodyMap.put("Target_Person_ID",				deviceConfig.getString("devices.device.body.cal.Target_Person_ID"));
			json = JSONObject.fromObject(bodyMap);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		} else if ("CAL000021".equals(messageId)) {	  //일정 주별 조회
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("Standard_Date",				deviceConfig.getString("devices.device.body.cal.Standard_Date"));
			bodyMap.put("Target_Person_ID",				deviceConfig.getString("devices.device.body.cal.Target_Person_ID"));
			json = JSONObject.fromObject(bodyMap);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		} else if ("CAL000011".equals(messageId)) {	  //일정 일별 조회
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("Standard_Start_Date",			deviceConfig.getString("devices.device.body.cal.Standard_Start_Date"));
			bodyMap.put("Standard_End_Date",			deviceConfig.getString("devices.device.body.cal.Standard_End_Date"));
			bodyMap.put("Target_Person_ID",				deviceConfig.getString("devices.device.body.cal.Target_Person_ID"));
			json = JSONObject.fromObject(bodyMap);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		} else if ("CAL000014".equals(messageId)) {	  //일정 상세
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("Doc_ID",						docIds[0]);
			bodyMap.put("Target_Person_ID",				deviceConfig.getString("devices.device.body.cal.Target_Person_ID"));
			json = JSONObject.fromObject(bodyMap);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		} else if ("CAL000016".equals(messageId)) {	  //일정 검색
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("Search_Type",					deviceConfig.getString("devices.device.body.cal.Search_Type"));
			bodyMap.put("Search_Term",					deviceConfig.getString("devices.device.body.cal.Search_Term"));
			bodyMap.put("Search_Keyword",				docSubjects[0]);
			bodyMap.put("Req_Page",						deviceConfig.getString("devices.device.body.cal.Req_Page"));
			bodyMap.put("Cal_ItemPerPage",				deviceConfig.getString("devices.device.body.cal.Cal_ItemPerPage"));
			json = JSONObject.fromObject(bodyMap);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		} else if ("CAL000013".equals(messageId)) {  //일정 삭제
			json = new JSONObject();
			json.put("Delete_Count", 					calList.size());
			json.put("List",							calList);
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

			JSONObject headerMapJson = JSONObject.fromObject(headerMap);
			header.setValue("Header_JSON", headerMapJson.toString());
		} catch (Exception e) {
			throw e;  
		}
		
		return header;
	}
}
