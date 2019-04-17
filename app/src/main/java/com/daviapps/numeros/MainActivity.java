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
import com.daviapps.numeros.old.*;
import com.daviapps.numeros.update.*;

public class MainActivity extends Activity {
	// Defines
	private static final int A_ACERTOU = 1000, A_ERROU = 1001, A_NAO_CONSEGUE = 1002, A_PERDEU = 1003;
	private static final int S_RUNNING = 1010, S_PAUSED = 1011, S_STOPED = 1012;
	
	private static final int btns_length = 4;
	private static final int T_MIN = 0, T_MAX = 200;
	private static final int T_MED = ((T_MIN + T_MAX) / 2);
	
	private static final int D_TEMPO = T_MED, D_PONTOS = 0, D_ACERTOS = 0, D_ERROS = 0, D_INCREMENT = 1, D_DECREMENT = 1;
	
	private static final int CHAR_INIT = 65;
	

	// Preferences Map
	public static String PREFS_AUDIO = "audio";
	public static String PREFS_ORDER = "order";
	public static String PREFS_PLAYER = "player";
	
	public static String PREFS_D_TEMPO = "time";
	public static String PREFS_D_SCORE = "score";
	public static String PREFS_D_ACERTOS = "acertos";
	public static String PREFS_D_ERRORS = "erros";
	public static String PREFS_D_INCREMENT = "inc";
	public static String PREFS_D_DECREMENT = "dec";
	
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
	private int acertos = 0, erros = 0;
	private int pontos = 0, tempo = 0;
	private int state = S_STOPED;
	private int increment = 1, decrement = 1;
	//private boolean charMode = true;		// Alfabeto = 65 - 90

	//public static int currentUserId = 1;
	
	private boolean update_asked = false;
	
	// Level variables
	private int currentLevel;
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
	private static boolean run = false;

	// Config
	private SharedPreferences prefs;
	
	// Database
	//public static Dcode

	// Timer
	private int tm = 250;
	private Timer t;

	//Menu item
	private MenuItem audioMenu;
	private MenuItem orderMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		// Variables
		btns_num = new int[btns_length];
		btns_cor = new int[btns_length];
		buffer = new int[btns_length];

		tempo = T_MED;

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

		time.setMax(T_MAX);
		time.setProgress(tempo);

		decrement_text = (TextView) findViewById(R.id.main_decrement);

		TextView version = (TextView) findViewById(R.id.main_version);
		version.setText("versão: " + version.getText());

		
		/*	*	*	*	Preferences 	*	Preferences 	*	*	*	*/
		
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

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

		// Timer
		try {
			t = new Timer();
			t.scheduleAtFixedRate(new TimerTask() {         
					@Override
					public void run() {
						runOnUiThread(new Runnable(){
								@Override
								public void run(){
									if(run){
										if(state == S_RUNNING){
											if(tempo > T_MIN){
												tempo -= (decrement > 1 ? decrement : 1) * levels[currentLevel].velocity;
												if(tempo <= T_MIN)
													perdeu();
											}
										}
										updateVisual();
									}
								}
							});
					}
				}, tm, tm);
		} catch(Exception ex){
			ErrorDialog.show(this, "Timer define", ex.getMessage());
		}

		try {
			refresh();
		} catch(Exception ex){
			ErrorDialog.show(this, "Refresh", ex.getMessage());
		}
		
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
		for(int i = btns_length - 1; i > 0; i--)
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
		if(!levels[currentLevel].charMode)	btns_num = getRandomArray(9, 0, btns_num);
		else btns_num = getRandomArray(25, 0, btns_num);
			
		btns_cor = getRandomArray(cor.length -1, 0, btns_cor);

		for(int i = 0; i < btns_length; i++){
			lay_cor[i].setBackgroundColor(cor[btns_cor[i]]);
			//btns[i].setText(charMode ? "" + (char) (CHAR_INIT + btns_num[i]) : "" + btns_num[i]);
			btns[i].setText(levels[currentLevel].charMode ? "" + (char) (CHAR_INIT + btns_num[i]) : "" + btns_num[i]);
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
		if(levels[currentLevel].charMode) 	num = button.getText().toString().toCharArray()[0] - CHAR_INIT;
		else 			num = Integer.parseInt(button.getText().toString());
		
		incrementBuffer(num);
		
		int a = buffer[0];//, b = buffer[1], c = buffer[2], d = buffer[3];
		
		int [] sorted = btns_num;
		java.util.Arrays.sort(sorted);
		
		if(buffer[3] != -1){
			acertou(); start();
		} else
		if(buffer[2] != -1){
			if(a != sorted[order ? 2 : 1])
				errou();
		} else
		if(buffer[1] != -1){
			if(a != sorted[order ? 1 : 2])
				errou();
		} else
		if(buffer[0] != -1){
			if(a != sorted[order ? 0 : 3])
				errou();
		}
	}
	
	public void updateVisual(){
		score.setText("Pontos: " + pontos);
		time.setProgress(tempo >= T_MIN ? tempo : T_MIN);
	}

	// Activity functions
	@Override
	protected void onResume(){
		this.run = true;
		
		try {
			if(state == S_PAUSED && this.pontos > 0){
				//this.run = true;
				super.onResume();
				return;
			}
			
			if(this.prefs.getInt(PREFS_D_SCORE, 0) > 0){
				
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
			}
				
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
		run = false;
		
		// Save game state
		if(state != S_STOPED){
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
			this.state = S_PAUSED;
		
		super.onPause();
	}

	@Override
	protected void onDestroy(){
		// Stop the thread
		t.cancel();
		
		super.onDestroy();
	}
	
	// Game functions
	public void start(){
		state = S_RUNNING;
	}

	public void pause(){
		state = S_PAUSED;
	}

	public void stop(){
		// Clean variables
		tempo = T_MED;
		acertos = erros = pontos = 0;
		decrement = increment = 1;
		
		state = S_STOPED;
		
		// Clean preferences
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
	
	// Level functions
	public void acertou(){
		som(A_ACERTOU);
		
		pontos += 5 * increment;
		acertos++; increment++;
		
		if(decrement > 1) decrement--;
		
		for(int i = 0; i < 2 * increment; i++)
			if(tempo < T_MAX + decrement) tempo++;
		
		refresh();
	}
	
	public void errou(){
		som(A_ERROU);
		
		increment = 1;
		
		if(state == S_RUNNING){ erros ++; decrement += 1;}
		if(state == S_PAUSED) start();
		
		enableButtons(true);
		buffer = clear(buffer, -1);
	}
	
	public void perdeu(){
		som(A_PERDEU);
		
		/*Score sc = new Score(prefs.getInt(PREFS_PLAYER, 1), acertos, erros, pontos);
		
		(new PerdeuDialog(this, sc){
			@Override
			public void onVerpontos(){
				this.dismiss();
				new ScoreDialog(MainActivity.this).show();
			}
		}).show();*/
		
		
		
		//Toast.makeText(this, "Em desenvolvimento", Toast.LENGTH_SHORT).show();
		
		stop();
		refresh();
	}
	
	public void levelUp(){
		if(currentLevel < levels.length -1){
			Toast.makeText(this, "Level Up!\nLevel " + (currentLevel + 1), Toast.LENGTH_SHORT).show();
			currentLevel++;
		}
	}

	// Audio
	public void som(int audioId){
		if(prefs.getBoolean(PREFS_AUDIO, false))
			if(audioId == A_ACERTOU)
				;
			else if(audioId == A_ERROU)
				MediaPlayer.create(this, R.raw.errou).start();
			else if(audioId == A_NAO_CONSEGUE)
				MediaPlayer.create(this, R.raw.nao_).start();
			else if(audioId == A_PERDEU)
				MediaPlayer.create(this, R.raw.nao).start();
	}

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

        if (id == R.id.menu_info) return true; else 
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
