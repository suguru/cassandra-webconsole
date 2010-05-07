package net.ameba.cassandra.web.service;

import org.springframework.stereotype.Service;

/**
 * Common functions
 * 
 */
@Service
public class CassandraService {

	private static String[] systemKeyspaces = new String[] {"system","definitions"};

	/**
	 * Check the keyspace is system or not.
	 * 
	 * @param keyspaceName
	 * @return
	 */
	public boolean isSystemKeyspace(String keyspaceName) {
		for (int i = 0; i < systemKeyspaces.length; i++) {
			if (systemKeyspaces[i].equals(keyspaceName)) {
				return true;
			}
		}
		return false;
	}
}
