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

public class MainActivity extends Activity {
	/*
		version 1.5.1 :: done
		 - Update checker
		 
		version 1.5.2 :: done
		 - Update downloader
		 
		version 1.5.3
		 - File path changed
	
		version 1.5.4
		 - Replace JSONPreferences to SharedPreferences
		 - Remove JSONOreferences
		 
		version 1.5.5
		 - Replace DDBTableFile to SQLite
		 - Remove DDBTableFile references
		
		version 1.5.6
		 - Make DAppsBackup
		
	*/
	
	// Defines
	private static final int A_ACERTOU = 1000, A_ERROU = 1001, A_NAO_CONSEGUE = 1002, A_PERDEU = 1003;
	private static final int S_RUNNING = 1010, S_PAUSED = 1011, S_STOPED = 1012;
	
	private static final int btns_length = 4;
	private static final int T_MIN = 0, T_MAX = 200;
	private static final int T_MED = ((T_MIN + T_MAX) / 2);
	
	private static final int D_TEMPO = T_MED, D_PONTOS = 0, D_ACERTOS = 0, D_ERROS = 0, D_INCREMENT = 1, D_DECREMENT = 1;
	
	private static final int CHAR_INIT = 65;
	
	// Statics
	//public static String download_links = "http://localhost:12345/download_list.json";
	//public static String download_links = "http://daviapps.6te.net/json/download_list.json";

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
	private LinearLayout main_lay;
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
	//public static DCodePreferences _old_prefs;
	//public static DCodePreferences3 preferences;
	public static Preferences <String, String> json_prefs;
	
	
	
	// Database
	//public static Dcode

	// Timer
	private int tm = 250;
	private Timer t;

	// Settings
	//private boolean audio = false;
	//private boolean order = false;

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

		 main_lay = (LinearLayout) findViewById(R.id.main_lay);
		 
		 //debug
		 //ErrorDialog.show(this, "Package", getExternalFilesDir("update.apk").getPath());
		 
		 // Config
		 
		 try {
			 this.json_prefs = new JSONPreferences(this, "settings.json");
			 
		 } catch(IOException ex){
			 ErrorDialog.show(this, "Fatal Error", "MainActivity.onCreate(): Init prefs error \n" + ex.getMessage(), new DialogInterface.OnDismissListener () {
				 @Override
				 public void onDismiss(DialogInterface p1){
					 MainActivity.this.finish();
				 }
			 });
		 }
		 
		 // Database
		 try {
			 ddb.setup(this);
			 
		 } catch (Exception e){
			 new AlertDialog.Builder(this)
			 	.setPositiveButton("Ok", null)
			 	.setTitle("Database setup error")
				.setMessage(e.getMessage())
				.show();
		 }

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
		else			btns_num = getRandomArray(25, 0, btns_num);
			
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
		
		boolean order = this.json_prefs.getBoolean(PREFS_ORDER, false);
		
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
			
			if(this.json_prefs.getInt(PREFS_D_SCORE, 0) > 0){
				
				new AlertDialog.Builder(this)
					.setTitle("Partida pendente")
					.setMessage("Existe uma partida pendente salva")
					.setPositiveButton("Continuar", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2){
							MainActivity.this.state = S_PAUSED;

							// Load game states
							MainActivity.this.tempo = MainActivity.this.json_prefs.getInt(PREFS_D_TEMPO, 0);
							MainActivity.this.pontos = MainActivity.this.json_prefs.getInt(PREFS_D_SCORE, 0);
							MainActivity.this.acertos = MainActivity.this.json_prefs.getInt(PREFS_D_ACERTOS, 0);
							MainActivity.this.erros = MainActivity.this.json_prefs.getInt(PREFS_D_ERRORS, 0);
							MainActivity.this.increment = MainActivity.this.json_prefs.getInt(PREFS_D_INCREMENT, 0);
							MainActivity.this.decrement = MainActivity.this.json_prefs.getInt(PREFS_D_DECREMENT, 0);
							
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
			Updater.checkForUpdate(this);
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

			this.json_prefs.put(PREFS_D_TEMPO, tempo);
			this.json_prefs.put(PREFS_D_SCORE, pontos);
			this.json_prefs.put(PREFS_D_ACERTOS, acertos);
			this.json_prefs.put(PREFS_D_ERRORS, erros);
			this.json_prefs.put(PREFS_D_INCREMENT, increment);
			this.json_prefs.put(PREFS_D_DECREMENT, decrement);
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
		this.json_prefs.put(PREFS_D_TEMPO, tempo);
		this.json_prefs.put(PREFS_D_SCORE, pontos);
		this.json_prefs.put(PREFS_D_ACERTOS, acertos);
		this.json_prefs.put(PREFS_D_ERRORS, erros);
		this.json_prefs.put(PREFS_D_INCREMENT, increment);
		this.json_prefs.put(PREFS_D_DECREMENT, decrement);
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
		
		Score sc = new Score(json_prefs.getInt(PREFS_PLAYER, 1), acertos, erros, pontos);
		
		(new PerdeuDialog(this, sc){
			@Override
			public void onVerpontos(){
				this.dismiss();
				new ScoreDialog(MainActivity.this).show();
			}
		}).show();
		
		
		
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
		if(json_prefs.getBoolean(PREFS_AUDIO, false))
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
			this.json_prefs.put(PREFS_AUDIO, active);

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
			this.json_prefs.put(PREFS_ORDER, order);
			
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
			
			confAudio(this.json_prefs.getBoolean(PREFS_AUDIO, false));
			confOrder(this.json_prefs.getBoolean(PREFS_ORDER, true));
			
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
			run = false;
			new ScoreDialog(MainActivity.this){
				@Override
				public void onStop(){
					run = true;
				}
			}.show();
		}
		else
		if (id == R.id.menu_info_settings){
			Toast.makeText(this, "Função em desenvolvimento", Toast.LENGTH_SHORT).show();
		}
		else
		if (id == R.id.menu_info_players){
			run = false;
			new PlayerDialog(MainActivity.this){
				@Override
				public void onStop(){
					run = true;
				}
			}.show();
		}
		else
		if (id == R.id.menu_som_power) {
			confAudio(!this.json_prefs.getBoolean(PREFS_AUDIO, false));
            return true;
		}
		else 
		if(id == R.id.menu_mode) {
			confOrder(!this.json_prefs.getBoolean(PREFS_ORDER, false));
			refresh();
            return true;
		}

        return super.onOptionsItemSelected(item);
    }
}

// Uploader prototype
class Updater {
	public static boolean checkForUpdate(Context context){
		new UpdateChecker(context).execute(context.getString(R.string.update_index));
		
		return false;
	}
}

class ApkVersion {
	private Version version;
	private Date release_date;
	private URL link;
	
	public ApkVersion(){}
	
	public ApkVersion(Version version, Date release_date, URL link){
		this.version = version; this.release_date = release_date; this.link = link;
	}

	public void setVersion(Version version){ this.version = version; }
	public Version getVersion(){ return version; }

	public void setReleaseDate(Date release_date){ this.release_date = release_date; }
	public Date getReleaseDate(){ return release_date; }

	public void setLink(URL link){ this.link = link; }
	public URL getLink(){ return link; }
}

class UpdateChecker extends AsyncTask<String, Integer, ApkVersion> {
	private static int S_ALRIGHT = 1, S_EX_IO = 2, S_EX_JSON;
	
	private Context context;
	private int status = 0;
	
	public UpdateChecker(Context context){
		this.context = context;
	}

	@Override
	protected void onPreExecute(){
		//Toast.makeText(this.context, "Buscando atualizações..", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected ApkVersion doInBackground(String..._url){
		ApkVersion version = null;
		
		try {
			URL url = new URL(_url[0]);
			//Scanner sc = new Scanner(new InputStreamReader(url.openStream())).useDelimiter("\\A");
			Scanner sc = new Scanner(url.openStream()).useDelimiter("\\A");
			
			JSONArray dLinks = new JSONArray(sc.next());
			
			sc.close();
			
			for(int i = 0; i < dLinks.length(); i++){
				// Load json index
				JSONObject dApp = dLinks.getJSONObject(i);
				
				// Check application name
				if(dApp.getString("name").equals(this.context.getString(R.string.app_name))){
					JSONArray dVersions = dApp.getJSONObject("platforms").getJSONArray("android");
					JSONObject lastVersion = dVersions.getJSONObject(0);
					
					version = new ApkVersion();
					
					version.setVersion(new Version(lastVersion.getJSONArray("version").join(".")));
					version.setReleaseDate(new Date(lastVersion.getJSONArray("release_date").join("/")));
					version.setLink(new URL(lastVersion.getString("link")));
					
					status = S_ALRIGHT;
					
					break;
				}
			}

		}
		catch (JSONException ex){ status = S_EX_JSON; }
		catch (IOException ex){ status = S_EX_IO; }
		
		return version;
	}

	@Override
	protected void onPostExecute(final ApkVersion result){
		try {
			if(status == S_ALRIGHT){
				if(result != null){
					int compare = result.getVersion().compareTo(new Version(this.context.getString(R.string.app_version)));

					// Aplicatico desatualizado
					if(compare == 1){
						//Toast.makeText(this.context, "Nova versão disponível", Toast.LENGTH_SHORT).show();

						new AlertDialog.Builder(this.context)
							.setCancelable(false)
							.setTitle("Nova atualização disponível")
							.setMessage(String.format("Versão: %s\nLançado em: %s", result.getVersion(), result.getReleaseDate().toLocaleString().split(" 00")[0]))
							.setPositiveButton("Atualizar", new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface p1, int p2){
									new UpdateDownloader(UpdateChecker.this.context).execute(result);
								}
							})
							.setNegativeButton("Ignorar", null)
							.show();
					}


				} else
					Toast.makeText(this.context, "Não foi possivel carregar a última versão.", Toast.LENGTH_LONG).show();

				this.cancel(true);
			}
			else
			if(status == S_EX_IO)
				Toast.makeText(this.context, "Não foi possivel buscar novas atualizações.\nVerifique a sua conexão com a internet.", Toast.LENGTH_LONG).show();
			else
			if(status == S_EX_JSON)
				Toast.makeText(this.context, "O index de download está corrompido.", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(this.context, "Aconteceu um erro inesperado ao obter novas atualizações.", Toast.LENGTH_LONG).show();
			
		}
		catch(Exception ex){
			ErrorDialog.show(this.context, "UpdateChecker.onPostExecute", ex.getClass().getName() + " - " + ex.getMessage());
		}
		
		super.onPostExecute(result);
	}
}

class UpdateDownloader extends AsyncTask<ApkVersion, Integer, File> {
	private Context context;
	private ProgressDialog dialog;
	
	public UpdateDownloader(Context context){
		this.context = context;
		this.dialog = new ProgressDialog(context);
	}

	@Override
	protected void onPreExecute(){
		this.dialog.setCancelable(false);
		this.dialog.setTitle("Baixando");
		this.dialog.setProgressNumberFormat("%1dKB / %2dKB");
		this.dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		this.dialog.setMax(1);
		this.dialog.show();
		
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Integer...progress){
		this.dialog.setProgress(progress[0]);
		
		super.onProgressUpdate(progress);
	}

	@Override
	protected File doInBackground(ApkVersion...lastVersion){
		try {
			int download_length_divide = 1024;
			
			URL url = lastVersion[0].getLink();
			File file = this.context.getExternalFilesDir("update.apk");
			
			// Network input
			
			HttpURLConnection ct = (HttpURLConnection) url.openConnection();
			//ct.setRequestMethod("GET");
			//ct.setDoInput(true);
			ct.connect();
			
			this.dialog.setMax(ct.getContentLength() / download_length_divide);
			
			// 8k buffer
			InputStream is = new BufferedInputStream(url.openStream(), 8129);
			
			// File output
			file.getParentFile().mkdirs();
			
			if(file.exists())
				file.delete();
			
			OutputStream os = new FileOutputStream(file);
			byte data[] = new byte[1024];
			int count = 0;
			int progress = 0;
			
			// Write file
			while((count = is.read(data)) != -1){
				progress += count;
				publishProgress(progress / download_length_divide);
				
				os.write(data, 0, count);
				
				//try{Thread.sleep(10);} catch(Exception ex){}
			}
			
			os.flush();
			
			os.close();
			is.close();
			
			return file;
			
			
		} catch(IOException ex){ return null; }
	}

	@Override
	protected void onPostExecute(final File result){
		this.dialog.dismiss();
		
		if(result == null){
			this.dialog.dismiss();
			Toast.makeText(context, "Download error", Toast.LENGTH_SHORT).show();
			return;
		}
		
		new AlertDialog.Builder(this.context)
			.setTitle("Download concluido")
			.setMessage("Agora falta pouco.\nPressione INSTALAR para concluir")
			.setCancelable(false)
			.setPositiveButton("Instalar", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2){
					Intent intent = new Intent(Intent.ACTION_VIEW);
					Uri uri = Uri.fromFile(result);
					
					intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, uri);
					intent.setDataAndType(uri, "application/vnd.android.package-archive");
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
					
					context.startActivity(intent);
				}
			})
			.show();
		
		
		super.onPostExecute(result);
	}
}

// Preferences prototype

interface Preferences <K extends Object, V extends Object> {
	void put(K key, V value);
	void put(K key, int value);
	void put(K key, float value);
	void put(K key, boolean value);

	V get(K key);
	V get(K key, V notFound);
	int getInt(K key);
	int getInt(K key, int notFound);
	float getFloat(K key);
	float getFloat(K key, float notFound);
	boolean getBoolean(K key);
	boolean getBoolean(K key, boolean notFound);

	boolean contains(K key);
	void remove(K key);
	void clear();
}

class JSONPreferences implements Preferences<String, String> {

	// Properties
	private Map <String, String> items;
	private Map <String, String> metas;

	// java.io
	private File file;

	//private static String fileExt = "json";
	private static String JW_KEY = "key";
	private static String JW_VALUE = "value";

	public JSONPreferences(Context ct, String fileName) throws IOException {
		this(new File(ct.getFilesDir(), fileName));
		//this(String.format("android/data/%s/%s", ct.getPackageName(), fileName));
	}

	public JSONPreferences(String fileName) throws IOException {
		this(new File(Environment.getExternalStorageDirectory(), fileName));
	}

	public JSONPreferences(File file) throws IOException {
		this.file = file;
		this.items = new HashMap<>();
		this.metas = new HashMap<>();

		this.read();
	}

	@Override
	public void put(String key, String value){
		this.items.put(key, value);
		this.tryWrite();
	}

	@Override
	public void remove(String key){
		this.items.remove(key);
		this.tryWrite();
	}

	@Override
	public String get(String key){
		return this.get(key, null);
	}

	@Override
	public String get(String key, String notFound){
		return this.items.containsKey(key) ? this.items.get(key) : notFound;
	}

	@Override
	public boolean contains(String key){
		return this.items.containsKey(key);
	}

	@Override
	public void clear(){
		this.items.clear();
		this.tryWrite();
	}

	// More Getters and Setters

	public void put(String key, int value){
		this.put(key, Integer.toString(value));
	}

	public void put(String key, float value){
		this.put(key, Float.toString(value));
	}

	public void put(String key, boolean value){
		this.put(key, Boolean.toString(value));
	}

	@Override
	public int getInt(String key){
		return this.getInt(key, 0);
	}

	@Override
	public int getInt(String key, int notFound){
		return (int) Float.parseFloat(this.get(key, Integer.toString(notFound)));
	}

	@Override
	public float getFloat(String key){
		return this.getFloat(key, 0);
	}

	@Override
	public float getFloat(String key, float notFound){
		return Float.parseFloat(this.get(key, Float.toString(notFound)));
	}

	@Override
	public boolean getBoolean(String key){
		return this.getBoolean(key, false);
	}

	@Override
	public boolean getBoolean(String key, boolean notFound){
		return Boolean.parseBoolean(this.get(key, Boolean.toString(notFound)));
	}

	// File methods
	private void read() throws IOException {
		System.out.println("JSONPreferences.read()");
		if(!this.file.exists()){
			this.create();
			return;
		}

		// Read file
		InputStream is = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		StringBuilder sb = new StringBuilder();

		while(br.ready())
			sb.append(br.readLine());

		br.close(); isr.close(); is.close();

		// JSON implements

		try {
			// Parse file data to JSON Object
			JSONObject obj = new JSONObject(sb.toString());

			// Get the meta array
			if(!obj.isNull("metas")){
				JSONArray mts = obj.getJSONArray("metas");

				for(int i = 0; i < mts.length(); i++) {
					JSONObject mt = mts.getJSONObject(i);
					// Put the data on hash set
					this.metas.put(mt.getString(JW_KEY), mt.getString(JW_VALUE));
				}

				// Debug
				System.out.println("JSONPreferences.read() - Debug.metas");
				for(String key : this.metas.keySet()){
					System.out.println(String.format("%s: %s", key, this.metas.get(key)));
					//System.out.println(String.format("key: %s, value: %s", key, this.metas.get(key)));
				}
				System.out.println();
			}

			// Get the preferences array
			if(!obj.isNull("preferences")){
				JSONArray prefs = obj.getJSONArray("preferences");

				for(int i = 0; i < prefs.length(); i++) {
					JSONObject pr = prefs.getJSONObject(i);
					// Put the data on hash set
					this.items.put(pr.getString(JW_KEY), pr.getString(JW_VALUE));
				}

				// Debug
				System.out.println("JSONPreferences.read() - Debug.items");
				for(String key : this.items.keySet()){
					System.out.println(String.format("%s: %s", key, this.items.get(key)));
					//System.out.println(String.format("key: %s, value: %s", key, this.items.get(key)));
				}
				System.out.println();
			}

		} catch (JSONException ex){
			System.out.println("JSONPreferences.read(): JSONException - " + ex.getMessage());
		}
	}

	private void write() throws IOException {
		System.out.println("JSONPreferences.write()");

		// Create file if not exists
		if(!this.file.exists()){
			this.create();
			return;
		}

		OutputStream os = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);

		// JSON implements
		try {
			JSONObject obj = new JSONObject();

			// Put the meta array
			JSONArray mts = new JSONArray();

			for(String key : this.metas.keySet()){
				JSONObject mt = new JSONObject();

				mt.put("key", key);
				mt.put("value", this.metas.get(key));

				mts.put(mt);
			}

			obj.put("metas", mts);

			// Put the preferences array
			JSONArray prefs = new JSONArray();

			for(String key : this.items.keySet()){
				JSONObject pr = new JSONObject();

				pr.put("key", key);
				pr.put("value", this.items.get(key));

				prefs.put(pr);
			}

			obj.put("preferences", prefs);

			// Put JSON data to file
			bw.append(obj.toString());

			// Close file streamers
			bw.close(); osw.close(); os.close();

		} catch(JSONException ex){
			System.out.println("JSONPreferences.write(): JSONException - " + ex.getMessage());
		}
	}

	private void tryRead(){
		try { this.read(); }
		catch(IOException ex){
			System.out.println("JSONPreferences.tryRead(): IOException - " + ex.getMessage());
		}
	}

	private void tryWrite(){
		try { this.write(); }
		catch(IOException ex){
			System.out.println("JSONPreferences.tryWite(): IOException - " + ex.getMessage());
		}
	}

	private void create () throws IOException {
		System.out.println("JSONPreferences.create()");
		// Create old file case exists
		if(this.file.exists())
			this.file.delete();

		// Create file parent diretory
		this.file.getParentFile().mkdirs();
		// Create a new file
		this.file.createNewFile();

		// Write beginner data
		this.write();
	}
}

// FullStackOverFlow Code

class Version implements Comparable<Version> {

    private String version;

    public final String get() {
        return this.version;
    }

    public Version(String version) {
        if(version == null)
            throw new IllegalArgumentException("Version can not be null");
        if(!version.matches("[0-9]+(\\.[0-9]+)*"))
            throw new IllegalArgumentException("Invalid version format");
        this.version = version;
    }

    @Override
	public int compareTo(Version that) {
        if(that == null)
            return 1;
        String[] thisParts = this.get().split("\\.");
        String[] thatParts = that.get().split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        for(int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ?
                Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ?
                Integer.parseInt(thatParts[i]) : 0;
            if(thisPart < thatPart)
                return -1;
            if(thisPart > thatPart)
                return 1;
        }
        return 0;
    }

    @Override
	public boolean equals(Object that) {
        if(this == that)
            return true;
        if(that == null)
            return false;
        if(this.getClass() != that.getClass())
            return false;
        return this.compareTo((Version) that) == 0;
    }

	@Override
	public String toString(){
		return this.version;
	}
}
