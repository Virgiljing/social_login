<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${request.contextPath}/social_login" />
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>网页集成(微博，QQ，微信)登录</title>
<meta name="title" content="网页集成(微博，QQ，微信)登录">
<meta name="keywords" content="网页集成(微博，QQ，微信)登录">
<meta name="description" content="">
<meta name="viewport" content="initial-scale=1, width=device-width, maximum-scale=1, minimum-scale=1, user-scalable=no">
<meta content="http://www.bfamily.me" property="og:url">
<meta name="author" content="DeronMeng">
<meta name="renderer" content="webkit">
<c:set var="csshome" value="${ctx}/css" />
<c:set var="jshome" value="${ctx}/js" />
<c:set var="imghome" value="${ctx}/images" />
<link href="${csshome}/bootstrap.css" rel="stylesheet" type="text/css" />
</head>
<body>
	<header class="intro-header"
		style="background-image: url('${imghome}/home_darkroom.jpg')">
		<div class="container">
			<div class="row">
				<div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1 ">
					<div class="site-heading">
						<h1>social login</h1>
						<span class="subheading">请您使用第三方平台账号登陆：</span>
					</div>
				</div>
			</div>
		</div>
	</header>

	<div class="container">
		<br />
		<div class="row">
			<div
				class="col-lg-8 col-lg-offset-1 col-md-8 col-md-offset-1 col-sm-12 col-xs-12 postlist-container">
				<a href="${ctx}/social/weibo" target="_top">
					<button type="button" class="btn btn-danger" style="width: 170px;">新浪微博登录</button>
				</a>
			</div>
		</div>
		<br />
		<div class="row">
			<div
				class="col-lg-8 col-lg-offset-1 col-md-8 col-md-offset-1 col-sm-12 col-xs-12 postlist-container">
				<a href="${ctx}/social/qq" target="_top">
					<button type="button" class="btn btn-info" style="width: 170px;">腾讯QQ登录</button>
				</a>
			</div>
		</div>
		<br />
		<div class="row">
			<div
				class="col-lg-8 col-lg-offset-1 col-md-8 col-md-offset-1 col-sm-12 col-xs-12 postlist-container">
				<a href="${ctx}/social/wechat" target="_top">
					<button type="button" class="btn btn-success social_wechat_login" style="width: 170px;">微信登录</button>
				</a>
			</div>
		</div>
		<br />
		<div class="row">
			<div
				class="col-lg-8 col-lg-offset-1 col-md-8 col-md-offset-1 col-sm-12 col-xs-12 postlist-container">
				<a href="${ctx}/social/mobile/wechat" target="_top">
					<button type="button" class="btn btn-success social_wechat_login" style="width: 170px;">移动端微信登录</button>
				</a>
			</div>
		</div>

        <br />
		<div class="row">
			<div
				class="col-lg-8 col-lg-offset-1 col-md-8 col-md-offset-1 col-sm-12 col-xs-12 postlist-container">
				<a href="${ctx}/social/base/wechat" target="_top">
					<button type="button" class="btn btn-success social_wechat_login" style="width: 170px;">微信静默授权</button>
				</a>
			</div>
		</div>
		<br />
		<div class="row">
			<div
				class="col-lg-8 col-lg-offset-1 col-md-8 col-md-offset-1 col-sm-12 col-xs-12 postlist-container">
				<a href="${ctx}/social/facebook" target="_top">
					<button type="button" class="btn btn-primary" style="width: 170px;background-color: #357ebd;border-color: #357ebd;color: #fff;">FaceBook登录</button>
				</a>
			</div>
		</div>
		<br />
		<div class="row">
			<div
				class="col-lg-8 col-lg-offset-1 col-md-8 col-md-offset-1 col-sm-12 col-xs-12 postlist-container">
				<a href="${ctx}/social/instagram" target="_top">
					<button type="button" class="btn btn-warning" style="width: 170px;">Instagram登录</button>
				</a>
			</div>
		</div>
		<br />

		<div class="row">
			<div
					class="col-lg-8 col-lg-offset-1 col-md-8 col-md-offset-1 col-sm-12 col-xs-12 postlist-container">
				<a href="javascript:void(0);" target="_top">
					<button type="button"  id="btnOK" class="btn btn-warning" style="width: 170px;">agax请求</button>
				</a>
			</div>
		</div>

	</div>
	<script type="text/javascript" src="${jshome}/jquery-1.11.0.min.js"></script>
	<script type="text/javascript" src="${jshome}/bootstrap.min.js"></script>
	
	<script type="text/javascript">
	$(function() {
		$("#btnOK").click(function() {
			wxLogin();
		});


        function getUrlParam(name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
            var r = window.location.search.substr(1).match(reg);
            if (r != null) return unescape(r[2]);
            return null;
        }
        function wxLogin() {
            var appId = 'wxb9a22f91ec6f89d6';
            var oauth_url = 'http://2272bi1948.51mypc.cn/social_login/callback/wechat';
            var url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appId + "&redirect_uri=" + location.href.split('#')[0] + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect"
            var code = getUrlParam("code");
            console.debug(code);

            if (!code) {
                window.location = url;
            } else {
                $.ajax({
                    type: 'GET',
                    url: oauth_url,
                    dataType: 'json',
                    data: {
                        code: code
                    },
                    success: function (data) {
                        if (data.code === 200) {
                            alert(data.data)
                        }
                    },
                    error: function (error) {
                        throw new Error(error)
                    }
                })
            }
        }
	});
	</script>
</body>
</html>