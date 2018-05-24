package com.iyunya.wechat.common;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;


public class AccessTokenTool {
	
	   	private static Logger log = LoggerFactory.getLogger(AccessTokenTool.class);
	    public static String access_token = "";
	    /**
	     * 过期时间7200秒， 因为微信token过期时间为2小时，即7200秒
	     */
	    private static int expireTime = 7200 * 1000;
	    private static long refreshTime;

	    /**
	     * 获取微信accesstoken
	     * 
	     * @return
	     */
	    public static synchronized String getAccessToken() {
	        return getAccessToken(false);
	    }

	    public static synchronized String getAccessToken(boolean refresh) {
	        if (StringUtils.isBlank(access_token) || (System.currentTimeMillis() - refreshTime) > expireTime || refresh) {
	            access_token = initAccessToken();
	            refreshTime = System.currentTimeMillis();
	        }

	        return access_token;
	    }

	    private static String initAccessToken() {
	        String responseContent = HttpUtil.sendGet(Consts.GET_ACCESS_TOKEN_URL);
	        Gson gson = new Gson();
	        try {
	        	AccessToken accessToken = gson.fromJson(responseContent,AccessToken.class);
	            return accessToken.getAccess_token();
	        } catch (Exception e) {
	        	log.error("获取token失败 ，反回：{}",responseContent,e);
	        }
	        return "";
	    }
}
