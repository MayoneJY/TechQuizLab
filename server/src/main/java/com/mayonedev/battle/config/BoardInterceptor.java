package com.mayonedev.battle.config;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class BoardInterceptor implements HandlerInterceptor{
	
	@Override //현재 세션 기반 - 로그인 검사 
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		HttpSession session = request.getSession(false);
		Object loginUser = null;
		if(session.getAttribute("loginUser") == null) {
			response.sendRedirect("/api/users/login");
			
			return false;
		};
		
		return true;
	}
	
	

}
