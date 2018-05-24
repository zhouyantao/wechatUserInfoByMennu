package com.iyunya.wechat.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class WechatUserUtil {
    private static final Logger logger = LoggerFactory.getLogger(WechatUserUtil.class);
    
    public static String exchangeCode2OpenId(String code){
    	String openid = "";
        try {
            String appId = Consts.AppId;
            String appSecret = Consts.AppSecret;
            // 换取access_token 其中包含了openid
            // 这里通过code换取的是一个特殊的网页授权access_token,与基础支持中的access_token（该access_token用于调用其他接口）不同。
            String URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code".replace("APPID", appId).replace("SECRET", appSecret).replace("CODE", code);
            String jsonStr = HttpUtil.sendGet(URL);
            logger.info("----------微信换取openid返回的结果:{}----------", jsonStr);
            Gson gson = new Gson();
            WechatOpenId wechatOpenId = gson.fromJson(jsonStr,WechatOpenId.class);
        	if(null != wechatOpenId) {
        		openid = wechatOpenId.getOpenid();
        	}
        } catch (Exception e) {
            logger.info("----------微信换取openid发生了异常:{}----------", e.getMessage());
        }
        return openid;
    }
    
    public static WechatUser getWechatUser(String openid) {
        String token = AccessTokenTool.getAccessToken();
        String URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
        String jsonResult = HttpUtil.sendGet(URL.replace("OPENID", openid).replace("ACCESS_TOKEN", token));
        logger.info("----------获取到的用户信息,{}----------", jsonResult);
        Gson gson = new Gson();
        WechatUser wechatUser = null;
        try {
            wechatUser = gson.fromJson(new String(jsonResult.getBytes("ISO-8859-1"), "UTF-8"), WechatUser.class);
            // 错误的openId
            if (StringUtils.isBlank(wechatUser.getOpenid())) {
                wechatUser = null;
            }
        } catch (JsonSyntaxException e) {
            logger.info("----------解析json出错----------");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return wechatUser;
    }
    
	public static String sha1(String str) {
		if (str == null || str.length() == 0)
			return "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			byte[] bytes = md.digest(str.getBytes());
			return byte2Hex(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			logger.error("SHA1加密出错：" + e.getMessage());
			throw new RuntimeException("SHA1加密出错");
		}
	}

	public static String byte2Hex(byte[] data) {
		if (data == null || data.length == 0)
			return "";
		StringBuilder sbu = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			if ((data[i] & 0xff) < 0x10) {
				sbu.append("0");
			}
			sbu.append(Integer.toHexString(data[i] & 0xff));
		}
		return sbu.toString();
	}
	
	/**
	 * 将token,timestamp,nonce按字典序排序，并返回拼接的字符串
	 */
	public static String sort(String token,String timestamp,String nonce) {
		String[] strArray = {token,timestamp,nonce};
		Arrays.sort(strArray);
		StringBuilder sbuilder = new StringBuilder();
		for (String str : strArray) {
			sbuilder.append(str);
		}
		return sbuilder.toString();
	}
    
    
}
