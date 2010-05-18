package net.ameba.cassandra.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ameba.cassandra.web.service.CassandraClientProvider;

import org.apache.cassandra.locator.RackAwareStrategy;
import org.apache.cassandra.locator.RackUnawareStrategy;
import org.apache.cassandra.thrift.CfDef;
import org.apache.cassandra.thrift.KsDef;
import org.apache.cassandra.thrift.TokenRange;
import org.apache.cassandra.thrift.Cassandra.Client;
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
				// Get list of keyspaces
				Set<String> keyspaceSet = client.describe_keyspaces();
				List<String> keyspaceList = new ArrayList<String>(keyspaceSet);
				Collections.sort(keyspaceList);
				model.addAttribute("keyspaces", client.describe_keyspaces());
				
				// Get list of column families from active keyspace.
				if (activeKeyspace.length() > 0) {
					Map<String, Map<String, String>> cfmap = client.describe_keyspace(activeKeyspace);
					List<String> cfList = new ArrayList<String>(cfmap.keySet());
					Collections.sort(cfList);
					model.addAttribute("columnFamilies", cfList);
				}
				
			} else {
				model.addAttribute("keyspaces", new ArrayList<String>());
			}
		} catch (TException ex) {
			model.addAttribute("keyspaces", new ArrayList<String>());
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
		Map<String, Map<String, String>> descriptionMap = client.describe_keyspace(keyspaceName);
		model.put("describeMap", descriptionMap);
		model.put("keyspaceName", keyspaceName);
		
		// Extracting list of column families
		List<String> columnFamilies = new ArrayList<String>(descriptionMap.size());
		columnFamilies.addAll(descriptionMap.keySet());
		Collections.sort(columnFamilies);
		model.put("columnFamilies", columnFamilies);
		
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
		
		KsDef ksDef = new KsDef();
		ksDef.setName(name);
		ksDef.setReplication_factor(replicationFactor);
		
		Class<?> strategyClass = null;
		if (strategy.equals("Rackaware")) {
			strategyClass = RackAwareStrategy.class;
		} else {
			strategyClass = RackUnawareStrategy.class;
		}
		ksDef.setStrategy_class(strategyClass.getName());
		ksDef.setCf_defs(new ArrayList<CfDef>());
		
		client.system_add_keyspace(ksDef);
		
		model.clear();
		return "redirect:./" + name + "/";
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
		return "redirect:../" + name + "/";
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
		return "redirect:../../";
	}

}
