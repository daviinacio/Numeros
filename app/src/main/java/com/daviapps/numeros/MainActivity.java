package com.daviapps.numeros;
//Deus é fiel
import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import java.util.*;
import android.media.*;
import android.content.*;
import android.widget.AdapterView.*;
import android.graphics.*;
import com.daviapps.dcode.preferences.*;
import com.daviapps.dcode.*;
import java.io.*;
import org.json.*;
import java.net.*;
import android.net.*;
import android.preference.*;
//import com.google.android.gms.ads.*;
import com.daviapps.numeros.domain.*;
import com.daviapps.numeros.dialog.*;
import com.daviapps.numeros.update.*;
import com.daviapps.numeros.database.*;
import com.daviapps.numeros.respack.*;
import android.content.res.*;
import android.util.*;

public class MainActivity extends Activity implements EnviromentGame.EventListener {
	// Defines
	//private static final int A_HIT = 1000, A_ERROU = 1001, A_, A_PERDEU = 1003;
	
	private static final int BTNS_LENGTH = 4;
	
	private static final int CHAR_INIT = 65;
	

	// Preferences Map
	public static String PREFS_AUDIO = "audio";
	public static String PREFS_ORDER = "order";
	public static String PREFS_PLAYER = "player";
	
	// Components
	private Button[] btns;
	private LinearLayout[] lay_cor;
	private TextView score;
	private ProgressBar time;
	//private RelativeLayout main_lay;
	private TextView decrement_text;

	// Variables
	private int [] btns_num;
	private int [] btns_cor;
	
	private int [] buffer;
	
	private EnviromentGame enviroment;
	
	private boolean update_asked = false;
	
	// Level variables
	
	private Level [] levels = {
		new Level(false, 1),
		new Level(false, 2),
		new Level(false, 4),
		new Level(true, 1),
		new Level(true, 2),
		new Level(true, 4)
	};
	
	// Static variables
	private static int [] cor;

	// Config
	private SharedPreferences prefs;
	
	// Database
	DataSet<Game> gameDB;
	DataSet<Player> playerDB;
	
	//Menu item
	private MenuItem audioMenu;
	private MenuItem orderMenu;
	
	// Resources
	private File externalAssetsPath;
	private SoundFXResourcePack soundfx;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		/*	*	*	*	  Database  	*	 Database 	   *	*	*	*/

		this.gameDB = new GameDB(this);
		this.playerDB = new PlayerDB(this);


		/*	*	*	*	Preferences 	*	Preferences 	*	*	*	*/

		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

		
		/*	*	*	*	  Resource  	*	 Resource 	   *	*	*	*/
		this.externalAssetsPath = getExternalFilesDir("assets");
		this.externalAssetsPath.mkdir();
		
		new CopyAssets(getAssets(), externalAssetsPath).copy();
		

		this.soundfx = new SoundFXResourcePack(externalAssetsPath, "default-soundfx.zip");
		
		
		/*	*	*	*	  AdMob    *   Admob   *  Admob 	*	*	*	*/

		/*MobileAds.initialize(this, "ca-app-pub-1507172442893539~2844160460");

		 AdView adView = findViewById(R.id.adView);

		 adView.setAdListener(new AdListener(){
		 @Override
		 public void onAdLoaded(){
		 Toast.makeText(MainActivity.this, "Admob: loaded", Toast.LENGTH_SHORT).show();
		 //ErrorDialog.show(MainActivity.this, "Admob", "Working");
		 }

		 @Override
		 public void onAdFailedToLoad(int p1){
		 Toast.makeText(MainActivity.this, "AdMob: Fail to load (" + p1 + ")", Toast.LENGTH_SHORT).show();
		 }
		 });

		 AdRequest adRequest = new AdRequest.Builder()
		 .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		 .addTestDevice("A4DDF5EC5E81FE0D117CA05BA164E8B8")
		 .build();


		 adView.loadAd(adRequest);*/
		
		// Variables
		btns_num = new int[BTNS_LENGTH];
		btns_cor = new int[BTNS_LENGTH];
		buffer = new int[BTNS_LENGTH];

		// Static variables
		if(cor == null)
			cor = new int[]{getResources().getColor(R.color.Laranja),
				getResources().getColor(R.color.Azul),
				getResources().getColor(R.color.Verde),
				getResources().getColor(R.color.Vermelho),
				getResources().getColor(R.color.VerdeAgua),
				getResources().getColor(R.color.Amarelo),
				getResources().getColor(R.color.roxo),
				getResources().getColor(R.color.VerdeEscuro),
				getResources().getColor(R.color.VerdeLimao),
				getResources().getColor(R.color.Magenta),
				getResources().getColor(R.color.Marrom)
			};

		// Components
		btns = new Button[]{
			(Button) findViewById(R.id.um),
		 	(Button) findViewById(R.id.dois),
			(Button) findViewById(R.id.tres),
		 	(Button) findViewById(R.id.quatro)
		};

		lay_cor = new LinearLayout[]{
			(LinearLayout) findViewById(R.id.lay_um),
		 	(LinearLayout) findViewById(R.id.lay_dois),
		 	(LinearLayout) findViewById(R.id.lay_tres),
		 	(LinearLayout) findViewById(R.id.lay_quatro)
		};

		score = (TextView) findViewById(R.id.pontos);
		time = (ProgressBar) findViewById(R.id.Progresso);

		time.setMax(EnviromentGame.TIME_MAX);
		time.setProgress(EnviromentGame.TIME_MED);

		decrement_text = (TextView) findViewById(R.id.main_decrement);

		TextView version = (TextView) findViewById(R.id.main_version);
		version.setText("versão: " + version.getText());
		
		// Game
		
		try {
			if(gameDB.count() == 0)
				gameDB.insert(new Game());
			
			this.enviroment = new EnviromentGame(this);
			enviroment.setEventListener(this);
			enviroment.setLevel(levels[0]);
			
			Game g = gameDB.selectById(1);
			//Game g = new Game();
			enviroment.setup(g);
			
			
			Toast.makeText(this, Integer.toString(gameDB.count()), Toast.LENGTH_SHORT).show();
			
			//this.game = new Game();
			//this.game = gameDB.select("1 = 1").get(0);
			/*this.game = gameDB.selectById(3);
			// Debug variables
			this.game.setLevel(levels[0]);
			this.game.setPlayer(new Player(1));


			this.game.setEventListener(this);
			this.game.setup(this);*/
			
		} catch(Exception ex){
			
			ErrorDialog.show(this, "MainActivity.onCreate - Create game enviroment", ex.getMessage());
		}
		
		
		/*try {
			refresh();
		} catch(Exception ex){
			ErrorDialog.show(this, "Refresh", ex.getMessage());
		}*/
		
		/*((Context)((Activity) this)).runOnUiThread(new Runnable(){
			@Override
			public void run(){}
		});*/
    }

	// Number functions
	public boolean verify(int array[], int num){
		for(int i = 0; i < array.length; i++)
			if(array[i] == num)
				return true;
		return false;
	}

	public int [] clear(int array[], int valor){
		for(int i = 0; i < array.length; i++)
			array[i] = valor;
		return array;
	}

	public int [] getRandomArray(int Max, int Min, int array[]){
		for(int i = 0; i < array.length;){
			int num = new Random().nextInt(Max - Min + 1);

			if(!verify(array, num)){
				array[i] = num;
				i++;
			}
		}

		return array;
	}
	
	public void incrementBuffer(int n){
		for(int i = BTNS_LENGTH - 1; i > 0; i--)
			buffer[i] = buffer[i - 1];
		buffer[0] = n;
	}
	
	// Components functions
	public void enableButtons(boolean enable){
		for(int i = 0; i < btns.length; i++)
			btns[i].setEnabled(enable);
	}

	public void refresh(){
		//if(!charMode)	btns_num = getRandomArray(9, 0, btns_num);
		if(!this.enviroment.getLevel().charMode)	btns_num = getRandomArray(9, 0, btns_num);
		else btns_num = getRandomArray(25, 0, btns_num);
			
		btns_cor = getRandomArray(cor.length -1, 0, btns_cor);

		for(int i = 0; i < BTNS_LENGTH; i++){
			lay_cor[i].setBackgroundColor(cor[btns_cor[i]]);
			//btns[i].setText(charMode ? "" + (char) (CHAR_INIT + btns_num[i]) : "" + btns_num[i]);
			btns[i].setText(this.enviroment.getLevel().charMode ? "" + (char) (CHAR_INIT + btns_num[i]) : "" + btns_num[i]);
		}

		enableButtons(true);
		buffer = clear(buffer, -1);
	}

	public void btnClick(View v){
		Button button = (Button) v;
		button.setEnabled(false);
		
		boolean order = this.prefs.getBoolean(PREFS_ORDER, false);
		
		int num = 0;
		
		// if(charMode) 	num = button.getText().toString().toCharArray()[0] - CHAR_INIT;
		if(enviroment.getLevel().charMode) 	num = button.getText().toString().toCharArray()[0] - CHAR_INIT;
		else 			num = Integer.parseInt(button.getText().toString());
		
		incrementBuffer(num);
		
		int a = buffer[0];//, b = buffer[1], c = buffer[2], d = buffer[3];
		
		int [] sorted = btns_num;
		java.util.Arrays.sort(sorted);
		
		if(buffer[3] != -1){
			enviroment.hit(); enviroment.start(); //acertou(); start();
		} else
		if(buffer[2] != -1){
			if(a != sorted[order ? 2 : 1])
				enviroment.fault(); // errou();
		} else
		if(buffer[1] != -1){
			if(a != sorted[order ? 1 : 2])
				enviroment.fault(); // errou();
		} else
		if(buffer[0] != -1){
			if(a != sorted[order ? 0 : 3])
				enviroment.fault(); // errou();
		}
	}
	
	/*public void updateVisual(){
		score.setText("Pontos: " + this.game.getScore());
		time.setProgress(this.game.getTime() >= Game.TIME_MIN ? this.game.getTime() : Game.TIME_MIN);
	}*/

	// Activity functions
	@Override
	protected void onResume(){
		if(this.enviroment != null)
			this.enviroment.wakeUp();
		
		try {
			/*if(this.match.getStatus() == Match.PAUSED && this.match.getScore() > 0){
				super.onResume();
				return;
			}*/
			
			/*if(this.prefs.getInt(PREFS_D_SCORE, 0) > 0){
				
				new AlertDialog.Builder(this)
					.setTitle("Partida pendente")
					.setMessage("Existe uma partida pendente salva")
					.setPositiveButton("Continuar", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2){
							MainActivity.this.state = S_PAUSED;

							// Load game states
							MainActivity.this.tempo = MainActivity.this.prefs.getInt(PREFS_D_TEMPO, 0);
							MainActivity.this.pontos = MainActivity.this.prefs.getInt(PREFS_D_SCORE, 0);
							MainActivity.this.acertos = MainActivity.this.prefs.getInt(PREFS_D_ACERTOS, 0);
							MainActivity.this.erros = MainActivity.this.prefs.getInt(PREFS_D_ERRORS, 0);
							MainActivity.this.increment = MainActivity.this.prefs.getInt(PREFS_D_INCREMENT, 0);
							MainActivity.this.decrement = MainActivity.this.prefs.getInt(PREFS_D_DECREMENT, 0);
							
							MainActivity.this.run = true;
						}
					})
					.setNegativeButton("Nova partida", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2){
							MainActivity.this.stop();
						}
					})
					.setOnCancelListener(new DialogInterface.OnCancelListener(){
						@Override
						public void onCancel(DialogInterface p1){
							MainActivity.this.onBackPressed();
						}
					})
					.show();
				
			} else {
				this.stop();
			}*/
				
		} catch (Exception ex){
			ErrorDialog.show(this, "onResume[" + ex.getClass().getSimpleName() + "]", ex.getMessage());
		}
		
		if(!update_asked){
			UpdateChecker.check(this);
			update_asked = true;
		}
		
		super.onResume();
	}

	@Override
	protected void onPause(){
		if(this.enviroment != null)
			this.enviroment.sleep();
			
		this.enviroment.pause();
			
		/*if(this.enviroment.getGame() != null)
			if(this.enviroment.getGame().getStatus() != EnviromentGame.STOPPED)
				gameDB.update(this.enviroment.getGame());*/
		
		//gameDB.insert(game);
		
		// Save game state
		/*if(state != S_STOPED){
			Toast.makeText(this, "Partida salva", Toast.LENGTH_SHORT).show();
			
			SharedPreferences.Editor edit = this.prefs.edit();
			
			edit.putInt(PREFS_D_TEMPO, tempo);
			edit.putInt(PREFS_D_SCORE, pontos);
			edit.putInt(PREFS_D_ACERTOS, acertos);
			edit.putInt(PREFS_D_ERRORS, erros);
			edit.putInt(PREFS_D_INCREMENT, increment);
			edit.putInt(PREFS_D_DECREMENT, decrement);
			
			edit.commit();
			edit.apply();
		}
		
		if(this.state == S_RUNNING)
			this.state = S_PAUSED;*/
		
		super.onPause();
	}

	@Override
	protected void onDestroy(){
		// Stop the thread
		if(this.enviroment != null)
			this.enviroment.finish();
		
		super.onDestroy();
	}
	
	// Game functions
	
	@Override
	protected void onStart(){
		// TODO: Implement this method
		super.onStart();
	}

	/*@Override
	public void onGameSetup(Game g){
		//Toast.makeText(this, "onGameSetup", Toast.LENGTH_SHORT).show();
		time.setMax(EnviromentGame.TIME_MAX);
		time.setProgress(g.getTime());
		
		try { refresh(); } catch(Exception ex){
			ErrorDialog.show(this, "MainActivity.onGameSetup - Refresh", ex.getMessage());
		}
	}*/

	@Override
	public void onGameSetup(Game g){
		Toast.makeText(this, "onGameSetup", Toast.LENGTH_SHORT).show();
		
		refresh();
		
		// TODO: Implement this method
	}

	@Override
	public void onGameStart(Game game){
		//Toast.makeText(this, "onGameStart", Toast.LENGTH_SHORT).show();
		
		
		// TODO: Implement this method
	}

	@Override
	public void onGamePause(Game g){
		Toast.makeText(this, "onGamePause\n" + g, Toast.LENGTH_LONG).show();
		
		if(g.getId() == 0)
			gameDB.insert(g);
		else
			gameDB.update(g);
	}

	@Override
	public void onGameStop(Game g){
		Toast.makeText(this, "onGameStop\n" + g, Toast.LENGTH_LONG).show();
		
		if(g.getId() == 0)
			gameDB.insert(g);
		else
			gameDB.update(g);
			
		enviroment.setup(new Game());
		
		this.soundfx.find("lose").start();
		refresh();
	}

	@Override
	public void onPlayerHit(Game g){
		//Toast.makeText(this, "onPlayerHit", Toast.LENGTH_SHORT).show();
		
		//this.soundfx.find("hit").start();
		refresh();
	}

	@Override
	public void onPlayerFault(Game g){
		//Toast.makeText(this, "onPlayerFault", Toast.LENGTH_SHORT).show();
		
		this.soundfx.find("fault").start();

		enableButtons(true);
		buffer = clear(buffer, -1);
	}

	@Override
	public void onVisualUpdate(Game g){
		score.setText("Pontos: " + g.getScore());
		time.setProgress(g.getTime() >= EnviromentGame.TIME_MIN ? g.getTime() : EnviromentGame.TIME_MIN);
	}
	
	
	// Audio
	/*public void som(int audioId){
		if(prefs.getBoolean(PREFS_AUDIO, false))
			if(audioId == A_ACERTOU)
				;
			else if(audioId == A_ERROU)
				//MediaPlayer.create(this, R.raw.errou).start();
			else if(audioId == A_NAO_CONSEGUE)
				//MediaPlayer.create(this, R.raw.nao_).start();
			else if(audioId == A_PERDEU)
				//MediaPlayer.create(this, R.raw.nao).start();
	}*/

	// Menu item
	public void confAudio(boolean active){
		try{
			SharedPreferences.Editor edit = this.prefs.edit();
			edit.putBoolean(PREFS_AUDIO, active);
			edit.commit();
			edit.apply();

		} catch(NullPointerException ex){
			ErrorDialog.show(this, "MainActivity.confAudio()", ex.getMessage());
		}
		
		
		if(!active)
			audioMenu.setIcon(getResources().getDrawable(R.drawable.audio_off));
		else
			audioMenu.setIcon(getResources().getDrawable(R.drawable.audio_on));
			
	}

	public void confOrder(boolean order){
		try{
			SharedPreferences.Editor edit = this.prefs.edit();
			edit.putBoolean(PREFS_ORDER, order);
			edit.commit();
			edit.apply();
			
		} catch(NullPointerException ex){
			ErrorDialog.show(this, "MainActivity.confOrder()", ex.getMessage());
		}
		
		if(order)
			orderMenu.setIcon(getResources().getDrawable(R.drawable.up_2));
		else
			orderMenu.setIcon(getResources().getDrawable(R.drawable.down_4));
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		try {
			audioMenu = menu.findItem(R.id.menu_som_power);
			orderMenu = menu.findItem(R.id.menu_mode);
			
			confAudio(this.prefs.getBoolean(PREFS_AUDIO, false));
			confOrder(this.prefs.getBoolean(PREFS_ORDER, true));
			
		} catch(Exception ex){
			ErrorDialog.show(this, "onPrepareOptionsMenu", ex.getMessage());
		}

		return true;
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_info) {	
			//gameDB.insert(this.game);
			//Toast.makeText(this, "Game state saved", Toast.LENGTH_SHORT).show();
			//Toast.makeText(this, "Database length (" + gameDB.count() + ")", Toast.LENGTH_SHORT).show();
		}
		else 
		if (id == R.id.menu_info_pontos){
			Toast.makeText(this, "Função em desenvolvimento", Toast.LENGTH_SHORT).show();
			/*run = false;
			new ScoreDialog(MainActivity.this){
				@Override
				public void onStop(){
					run = true;
				}
			}.show();*/
		}
		else
		if (id == R.id.menu_info_settings){
			Toast.makeText(this, "Função em desenvolvimento", Toast.LENGTH_SHORT).show();
		}
		else
		if (id == R.id.menu_info_players){
			new PlayerEditorDialog(this).show();
			Toast.makeText(this, "Função em desenvolvimento", Toast.LENGTH_SHORT).show();
			/*run = false;
			new PlayerDialog(MainActivity.this){
				@Override
				public void onStop(){
					run = true;
				}
			}.show();*/
		}
		else
		if (id == R.id.menu_som_power) {
			confAudio(!this.prefs.getBoolean(PREFS_AUDIO, false));
            return true;
		}
		else 
		if(id == R.id.menu_mode) {
			confOrder(!this.prefs.getBoolean(PREFS_ORDER, false));
			refresh();
            return true;
		}

        return super.onOptionsItemSelected(item);
    }
	
}
