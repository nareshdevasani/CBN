package com.free.dao;

import java.util.ArrayList;
import java.util.List;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public final class DatabaseInitializer implements DatabaseConstants {
	public static void initialize() {
		Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
		Session session = cluster.connect();

		List<String> queries = new ArrayList<>();
		// create keyspace
		queries.add("CREATE KEYSPACE if not exists freecom WITH replication = {'class':'SimpleStrategy', 'replication_factor':3};");

		// create required tables
		// 1. Instrument table
		queries.add("create table if not exists freecom." + INSTRUMENT_TABLE + " ("
				+ " name text, "
				+ " symbol text, "
				+ " series text, "
				+ " sector text, "
				+ " listingDate text, "
				+ " isin text PRIMARY KEY"
				+ " ) ");


		// 2. Mutualfund portfolio table
		queries.add("create table if not exists freecom." + MF_PORTFOLIO_TABLE + " ("
				+ " name text PRIMARY KEY, "
				+ " portfolioDate timestamp, "
				+ " instruments text "
				+ " ) ");

		for (String query : queries) {
			session.execute(query);
		}

		session.close();
		cluster.close();
	}

}
