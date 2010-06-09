package net.ameba.cassandra.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.ameba.cassandra.web.util.ByteArray;

import org.apache.cassandra.thrift.CfDef;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.cassandra.thrift.KeySlice;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.commons.codec.binary.Hex;
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
			@RequestParam("keyCache") double keyCache,
			@RequestParam("rowCache") double rowCache,
			@RequestParam(value="preloadRowCache", required=false, defaultValue="false") boolean preloadRowCache,
			ModelMap model) throws Exception {
		
		Client client = clientProvider.getThriftClient();
		client.set_keyspace(keyspaceName);
		
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
		cfdef.setKey_cache_size(keyCache);
		cfdef.setRow_cache_size(rowCache);
		cfdef.setPreload_row_cache(preloadRowCache);
		
		client.system_add_column_family(cfdef);
		
		// redirecting to keyspace page.
		model.clear();
		return "redirect:./" + name + "/";
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
			@RequestParam(value="end", defaultValue="") String end,
			@RequestParam(value="count", defaultValue="50") int count,
			@RequestParam(value="columnCount", defaultValue="5") int columnCount,
			@RequestParam(value="encode", defaultValue="hex") String encode,
			ModelMap model) throws Exception {
		
		model.addAttribute("count", count);
		model.addAttribute("encode", encode);
		
		Client client = clientProvider.getThriftClient();
		// set target keyspace.
		client.set_keyspace(keyspaceName);
		// set target column family
		ColumnParent parent = new ColumnParent(columnFamilyName);
		// create target range
		SliceRange sliceRange = new SliceRange();
		sliceRange.setStart(new byte[0]);
		sliceRange.setFinish(new byte[0]);
		sliceRange.setCount(columnCount + 1);
		
		
		SlicePredicate slicePredicate = new SlicePredicate();
		slicePredicate.setSlice_range(sliceRange);
		
		// Set row range from request
		KeyRange range = new KeyRange(count + 1);
		if (start.length() > 0) {
			range.setStart_key(Hex.decodeHex(start.toCharArray()));
		} else {
			range.setStart_key(new byte[0]);
		}
		if (end.length() > 0) {
			range.setEnd_key(Hex.decodeHex(end.toCharArray()));
		} else {
			range.setEnd_key(new byte[0]);
		}
		
		try {
			// getting slice
			List<KeySlice> slices = client.get_range_slices(
					parent,
					slicePredicate,
					range,
					ConsistencyLevel.ONE);
			
			KeySliceType[] types = new KeySliceType[slices.size()];
			for (int i = 0; i < types.length; i++) {
				KeySlice keySlice = slices.get(i);
				KeySliceType type = new KeySliceType();
				type.key = ByteArray.toUTF(keySlice.getKey());
				type.keyHex = new String(Hex.encodeHex(keySlice.getKey()));
				int clen = Math.min(columnCount, keySlice.getColumnsSize());
				type.columns = new String[clen];
				for (int j = 0; j < clen; j++) {
					ColumnOrSuperColumn cos = keySlice.columns.get(j);
					if (cos.isSetColumn()) {
						type.columns[j] = new String(Hex.encodeHex(cos.column.name));
					} else if (cos.isSetSuper_column()) {
						type.columns[j] = new String(Hex.encodeHex(cos.super_column.name));
					} else {
						type.columns[j] = "Unknown";
					}
				}
				if (keySlice.getColumnsSize() > columnCount) {
					type.hasMoreColumn = true;
				}
				types[i] = type;
			}
			
			model.addAttribute("slices", types);
			
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
		client.set_keyspace(keyspaceName);
		client.system_rename_column_family(
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
		client.set_keyspace(keyspaceName);
		client.system_drop_column_family(
				columnFamilyName
		);
		model.clear();
		return "redirect:../";
		
	}
	
	@RequestMapping(value="/keyspace/{keyspaceName}/{columnFamilyName}/truncate", method=RequestMethod.GET)
	public String truncateColyumnFamily(
			@PathVariable("keyspaceName") String keyspaceName,
			@PathVariable("columnFamilyName") String columnFamilyName,
			ModelMap model) throws Exception {
		
		model.addAttribute("keyspaceName", keyspaceName);
		model.addAttribute("columnFamilyName", columnFamilyName);
		
		return "/columnfamily_truncate";
	}
	
	@RequestMapping(value="/keyspace/{keyspaceName}/{columnFamilyName}/truncate", method=RequestMethod.POST)
	public String truncateColumnFamilyExecute(
			@PathVariable("keyspaceName") String keyspaceName,
			@PathVariable("columnFamilyName") String columnFamilyName,
			ModelMap model) throws Exception {
		
		Client client = clientProvider.getThriftClient();
		client.set_keyspace(keyspaceName);
		client.truncate(
				columnFamilyName
		);
		model.clear();
		return "redirect:./";
		
	}
	
	public static class KeySliceType {
		private String key;
		private String keyHex;
		private String[] columns;
		private boolean hasMoreColumn = false;
		public String getKey() {
			return key;
		}
		public String[] getColumns() {
			return columns;
		}
		public String getKeyHex() {
			return keyHex;
		}
		public boolean isHasMoreColumn() {
			return hasMoreColumn;
		}
	}
}

