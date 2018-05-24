package com.iyunya.wechat.api;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.iyunya.wechat.common.Consts;
import com.iyunya.wechat.common.WechatUser;
import com.iyunya.wechat.common.WechatUserUtil;


@Controller
@RequestMapping("/wechat")
public class WechatController{
	public static final Logger log = LoggerFactory.getLogger(WechatController.class);
	
	
	@RequestMapping(method=RequestMethod.GET)
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException {
			// 微信会在配置的回调地址上加上signature,nonce,timestamp,echostr4个参数
			String signature = request.getParameter("signature");
			String timestamp = request.getParameter("timestamp");
			String nonce = request.getParameter("nonce");
			String echostr = request.getParameter("echostr");
			log.info("微信传递的签名参数  signature：{} timestamp：{} nonce:{} echostr:{}",signature,timestamp,nonce,echostr);
			// 1).排序
			String sortString = WechatUserUtil.sort(Consts.SELF_CODE_WX_TOKEN, timestamp, nonce);
			// 2).加密
			String mytoken = WechatUserUtil.sha1(sortString);
			// 3).校验签名
			if (!StringUtils.isEmpty(mytoken) && mytoken.equals(signature)) {
				log.info("微信签名校验通过。");
				response.getWriter().println(echostr);
			}else {
				log.warn("微信签名校验失败。");
			}
		}
		
	@RequestMapping(path = "/oauth", method = RequestMethod.GET)
	public String oauth(HttpServletRequest request, HttpServletResponse response){
		try {
			String bindUrl = URLEncoder.encode("http://" + Consts.DOMAIN + "/wechat/bindUser",Consts.ENCODING);
			//snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid）  snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且，即使在未关注的情况下，只要用户授权，也能获取其信息）
			String wechatAuthUrl = "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=BIND_URL?response_type=code&scope=snsapi_userinfo&state=1&connect_redirect=1#wechat_redirect".replaceAll("APPID",Consts.AppId).replaceAll("BIND_URL",bindUrl);
			return wechatAuthUrl;
		} catch (Exception e) {
			log.error("跳转微信授权失败：",e);
		}
	    return "redirect:http://" + Consts.DOMAIN + "/wechat/oauth";
	}
	
	@RequestMapping(path = "/bindUser", method = RequestMethod.GET)
	public String user(String code,HttpServletRequest request,HttpServletResponse response,Model model){
		try {
			if(!StringUtils.isEmpty(code)) {
				log.info("获取到的 wechat code:{}",code);
				//获取openid
				String openid = WechatUserUtil.exchangeCode2OpenId(code);
				if(!StringUtils.isEmpty(openid)) {
					//获取微信用户信息
					WechatUser wechatUser = WechatUserUtil.getWechatUser(openid);
					log.info("获取到微信用户信息:",wechatUser);
					model.addAttribute(Consts.WECHAT_USER_INFO,wechatUser);
					request.getSession().setAttribute(Consts.WECHAT_USER_INFO,wechatUser);
					return "wechat/userBind";
				}
			}else {
				log.info("未获取到来自  wechat 端的 code");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	    
	    return "user";
	}
	
}
