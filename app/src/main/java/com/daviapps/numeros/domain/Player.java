package com.daviapps.numeros.domain;

public class Player {
	// id, nickname, passwd, maxScore
	
	private int id;
	private String nickname;
	private String passwd;
	private int maxScore;
	
	public Player(){}
	
	public Player(int id){
		this.id = id;
	}
	
	public Player(String nickname){
		this.nickname = nickname;
	}

	public Player(String nickname, String passwd, int maxScore){
		this.nickname = nickname;
		this.passwd = passwd;
		this.maxScore = maxScore;
	}

	public int getId(){
		return id;
	}

	public void setNickname(String nickname){
		this.nickname = nickname;
	}

	public String getNickname(){
		return nickname;
	}

	public void setPassword(String passwd){
		this.passwd = passwd;
	}

	public String getPassword(){
		return passwd;
	}

	public void setMaxScore(int maxScore){
		this.maxScore = maxScore;
	}

	public int getMaxScore(){
		return maxScore;
	}
	
}
