package net.ameba.cassandra.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ameba.cassandra.web.service.CassandraClientProvider;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * {@link CassandraClientInterceptor} intercepts requests to prevent leaking client connection.
 */
public class CassandraClientInterceptor implements HandlerInterceptor {
	
	@Autowired
	private CassandraClientProvider clientProvider;
	
	public CassandraClientInterceptor() {
	}

	@Override
	public void afterCompletion(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler,
			Exception exception)
			throws Exception {
	}

	@Override
	public void postHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler,
			ModelAndView mav) throws Exception {
		
		// If client has thrift client, closing it.
		if (clientProvider.hasThriftClient()) {
			Client client = clientProvider.getThriftClient();
			// closing transports
			client.getInputProtocol().getTransport().close();
			client.getOutputProtocol().getTransport().close();
			// clean up provider
			clientProvider.clean();
		}
		
	}

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		return true;
	}

}
