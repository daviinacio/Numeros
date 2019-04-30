package com.daviapps.numeros.domain;
import java.util.*;
import android.app.*;
import android.content.*;
import com.daviapps.numeros.database.*;

public class EnviromentGame {
	// id, player, status, level, hits, faults, score, time, incr, decr 
	
	// Statics
	public static final int TIME_MIN = 0, TIME_MAX = 200;
	public static final int TIME_MED = ((TIME_MIN + TIME_MAX) / 2);
	private static final int TIMER_DELAY = 250;
	
	// Static status
	public static final int RUNNING = 1010, PAUSED = 1011, STOPPED = 1012;
	
	// Identity
	private Game game;
	private Player player;
	private Level level;

	private boolean run;
	
	// Listener
	private EventListener eventListener;
	
	// Timers
	private Timer timer;

	// Constructors
	public EnviromentGame(final Activity activity){
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {         
			@Override
			public void run() {
				activity.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						if(run){
							if(EnviromentGame.this.game == null)
								return;
							
							if(EnviromentGame.this.game.getStatus() == EnviromentGame.RUNNING){
								if(EnviromentGame.this.game.getTime() > EnviromentGame.TIME_MIN){
									game.setTime(game.getTime() - ((game.getDecrement() > 1 ? game.getDecrement() : 1) * level.velocity));
									if(EnviromentGame.this.game.getTime() <= EnviromentGame.TIME_MIN)
										EnviromentGame.this.stop();
									
									/*time -= (decr > 1 ? decr : 1) * level.velocity;
									if(time <= EnviromentGame.TIME_MIN)
										lose();*/
								}
							}
							if(EnviromentGame.this.eventListener != null)
								EnviromentGame.this.eventListener.onVisualUpdate(EnviromentGame.this.game);
						}
					}
				});
			}
		}, TIMER_DELAY, TIMER_DELAY);
	}
	
	public void sleep(){
		this.run = false;
	}
	
	public void wakeUp(){
		this.run = true;
	}
	
	public void finish(){
		this.timer.cancel();
	}
	
	// Game Methods
	
	public void setup(Game game){
		this.game = game;
			
		if(this.eventListener != null)
			this.eventListener.onGameSetup(this.game);
			
		if(this.eventListener != null)
			this.eventListener.onVisualUpdate(this.game);
	}
	
	public void start(){
		if(this.game == null)
			return;
		
		this.wakeUp();
		this.game.setStatus(EnviromentGame.RUNNING);
		
		if(this.eventListener != null)
			this.eventListener.onGameStart(this.game);
	}
	
	public void pause(){
		if(this.game == null)
			return;
		
		this.game.setStatus(EnviromentGame.PAUSED);
		
		if(this.eventListener != null)
			this.eventListener.onGamePause(this.game);
	}
	
	public void stop(){
		if(this.game == null)
			return;
		
		this.game.setStatus(EnviromentGame.STOPPED);
		
		/*hits = faults = score = 0;
		time = TIME_MED;
		incr = decr = 1;*/
		
		if(this.eventListener != null)
			this.eventListener.onGameStop(this.game);
	}
	
	// Player Methods
	public void hit(){
		if(this.game == null)
			return;
		
		this.game.setScore(this.game.getScore() + 5 * this.game.getIncrement());
		this.game.setHits(this.game.getHits() + 1);
		this.game.setIncrement(this.game.getIncrement() + 1);
		
		if(this.game.getDecrement() > 1) 
			this.game.setDecrement(this.game.getDecrement() - 1);

		for(int i = 0; i < 2 * this.game.getIncrement(); i++)
			if(this.game.getTime() < TIME_MAX + this.game.getDecrement())
				this.game.setTime(this.game.getTime() + 1);
				
		if(this.game.getStatus() == EnviromentGame.PAUSED)
			this.start();

		if(this.eventListener != null)
			this.eventListener.onPlayerHit(this.game);
	}

	public void fault(){
		if(this.game == null)
			return;
		
		//if(this.game.getStatus() == EnviromentGame.RUNNING){
			this.game.setIncrement(1);
			this.game.setFaults(this.game.getFaults() + 1);
			this.game.setDecrement(this.game.getDecrement() + 1);
		//}
		//else
		if(this.game.getStatus() == EnviromentGame.PAUSED)
			this.start();

		if(this.eventListener != null)
			this.eventListener.onPlayerFault(this.game);
	}
	
	// Getters and Setters
	public void setLevel(Level level){
		this.level = level;
	}
	public Level getLevel(){
		return this.level;
	}
	
	public void setPlayer(Player player){
		this.player = player;
	}

	public Player getPlayer(){
		return this.player;
	}
	
	/*public Game getGame(){
		return this.game;
	}*/
	
	
	//Listener
	public void setEventListener(EventListener listener) {
		this.eventListener = listener;
	}
	
	// Interfaces
	public interface EventListener {
		void onGameSetup(Game game);
		void onGameStart(Game game);
		void onGamePause(Game game);
		void onGameStop(Game game);
		void onPlayerHit(Game game);
		void onPlayerFault(Game game);
		void onVisualUpdate(Game game);
	}
}
