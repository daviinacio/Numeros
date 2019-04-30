package com.daviapps.numeros.database;
import java.util.*;
import java.text.*;

public abstract class DataSet <E> {
	SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
	
	// Abstract methods
	public abstract void insert(E item);
	public abstract void update(E item);
	public abstract void delete(E item);
	public abstract List<E> select(String where, String order);
	
	// Super methods
	public List<E> select(String where){
		return select(where, null);
	}
	
	public E selectById(int id){
		return this.select(String.format("id = %s", id)).get(0);
	}
	
	public int count(String where){
		return this.select(where).size();
	}
	
	public int count(){
		return this.count("1 = 1");
	}
}
