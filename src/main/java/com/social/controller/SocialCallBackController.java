package com.social.controller;

import java.io.IOException;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import weibo4j.Oauth;
import weibo4j.Users;
import weibo4j.http.AccessToken;
import weibo4j.model.User;
import weibo4j.model.WeiboException;

import com.qq.connect.api.OpenID;
import com.social.domain.vo.UserVO;
import com.social.service.UserService;
import com.social.util.AppConfig;
import com.social.util.HttpClientUtils;
import com.social.util.Constans.OpenIdType;
import com.social.util.WeChatDevUtils;
import com.social.util.WxUserinfo;

@Controller
@RequestMapping("/callback")
public class SocialCallBackController extends BaseController{
	private static Logger log = Logger.getLogger(SocialCallBackController.class);
	
	@Resource
    private UserService userService;
	
	private UserVO userVO;

	@RequestMapping("/weibo")
	public void weibo() throws IOException, WeiboException {
		String code = getRequest().getParameter("code");
		String url = (String) getRequest().getSession().getAttribute("login_current_url");

		sinaWeiboLoginAction(code);
		
		getRequest().getSession().removeAttribute("login_current_url");
		getResponse().sendRedirect(StringUtils.isBlank(url) ? "index" : url);
	}
	
	@RequestMapping("/qq")
	public void qq() throws Exception {
		String accessToken = getQQAccessToken();
        String openId =  getQQOpenId(accessToken);
        userVO = userService.getUserByOpenId(openId, OpenIdType.QQ);
        if(userVO == null){
        	getQQUserInfoByAccessToken(accessToken, openId);
        }
		String url = getRequest().getSession().getAttribute("login_current_url")==null ? "index" : (String)getRequest().getSession().getAttribute("login_current_url") ;
		getRequest().getSession().removeAttribute("login_current_url");
		getResponse().sendRedirect(url);
	}
	
	@RequestMapping("/wechat")
	@ResponseBody
	public void wechat() throws Exception { // web and mobileweb common method
//		String state = getRequest().getParameter("state");
//		String sessionState = getRequest().getSession().getAttribute("state").toString();
		String code = getRequest().getParameter("code");

		
		JSONObject result = WeChatDevUtils.oauth(WeChatDevUtils.SOCIAL_LOGIN_CLIENT_ID, WeChatDevUtils.SOCIAL_LOGIN_CLIENT_SERCRET, code);
		log.info("======================getaccesstoken result " + result.toString());
		String openId = result.getString("openid");
		userVO = userService.getUserByOpenId(openId, OpenIdType.WECHAT);
		if(userVO == null) {
			// by Access Token and openid in exchange for user's base information
			WxUserinfo wxUserinfo = wechatResultJson(openId);
			responseResult(getResponse(),wxUserinfo);
			return;
		}

		responseResult(getResponse(),userVO);
//
//		String url = (String) getRequest().getSession().getAttribute("login_current_url");
//		getRequest().getSession().removeAttribute("login_current_url");
//		getResponse().sendRedirect(StringUtils.isBlank(url) ? "http://2272bi1948.51mypc.cn/social_login/social/index" : url);
	}
	
	private void sinaWeiboLoginAction(String code) throws WeiboException, IOException {
		Oauth oauth = new Oauth();
		AccessToken accessToken = oauth.getAccessTokenByCode(code);
		String uid = accessToken.getUid();
		
		log.info("=====================weibo accessToken "+accessToken.getAccessToken() + " uid " + uid);
		
		getRequest().getSession().setAttribute("accessToken", accessToken);
		getRequest().getSession().setAttribute("uid", uid);
		
		userVO = userService.getUserByOpenId(uid, OpenIdType.WEIBO);
		if(userVO == null) {
			Users um = new Users();
			um.client.setToken(accessToken.getAccessToken());
			User user = um.showUserById(uid);
			
			String screenName = user.getScreenName();
			String avatar = user.getAvatarLarge();
			String gender = null;
	        
	        getRequest().getSession().setAttribute("screenName", screenName);
	        getRequest().getSession().setAttribute("avatar", avatar);
	        getRequest().getSession().setAttribute("gender", gender);
	        
	        chkLogin(uid, OpenIdType.WEIBO, screenName);
		}
	}
	
	private void getQQUserInfoByAccessToken(String accessToken, String openID) throws Exception{
        String url = AppConfig.getProperty("getUserInfoURL") + "?access_token="+accessToken+"&oauth_consumer_key="+AppConfig.getProperty("app_ID")+"&openid="+openID;
        String userinfo = HttpClientUtils.sendGetRequest(url, "utf-8");
        JSONObject jsonUserInfo = JSONObject.fromObject(userinfo);
        chkLogin(openID, OpenIdType.QQ, jsonUserInfo.getString("nickname"));
	}
	
	private String getQQOpenId(String accessToken) throws Exception{
		OpenID openIDObj =  new OpenID(accessToken);
        return openIDObj.getUserOpenID();
	}
	
	private String getQQAccessToken() throws Exception{
		com.qq.connect.javabeans.AccessToken accessTokenObj = (new com.qq.connect.oauth.Oauth()).getAccessTokenByRequest(getRequest());
        return accessTokenObj.getAccessToken();
	}
	
	private WxUserinfo wechatResultJson(String openId) throws Exception {

		com.social.util.AccessToken accessToken = WeChatDevUtils.getAccessToken(WeChatDevUtils.SOCIAL_LOGIN_CLIENT_ID, WeChatDevUtils.SOCIAL_LOGIN_CLIENT_SERCRET);
		String token = accessToken.getToken();
//		if(!chkAccessToken(token, openId)) {
//			JSONObject jsonResult = WeChatDevUtils.refreshToken(accessToken.getToken());
//			log.info("=====================refreshToken " + jsonResult.toString());
//			token = jsonResult.getString("access_token");
//		}
		
		WxUserinfo userInfo = WeChatDevUtils.getUserInfoBySns(token, openId);
		if(userInfo == null) {
			log.error("======================== unable to obtain authorization user information£¡");
			return null;
		}
		
		log.info("=======================userInfo " + userInfo);
		log.info("====================openId " + openId);
		
		chkLogin(openId, OpenIdType.WECHAT, userInfo.getNickname());
		return userInfo;
	}
	
	private boolean chkAccessToken(String accessToken, String openId) throws Exception {
		JSONObject jsonResult = WeChatDevUtils.checkTokenIsValid(accessToken, openId);
		log.info("========================checkTokenIsValid " + jsonResult.toString());
		String errmsg = jsonResult.getString("errmsg");
		if(errmsg.equals("ok")) {
			return true;
		} else {
			return false;
		}
	}
	
	public void chkLogin(String openId, OpenIdType openIdType, String name) throws IOException {
		com.social.domain.User newUser = new com.social.domain.User();
		if(StringUtils.isNotBlank(name)) {
			name = new String(name.getBytes("UTF-8"), "GBK");
		}
		newUser.setUserName(name);
		newUser.setOpenId(openId);
		newUser.setOpenIdType(openIdType.name());
		newUser.setAge(10);
		newUser.setPassword("");

		userService.insertSelective(newUser);
	}

	public UserVO getUserVO() {
		return userVO;
	}

	public void setUserVO(UserVO userVO) {
		this.userVO = userVO;
	}

}
