package net.ameba.cassandra.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ameba.cassandra.web.service.CassandraClientProvider;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.tools.NodeProbe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SystemController extends AbstractBaseController {

	@Autowired
	private CassandraClientProvider clientProvider;
	
	@RequestMapping(value="/info", method=RequestMethod.GET)
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
	@RequestMapping(value="/ring", method=RequestMethod.GET)
	public void showRing(ModelMap model) throws Exception {
		
		NodeProbe probe = clientProvider.getProbe();
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
		return uptime + "d " + uptimeHour + "h " + uptimeMin + "m " + uptimeSec + "s";
	}
	
	/**
	 * {@link Node} represents cassandra node info
	 */
	public static class Node {
		private String address;
		private String load;
		private boolean up;
		public String getAddress() {
			return (address == null) ? "" : address;
		}
		public String getLoad() {
			return (load == null) ? "" : load;
		}
		public boolean isUp() {
			return up;
		}
	}
}
