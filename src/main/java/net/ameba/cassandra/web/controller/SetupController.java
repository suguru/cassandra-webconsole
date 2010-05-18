package net.ameba.cassandra.web.controller;

import net.ameba.cassandra.web.service.CassandraProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The controller for initial setup.
 */
@Controller
public class SetupController extends AbstractBaseController {
	
	@Autowired
	private CassandraProperties properties;

	public SetupController() {
	}
	
	@RequestMapping(value="/setup", method=RequestMethod.GET)
	public void setup(ModelMap model) {
		if (properties.hasProperties()) {
			
			model.put("host", properties.getProperty(CassandraProperties.HOST));
			model.put("jmxPort", properties.getProperty(CassandraProperties.JMX_PORT));
			model.put("thriftPort", properties.getProperty(CassandraProperties.THRIFT_PORT));
			model.put("framedTransport", properties.getProperty(CassandraProperties.FRAMED_TRANSPORT));
			
		} else {
			
			model.put("host", "");
			model.put("jmxPort", 8080);
			model.put("thriftPort", 9160);
			model.put("framedTransport", "false");
			
		}
	}
	
	@RequestMapping(value="/setup", method=RequestMethod.POST)
	public String setupExecute(
			@RequestParam("host") String host,
			@RequestParam("thriftPort") int thriftPort,
			@RequestParam("jmxPort") int jmxPort,
			@RequestParam(value="framedTransport", required=false, defaultValue="false") boolean framedTransport,
			ModelMap model) throws Exception {
		
		properties.setProperty(CassandraProperties.HOST, host);
		properties.setProperty(CassandraProperties.JMX_PORT, String.valueOf(jmxPort));
		properties.setProperty(CassandraProperties.THRIFT_PORT, String.valueOf(thriftPort));
		properties.setProperty(CassandraProperties.FRAMED_TRANSPORT, Boolean.toString(framedTransport));
		properties.saveProperties();
		
		model.clear();
		
		return "redirect:./";
	}

}
