package com.free.interfaces.dao;

/**
 * @author nareshd
 *
 */
public interface CRUD <T extends DataObject> {

	T create(T object);
	T modify(T object);
	T delete(String isin);
	T get(String isin);
}
