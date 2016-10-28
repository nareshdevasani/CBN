package com.free.dao;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public final class DatabaseInitializer implements DatabaseConstants {
	public static void initialize() {
		Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
		Session session = cluster.connect();

		// create keyspace
		String query = "CREATE KEYSPACE if not exists freecom WITH replication = {'class':'SimpleStrategy', 'replication_factor':3};";
		session.execute(query);

		// create required tables
		query = "create table if not exists freecom." + INSTRUMENT_TABLE + " ("
				+ " name text, "
				+ " symbol text, "
				+ " series text, "
				+ " sector text, "
				+ " listingDate text, "
				+ " isin text PRIMARY KEY"
				+ " ) ";

		session.execute(query);

		session.close();
		cluster.close();
	}

}
