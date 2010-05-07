package net.ameba.cassandra.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.ameba.cassandra.web.service.CassandraClientProvider;
import net.ameba.cassandra.web.service.CassandraService;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.thrift.transport.TTransportException;
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
	 * Getting a list of keyspaces.
	 * @return
	 * @throws Exception
	 */
	@ModelAttribute("keyspaces")
	public List<String> getKeyspaces() throws Exception {
		try {
			Client client = clientProvider.getThriftClient();
			if (client == null) {
				return new ArrayList<String>();
			} else {
				List<String> keyspaceNames = new ArrayList<String>(client.describe_keyspaces());
				Collections.sort(keyspaceNames);
				return keyspaceNames;
			}
		} catch (TTransportException ex) {
			// Connection failed
			return new ArrayList<String>();
		}
	}
	
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
