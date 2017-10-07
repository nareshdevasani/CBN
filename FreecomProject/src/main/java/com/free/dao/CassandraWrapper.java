package com.free.dao;

import java.util.HashMap;
import java.util.Map;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;

public class CassandraWrapper {

	private static Cluster cluster = null;
	private static Session session = null;
	private static Map<String, PreparedStatement> stmtCache = null;
	static {
		DatabaseInitializer.initialize();

		cluster = Cluster.builder().addContactPoint("localhost").build();
		session = cluster.connect("freecom");
		stmtCache = new HashMap<>();
	}

	public static ResultSet executeQueryWithParams(String query, Object... args) {
		PreparedStatement pStatement = stmtCache.get(query);
		if (null == pStatement) {
			pStatement = session.prepare(query);
			stmtCache.put(query, pStatement);
		}
		Statement statement = pStatement.bind(args);

		return session.execute(statement);
	}

	public static ResultSet executeQuery(String query) {
		return session.execute(query);
	}
}
