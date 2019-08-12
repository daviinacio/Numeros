package com.daviapps.numeros.domain;
import java.util.*;

public class Game {
	//"id", "player", "status", "level", "hits", "faults", "score", "time", "incr", "decr" , "dstart", "dlast"
	
	//Identity
	private int id = 0, player = 1;
	
	// Score
	private int hits = 0, faults = 0, score = 0;

	// Time
	private int time = EnvironmentGame.TIME_MED, incr = 1, decr = 1;

	// Date
	private Date dateStart, dateLast;
	
	// Status
	private int status = EnvironmentGame.STOPPED;
	private int level;
	
	public Game(){
		this.dateStart = new Date();
		this.dateLast = new Date();
	}
	
	public Game(int player){
		this();
		this.player = player;
	}

	public int getId(){ return id; }

	void setPlayer(int player){ this.player = player; }
	public int getPlayer(){ return player; }

	void setHits(int hits){ this.hits = hits; }
	public int getHits(){ return hits; }

	void setFaults(int faults){ this.faults = faults; }
	public int getFaults(){ return faults; }

	void setScore(int score){ this.score = score; }
	public int getScore(){ return score; }

	void setTime(int time){ this.time = time; }
	public int getTime(){ return time; }

	void setIncrement(int incr){ this.incr = incr; }
	public int getIncrement(){ return incr; }

	void setDecrement(int decr){ this.decr = decr; }
	public int getDecrement(){ return decr; }

	void setDateStart(Date dateStart){ this.dateStart = dateStart; }
	public Date getDateStart(){ return dateStart; }

	void setDateLast(Date dateLast){ this.dateLast = dateLast; }
	public Date getDateLast(){ return dateLast; }

	void setStatus(int status){ this.status = status; }
	public int getStatus(){ return status; }

	void setLevel(int level){ this.level = level;}
	public int getLevel(){ return level; }
	
	public int getAverage(){
		return (((int) (((double) this.getHits() / (this.getHits() + this.getFaults())) * 100)));
	}

	@Override
	public String toString(){
		//"id", "player", "status", "level", "hits", "faults", "score", "time", "incr", "decr" , "dstart", "dlast"
		
		return String.format("id:\t%s\nplayer:\t%s\nstatus:\t%s\nlevel:\t%s\nhits:\t%s\nfaults:\t%s\nscore:\t%s\ntime:\t%s\nincrement:\t%s\ndecrement:\t%s\ndate start:\t%s\ndate last:\t%s\n",
								id, player, status, level, hits, faults, score, time, incr, decr, dateStart, dateLast);
	}
	
	
	public static class Builder {
		private Game game;

		public Builder(){
			this.game = new Game();
		}

		public Builder setId(int id){
			this.game.id = id;
			return this;
		}

		public Builder setPlayer(int playerId){
			this.game.player = playerId;
			return this;
		}

		public Builder setLevel(int levelId){
			this.game.level = levelId;
			return this;
		}

		public Builder setStatus(int status){
			this.game.status = status;
			return this;
		}

		public Builder setHits(int hits){
			this.game.hits = hits;
			return this;
		}

		public Builder setFaults(int faults){
			this.game.faults = faults;
			return this;
		}

		public Builder setScore(int score){
			this.game.score = score;
			return this;
		}

		public Builder setTime(int time){
			this.game.time = time;
			return this;
		}

		public Builder setIncrement(int incr){
			this.game.incr = incr;
			return this;
		}

		public Builder setDecrement(int decr){
			this.game.decr = decr;
			return this;
		}

		public Builder setDateStart(Date dateStart){
			this.game.dateStart = dateStart;
			return this;
		}

		public Builder setDateLast(Date dateLast){
			this.game.dateLast = dateLast;
			return this;
		}

		public Game build(){
			return this.game;
		}
	}
}
