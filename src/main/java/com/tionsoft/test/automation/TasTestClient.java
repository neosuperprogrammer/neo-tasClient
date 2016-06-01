/**
 * 
 */
package com.tionsoft.test.automation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;

import com.btb.meap.mas.tas.bean.platform.PlatformHeader;
import com.btb.meap.mas.tas.bean.platform.PlatformHeaderFactory;
import com.btb.meap.mas.tas.client.TasClient;
import com.btb.meap.mas.tas.client.ssl.TasClientSslConfig;

/**
 * 
 * @author 박정희
 */
public abstract class TasTestClient {
	public static final long RESPONSE_TIME_OUT = 50*1000; 
	
	private String authKey;
	private String serverIp;
	private int sererPort;
	private PlatformHeader platformHeader;
	private String keystorePassword;
	protected String basePath;  
	protected TasClient tasClient = null;

	protected XMLConfiguration deviceConfig;
	
	//----------------------------------------------------
	// Private methods
	//----------------------------------------------------

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	public String getServerIp() {
		return serverIp;
	}
	
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getSererPort() {
		return sererPort;
	}

	public void setSererPort(int sererPort) {
		this.sererPort = sererPort;
	}

	public PlatformHeader getPlatformHeader() {
		return platformHeader;
	}

	public void setPlatformHeader(PlatformHeader platformHeader) {
		this.platformHeader = platformHeader;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	public String getKeystorePassword() {
		return keystorePassword;
	}

	/**
	 * 
	 * @param keystorePassword
	 */
	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	/**
	 * 
	 * @param applicationCode
	 * @throws IOException
	 * @throws ConfigurationException
	 */
	protected void initialize(String applicationCode) throws IOException, ConfigurationException {
		XMLConfiguration protocolConfig = null;
		protocolConfig = new XMLConfiguration(new File(basePath + applicationCode + "/protocol.xml"));
		protocolConfig.setExpressionEngine(new XPathExpressionEngine());
		
		InputStream inCertfile = null;         
	    try {             
	    	inCertfile = new FileInputStream(new File(basePath + "mas.jks"));         
    	} catch (FileNotFoundException e) {             
	    		e.printStackTrace();         
    	}    
		    
		TasClientSslConfig tasClientSslConfig = new TasClientSslConfig("TLS", "SunX509", keystorePassword, inCertfile, "JKS");
		tasClient = new TasClient(serverIp, sererPort, -1, protocolConfig, tasClientSslConfig);				
	}
	
	public abstract void execute(Object...params) throws Exception;

	protected PlatformHeader createPlatformHeader(String messageId) throws ConfigurationException {
				deviceConfig = new XMLConfiguration(new File(basePath + "device.xml"));
				
				PlatformHeader ph = PlatformHeaderFactory.getPlatformHeader(deviceConfig.getString("devices.device.platformHeader.phv"));
				ph.setValue(PlatformHeader.APPLICATION_ID,		deviceConfig.getString("devices.device.platformHeader.APPLICATION_ID"));
				ph.setValue(PlatformHeader.MESSAGE_ID,			messageId);
				ph.setValue(PlatformHeader.SESSION_ID,			deviceConfig.getLong("devices.device.platformHeader.SESSION_ID"));
				ph.setValue(PlatformHeader.TRANSACTION_ID,		System.currentTimeMillis());
				ph.setValue(PlatformHeader.SERVICE_ID,			deviceConfig.getInt("devices.device.platformHeader.SERVICE_ID"));
				ph.setValue(PlatformHeader.IMEI,				deviceConfig.getString("devices.device.platformHeader.IMEI"));
				ph.setValue(PlatformHeader.WIFI_MAC,			deviceConfig.getString("devices.device.platformHeader.WIFI_MAC"));
				ph.setValue(PlatformHeader.MSISDN,				deviceConfig.getString("devices.device.platformHeader.MSISDN"));
				ph.setValue(PlatformHeader.MODEL_NO,			deviceConfig.getString("devices.device.platformHeader.MODEL_NO"));
				ph.setValue(PlatformHeader.ISP_NAME,			deviceConfig.getString("devices.device.platformHeader.ISP_NAME"));
				ph.setValue(PlatformHeader.OS_TYPE,				deviceConfig.getString("devices.device.platformHeader.OS_TYPE"));
				ph.setValue(PlatformHeader.OS_VERSION,			deviceConfig.getString("devices.device.platformHeader.OS_VERSION"));
				ph.setValue(PlatformHeader.UUID,				deviceConfig.getString("devices.device.platformHeader.UUID"));
				ph.setValue(PlatformHeader.BODY_TYPE,			deviceConfig.getShort("devices.device.platformHeader.BODY_TYPE"));
				ph.setValue(PlatformHeader.STATUS_CODE,		deviceConfig.getShort("devices.device.platformHeader.STATUS_CODE"));
				
				return ph;
			}	
}
