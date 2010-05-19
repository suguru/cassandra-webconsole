package net.ameba.cassandra.web.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

/**
 * Common functions
 * 
 */
@Service
public class CassandraService {

	private static String[] systemKeyspaces = new String[] {"system","definitions"};
	
	// background executor
	private ExecutorService backgroundExecutor;
	
	public CassandraService() {
		backgroundExecutor = Executors.newCachedThreadPool(new ThreadFactory() {
			private AtomicInteger idCounter = new AtomicInteger();
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setName("CassandraWebConsoleExecutor-" + idCounter.incrementAndGet());
				thread.setDaemon(true);
				return thread;
			}
		});
	}

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
	
	/**
	 * Schedule background execution
	 * 
	 * @param runnable
	 */
	public void scheduleExecution(Runnable runnable) {
		backgroundExecutor.submit(runnable);
	}
}
