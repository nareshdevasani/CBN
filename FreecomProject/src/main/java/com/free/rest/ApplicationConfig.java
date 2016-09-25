package com.free.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath ("apis")
public class ApplicationConfig extends Application {
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> restClasses = new HashSet<>();
		addRestClasses(restClasses);
		return restClasses;
	}

	private void addRestClasses(Set<Class<?>> restClasses) {
		restClasses.add(com.free.beans.FundManager.class);
	}
}
