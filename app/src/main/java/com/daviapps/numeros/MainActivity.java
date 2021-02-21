package com.daviapps.numeros;
//Deus é fiel
import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import java.util.*;
import android.content.*;
import java.io.*;
import android.preference.*;
import com.google.android.gms.ads.*;
import com.daviapps.numeros.domain.*;
import com.daviapps.numeros.dialog.*;
import com.daviapps.numeros.update.*;
import com.daviapps.numeros.database.*;
import com.daviapps.numeros.respack.*;
//import io.socket.client.IO;
//import io.socket.client.Socket;
//import io.socket.emitter.Emitter;
//import java.net.*;
import org.json.*;
//import com.google.firebase.storage.*;

public class MainActivity extends Activity implements EnvironmentGame.EventListener {
	// Defines
	//private static final int A_HIT = 1000, A_ERROU = 1001, A_, A_PERDEU = 1003;
	
	private static final int BTNS_LENGTH = 4;
	private static final int CHAR_INIT = 65;
	
	private static final int VIBRATOR_FAULT = 500, VIBRATOR_HIT = 75, VIBRATOR_CLICK = 25;
	private static final int DELAY_FAULT = 500;

	// Preferences Map
	public static String PREFS_AUDIO = "audio";
	//public static String PREFS_ORDER = "order";
	//public static String PREFS_PLAYER = "player";
	
	// Components
	private Button[] btns;
	private LinearLayout[] lay_cor;
	private TextView tvScore, tvLevel;
	private ProgressBar pbTime;
	//private RelativeLayout main_lay;
	private TextView decrement_text;

	// Variables
	private int [] btns_num;
	private int [] btns_colors;
	
	private int [] num_buffer;
	
	private EnvironmentGame enviroment;
	
	//private boolean update_asked = false;
	
	private boolean order = false;
	
	// Android
	private Vibrator vibrator;
	
	// Level variables
	/*private Level [] levels = {
		new Level(false, 1),
		new Level(false, 2),
		new Level(false, 4),
		new Level(true, 1),
		new Level(true, 2),
		new Level(true, 4)
	};*/
	
	// Static variables
	private static int [] color_array;

	// Config
	private SharedPreferences prefs;
	
	// Database
	private DataSet<Game> gameDB;
	private DataSet<Player> playerDB;
	//private FirebaseStorage storage;
	
	//Menu item
	//private MenuItem audioMenu;
	private MenuItem statusMenu;
	//private MenuItem orderMenu;
	
	// Resources
	private File externalAssetsPath;
	private SoundFXResourcePack soundfx;
	
	// Learn example
	/*private Socket mSocket;
	{
		try {
			mSocket = IO.socket("http://api-daviapps.herokuapp.com");
		} catch (URISyntaxException e) {}
	}*/

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		//mSocket.on("new message", onNewMessage);
		//mSocket.connect();
		
		//startActivity(new Intent(MainActivity.this, LibraryActivity.class));
		
		/*Notification.Builder nBuilder = new Notification.Builder(this)
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle("Numeros")
			.setContentText("Content text")
			.setPriority(Notifica
			tion.PRIORITY_HIGH)
			//.setChannelId("numeros")
			.;
			
		Notification notif = nBuilder.build();
			
		
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.notify(1, notif);

		nBuilder.build();*/
		
		
		/*	*	*	*	  Database  	*	 Database 	   *	*	*	*/

		this.gameDB = new GameDB(this);
		this.playerDB = new PlayerDB(this);
		
		//storage = FirebaseStorage.getInstance("gs://numeros-df4a8.appspot.com");
		
		/*	*	*	*	Preferences 	*	Preferences 	*	*	*	*/

		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

		
		/*	*	*	*	  Resource  	*	 Resource 	   *	*	*	*/
		this.externalAssetsPath = getExternalFilesDir("assets");
		this.externalAssetsPath.mkdir();
		
		CopyAssets ca = new CopyAssets(getAssets(), externalAssetsPath);
		
		//ca.copy("respack");
		//ca.copy("");
		ca.copyWhatContainsString(".zip");
		

		this.soundfx = new SoundFXResourcePack(externalAssetsPath, "default-soundfx.zip");
		
		
		/*	*	*	*	  AdMob    *   Admob   *  Admob 	*	*	*	*/

		MobileAds.initialize(this, "ca-app-pub-1507172442893539~2844160460");
		
		AdView adView = findViewById(R.id.adView);
		
		adView.setAdListener(new AdListener(){
			@Override
			public void onAdLoaded(){
				//Toast.makeText(MainActivity.this, "Admob: loaded", Toast.LENGTH_SHORT).show();
				//ErrorDialog.show(MainActivity.this, "Admob", "Working");
			}
		
			@Override
			public void onAdFailedToLoad(int p1){
				//Toast.makeText(MainActivity.this, "AdMob: Fail to load (" + p1 + ")", Toast.LENGTH_SHORT).show();
			}
		});
		
		AdRequest adRequest = new AdRequest.Builder()
			.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
			.addTestDevice("A4DDF5EC5E81FE0D117CA05BA164E8B8") // LG K10
			.addTestDevice("B32DF5960E16B6E638F0861FB8E63372") // LG G6
			.build();
		
		adView.loadAd(adRequest);
		
		/*	*	*	*	Variables 	*	Variables 	*	Variables 	*	*	*	*/
		
		btns_num = new int[BTNS_LENGTH];
		btns_colors = new int[BTNS_LENGTH];
		num_buffer = new int[BTNS_LENGTH];

		// Static variables
		if(color_array == null)
			color_array = getResources().getIntArray(R.array.color_array);
			
		/*	*	*	*	Android *	Android *	Android *	Android *	*	*	*/
		
		this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
		} else {
			//deprecated in API 26 
			v.vibrate(500);
		}*/

		/*	*	*	*	Layout 	*	Layout 	*	Layout 	*	Layout 	*	*	*	*/
		
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

		tvScore = findViewById(R.id.pontos);
		tvLevel = findViewById(R.id.level);
		pbTime = findViewById(R.id.Progresso);

		pbTime.setMax(EnvironmentGame.TIME_MAX);
		pbTime.setProgress(EnvironmentGame.TIME_MED);

		decrement_text = findViewById(R.id.main_decrement);

		TextView version = findViewById(R.id.main_version);
		version.setText(String.format("%s: %s",
				getString(R.string.version), getString(R.string.app_version)
		));
		
		/*	*	*	*	Game 	*	Game 	*	Game  	*	Game 	*	*	*	*/
		
		try {
			if(gameDB.count() == 0)
				gameDB.insert(new Game());
			
			this.enviroment = new EnvironmentGame(this);
			enviroment.setEventListener(this);
			
			this.selectGame();
			
		} catch(Exception ex){
			
			ErrorDialog.show(this, "MainActivity.onCreate - Create game enviroment", ex.getMessage());
		}
    }
	
	private void selectGame(){
		new GameListDialog.Builder(this)
			.setOnSelectListener(new GameListDialog.OnSelectListener(){
				@Override
				public void onSelect(Game game){
					//Toast.makeText(MainActivity.this, game.toString(), Toast.LENGTH_SHORT).show();
					MainActivity.this.enviroment.setup(game);
				}
			})
			.setOnCancelListener(new DialogInterface.OnCancelListener(){
				@Override
				public void onCancel(DialogInterface di){
					MainActivity.this.finish();
				}
			})
			.build()
			.show();
	}
	
	/*private Emitter.Listener onNewMessage = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						JSONObject data = (JSONObject) args[0];
						String username;
						String message;
						try {
							username = data.getString("username");
							message = data.getString("message");
						} catch (JSONException e) {
							return;
						}
						
						Toast.makeText(MainActivity.this, username + ": " + message, Toast.LENGTH_SHORT).show();

						// add the message to view
						//addMessage(username, message);
					}
				});
		}
	};*/

	// Number functions
	public boolean verify(int array[], int num){
		for(int i = 0; i < array.length; i++)
			if(array[i] == num)
				return true;
		return false;
	}

	/*public int [] clear(int array[], int valor){
		for(int i = 0; i < array.length; i++)
			array[i] = valor;
		return array;
	}*/

	public int [] getRandomArray(int Max, int Min, int array[]){
		for(int i = 0; i < array.length;){
			int num = new Random().nextInt(Max - Min + 1);

			if(!verify(array, num)){
			//if(!(Arrays.binarySearch(array, num) >= 0)){
				array[i] = num;
				i++;
			}
		}

		return array;
	}
	
	public void incrementBuffer(int n){
		for(int i = BTNS_LENGTH - 1; i > 0; i--)
			num_buffer[i] = num_buffer[i - 1];
		num_buffer[0] = n;
	}
	
	// Components functions
	public void enableButtons(boolean enable){
		for(int i = 0; i < btns.length; i++)
			btns[i].setEnabled(enable);
	}

	public void refresh(){
		//if(!charMode)	btns_num = getRandomArray(9, 0, btns_num);
		if(!this.enviroment.getLevel().isChar())	btns_num = getRandomArray(9, 0, btns_num);
		else btns_num = getRandomArray(25, 0, btns_num);
			
		btns_colors = getRandomArray(color_array.length -1, 0, btns_colors);

		for(int i = 0; i < BTNS_LENGTH; i++){
			lay_cor[i].setBackgroundColor(color_array[btns_colors[i]]);
			//btns[i].setText(charMode ? "" + (char) (CHAR_INIT + btns_num[i]) : "" + btns_num[i]);
			btns[i].setText(this.enviroment.getLevel().isChar() ? "" + (char) (CHAR_INIT + btns_num[i]) : "" + btns_num[i]);
		}

		enableButtons(true);
		//buffer = clear(buffer, -1);
		Arrays.fill(num_buffer, -1);
	}

	public void btnClick(View v){
		Button button = (Button) v;
		button.setEnabled(false);
		
		//boolean order = this.prefs.getBoolean(PREFS_ORDER, false);
		
		int num = 0;
		
		// if(charMode) 	num = button.getText().toString().toCharArray()[0] - CHAR_INIT;
		if(enviroment.getLevel().isChar()) 	num = button.getText().toString().toCharArray()[0] - CHAR_INIT;
		else 								num = Integer.parseInt(button.getText().toString());
		
		incrementBuffer(num);
		
		//int a = buffer[0]; //b = buffer[1], c = buffer[2], d = buffer[3];
		
		
		int [] sorted = btns_num;
		java.util.Arrays.sort(sorted);
		
		//boolean order = true;
		vibrate(VIBRATOR_CLICK);
		
		
		if(num_buffer[3] != -1){
			enviroment.hit(); enviroment.start(); //acertou(); start();
		} else
		if(num_buffer[2] != -1){
			if(num_buffer[0] != sorted[order ? 2 : 1])
				enviroment.fault(); // errou();
		} else
		if(num_buffer[1] != -1){
			if(num_buffer[0] != sorted[order ? 1 : 2])
				enviroment.fault(); // errou();
		} else
		if(num_buffer[0] != -1){
			order = num_buffer[0] == sorted[0];
			
			// Toast.makeText(this, "Order: " + order, Toast.LENGTH_SHORT).show();
			
			if(num_buffer[0] != sorted[order ? 0 : 3])
				enviroment.fault(); // errou();
		}
		
		/*if(buffer[BTNS_LENGTH -1] != -1)
			enviroment.hit();
		else
		if(buffer[1] != -1){
			//boolean order = buffer[0] > buffer[1];
			boolean order = true;

			for(int i = BTNS_LENGTH -1; i >= 0; i--){
				if(buffer[i] != -1){
					if(buffer[0] != (order ? sorted[i] : sorted[BTNS_LENGTH - i]))
						enviroment.fault();
					return;
				}
			}
		}
		else
		if(buffer[0] != -1){
			if(buffer[0] != sorted[0] && buffer[0] != sorted[BTNS_LENGTH -1])
				enviroment.fault();
		}
		else return;*/
		
		
		/*if(buffer[3] != -1){
			enviroment.hit(); enviroment.start();
		} else
		if(buffer[2] != -1){
			if(a != sorted[2] && a != sorted[1])
				enviroment.fault();
		} else
		if(buffer[1] != -1){
			if(a != sorted[1] && a != sorted[2])
				enviroment.fault();
		} else
		if(buffer[0] != -1){
			if(a != sorted[0] && a != sorted[3])
				enviroment.fault();
		}*/
		
		/*if(buffer[3] != -1){
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
		}*/
	}
	
	private void vibrate(int duration){
		if(prefs.getBoolean("vibrate", true))
			vibrator.vibrate(duration);
	}
	
	/*public void updateVisual(){
		score.setText("Pontos: " + this.game.getScore());
		time.setProgress(this.game.getTime() >= Game.TIME_MIN ? this.game.getTime() : Game.TIME_MIN);
	}*/

	// Activity functions
	
	@Override
	protected void onStart(){
		// Check for update
		// TODO: Use firebase onapp update
		/*if(prefs.getBoolean("checkUpdates", true)){
			UpdateChecker.check(this);
		}*/
		
		if(prefs.getString("lastNewsVersion", "").equals(getString(R.string.app_version))){
			
		}
		
		//Toast.makeText(this, "Socket.io connected: " + mSocket.connected(), Toast.LENGTH_SHORT).show();
		
		
		super.onStart();
	}
	
	@Override
	protected void onResume(){
		if(this.enviroment != null)
			this.enviroment.wakeUp();
			
		// TODO: Load preferences
		
		/*if(!update_asked){
			UpdateChecker.check(this);
			update_asked = true;
		}*/
		
		super.onResume();
	}

	@Override
	protected void onPause(){
		if(this.enviroment != null)
			this.enviroment.sleep();
			
		this.enviroment.pause();
		
		super.onPause();
	}

	@Override
	protected void onDestroy(){
		//mSocket.disconnect();
		//mSocket.off("new message", onNewMessage);
		
		// Stop the thread
		if(this.enviroment != null)
			this.enviroment.finish();
		
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed(){
		enviroment.pause();
		selectGame();
	}
	
	// Game functions
	@Override
	public void onGameSetup(Game g){
		//Toast.makeText(this, "onGameSetup", Toast.LENGTH_SHORT).show();
		
		refresh();
		
		statusMenu.setIcon(getResources().getDrawable(R.drawable.baseline_pause_white_48dp));
		statusMenu.setEnabled(false);
	}

	@Override
	public void onGameStart(Game game){
		//Toast.makeText(this, "onGameStart", Toast.LENGTH_SHORT).show();
		
		
		statusMenu.setIcon(getResources().getDrawable(R.drawable.baseline_pause_white_48dp));
		statusMenu.setEnabled(true);
		
		refresh();
	}

	@Override
	public void onGamePause(Game g){
		//Toast.makeText(this, "onGamePause\n" + g, Toast.LENGTH_LONG).show();
			
		if(g.getScore() < 1){
			gameDB.delete(g);
			Toast.makeText(this, R.string.toast_game_without_score, Toast.LENGTH_SHORT).show();
		}
		else {
			if(gameDB.count("id = " + g.getId()) == 0)
				gameDB.insert(g);
			else
				gameDB.update(g);
		}
		
		statusMenu.setIcon(getResources().getDrawable(R.drawable.baseline_play_arrow_white_48dp));
		statusMenu.setEnabled(true);
		
		enableButtons(false);
	}

	@Override
	public void onGameStop(Game g){
		//Toast.makeText(this, "onGameStop\n" + g, Toast.LENGTH_LONG).show();

		if(gameDB.count("id = " + g.getId()) == 0)
			gameDB.insert(g);
		else
			gameDB.update(g);
		
		//enviroment.setup(new Game());
		
		this.soundfx.play("lose");
		refresh();
		
		statusMenu.setIcon(getResources().getDrawable(R.drawable.baseline_stop_white_48dp));
		statusMenu.setEnabled(false);
		
		enableButtons(false);
		
		new LoseDialog.Builder(this, g)
				.setOnTryAgainListener(new LoseDialog.OnTryAgainListener(){
					@Override
					public void onTryAgain(){
						enviroment.setup(new Game());
						//Toast.makeText(MainActivity.this, "try again", Toast.LENGTH_SHORT).show();
						
					}
				})
				.setOnCancelListener(new DialogInterface.OnCancelListener(){
					@Override
					public void onCancel(DialogInterface p1){
						//Toast.makeText(MainActivity.this, "cancel", Toast.LENGTH_SHORT).show();
						selectGame();
					}
				})
				.show();
	}

	@Override
	public void onLevelUp(Level level) {
		Toast.makeText(this, "LevelUp [" + level.getLevel() + "]", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onBonus(int bonus) {
		Toast.makeText(this, "Bônus: " + bonus + " pontos", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onPlayerHit(Game g){
		//Toast.makeText(this, "onPlayerHit", Toast.LENGTH_SHORT).show();
		
		//this.soundfx.find("hit").start();
		
		vibrate(VIBRATOR_HIT);
		this.soundfx.play("hit");
		
		refresh();
	}

	@Override
	public void onPlayerFault(Game g){
		//Toast.makeText(this, "onPlayerFault", Toast.LENGTH_SHORT).show();
		
		this.soundfx.play("fault");
		
		vibrate(VIBRATOR_FAULT);
		enableButtons(false);
		
		for(int i = 0; i < BTNS_LENGTH; i++)
			lay_cor[i].setBackgroundResource(R.color.darkGray);
			
		enviroment.sleep();

		new Thread(new Runnable(){
			@Override
			public void run(){
				try { Thread.sleep(DELAY_FAULT); } 
				catch(InterruptedException ex){}
				
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						refresh();
						enviroment.wakeUp();
						
						/*enableButtons(true);
						//buffer = clear(buffer, -1);
						Arrays.fill(num_buffer, -1);*/
					}
				});
			}
		}).start();
	}

	@Override
	public void onVisualUpdate(Game g){
		tvScore.setText(String.format("%s: %s",
				getString(R.string.score),
				g.getScore()
		));
		
		tvLevel.setText(String.format("%s: %s",
				getString(R.string.level),
				g.getLevel().getLevel()
		));
		
		
		pbTime.setProgress(g.getTime() >= EnvironmentGame.TIME_MIN ? g.getTime() : EnvironmentGame.TIME_MIN);
	}
	
	// Menu item
	/*public void confAudio(boolean active){
		try{
			SharedPreferences.Editor edit = this.prefs.edit();
			edit.putBoolean(PREFS_AUDIO, active);
			edit.commit();
				edit.apply();

		} catch(NullPointerException ex){
			ErrorDialog.show(this, "MainActivity.confAudio()", ex.getMessage());
		}
		
		
		if(active){
			audioMenu.setIcon(getResources().getDrawable(R.drawable.audio_on));
			audioMenu.setTitle("Audio ativo");
		}
		else {
			audioMenu.setIcon(getResources().getDrawable(R.drawable.audio_off));
			audioMenu.setTitle("Audio desativado");
		}
	}*/

	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		try {
			//audioMenu = menu.findItem(R.id.menu_sound);
			statusMenu = menu.findItem(R.id.menu_status);
			//orderMenu = menu.findItem(R.id.menu_mode);
			
			//confAudio(this.prefs.getBoolean(PREFS_AUDIO, false));
			//confOrder(this.prefs.getBoolean(PREFS_ORDER, true));
			
			
		} catch(Exception ex){
			ErrorDialog.show(this, "onPrepareOptionsMenu", ex.getMessage());
		}

		return true;
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
		
		/*if(menu instanceof MenuBuilder){
            MenuBuilder menuBuilder = (MenuBuilder) menu;
            menuBuilder.setOptionalIconsVisible(true);
        }*/
		
        return true;
    }

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
		switch(item.getItemId()){
			/*case R.id.menu_sound:
				confAudio(!this.prefs.getBoolean(PREFS_AUDIO, false));
				return true;*/
				
			case R.id.menu_status:
				if(enviroment.getStatus() == EnvironmentGame.RUNNING)
					enviroment.pause();
				else
				if(enviroment.getStatus() == EnvironmentGame.PAUSED)
					enviroment.start();
				return true;
				
			/*case R.id.menu_import:
				//storage.
				return true;
				
			case R.id.menu_export:
				
				return true;*/
			
			case R.id.menu_settings:
				Toast.makeText(this, R.string.toast_on_dev_functionality, Toast.LENGTH_SHORT).show();
				//startActivity(new Intent(MainActivity.this, SettingsActivity.class));
				return true;
				
			/*case R.id.menu_about:
				new AboutDialog.Builder(this).build();
				return true;*/
				
			// Unknown action
			default: return super.onOptionsItemSelected(item);
				
				/*new LoseDialog.Builder(this, new Game())
						.setOnTryAgainListener(new LoseDialog.OnTryAgainListener(){
							@Override
							public void onTryAgain(){
								Toast.makeText(MainActivity.this, "Try again", Toast.LENGTH_SHORT).show();
							}
						})
						.setOnCancelListener(new LoseDialog.OnCancelListener(){
							@Override
							public void onCancel(DialogInterface p1){
								Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
							}
						})
						.show();*/
				
				//new PlayerListDialog(this).show();
				
				/*Player pl = new Player();
				
				pl.setNickname("Player teste");
				pl.setPassword("abcd");
				
				new PlayerEditorDialog.Builder(this, pl)
						.setOnSaveListener(new EditorDialog.OnSaveListener<Player>(){
							@Override
							public boolean onSave(Player item){
								Toast.makeText(MainActivity.this, "Player editor saved\n" + item, Toast.LENGTH_LONG).show();
								return true;
							}
						})
						.setOnDeleteListener(new EditorDialog.OnDeleteListener<Player>(){
							@Override
							public boolean onDelete(Player item){
								Toast.makeText(MainActivity.this, "Player editor deleted\n" + item, Toast.LENGTH_LONG).show();
								return true;
							}
						})
						.setOnCancelListener(new EditorDialog.OnCancelListener(){
							@Override
							public void onCancel(DialogInterface p1){
								Toast.makeText(MainActivity.this, "Player editor canceled", Toast.LENGTH_SHORT).show();
							}
						})
						.show();*/
				
				
				/*new PlayerEditorDialog.Builder(this)
						.setOnDoneListener(new EditorDialog.OnDoneListener<Player>() {
							@Override
							public void onDone(Player e){
								
							}
						})
						.show();*/
				//return true;
		}
    }
}
