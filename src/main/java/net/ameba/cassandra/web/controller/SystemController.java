package net.ameba.cassandra.web.controller;

import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import net.ameba.cassandra.web.service.CassandraClientProvider;

import org.apache.cassandra.concurrent.IExecutorMBean;
import org.apache.cassandra.db.ColumnFamilyStoreMBean;
import org.apache.cassandra.dht.Token;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.tools.NodeProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SystemController extends AbstractBaseController {
	
	private static final Logger log = LoggerFactory.getLogger(SystemController.class);

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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/ring/", method=RequestMethod.GET)
	public void describeRing(ModelMap model) throws Exception {
		
		NodeProbe probe = clientProvider.getProbe();
		if (probe == null) {
			// TODO JMX Connection failed
			throw new RuntimeException("JMX Connection failed.");
		}
		
		Set<String> liveNodes = probe.getLiveNodes();
		Set<String> deadNodes = probe.getUnreachableNodes();
		Set<String> joiningNodes = probe.getJoiningNodes();
		Set<String> leavingNodes = probe.getLeavingNodes();
		Map<String, String> loadMap = probe.getLoadMap();
		
		Map<Token, String> endpointMap = probe.getTokenToEndpointMap();
		List<Node> nodes = new ArrayList<Node>(endpointMap.size());
		List<Token> sortedTokens = new ArrayList<Token>(endpointMap.keySet());
		Collections.sort(sortedTokens);

		for (Object token : sortedTokens) {
			
			String primaryEndpoint = endpointMap.get(token);

			Node node = new Node();
			node.address = primaryEndpoint;
			node.token = token.toString();
			node.load = loadMap.get(node.address);
			node.up = 
				liveNodes.contains(primaryEndpoint) ? "up" :
				deadNodes.contains(primaryEndpoint) ? "down" :
                "?";
			
			node.state =
				joiningNodes.contains(primaryEndpoint) ? "Joining" :
				leavingNodes.contains(primaryEndpoint) ? "Leaving" :
				"Normal";
			
			if (node.load == null) {
				node.load = "?";
			}
			nodes.add(node);
			
			NodeProbe inProbe = clientProvider.getProbe(node.address);
			if (inProbe != null) {
				node.operationMode = inProbe.getOperationMode();
				node.uptime = getUptimeString(inProbe.getUptime());
				node.jmx = true;
				
				MemoryUsage memory = inProbe.getHeapMemoryUsage();
		        
		        node.memoryUsed = String.format("%.2f MB", (double) memory.getUsed() / (1024 * 1024));
		        node.memoryMax  = String.format("%.2f MB", (double) memory.getMax() / (1024 * 1024));
		        node.memoryCommited = String.format("%.2f MB", (double) memory.getCommitted() / (1024 * 1024));
			}
		}
		
		// List live nodes which are not in range.
		for (String deadAddress : deadNodes) {
			Node deadNode = new Node();
			deadNode.address = deadAddress;
			deadNode.load = loadMap.get(deadAddress);
			NodeProbe inProbe = clientProvider.getProbe(deadAddress);
			if (inProbe != null) {
				deadNode.operationMode = inProbe.getOperationMode();
				deadNode.uptime = getUptimeString(inProbe.getUptime());
			}
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
		
		model.addAttribute("address", address);
		model.addAttribute("token", probe.getToken());
		model.addAttribute("mode", probe.getOperationMode());
		model.addAttribute("uptime", getUptimeString(probe.getUptime()));
		
		// Column family store
		probe.getColumnFamilyStoreMBeanProxies();
		Iterator<Entry<String, ColumnFamilyStoreMBean>> iterator = probe.getColumnFamilyStoreMBeanProxies();
		
		Map<String, Map<String, ColumnFamilyStoreMBean>> cfparent = new TreeMap<String, Map<String,ColumnFamilyStoreMBean>>();
		
		while (iterator.hasNext()) {
			Entry<String, ColumnFamilyStoreMBean> entry = iterator.next();
			String keyspace = entry.getKey();
			String columnFamily = entry.getValue().getColumnFamilyName();
			
			Map<String, ColumnFamilyStoreMBean> cfmap = cfparent.get(keyspace);
			if (cfmap == null) {
				cfmap = new TreeMap<String, ColumnFamilyStoreMBean>();
				cfparent.put(keyspace, cfmap);
			}
			cfmap.put(columnFamily, entry.getValue());
		}
		
		// Thread pool stats
		Iterator<Entry<String, IExecutorMBean>> tpIterator = probe.getThreadPoolMBeanProxies();
		Map<String, IExecutorMBean> tpMap = new TreeMap<String, IExecutorMBean>();
		while (tpIterator.hasNext()) {
			Entry<String, IExecutorMBean> entry = tpIterator.next();
			tpMap.put(entry.getKey(), entry.getValue());
		}
		
		model.addAttribute("cfparent", cfparent);
		model.addAttribute("tpmap", tpMap);
		model.addAttribute("address", address);
		model.addAttribute("menu_ring", true);
		
		// TODO not implemented yet
//		model.addAttribute("streamDestinations", probe.getStreamDestinations());
//		model.addAttribute("streamSources", probe.getStreamSources());
		model.addAttribute("currentGenerationNumber", probe.getCurrentGenerationNumber());
		
		/*
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bout);
		probe.getCompactionThreshold(ps, null, null);
		ps.flush();
		String compactionThreshold = new String(bout.toByteArray());
		if (compactionThreshold.startsWith("Current compaction threshold: ")) {
			compactionThreshold = compactionThreshold.substring(30);
		}
		model.addAttribute("compactionThreshold", compactionThreshold);
		*/
		
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
		private String up = "?";
		// State
		private String state = "";
		// JMX available
		private boolean jmx = false;
		// Token
		private String token = null;
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
		public String getUp() {
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
		public String getState() {
			return state;
		}
	}
	
	/**
	 * Prepare executing control
	 * 
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value="/ring/{address}/{control}", method=RequestMethod.GET)
	public String prepareControl(
			@PathVariable("address") String address,
			@PathVariable("control") String control,
			ModelMap model) throws Exception {
		model.put("address", address);
		model.put("control", control);
		return "/ring_" + control;
	}
	
	/**
	 * Prepare executing column family control
	 * 
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value="/ring/{address}/{keyspace}/{columnFamily}/{control}", method=RequestMethod.GET)
	public String prepareControl(
			@PathVariable("address") String address,
			@PathVariable("control") String control,
			@PathVariable("keyspace") String keyspace,
			@PathVariable("columnFamily") String columnFamily,
			ModelMap model) throws Exception {
		model.put("address", address);
		model.put("control", control);
		model.put("keyspace", keyspace);
		model.put("columnFamily", columnFamily);
		return "/ring_" + control;
	}
	
	/**
	 * Execute loadbalance 
	 * 
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value="/ring/{address}/loadbalance", method=RequestMethod.POST)
	public String loadbalance(
			@PathVariable("address") final String address,
			ModelMap model) throws Exception {
		cassandraService.scheduleExecution(new Runnable() {
			@Override
			public void run() {
				NodeProbe probe = clientProvider.getProbe(address);
				if (probe != null) {
					try {
						probe.loadBalance();
					} catch (Exception e) {
						log.error("Failed to loadbalance " + address, e);
					}
				}
			}
		});
		model.clear();
		return "redirect:/ring/";
	}
	
	/**
	 * Execute cleanup 
	 * 
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value="/ring/{address}/cleanup", method=RequestMethod.POST)
	public String cleanup(
			@PathVariable("address") final String address,
			ModelMap model) throws Exception {
		cassandraService.scheduleExecution(new Runnable() {
			@Override
			public void run() {
				NodeProbe probe = clientProvider.getProbe(address);
				if (probe != null) {
					try {
						probe.forceTableCleanup();
					} catch (Exception e) {
						log.error("Failed to cleanup " + address, e);
					}
				}
			}
		});
		model.clear();
		return "redirect:/ring/" + address + "/";
	}

	/**
	 * Execute compact 
	 * 
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value="/ring/{address}/compact", method=RequestMethod.POST)
	public String compact(
			@PathVariable("address") final String address,
			ModelMap model) throws Exception {
		cassandraService.scheduleExecution(new Runnable() {
			@Override
			public void run() {
				NodeProbe probe = clientProvider.getProbe(address);
				if (probe != null) {
					try {
						probe.forceTableCompaction();
					} catch (Exception e) {
						log.error("Failed force table compaction " + address, e);
					}
				}
			}
		});
		model.clear();
		return "redirect:/ring/" + address + "/";
	}

	/**
	 * Execute drain 
	 * 
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value="/ring/{address}/drain", method=RequestMethod.POST)
	public String drain(
			@PathVariable("address") final String address,
			ModelMap model) throws Exception {
		cassandraService.scheduleExecution(new Runnable() {
			@Override
			public void run() {
				NodeProbe probe = clientProvider.getProbe(address);
				if (probe != null) {
					try {
						probe.drain();
					} catch (Exception e) {
						log.error("Failed to drain " + address, e);
					}
				}
			}
		});
		model.clear();
		return "redirect:./";
	}
	
	/**
	 * Execute decomission 
	 * 
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value="/ring/{address}/decomission", method=RequestMethod.POST)
	public String decomission(
			@PathVariable("address") final String address,
			ModelMap model) throws Exception {
		cassandraService.scheduleExecution(new Runnable() {
			@Override
			public void run() {
				NodeProbe probe = clientProvider.getProbe(address);
				if (probe != null) {
					try {
						probe.decommission();
					} catch (Exception e) {
						log.error("Failed to decomission " + address, e);
					}
				}
			}
		});
		model.clear();
		return "redirect:/" + address + "/";
	}

	/**
	 * Execute drain 
	 *
	 * @param token Token
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value="/ring/{address}/move", method=RequestMethod.POST)
	public String move(
			@PathVariable("address") final String address,
			@RequestParam("token") final String token,
			ModelMap model) throws Exception {
		cassandraService.scheduleExecution(new Runnable() {
			@Override
			public void run() {
				NodeProbe probe = clientProvider.getProbe(address);
				if (probe != null) {
					try {
						probe.move(token);
					} catch (Exception e) {
						log.error("Failed to move " + token, e);
					}
				}
			}
		});
		model.clear();
		return "redirect:/ring/" + address + "/";
	}
	
	/**
	 * Execute flush
	 * @param address
	 * @param token
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/ring/{address}/{keyspace}/{columnFamily}/flush", method=RequestMethod.POST)
	public String flush(
			@PathVariable("address") final String address,
			@PathVariable("keyspace") final String keyspace,
			@PathVariable("columnFamily") final String columnFamily,
			ModelMap model) throws Exception {
		final String[] columnFamilies = columnFamily.split(",");
		cassandraService.scheduleExecution(new Runnable() {
			@Override
			public void run() {
				NodeProbe probe = clientProvider.getProbe(address);
				if (probe != null) {
					try {
						probe.forceTableFlush(keyspace, columnFamilies);
					} catch (Exception e) {
						log.error("Failed to flush column families " + keyspace + ":" + columnFamilies, e);
					}
				}
			}
		});
		model.clear();
		return "redirect:/ring/" + address + "/";
	}
	
	/**
	 * Execute repair
	 * @param address
	 * @param token
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/ring/{address}/{keyspace}/{columnFamily}/repair", method=RequestMethod.POST)
	public String repair(
			@PathVariable("address") final String address,
			@PathVariable("keyspace") final String keyspace,
			@PathVariable("columnFamily") final String columnFamily,
			ModelMap model) throws Exception {
		final String[] columnFamilies = columnFamily.split(",");
		cassandraService.scheduleExecution(new Runnable() {
			@Override
			public void run() {
				NodeProbe probe = clientProvider.getProbe(address);
				if (probe != null) {
					try {
						probe.forceTableRepair(keyspace, columnFamilies);
					} catch (Exception e) {
						log.error("Failed to repair column families " + keyspace + ":" + columnFamilies, e);
					}
				}
			}
		});
		model.clear();
		return "redirect:/ring/" + address + "/";
	}
	
	/**
	 * Prepare for removing the token
	 *
	 * @param token Token
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value="/token/remove", method=RequestMethod.GET)
	public String removeToken(ModelMap model) throws Exception {
		return "/token_remove";
	}
	
	/**
	 * Remove the token
	 *
	 * @param token Token
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping(value="/token/remove", method=RequestMethod.POST)
	public String removeTokenExecute(
			@RequestParam("token") final String token,
			ModelMap model) throws Exception {
		cassandraService.scheduleExecution(new Runnable() {
			@Override
			public void run() {
				NodeProbe probe = clientProvider.getProbe();
				if (probe != null) {
					try {
						probe.removeToken(token);
					} catch (Exception e) {
						log.error("Failed to remove token " + token, e);
					}
				}
			}
		});
		model.clear();
		return "redirect:/ring/";
	}
	
}
