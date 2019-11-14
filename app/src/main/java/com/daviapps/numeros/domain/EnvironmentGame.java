package com.daviapps.numeros.domain;
import java.util.*;
import android.app.*;
import android.content.*;
import com.daviapps.numeros.database.*;

public class EnvironmentGame {
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

	private boolean run;
	
	// Listener
	private EventListener eventListener;
	
	// Timers
	private Timer timer;

	// Constructors
	public EnvironmentGame(final Activity activity){
		this.timer = new Timer();
		this.timer.scheduleAtFixedRate(new TimerTask() {         
			@Override
			public void run() {
				activity.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						if(run){
							if(EnvironmentGame.this.game == null)
								return;
							
							if(EnvironmentGame.this.game.getStatus() == EnvironmentGame.RUNNING){
								if(EnvironmentGame.this.game.getTime() > EnvironmentGame.TIME_MIN){
									game.setTime(game.getTime() - ((game.getDecrement() > 1 ? game.getDecrement() : 1) * ((int) game.getLevel().getSpeed())));
									if(EnvironmentGame.this.game.getTime() <= EnvironmentGame.TIME_MIN)
										EnvironmentGame.this.stop();
									
									/*time -= (decr > 1 ? decr : 1) * level.velocity;
									if(time <= EnviromentGame.TIME_MIN)
										lose();*/
								}
							}
							if(EnvironmentGame.this.eventListener != null && EnvironmentGame.this.game != null)
								EnvironmentGame.this.eventListener.onVisualUpdate(EnvironmentGame.this.game);
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
		
		//this.start();
			
		if(this.eventListener != null)
			this.eventListener.onGameSetup(this.game);
			
		if(this.eventListener != null)
			this.eventListener.onVisualUpdate(this.game);
	}
	
	public void start(){
		if(this.game == null || this.game.getStatus() == EnvironmentGame.RUNNING)
			return;
		
		this.wakeUp();
		this.game.setStatus(EnvironmentGame.RUNNING);
		
		if(this.eventListener != null)
			this.eventListener.onGameStart(this.game);
	}
	
	public void pause(){
		if(this.game == null || this.game.getStatus() == EnvironmentGame.PAUSED)
			return;
		
		this.game.setStatus(EnvironmentGame.PAUSED);
		
		if(this.eventListener != null)
			this.eventListener.onGamePause(this.game);
	}
	
	public void stop(){
		if(this.game == null || this.game.getStatus() == EnvironmentGame.STOPPED)
			return;
		
		this.game.setStatus(EnvironmentGame.STOPPED);
		
		/*hits = faults = score = 0;
		time = TIME_MED;
		incr = decr = 1;*/
		
		if(this.eventListener != null)
			this.eventListener.onGameStop(this.game);
			
		this.game = null;
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
				
		if(this.game.getStatus() == EnvironmentGame.PAUSED)
			this.start();
			
		// Level up
		if(this.game.getLevel().isChar()){
			this.game.levelUp();
			
			int bonus = this.game.getLevel().getLevel() * 10;
			
			this.game.setScore(this.game.getScore() + bonus);
			
			if(this.eventListener != null)
				this.eventListener.onBonus(bonus);
		}
		else {
			if(this.game.getCount() % 10 == 0){
				this.game.levelUp();

				if(this.eventListener != null)
					this.eventListener.onLevelUp(this.game.getLevel());
			}
		}

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
		if(this.game.getStatus() == EnvironmentGame.PAUSED)
			this.start();

		if(this.eventListener != null)
			this.eventListener.onPlayerFault(this.game);
	}
	
	// Getters and Setters
	public void setLevel(int level){
		this.game.setLevel(level);
	}
	
	public Level getLevel(){
		return this.game.getLevel();
	}
	
	public void setPlayer(Player player){
		this.player = player;
	}

	public Player getPlayer(){
		return this.player;
	}
	
	public int getStatus(){
		return this.game.getStatus();
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
		void onLevelUp(Level level);
		void onBonus(int bonus);
	}
}
