package com.social.util;

import java.text.MessageFormat;

import com.qq.connect.QQConnectException;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class WeChatDevUtils {
	
	public static final String GET_QRCONNECT_URL = "https://open.weixin.qq.com/connect/qrconnect?appid={0}&redirect_uri={1}&response_type=code&scope=snsapi_login&state={2}#wechat_redirect";
	public static final String GET_OAUTH_USER_AGREE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid={0}&redirect_uri={1}&response_type=code&scope={2}&state=1#wechat_redirect";
	public static final String GET_OAUTH_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid={0}&secret={1}&code={2}&grant_type=authorization_code";
	//public static final String GET_USER_INFO = "https://api.weixin.qq.com/sns/userinfo?access_token={0}&openid={1}";
	public static final String GET_USER_INFO = "https://api.weixin.qq.com/cgi-bin/user/info?access_token={0}&openid={1}";
	public static final String CHECK_OAUTH_TOKEN_IS_VALID = "https://api.weixin.qq.com/sns/auth?access_token={0}&openid={1}";
	public static final String REFRESH_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid={0}&grant_type=refresh_token&refresh_token={1}";

	public final static String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={CorpID}&secret={SECRET}";

	public static final String SOCIAL_LOGIN_CLIENT_ID = AppConfig.getProperty(Constans.WX.SOCIAL_LOGIN_CLIENT_ID);
	public static final String SOCIAL_LOGIN_CLIENT_SERCRET = AppConfig.getProperty(Constans.WX.SOCIAL_LOGIN_CLIENT_SERCRET);
	/**
	 * 获取access_token
	 *
	 * @param appid
	 *            凭证
	 * @param appsecret
	 *            密钥
	 * @return
	 */
	public static AccessToken getAccessToken(String appid, String appsecret) throws Exception {
		AccessToken accessToken = null;

		String requestUrl = ACCESS_TOKEN_URL.replace("{CorpID}", appid)
				.replace("{SECRET}", appsecret);
		String s = HttpClientUtils.sendRequest(requestUrl);

		if (null != s) {
			try {
				JSONObject jsonObject = JSONObject.fromObject(s);
				accessToken = new AccessToken();
				accessToken.setToken(jsonObject.getString("access_token"));
				accessToken.setExpiresIn(jsonObject.getInt("expires_in"));
				/*logger.debug("获取token成功:"
						+ jsonObject.getString("access_token"));*/
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return accessToken;
	}
	/**
	 *  wechat login request code
	 */
	public static String getQrconnect(String redirectUri, String state) throws Exception {
		String getQrcodeUrl = MessageFormat.format(GET_QRCONNECT_URL, SOCIAL_LOGIN_CLIENT_ID, redirectUri, state);
		return getQrcodeUrl;
	}
	
	/**
	 * get authorization address - scope snsapi_userinfo | snsapi_base
	 */
	public static String getAuthrUrl(String redirectUri, String scope) throws Exception {
		String getCodeUrl = MessageFormat.format(GET_OAUTH_USER_AGREE_URL, SOCIAL_LOGIN_CLIENT_ID, java.net.URLEncoder.encode(redirectUri, "utf-8"), scope);
		return getCodeUrl;
	}
	
	/**
	 * by code in exchange for web authorization - access_token
	 */
	public static JSONObject oauth(String appId, String appSecret, String code) throws Exception {
		String oauthUrl = MessageFormat.format(GET_OAUTH_URL, SOCIAL_LOGIN_CLIENT_ID, SOCIAL_LOGIN_CLIENT_SERCRET, code);
		String response = HttpClientUtils.sendRequest(oauthUrl);
		return JSONObject.fromObject(response);
	}
	
	/**
	 * Gets the basic information for the login user
	 */
	public static WxUserinfo getUserInfoBySns(String accessToken, String openId) throws Exception {
		String getUserInfoUrl = MessageFormat.format(GET_USER_INFO, accessToken, openId);
		String response = HttpClientUtils.sendRequest(getUserInfoUrl);
		WxUserinfo wxUserinfo = (WxUserinfo) JSONObject.toBean(JSONObject.fromObject(response), WxUserinfo.class);
		return wxUserinfo;
	}
	
	/**
	 * Check whether the authorization certificate is valid accessToken
	 */
	public static JSONObject checkTokenIsValid(String accessToken, String openId) throws Exception {
		String checkTokenUrl = MessageFormat.format(CHECK_OAUTH_TOKEN_IS_VALID, accessToken, openId);
		String response = HttpClientUtils.sendRequest(checkTokenUrl);
		return JSONObject.fromObject(response);
	}
	
	/**
	 * refresh access_token(if need) refreshToken
	 */
	public static JSONObject refreshToken(String refreshToken) throws Exception {
		String refreshTokenUrl = MessageFormat.format(REFRESH_TOKEN_URL, SOCIAL_LOGIN_CLIENT_ID, refreshToken);
		String response = HttpClientUtils.sendRequest(refreshTokenUrl);
		return JSONObject.fromObject(response);
	}
}
