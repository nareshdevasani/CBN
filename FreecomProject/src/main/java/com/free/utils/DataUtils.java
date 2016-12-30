package com.free.utils;

import java.lang.reflect.Modifier;
import java.util.Set;

import org.reflections.Reflections;

import com.free.interfaces.funds.portfolio.PortfolioInitializer;

public class DataUtils {

	public static void initializeAllData() {
		Reflections reflections = new Reflections("com.free");
		Set<Class<? extends PortfolioInitializer>> subTypes = reflections.getSubTypesOf(PortfolioInitializer.class);
		for (Class<? extends PortfolioInitializer> initializer : subTypes) {
			if (!Modifier.isAbstract(initializer.getModifiers())) {
				try {
					initializer.newInstance().initialize();
				} catch (InstantiationException | IllegalAccessException e) {
					System.out.println("Failed to initialize the data for - " + initializer.getName());
				}
			}
		}
		System.out.println("Finished initializing all the data.");
	}

	public static void main(String[] args) {
		DataUtils.initializeAllData();
	}
}
