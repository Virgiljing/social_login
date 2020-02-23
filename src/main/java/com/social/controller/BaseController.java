package com.social.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ModelAttribute;

public class BaseController {
	private static Logger log = Logger.getLogger(BaseController.class);
	
	/**
	 * ThreadLocal确保高并发下每个请求的request，response都是独立的
	 */
	private static ThreadLocal<ServletRequest> currentRequest = new ThreadLocal<ServletRequest>();
	private static ThreadLocal<ServletResponse> currentResponse = new ThreadLocal<ServletResponse>();
	
	/**
	 * thread safe init request，response obj
	 * @param request
	 * @param response
	 */
	@ModelAttribute
	public void init(HttpServletRequest request, HttpServletResponse response) {
		currentRequest.set(request);
		currentResponse.set(response);
		getRequest().getSession().setAttribute("login_current_url", getRequest().getHeader("Referer"));
		log.info("================login_current_url " + getRequest().getHeader("Referer"));
	}
	
	/**
	 * thread safe
	 * @return
	 */
	public HttpServletRequest getRequest() {
		return (HttpServletRequest) currentRequest.get();
	}
	public void responseResult(HttpServletResponse response, Object result) {
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Type", "application/json");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Content-type", "application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json;charset=UTF-8");
//        ServletOutputStream out = null;
		PrintWriter writer=null;
		try {
			// JSON.toJSONString(result)要获取完整的json字符串，每一个字段都要有set和get方法，不然会缺少某个字段
//            response.getWriter().write(JSON.toJSONString(result));
			writer=response.getWriter();
//            writer.write(result.toString());
			writer.write(JSON.toJSONString(result));
			writer.flush();
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}finally {
			if(writer!=null) {
				writer.close();
			}
		}
	}
	/**
	 * thread safe
	 * @return
	 */
	public HttpServletResponse getResponse() {
		return (HttpServletResponse) currentResponse.get();
	}
	
	public static final String SUCESS = "success";

	public static final String FAILURE = "failure";// jsp

	public static final String ERROR = "error";// app

	public static final String JSON_VIEW = "json/json";
	
	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		return (s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24)).substring(0, 32);
	}

}
