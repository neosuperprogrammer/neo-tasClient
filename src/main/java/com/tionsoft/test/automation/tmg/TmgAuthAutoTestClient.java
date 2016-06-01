package com.tionsoft.test.automation.tmg;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;

import com.btb.meap.mas.tas.bean.TasBean;
import com.btb.meap.mas.tas.bean.platform.PlatformHeader;
import com.btb.meap.mas.tas.client.message.TasRequest;
import com.btb.meap.mas.tas.client.message.TasResponse;
import com.tionsoft.test.automation.TasTestClient;

public class TmgAuthAutoTestClient extends TasTestClient {
	private final Logger logger = LoggerFactory.getLogger("logger");
	
	@Override
	public void execute(Object...params) throws Exception {
		
		String messageId = params[0].toString();
		
		initialize("tmg");	
		TasResponse response = tasClient.sendSyncRequest(createTelegram(messageId), TasTestClient.RESPONSE_TIME_OUT);

		JSONObject headerJsonObj = JSONObject.fromObject(response.getHeader("Header_JSON", String.class));
		String status = headerJsonObj.get("Status").toString();
		setAuthKey(headerJsonObj.get("AuthKey").toString());

		if (!"0".equals(status)) {
			System.out.println(messageId + " - Fail");
			logger.info(messageId + " - Fail");
			throw new Exception(messageId + " - Fail");
		}

		System.out.println( messageId + " - OK");
		logger.info(messageId + " - OK");
	}
	
	private TasRequest createTelegram(String messageId) throws Exception {
		PlatformHeader ph = createPlatformHeader(messageId);
		TasBean header = createHeader(messageId);
		TasBean body = new TasBean();
		Map<String,Object> bodyMap = null;
		JSONObject json = null;
		TasRequest request = null;
		
		if ("AUTH00001".equals(messageId)) {  //계정 등록  
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("Password",		deviceConfig.getString("devices.device.body.auth.Password"));
			bodyMap.put("Signature",		deviceConfig.getString("devices.device.body.auth.Signature"));
			bodyMap.put("App_Version",		deviceConfig.getString("devices.device.body.auth.App_Version"));
			json = JSONObject.fromObject(bodyMap);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		} else if ("COM000001".equals(messageId)) {  //초기데이터(어플 버전 체크 용)
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("App_Version",		deviceConfig.getString("devices.device.body.auth.App_Version"));
			json = JSONObject.fromObject(bodyMap);
			body.setValue("In_JSON", json.toString());
			request = new TasRequest(ph, header, body);
		} else if ("COM000002".equals(messageId)) {	  //헬프데스크
			request = new TasRequest(ph, header, null);
		} else if ("AUTH00002".equals(messageId)) {	  //메일 서명 조회
			request = new TasRequest(ph, header, null);
		} else if ("AUTH00003".equals(messageId)) {	  //메일 서명 변경
			bodyMap = new HashMap<String, Object>();
			bodyMap.put("Signature",		deviceConfig.getString("devices.device.body.auth.Signature"));
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


			headerMap.put("Language",		"ko");
			headerMap.put("AppType",		"test");
			headerMap.put("GroupCode",		"test");
			headerMap.put("AppId",		"test");

			JSONObject headerMapJson = JSONObject.fromObject(headerMap);
			header.setValue("Header_JSON", headerMapJson.toString());
		} catch (Exception e) {
			throw e;  
		}
		
		return header;
	}
}
