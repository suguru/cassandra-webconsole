package net.ameba.cassandra.web.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class NoCacheFilter implements Filter {
	
	public NoCacheFilter() {
	}
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(
			ServletRequest request,
			ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.addDateHeader("Expire", 0L);
		httpResponse.addHeader("Pragma", "no-cache");
		httpResponse.addHeader("Cache-Control", "no-cache, must-revalidate, post-check=0, pre-check=0");
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

}
