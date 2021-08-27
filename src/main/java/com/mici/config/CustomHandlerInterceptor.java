package com.mici.config;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class CustomHandlerInterceptor implements HandlerInterceptor {

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView model) throws Exception {
		if(!Objects.isNull(model)) {
			String loggedInUser = request.getRemoteUser();
			model.addObject("loggedUser", Objects.isNull(loggedInUser) ? "" : loggedInUser);
		}
	}
	
}
