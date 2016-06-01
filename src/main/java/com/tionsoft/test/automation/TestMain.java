package com.tionsoft.test.automation;

import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.codehaus.plexus.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btb.meap.mas.tas.exception.TasClientException;
import com.tionsoft.test.automation.tmg.TmgAddrAutoTestClient;
import com.tionsoft.test.automation.tmg.TmgAuthAutoTestClient;
import com.tionsoft.test.automation.tmg.TmgCalAutoTestClient;
import com.tionsoft.test.automation.tmg.TmgMailAutoTestClient;
import com.tionsoft.test.automation.tools.ConfigProperty;
import com.tionsoft.test.automation.tools.EmailSender2;


public class TestMain {
	private static String authkey = "";
	private static String attachedFileYN = "";
	private static String status = "true";
	
	public static void main(String [] args) throws ConfigurationException, IOException {
		Logger errorLogger = LoggerFactory.getLogger("error");
		
		String serverIP = args[0];
		int serverPort = Integer.parseInt(args[1]);
		String keystorePassword = args[2];
		String basePath = args[3];

		String emailFrom		= ConfigProperty.get("emailFrom");
		String emailTo			= ConfigProperty.get("emailTo");
		String defaultMailTitle = new String(ConfigProperty.get("DefaultMailTitle").getBytes("ISO-8859-1"),"UTF-8");
		
		if("true".equals(status)) {
			status = "false";
			TmgAuthAutoTestClient tmgAuth = new TmgAuthAutoTestClient();
			tmgAuth.setServerIp(serverIP);
			tmgAuth.setSererPort(serverPort);
			tmgAuth.setKeystorePassword(keystorePassword);
			tmgAuth.setBasePath(basePath);
			while (true) {
				try {
//					tmgAuth.execute("AUTH00001");
					tmgAuth.execute("COM000001");
//					tmgAuth.execute("COM000002");
//					tmgAuth.execute("AUTH00002");
//					tmgAuth.execute("AUTH00003");
					authkey = tmgAuth.getAuthKey();
					status = "true";
					break;
				} catch (TasClientException e) {
					errorLogger.info(ExceptionUtils.getStackTrace(e));
				} catch (Exception e) {
					errorLogger.info(ExceptionUtils.getStackTrace(e));
//					sendExceptionMail(emailFrom, emailTo, defaultMailTitle, e);
					status = "false";
					break;
				}
			}
		}
//
//
//		if("true".equals(status)) {
//			status = "false";
//			TmgMailAutoTestClient tmgMail = new TmgMailAutoTestClient();
//			tmgMail.setServerIp(serverIP);
//			tmgMail.setSererPort(serverPort);
//			tmgMail.setKeystorePassword(keystorePassword);
//			tmgMail.setBasePath(basePath);
//			tmgMail.setAuthKey(authkey);
//
//			try {
//				tmgMail.execute("MAIL00026");
//				tmgMail.execute("MAIL00002");
//				tmgMail.execute("MAIL00005");
//				tmgMail.execute("MAIL00003");
//				tmgMail.execute("MAIL00028");
//				attachedFileYN = tmgMail.getAttachedFileYN();
//				if("Y".equals(attachedFileYN)){
//					try {
//						tmgMail.execute("FILE00001");
//					} catch (Exception e) {
//					}
//				}
//				status = "true";
//			} catch (TasClientException e) {
//				errorLogger.info(ExceptionUtils.getStackTrace(e));
//			} catch (Exception e) {
//				errorLogger.info(ExceptionUtils.getStackTrace(e));
//				sendExceptionMail(emailFrom, emailTo, defaultMailTitle, e);
//			}
//		}
//
//		if("true".equals(status)) {
//			status = "false";
//			TmgAddrAutoTestClient tmgAddr = new TmgAddrAutoTestClient();
//			tmgAddr.setServerIp(serverIP);
//			tmgAddr.setSererPort(serverPort);
//			tmgAddr.setKeystorePassword(keystorePassword);
//			tmgAddr.setBasePath(basePath);
//			tmgAddr.setAuthKey(authkey);
//
//			try {
//				tmgAddr.execute("ADDR00020","");
//				tmgAddr.execute("ADDR00019","");
//				tmgAddr.execute("ADDR00014","");
//				tmgAddr.execute("ADDR00016","ROOT");
//				String addrId = tmgAddr.getAddrId();
//				tmgAddr.execute("ADDR00016",addrId);
//				addrId = tmgAddr.getAddrId();
//				tmgAddr.execute("ADDR00016",addrId);
//				addrId = tmgAddr.getAddrId();
//				tmgAddr.execute("ADDR00017","");
//				tmgAddr.execute("ADDR00018",addrId);
//				status = "true";
//			} catch (TasClientException e) {
//				errorLogger.info(ExceptionUtils.getStackTrace(e));
//			} catch (Exception e) {
//				errorLogger.info(ExceptionUtils.getStackTrace(e));
//				sendExceptionMail(emailFrom, emailTo, defaultMailTitle, e);
//			}
//		}
//
//		if("true".equals(status)) {
//			status = "false";
//			TmgCalAutoTestClient tmgCal = new TmgCalAutoTestClient();
//			tmgCal.setServerIp(serverIP);
//			tmgCal.setSererPort(serverPort);
//			tmgCal.setKeystorePassword(keystorePassword);
//			tmgCal.setBasePath(basePath);
//			tmgCal.setAuthKey(authkey);
//
//			try {
//				tmgCal.execute("CAL000022");
//				tmgCal.execute("CAL000021");
//				tmgCal.execute("CAL000011");
//
//				status = "true";
//			} catch (TasClientException e) {
//				errorLogger.info(ExceptionUtils.getStackTrace(e));
//			} catch (Exception e) {
//				errorLogger.info(ExceptionUtils.getStackTrace(e));
//				sendExceptionMail(emailFrom, emailTo, defaultMailTitle, e);
//			}
//		}
		
		System.exit(0);
	}

	private static void sendExceptionMail(String emailFrom, String emailTo, String defaultMailTitle, Exception e) {
		try {
			new EmailSender2().sendEmail(emailFrom, emailTo, defaultMailTitle, ExceptionUtils.getStackTrace(e).replaceAll("(\r\n|\n)", "<br/>"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
