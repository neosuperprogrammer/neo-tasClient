package com.tionsoft.test.automation.tmg;

import java.util.HashMap;
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
//import com.tionsoft.test.automation.tools.ConfigProperty;

public class TmgAddrAutoTestClient extends TasTestClient {
	private String addrId;
	private String name;
	private final Logger logger = LoggerFactory.getLogger("logger");

	public String getAddrId() {
		return addrId;
	}

	public void setAddrId(String addrId) {
		this.addrId = addrId;
	}

	@Override
	public void execute(Object...params) throws Exception {
		
		String messageId = params[0].toString();
		
		setAddrId(params[1].toString());

		initialize("tmg");	
		TasResponse response = tasClient.sendSyncRequest(createTelegram(messageId), TasTestClient.RESPONSE_TIME_OUT);
		
		JSONObject headerJsonObj = JSONObject.fromObject(response.getHeader("Header_JSON", String.class));
		String status = headerJsonObj.get("Status").toString();
		
		if ("14110".equals(status)) {
		} else 	if (!"14110".equals(status) && !"0".equals(status)) {
			System.out.println(messageId + " - Fail");
			logger.info(messageId + " - Fail");
			throw new Exception(messageId + " - Fail");
		}

		System.out.println( messageId + " - OK");
		logger.info(messageId + " - OK");
		
		JSONObject bodyOutJsonObj = null;

		if ("ADDR00020".equals(messageId)) { 
			bodyOutJsonObj = JSONObject.fromObject(response.getBody("Out_JSON", String.class));
			addrId = bodyOutJsonObj.get("Address_ID").toString();
		} else if ("ADDR00014".equals(messageId)) { 
			bodyOutJsonObj = JSONObject.fromObject(response.getBody("Out_JSON", String.class));
		}else 	if ("ADDR00016".equals(messageId)) { 
			bodyOutJsonObj = JSONObject.fromObject(response.getBody("Out_JSON", String.class));
			JSONArray addrIdList = bodyOutJsonObj.getJSONArray("List");
			
			if (addrIdList.size() != 0) {
				JSONObject addrIdObject = addrIdList.getJSONObject(0);
				
				if ("3".equals(addrIdObject.get("Address_Type").toString())) {
					setAddrId(addrIdObject.get("Address_ID").toString());
					name = addrIdObject.get("Name").toString().split("\\|")[0];
				} else {
					setAddrId(addrIdObject.get("Address_ID").toString());
				}
			}
		} else if ("ADDR00017".equals(messageId)) { 
			bodyOutJsonObj = JSONObject.fromObject(response.getBody("Out_JSON", String.class));
		} else if ("ADDR00018".equals(messageId)) { 
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

		if ("ADDR00020".equals(messageId)) {	  //개인 주소 연락처 추가/수정 하기
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("Addr_Doc_ID",			"");
			bodyMap.put("Name",					deviceConfig.getString("devices.device.body.addr.Name"));
			bodyMap.put("Email",				deviceConfig.getString("devices.device.body.addr.Email"));
			bodyMap.put("Company",				deviceConfig.getString("devices.device.body.addr.Company"));
			bodyMap.put("TelePhone",			deviceConfig.getString("devices.device.body.addr.TelePhone"));
			bodyMap.put("MobilePhone",			deviceConfig.getString("devices.device.body.addr.MobilePhone"));
			bodyMap.put("HomePhone",			"");
			json = JSONObject.fromObject(bodyMap);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		} else if ("ADDR00019".equals(messageId)) {	  //개인 주소 초성 Count  목록
			request = new TasRequest(ph, header, null);
		} else if ("ADDR00014".equals(messageId)) {	  //개인 주소 초성 목록
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("Category",				deviceConfig.getString("devices.device.body.addr.Category"));
			bodyMap.put("Req_Page",				deviceConfig.getString("devices.device.body.addr.Req_Page"));
			bodyMap.put("AddressPerPage",		deviceConfig.getString("devices.device.body.addr.AddressPerPage"));
			json = JSONObject.fromObject(bodyMap);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		} else if ("ADDR00016".equals(messageId)) {  //그룹내 주소 목록
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("Group_ID",				addrId);
			bodyMap.put("Req_Page",				deviceConfig.getString("devices.device.body.addr.Req_Page"));
			bodyMap.put("AddressPerPage",		deviceConfig.getString("devices.device.body.addr.AddressPerPage"));
			json = JSONObject.fromObject(bodyMap);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		} else if ("ADDR00017".equals(messageId)) {	  //주소 검색
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("Search_Type",			deviceConfig.getString("devices.device.body.addr.Search_Type"));
			bodyMap.put("Search_Keyword",		name);
			bodyMap.put("Search_Keyword_Type",	deviceConfig.getString("devices.device.body.addr.Search_Keyword_Type"));
			bodyMap.put("Req_Page",				deviceConfig.getString("devices.device.body.addr.Req_Page"));
			bodyMap.put("AddressPerPage",		deviceConfig.getString("devices.device.body.addr.AddressPerPage"));
			json = JSONObject.fromObject(bodyMap);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		} else if ("ADDR00018".equals(messageId)) {	  //주소 상세
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("Address_ID",			addrId);
			bodyMap.put("Search_Type",			deviceConfig.getString("devices.device.body.addr.Search_Type"));
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

			JSONObject headerMapJson = JSONObject.fromObject(headerMap);
			header.setValue("Header_JSON", headerMapJson.toString());
		} catch (Exception e) {
			throw e;  
		}
		
		return header;
	}
}
