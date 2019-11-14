package com.daviapps.numeros.domain;

public class Level {
	private int level = 1;
	private float speed = 1; 		// 1x
	private boolean mode = false;	// char
	
	private OnLevelUpListener onLevelUpListener;
	
	public float getSpeed(){
		return this.speed;
	}
	
	public int getLevel(){
		return level;
	}
	
	public boolean isChar(){
		return mode;
	}
	
	public Level next(){
		Level next = newInstance(this.level + 1);
		
		handleOnLevelUp(this, next);
		
		return next;
	}
	
	private void handleOnLevelUp(Level older, Level newer){
		if(this.onLevelUpListener != null)
			this.onLevelUpListener.onLevelUp(older, newer);
	}
	
	public static Level newInstance(int level){	
		Level l = new Level();
		
		if(level <= 0)
			return l;
		
		l.level = level;
			
		// Plus 0.1x per level
		l.speed += (level / 10);
		
		// Number each 4 levels
		l.mode = level % 4 == 0;
		
		return l;
	}
	
	public interface OnLevelUpListener {
		void onLevelUp(Level older, Level newer);
	}
}
