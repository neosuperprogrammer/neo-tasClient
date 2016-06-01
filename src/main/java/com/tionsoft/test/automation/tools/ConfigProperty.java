/*
 * com.btb.gqms.common.util.ConfigProperty
 *
 * Created on 2011. 9. 27.
 * 
 * Copyright (c) 2002-2011 BTBSolution Co., Ltd. All Rights Reserved.
 */
package com.tionsoft.test.automation.tools;

import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;


public class ConfigProperty {
	static Properties props = new Properties();
	static {
		try {
			props = PropertiesLoaderUtils.loadAllProperties("config/config.properties");
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
 /**
  * Return the string value matching to given key.
  * If given key is not found , return null.
  *
  * @param key
  * @return
  */
	public static String get(String key) {
		try{
			return props.getProperty(key);
		}catch (Exception e) {
			return "";
		}
	}
 /**
  * Return the integer value matching to given key.
  * If given key is not found or value is not number format, throw exception.
  *
  * @param key
  * @return
  */
	public static int getInt(String key) {
		try{
			return Integer.parseInt(props.getProperty(key));
		}catch (Exception e) {
			return 0;
		}
	}
 /**
  * Return the boolean value matching to given key.
  * If given key is not found or value is not boolean format, throw exception.
  *
  * @param key
  * @return
  */
	public static boolean getBoolean(String key) {
		try{
			return Boolean.valueOf(props.getProperty(key)).booleanValue();
		}catch (Exception e) {
			return false;
		}
	}
}
