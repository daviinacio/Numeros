package com.daviapps.numeros.domain;

public class _Level {
	// Properties
	private int id;
	public boolean charMode;
	public int velocity;

	public _Level(int id, boolean charMode, int velocity){
		this.id = id;
		this.charMode = charMode;
		this.velocity = velocity;
	}

	public _Level(boolean charMode, int velocity){
		this.charMode = charMode;
		this.velocity = velocity;
	}
	
	public int getId(){ return this.id; }
}
