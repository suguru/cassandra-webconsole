package net.ameba.cassandra.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ameba.cassandra.web.service.CassandraClientProvider;

import org.apache.cassandra.locator.NetworkTopologyStrategy;
import org.apache.cassandra.locator.OldNetworkTopologyStrategy;
import org.apache.cassandra.locator.SimpleStrategy;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.CfDef;
import org.apache.cassandra.thrift.KsDef;
import org.apache.cassandra.thrift.TokenRange;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * {@link KeyspaceController} supports functions to manage keyspaces.
 */
@Controller
public class KeyspaceController extends AbstractBaseController {
	
	@Autowired
	private CassandraClientProvider clientProvider;
	
	@RequestMapping("/keyspaces")
	public String listKeyspaces(
			@RequestParam("keyspace") String activeKeyspace,
			@RequestParam("columnFamily") String activeColumnFamily,
			ModelMap model) throws Exception {
		
		try {
			Client client = clientProvider.getThriftClient();
			if (client != null) {
				//Collections.sort(keyspaceSet);
				model.addAttribute("keyspaces", client.describe_keyspaces());
				
				// Get list of column families from active keyspace.
				if (activeKeyspace.length() > 0) {
					KsDef ksdef = client.describe_keyspace(activeKeyspace);
					List<CfDef> cfList = new ArrayList<CfDef>(ksdef.getCf_defs());
					Collections.sort(cfList);
					model.addAttribute("columnFamilies", cfList);
				}
				
			} else {
				model.addAttribute("keyspaces", new ArrayList<KsDef>());
			}
		} catch (TException ex) {
			model.addAttribute("keyspaces", new ArrayList<KsDef>());
		}
		
		model.addAttribute("activeKeyspace", activeKeyspace);
		model.addAttribute("activeColumnFamily", activeColumnFamily);
		
		return "/keyspace_list";
	}
	
	@RequestMapping(value="/keyspace/{name}/", method=RequestMethod.GET)
	public String describeKeyspace(
			@PathVariable("name") String keyspaceName,
			ModelMap model) throws Exception {
		
		Client client = clientProvider.getThriftClient();
		
		// Getting keyspace information
		KsDef ksDef = client.describe_keyspace(keyspaceName);
		model.put("keyspace", ksDef);
		model.put("keyspaceName", keyspaceName);
		
		// Check the system keyspace
		boolean isSystem = cassandraService.isSystemKeyspace(keyspaceName);
		if (isSystem) {
			model.put("system", true);
		} else {
			List<TokenRange> tokenRange = client.describe_ring(keyspaceName);
			Collections.sort(tokenRange);
			model.put("tokenRanges", tokenRange);
		}
		
		return "/keyspace";
	}
	
	@RequestMapping(value="/keyspace/create", method=RequestMethod.GET)
	public String createKeyspace(ModelMap model) {
		return "/keyspace_create";
	}
	
	@RequestMapping(value="/keyspace/create", method=RequestMethod.POST)
	public String createKeyspaceExecute(
			@RequestParam("name") String name,
			@RequestParam("replicationFactor") int replicationFactor,
			@RequestParam("strategy") String strategy,
			ModelMap model
			) throws Exception {
		
		Client client = clientProvider.getThriftClient();
		
		name = name.trim();
		if (name.length() == 0) {
			throw new IllegalArgumentException("Name must not be empty");
		}
		
		Class<?> strategyClass = null;
		if (strategy.equals("Simple")) {
			strategyClass = SimpleStrategy.class;
		} else if (strategy.equals("NetworkTopology")) {
			strategyClass = NetworkTopologyStrategy.class;
		} else if (strategy.equals("OldNetworkTopology")) {
			strategyClass = OldNetworkTopologyStrategy.class;
		}
		
		KsDef ksDef = new KsDef(name, strategyClass.getName(), replicationFactor, new ArrayList<CfDef>());
		client.system_add_keyspace(ksDef);
		
		model.clear();
		return "redirect:/keyspace/" + name + "/";
	}

	@RequestMapping(value="/keyspace/{name}/rename", method=RequestMethod.GET)
	public String renameKeyspace(
			@PathVariable("name") String keyspaceName,
			ModelMap model) {
		model.addAttribute("name", keyspaceName);
		return "/keyspace_rename";
	}
	
	@RequestMapping(value="/keyspace/{name}/rename", method=RequestMethod.POST)
	public String renameKeyspaceExecute(
			@PathVariable("name") String originalName,
			@RequestParam("name") String name,
			ModelMap model
			) throws Exception {
		
		Client client = clientProvider.getThriftClient();
		
		name = name.trim();
		if (name.length() == 0) {
			throw new IllegalArgumentException("Name must not be empty");
		}

		client.set_keyspace(originalName);
		client.system_rename_keyspace(originalName, name);
		
		model.clear();
		return "redirect:/keyspace/" + name + "/";
	}

	@RequestMapping(value="/keyspace/{name}/drop", method=RequestMethod.GET)
	public String dropKeyspace(
			@PathVariable("name") String keyspaceName,
			ModelMap model) {
		model.addAttribute("name", keyspaceName);
		return "/keyspace_drop";
	}
	
	@RequestMapping(value="/keyspace/{name}/drop", method=RequestMethod.POST)
	public String dropKeyspaceExecute(
			@PathVariable("name") String keyspaceName,
			ModelMap model
			) throws Exception {
		
		Client client = clientProvider.getThriftClient();

		client.set_keyspace(keyspaceName);;
		client.system_drop_keyspace(keyspaceName);
		
		model.clear();
		return "redirect:/";
	}

}
