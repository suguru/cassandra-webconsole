package net.ameba.cassandra.web.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.springframework.stereotype.Component;

/**
 * {@link CassandraProperties} provides per user properties.
 * 
 * @author suguru
 *
 */
@Component
public class CassandraProperties {
	
	public static final String HOST = "cassandra.host";
	public static final String JMX_PORT = "cassandra.jmx.port";
	public static final String THRIFT_PORT = "cassandra.thrift.port";
	public static final String FRAMED_TRANSPORT = "cassandra.framedTransport";
	
	private String fileName = ".cassandra-webconsole";
	
	private Properties properties;

	public CassandraProperties() throws IOException {
		loadProperties();
	}
	
	/**
	 * Load properties from the file.
	 * 
	 * @throws IOException
	 */
	public void loadProperties() throws IOException {
		
		File userPropertyFile = getPropertiesFile();
		
		if (userPropertyFile.exists()) {
			properties = new Properties();
			FileInputStream fis = new FileInputStream(userPropertyFile);
			try {
				properties.load(fis);
			} finally {
				fis.close();
			}
		}

	}
	
	/**
	 * Get property value.
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		if (properties == null) {
			return null;
		} else {
			return properties.getProperty(key);
		}
	}
	
	/**
	 * Set property key and value.
	 * @param key
	 * @param value
	 */
	public synchronized void setProperty(String key, String value) {
		if (properties == null) {
			properties = new Properties();
		}
		properties.setProperty(key, value);
	}
	
	/**
	 * Save properties to the file.
	 * @throws IOException
	 */
	public void saveProperties() throws IOException {
		File userPropertyFile = getPropertiesFile();
		FileOutputStream fos = new FileOutputStream(userPropertyFile);
		try {
			properties.store(fos, "Cassandra WebConsole User Properties");
		} finally {
			fos.close();
		}
	}
	
	/**
	 * Check whether properties are loaded.
	 * @return
	 */
	public boolean hasProperties() {
		return properties != null;
	}
	
	/**
	 * Get the property file related to current user.
	 * @return
	 */
	private File getPropertiesFile() {
		return new File(
				System.getProperty("user.home"),
				fileName
		);
	}
}
