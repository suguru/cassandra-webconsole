package net.ameba.cassandra.web.controller;

import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import net.ameba.cassandra.web.service.CassandraClientProvider;

import org.apache.cassandra.concurrent.IExecutorMBean;
import org.apache.cassandra.db.ColumnFamilyStoreMBean;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.tools.NodeProbe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SystemController extends AbstractBaseController {

	@Autowired
	private CassandraClientProvider clientProvider;
	
	@RequestMapping(value="/info/", method=RequestMethod.GET)
	public void showInfo(ModelMap model) throws Exception {
		
		Client client = clientProvider.getThriftClient();
		model.put("clusterName", client.describe_cluster_name());
		model.put("version", client.describe_version());
		
		/*
		NodeProbe probe = clientProvider.getProbe();
		model.put("liveNodes", probe.getLiveNodes());
		model.put("unreachableNodes", probe.getUnreachableNodes());
		
		model.put("uptime", getUptimeString(probe.getUptime()));
		model.put("token", probe.getToken());
		*/
		model.put("menu_info", Boolean.TRUE);
	}
	
	/**
	 * Show ring
	 * 
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value="/ring/", method=RequestMethod.GET)
	public void describeRing(ModelMap model) throws Exception {
		
		NodeProbe probe = clientProvider.getProbe();
		if (probe == null) {
			// TODO JMX Connection failed
			throw new RuntimeException("JMX Connection failed");
		}
		Set<String> liveNodes = probe.getLiveNodes();
		Set<String> unreachableNodes = probe.getUnreachableNodes();
		Map<String, String> loadMap = probe.getLoadMap();
//		Map<Range, List<String>> rangeMap = probe.getRangeToEndpointMap(null);
//		List<Range> rangeList = new ArrayList<Range>(rangeMap.keySet());
		
		List<Node> nodes = new ArrayList<Node>(liveNodes.size() + unreachableNodes.size());
		for (String addr : liveNodes) {
			Node node = new Node();
			node.up = true;
			node.address = addr;
			node.load = loadMap.get(addr);
			nodes.add(node);
			
			NodeProbe inProbe = clientProvider.getProbe(node.address);
			if (inProbe != null) {
				node.token = inProbe.getToken();
				node.operationMode = inProbe.getOperationMode();
				node.uptime = getUptimeString(inProbe.getUptime());
				node.jmx = true;
				
				MemoryUsage memory = inProbe.getHeapMemoryUsage();
		        
		        node.memoryUsed = String.format("%.2f MB", (double) memory.getUsed() / (1024 * 1024));
		        node.memoryMax  = String.format("%.2f MB", (double) memory.getMax() / (1024 * 1024));
		        node.memoryCommited = String.format("%.2f MB", (double) memory.getCommitted() / (1024 * 1024));
			}
		}
		for (String addr : unreachableNodes) {
			Node node = new Node();
			node.up = false;
			node.address = addr;
			node.load = loadMap.get(addr);
			nodes.add(node);
		}
		
		model.put("nodes", nodes);
		model.put("menu_ring", Boolean.TRUE);

	}

	/**
	 * Show node statistics
	 * 
	 * @param address
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/ring/{address}/", method=RequestMethod.GET)
	public String describeNode(
			@PathVariable("address") String address,
			ModelMap model) {
		
		NodeProbe probe = clientProvider.getProbe(address);
		probe.getColumnFamilyStoreMBeanProxies();
		Iterator<Entry<String, ColumnFamilyStoreMBean>> iterator = probe.getColumnFamilyStoreMBeanProxies();
		Map<String, ColumnFamilyStoreMBean> cfMap = new TreeMap<String, ColumnFamilyStoreMBean>();
		while (iterator.hasNext()) {
			Entry<String, ColumnFamilyStoreMBean> entry = iterator.next();
			String keyspace = entry.getKey();
			String columnFamily = entry.getValue().getColumnFamilyName();
			cfMap.put(keyspace + ":" + columnFamily, entry.getValue());
		}
		
		Iterator<Entry<String, IExecutorMBean>> tpIterator = probe.getThreadPoolMBeanProxies();
		Map<String, IExecutorMBean> tpMap = new TreeMap<String, IExecutorMBean>();
		while (tpIterator.hasNext()) {
			Entry<String, IExecutorMBean> entry = tpIterator.next();
			tpMap.put(entry.getKey(), entry.getValue());
		}
		
		model.addAttribute("cfmap", cfMap);
		model.addAttribute("tpmap", tpMap);
		model.addAttribute("address", address);
		model.addAttribute("menu_ring", true);
		
		return "/ring_node";
	}
	
	/**
	 * Convert long to uptime string
	 * 
	 * @param uptime
	 * @return
	 */
	private String getUptimeString(long uptime) {
		uptime = uptime / 1000L;
		long uptimeSec = uptime % 60L;
		uptime = uptime / 60L;
		long uptimeMin = uptime % 60L;
		uptime = uptime / 60L;
		long uptimeHour = uptime % 24L;
		uptime = uptime / 24L;
		return String.format(
				"%dd %02dh %02dm %02ds", uptime, uptimeHour, uptimeMin, uptimeSec
		);
	}
	
	/**
	 * {@link Node} represents cassandra node info
	 */
	public static class Node {
		// IP Address
		private String address = "";
		// Loaded bytes
		private String load = "";
		// Status
		private boolean up = false;
		// JMX available
		private boolean jmx = false;
		// Token
		private String token = "";
		// Operation
		private String operationMode = "";
		// Memory Usage
		private String memoryUsed = "";
		private String memoryCommited = "";
		private String memoryMax = "";
		// Uptime
		private String uptime = "";
		
		public String getAddress() {
			return (address == null) ? "" : address;
		}
		public String getLoad() {
			return (load == null) ? "" : load;
		}
		public boolean isUp() {
			return up;
		}
		public boolean isJmx() {
			return jmx;
		}
		public String getOperationMode() {
			return operationMode;
		}
		public String getToken() {
			return token;
		}
		public String getUptime() {
			return uptime;
		}
		public String getMemoryCommited() {
			return memoryCommited;
		}
		public String getMemoryMax() {
			return memoryMax;
		}
		public String getMemoryUsed() {
			return memoryUsed;
		}
	}
	
	
}
