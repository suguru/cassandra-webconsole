package net.ameba.cassandra.web.standalone;

import java.io.File;
import java.net.URI;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Standalone server of cassandra web console.
 * 
 * @author suguru
 */
public class StandaloneServer {
	
	// bind port
	private int port = 8080;
	
	// jetty server
	private Server server;
	
	// war base path
	private File basePath;
	
	/**
	 * Initialize server.
	 * @param port
	 */
	public StandaloneServer(int port) {
		this.port = port;
	}
	
	/**
	 * Set the base path of the web application.
	 * @param basePath
	 */
	public void setBasePath(String basePath) {
		this.basePath = new File(basePath);
	}
	
	/**
	 * Start the server.
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		
		server = new Server(port);
		
		WebAppContext context = new WebAppContext();
		context.setContextPath("/");
		
		File file = new File(basePath, "webapp");
		URI uri = file.toURI();
		
		context.setWar(uri.toString());
		
		server.setHandlers(new Handler[] {
				context,
				new DefaultHandler()
		});
		
		server.start();
		
	}
	
	/**
	 * Shutdown the server
	 * 
	 * @throws Exception
	 */
	public void shutdown() throws Exception {
		server.stop();
	}
	
	public static void main(String[] args) {
		
		Options options = new Options();
		options.addOption("p", "port", true, "bind port");
		options.addOption("b", "base", true, "base path");
		
		try {
			CommandLine cl = new PosixParser().parse(options, args);
			
			int port = 8080;
			if (cl.hasOption('p')) {
				port = Integer.parseInt(cl.getOptionValue('p'));
			}
			String basePath = "src/main";
			if (cl.hasOption('b')) {
				basePath = cl.getOptionValue('b');
			}
			
			StandaloneServer server = new StandaloneServer(port);
			server.setBasePath(basePath);
			server.start();
			
		} catch (ParseException ex) {
			System.out.println(ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

}
