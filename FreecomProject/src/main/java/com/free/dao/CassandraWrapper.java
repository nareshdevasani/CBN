package com.free.dao;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;

public class CassandraWrapper {

	private static Cluster cluster = null;
	private static Session session = null;
	static {
		DatabaseInitializer.initialize();

		cluster = Cluster.builder().addContactPoint("localhost").build();
		session = cluster.connect("freecom");
	}

	public static ResultSet getResultSet(String query, Object... args) {
		PreparedStatement pStatement = session.prepare(query);
		Statement statement = pStatement.bind(args);

		return session.execute(statement);
	}

	public static ResultSet executeQuery(String query) {
		return session.execute(query);
	}
}
