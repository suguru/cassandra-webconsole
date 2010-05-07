package net.ameba.cassandra.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.ameba.cassandra.web.util.ByteArray;

import org.apache.cassandra.thrift.CfDef;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.cassandra.thrift.KeySlice;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.UnavailableException;
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
		if (cassandraService.isSystemKeyspace(keyspaceName)) {
			model.put("system", true);
		}
		
		return "/columnfamily";
	}
	
	@RequestMapping(value="/keyspace/{keyspaceName}/{columnFamilyName}/browse", method=RequestMethod.GET)
	public String browseColumnFamily(
			@PathVariable("keyspaceName") String keyspaceName,
			@PathVariable("columnFamilyName") String columnFamilyName,
			@RequestParam(value="start", defaultValue="") String start,
			@RequestParam(value="count", defaultValue="50") int count,
			ModelMap model) throws Exception {
		
		Client client = clientProvider.getThriftClient();
		// set target keyspace.
		client.set_keyspace(keyspaceName);
		// set target column family
		ColumnParent parent = new ColumnParent(columnFamilyName);
		// create target range
		SliceRange sliceRange = new SliceRange();
		sliceRange.setStart(new byte[0]);
		sliceRange.setFinish(new byte[0]);
		sliceRange.setCount(10);
		
		SlicePredicate slicePredicate = new SlicePredicate();
		slicePredicate.setSlice_range(sliceRange);
		
		KeyRange range = new KeyRange(count + 1);
		range.setStart_key(ByteArray.toBytes(start));
		range.setEnd_key(new byte[0]);
		
		try {
			List<KeySlice> slices = client.get_range_slices(
					parent,
					slicePredicate,
					range,
					ConsistencyLevel.ONE);
			
			model.addAttribute("slices", slices);
		} catch (UnavailableException ex) {
			model.addAttribute("unavailable", true);
		}
		
		// setting names
		model.addAttribute("keyspaceName", keyspaceName);
		model.addAttribute("columnFamilyName", columnFamilyName);
		
		return "/columnfamily_browse";
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
