package net.ameba.cassandra.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.CfDef;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The controller supports functions to manage column families.
 */
@Controller
public class ColumnFamilyController extends AbstractBaseController {

	@RequestMapping(value="/keyspace/{name}/addcf", method=RequestMethod.GET)
	public String addColumnFamily() {
		return "/keyspace_addcf";
	}
	
	@RequestMapping(value="/keyspace/{name}/addcf", method=RequestMethod.POST)
	public String addColumnFamilyExecute(
			@PathVariable("name") String keyspaceName,
			@RequestParam("name") String name,
			@RequestParam("type") String type,
			@RequestParam("comparator") String comparator,
			@RequestParam("subComparator") String subComparator,
			@RequestParam("comment") String comment,
			ModelMap model) throws Exception {
		
		Client client = clientProvider.getThriftClient();
		
		// trim name
		name = name.trim();
		if (name.length() == 0) {
			throw new IllegalArgumentException("CFName must not be empty");
		}
		// clear sub comparator if the type is Standard.
		if ("Standard".equals(type)) {
			subComparator = "";
		}
		
		// creating definition.
		CfDef cfdef = new CfDef();
		cfdef.setName(name);
		cfdef.setComment(comment);
		cfdef.setColumn_type(type);
		cfdef.setComparator_type(comparator);
		cfdef.setSubcomparator_type(subComparator);
		cfdef.setTable(keyspaceName);
		
		client.system_add_column_family(cfdef);
		
		// redirecting to keyspace page.
		model.clear();
		return "redirect:./";
	}
	
	@RequestMapping(value="/keyspace/{keyspaceName}/{columnFamilyName}/", method=RequestMethod.GET)
	public String describeColumnFamily(
			@PathVariable("keyspaceName") String keyspaceName,
			@PathVariable("columnFamilyName") String columnFamilyName,
			ModelMap model) throws Exception {
		
		Client client = clientProvider.getThriftClient();
		
		// Extracting list of column families
		Map<String, Map<String, String>> descriptionMap = client.describe_keyspace(keyspaceName);
		List<String> columnFamilies = new ArrayList<String>(descriptionMap.size());
		columnFamilies.addAll(descriptionMap.keySet());
		Collections.sort(columnFamilies);
		model.put("columnFamilies", columnFamilies);
		
		// setting names
		model.addAttribute("keyspaceName", keyspaceName);
		model.addAttribute("columnFamilyName", columnFamilyName);
		
		return "/columnfamily";
	}
	
	@RequestMapping(value="/keyspace/{keyspaceName}/{columnFamilyName}/rename", method=RequestMethod.GET)
	public String renameColumnFamily(
			@PathVariable("keyspaceName") String keyspaceName,
			@PathVariable("columnFamilyName") String columnFamilyName,
			ModelMap model) throws Exception {
		
		model.addAttribute("keyspaceName", keyspaceName);
		model.addAttribute("columnFamilyName", columnFamilyName);
		
		return "/columnfamily_rename";
	}
	
	@RequestMapping(value="/keyspace/{keyspaceName}/{columnFamilyName}/rename", method=RequestMethod.POST)
	public String renameColumnFamilyExecute(
			@PathVariable("keyspaceName") String keyspaceName,
			@PathVariable("columnFamilyName") String originalName,
			@RequestParam("name") String columnFamilyName,
			ModelMap model) throws Exception {
		
		Client client = clientProvider.getThriftClient();
		client.system_rename_column_family(
				keyspaceName,
				originalName,
				columnFamilyName
		);
		model.clear();
		return "redirect:../" + columnFamilyName + "/";
		
	}
			
	@RequestMapping(value="/keyspace/{keyspaceName}/{columnFamilyName}/drop", method=RequestMethod.GET)
	public String dropColumnFamily(
			@PathVariable("keyspaceName") String keyspaceName,
			@PathVariable("columnFamilyName") String columnFamilyName,
			ModelMap model) throws Exception {
		
		model.addAttribute("keyspaceName", keyspaceName);
		model.addAttribute("columnFamilyName", columnFamilyName);
		
		return "/columnfamily_drop";
	}
	
	@RequestMapping(value="/keyspace/{keyspaceName}/{columnFamilyName}/drop", method=RequestMethod.POST)
	public String dropColumnFamilyExecute(
			@PathVariable("keyspaceName") String keyspaceName,
			@PathVariable("columnFamilyName") String columnFamilyName,
			ModelMap model) throws Exception {
		
		Client client = clientProvider.getThriftClient();
		client.system_drop_column_family(
				keyspaceName,
				columnFamilyName
		);
		model.clear();
		return "redirect:../";
		
	}
}
