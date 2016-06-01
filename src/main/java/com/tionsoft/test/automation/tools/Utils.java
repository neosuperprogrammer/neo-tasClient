package com.tionsoft.test.automation.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Utils {
	public static List<Map<String, Object>> parseMailList(JSONArray sourceList) {
		List<Map<String, Object>> mails = new ArrayList<Map<String, Object>>();
		Map<String, Object> mail;
		
		for (int i = 0; i < sourceList.size(); i++) {
			mails.add(mail = new HashMap<String, Object>());
			
			mail.put("Mail_ID",			((JSONObject)sourceList.get(i)).getString("Mail_ID"));
			mail.put("Mail_Subject",	((JSONObject)sourceList.get(i)).getString("Mail_Subject"));
			mail.put("Mail_From",		((JSONObject)sourceList.get(i)).getString("Mail_From"));
			mail.put("Mail_To",			((JSONObject)sourceList.get(i)).getString("Mail_To"));
			mail.put("Mail_Status",		((JSONObject)sourceList.get(i)).getString("Mail_Status"));
		}		

		return mails;
	}

	public static List<Map<String, Object>> getMailIDList(JSONArray sourceList, String mailFrom) {
		List<Map<String, Object>> mailIDs = new ArrayList<Map<String, Object>>();
		Map<String, Object> mailID;

		JSONObject mail = null;

		//받은 메일 추출
		mail = sourceList.getJSONObject(0);
		if (mailFrom.equals(mail.get("Mail_From").toString())) {
			mailIDs.add(mailID = new HashMap<String, Object>());
			mailID.put("Mail_ID", mail.get("Mail_ID"));
		}		
		
		return mailIDs;
	}

	public static List<Map<String, Object>> getCalList(JSONArray sourceList, String docTitle) {
		List<Map<String, Object>> docIDs = new ArrayList<Map<String, Object>>();
		Map<String, Object> docID;

		JSONObject doc = null;
		// 추가한 일정 추출
		for (int i = 0; i < sourceList.size(); i++) {
			doc = sourceList.getJSONObject(i);
			if (docTitle.equals(doc.get("Doc_Subject").toString())) {
				docIDs.add(docID = new HashMap<String, Object>());
				docID.put("Doc_ID", doc.get("Doc_ID"));
			}
		}		
		
		return docIDs;
	}
	
}
