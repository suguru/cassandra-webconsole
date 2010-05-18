package net.ameba.cassandra.web.service;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.tools.NodeProbe;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * {@link CassandraClientProvider} provides {@link Client} and {@link NodeProbe}.
 *
 * @author suguru
 */
@Component
public class CassandraClientProvider {
	
	@Autowired
	private CassandraProperties properties;
	
	private ThreadLocal<Client> th = new ThreadLocal<Client>();

	private Map<String, NodeProbe> probeMap;
	
	public CassandraClientProvider() {
		probeMap = new HashMap<String, NodeProbe>();
	}
	
	/**
	 * Returns true if client has been already created.
	 * @return
	 */
	public boolean hasThriftClient() {
		return th.get() != null;
	}
	
	/**
	 * Cleaning opened thrift client
	 */
	public void clean() {
		th.set(null);
	}

	/**
	 * Get Thrift Client
	 * @return
	 * @throws TTransportException
	 * @throws ConnectException
	 */
	public Client getThriftClient() throws TTransportException, ConnectException {
		
		if (!properties.hasProperties()) {
			return null;
		}
		
		String host = properties.getProperty(CassandraProperties.HOST);
		int port = Integer.parseInt(properties.getProperty(CassandraProperties.THRIFT_PORT));
		
		// Check usage of framed transport
		String framed = properties.getProperty(CassandraProperties.FRAMED_TRANSPORT);
		boolean isFramed = (framed != null && Boolean.valueOf(framed));
		
		Client client = th.get();
		if (client == null) {
			TTransport transport = new TSocket(host, port);
			// wrap transport if framed transport is enabled
			if (isFramed) {
				transport = new TFramedTransport(transport);
			}
			TProtocol protocol = new TBinaryProtocol(transport);
			transport.open();
			client = new Client(protocol);
			th.set(client);
		}
		return client;
	}
	
	/**
	 * Get NodeProbe connected to default host and port.
	 * @return
	 */
	public NodeProbe getProbe() {
		String host = properties.getProperty("cassandra.host");
		return getProbe(host);
	}
	
	/**
	 * Get NodeProbed connected to specified host.
	 * 
	 * @param host
	 * @return
	 */
	public synchronized NodeProbe getProbe(String host) {
		
		int port = Integer.parseInt(properties.getProperty("cassandra.jmx.port"));
		
		NodeProbe probe = probeMap.get(host + ":" + port);
		if (probe == null) {
			try {
				probe = new NodeProbe(host, port);
				probeMap.put(host + ":" + port, probe);
			} catch (IOException e) {
				return null;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			try {
				// check the connectivity
				probe.getToken();
			} catch (RuntimeException e) { 
				// clean jmx if failed to get token
				probeMap.remove(host + ":" + port);
				// retry
				try {
					probe = new NodeProbe(host, port);
					probeMap.put(host + ":" + port, probe);
				} catch (Exception ee) {
					return null;
				}
			}
		}
		return probe;
	}

}
