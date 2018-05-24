package com.iyunya.wechat.common;

public class Consts {
	//AppId
    public static final String AppId = "你的微信公众AppID";
    //AppSecret
    public static final String AppSecret = "你的微信公众密码";
    //Access token
    public static final String GET_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+ AppId + "&secret=" + AppSecret;
    //域名
    public static final String DOMAIN = "sellerspirit.cn";
    //个人
    public static final String SELF_CODE_WX_TOKEN = "wechat";
    //character
    public static final String ENCODING = "utf-8";
    //wechat session user
    public static final String WECHAT_USER_INFO = "WECHAT_USER_INFO";
    
    
}
