package com.daviapps.numeros.interfaces;

public interface GenericBaseAdapter<E extends Object> {
	E addItem(E e);
	void setItem(E e);
	void removeItem(E e);
	void load();
	void load(String where);
}
