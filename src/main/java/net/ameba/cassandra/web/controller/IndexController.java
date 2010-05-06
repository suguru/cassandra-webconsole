package net.ameba.cassandra.web.controller;

import net.ameba.cassandra.web.service.CassandraProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Base index controller
 */
@Controller
public class IndexController extends AbstractBaseController {
	
	@Autowired
	private CassandraProperties properties;

	public IndexController() {
	}
	
	@RequestMapping("/index")
	public void index(ModelMap model) {
		if (!properties.hasProperties()) {
			model.addAttribute("setupNeeded", true);
		}
	}

}
