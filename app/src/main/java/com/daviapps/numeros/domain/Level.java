package com.daviapps.numeros.domain;

public class Level {
	// Properties
	private int id;
	public boolean charMode;
	public int velocity;

	public Level(int id, boolean charMode, int velocity){
		this.id = id;
		this.charMode = charMode;
		this.velocity = velocity;
	}

	public Level(boolean charMode, int velocity){
		this.charMode = charMode;
		this.velocity = velocity;
	}
	
	public int getId(){ return this.id; }
}
