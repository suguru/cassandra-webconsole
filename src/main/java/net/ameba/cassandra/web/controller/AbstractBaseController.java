package net.ameba.cassandra.web.controller;

import javax.servlet.http.HttpServletRequest;

import net.ameba.cassandra.web.service.CassandraClientProvider;
import net.ameba.cassandra.web.service.CassandraService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * {@link AbstractBaseController} is extended by all controllers
 * used for cassandra-webconsole.
 */
public abstract class AbstractBaseController {
	
	@Autowired
	protected CassandraClientProvider clientProvider;

	@Autowired
	protected CassandraService cassandraService;

	/**
	 * Getting the context path of the application.
	 * @param request
	 * @return
	 */
	@ModelAttribute("contextPath")
	public String getContextPath(HttpServletRequest request) {
		return request.getContextPath();
	}
}
