package com.daviapps.numeros.old;
import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import com.daviapps.dcode.*;
import android.widget.AdapterView.*;
import android.os.*;
import java.io.*;
import com.daviapps.dcode.ddb.table.*;
import android.preference.*;
import com.daviapps.numeros.dialog.*;

/*class ScoreDialog extends Dialog {
	private ListView list;
	private ScoreAdapter adapter;
	
	public ScoreDialog(Context context){
		super(context);
		setContentView(R.layout.pontos);
		setTitle("Pontos");
		
		// Components
		list = (ListView) findViewById(R.id.pontos_lista);
		
		adapter = new ScoreAdapter(context);
		list.setAdapter(adapter);
		//updateLista();
		
		// onClick
		findViewById(R.id.pontos_sobre).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Toast.makeText(ScoreDialog.this.getContext(), "Sobre", Toast.LENGTH_SHORT).show();
			}
		});
		
		findViewById(R.id.pontos_settings).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Toast.makeText(ScoreDialog.this.getContext(), "Configuração", Toast.LENGTH_SHORT).show();
			}
		});
	}
}

class ScoreAdapter extends BaseAdapter implements AdapterBaseStruct<Score> {
	//private DCode dcode = new DCode(DCode.ARRAY);
	private Context context;
	private List<Score> list;
	//private DCodeFile dFile;

	public ScoreAdapter(Context context){ // To dialog
		this();
		this.context = context;
	}
	
	public ScoreAdapter(){ // To anonymous
		list = ddb.scores.select(null);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Score getItem(int arg0) {
		return list.get(arg0);
	}

	public void addItem(Score item){
		ddb.scores.insert(item);
		
		list.add(item);
		onAdd(item);
		
		onUpdate();
	}

	@Override
	public void setItem(int i, Score item){
		ddb.scores.update(item);
		list.set(i, item);
		onUpdate();
	}

	public void removeItem(int i){
		this.removeItem(getItem(i));
	}

	@Override
	public void removeItem(Score item){
		ddb.scores.deleteById(item.getId());
		list.remove(item);
		onUpdate();
	}

	@Override
	public long getItemId(int arg0) {
		return list.get(arg0).getId();
	}

	@Override
	public View getView(final int i, View arg1, ViewGroup arg2) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.pontos_list, null);
		
		((TextView) layout.findViewById(R.id.pontos_list_user)).setText(list.get(i).getPlayer().getName());
		((TextView) layout.findViewById(R.id.pontos_list_pontos)).setText(list.get(i).getPontos() + "");
		((TextView) layout.findViewById(R.id.pontos_list_acertos)).setText(((int) (((double) list.get(i).getAcertos() / (list.get(i).getAcertos() + list.get(i).getErros())) * 100)) + "%");
		
		((Button) layout.findViewById(R.id.pontos_list_remove)).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				new AlertDialog.Builder(ScoreAdapter.this.context)
					.setTitle("Apagar ponto")
					.setMessage("Este ponto será apagado.\nClique em 'ok' para continuar")
					.setNegativeButton("Cancelar", null)
					.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2){
							removeItem(i);
						}
					})
					.setCancelable(false)
					.show();
			}
		});
		
		return layout;
	}
	
	public void onAdd(Score pt){}
	
	public void onRemove(int i){}
	
	public void onUpdate(){
		this.notifyDataSetChanged();
	}
}

class Score {
	private int id, user, acertos, erros, pontos;
	
	public Score(){ }
	
	public Score(int id){
		this.id =id;
	}
	
	public Score(int id, int user, int acertos, int erros, int pontos){
		this.id = id; this.user = user; this.acertos = acertos; 
		this.erros = erros; this.pontos = pontos;
	}
	
	public Score(int player, int acertos, int erros, int pontos){
		this(0, player, acertos, erros, pontos);
	}
	
	public int getId(){
		return id;
	}

	public void setPlayer(int id){
		this.id = id;
	}
	
	public OldPlayer getPlayer(){
		return ddb.players.selectById(user);
	}
	
	public int getAcertos(){ return acertos; }
	public int getErros(){ return erros; }
	public int getPontos(){ return pontos; }
}

class PerdeuDialog extends Dialog {
	private Score pt;
	private SharedPreferences prefs;
	
	public PerdeuDialog(final Context context, Score pt){
		super(context);
		setTitle("Você perdeu");
		setContentView(R.layout.perdeu);
		setCancelable(false);
		
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		this.pt = pt;
		
		//MainActivity.preferences.add(MainActivity.PREFS_PLAYER, 0);
		
		// Positive: Tentar novamente
		// Negative: Ver pontow
		
		// Components
		((TextView) findViewById(R.id.perdeu_acertos)).setText("" + pt.getAcertos());
		((TextView) findViewById(R.id.perdeu_erros)).setText("" + pt.getErros());
		((TextView) findViewById(R.id.perdeu_pontos)).setText("" + pt.getPontos());

		((TextView) findViewById(R.id.perdeu_acertos_label)).setText("Acerto" + (pt.getAcertos() > 1 ? "s" : ""));
		((TextView) findViewById(R.id.perdeu_erros_label)).setText("Erro" + (pt.getErros() > 1 ? "s" : ""));
		((TextView) findViewById(R.id.perdeu_pontos_label)).setText("Ponto" + (pt.getErros() > 1 ? "s" : ""));

		((TextView) findViewById(R.id.perdeu_media_label)).setText((int) (((double) pt.getAcertos() / (pt.getAcertos() + pt.getErros())) * 100) + "%");
		
		final Spinner players = (Spinner) findViewById(R.id.perdeu_player);
		
		ProgressBar media = (ProgressBar) findViewById(R.id.perdeu_media);

		media.setMax(pt.getAcertos() + pt.getErros());
		media.setProgress(pt.getAcertos());
		
		final PlayerAdapter pAdapter = new PlayerAdapter(context, PlayerAdapter.SPINNER){
			@Override
			public void onUpdate(){
				players.setAdapter(this);
			}
		};
		
		players.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView <?> p1, View v, int i, long p4){
				Toast.makeText(context, "Player id changed. [" + PerdeuDialog.this.pt.getId() + "] to [" + pAdapter.getItemId(i) + "]", Toast.LENGTH_SHORT).show();
				
				PerdeuDialog.this.pt.setPlayer((int) pAdapter.getItemId(i));
				
				SharedPreferences.Editor edit = PerdeuDialog.this.prefs.edit();
				edit.putInt(MainActivity.PREFS_PLAYER, pAdapter.getItem(i).getId());
				edit.commit();
				edit.apply();
			}
			
			@Override
			public void onNothingSelected(AdapterView <?> p1){}
		});
		
		players.setAdapter(pAdapter);
		//players.setSelection(MainActivity.prefs.getInt("player")); // Player saved
		
		findViewById(R.id.perdeu_tryAgain).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){ onTentarNovamente(); }
		});
		
		findViewById(R.id.perdeu_viewScores).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){ onVerpontos(); }
		});
	}

	@Override
	protected void onStop(){
		new ScoreAdapter().addItem(this.pt);
		super.onStop();
	}
	
	public void onVerpontos(){}
	public void onTentarNovamente(){this.dismiss();}
}

class EditPlayerDialog extends Dialog {
	private EditText editName;
	
	private OnSaveListener onSaveListener;
	
	public EditPlayerDialog(Context context){
		super(context);
		setContentView(R.layout.dialog_edit_player);
		setCanceledOnTouchOutside(false);
		setTitle("Novo player");
		
		editName = (EditText) findViewById(R.id.dialog_edit_player_edit_name);
		
		((Button) findViewById(R.id.dialog_edit_player_btn_save)).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				try {
					// Check for empty name
					if(editName.getText().toString().equals(""))
						throw new Exception("The name can't be empty");
						
					// Check for existent username
					if(ddb.players.count(String.format("name = %s", editName.getText().toString())) > 0)
						throw new Exception("This username already exists");
					
					OldPlayer item = new OldPlayer();
					
					item.setName(editName.getText().toString());

					if(onSaveListener != null)
						onSaveListener.onSave(item);

					dismiss();
				} catch(Exception ex){
					ErrorDialog.show(EditPlayerDialog.this.getContext(), "EditPlayerDialog->Save", ex.getClass().getSimpleName() + '\n' + ex.getMessage());
				}
			}
		});
		
	}
	
	public EditPlayerDialog setOnSaveListener(OnSaveListener listener){
		this.onSaveListener = listener;
		return this;
	}
	
	// Listeners
	public interface OnSaveListener {
		public void onSave(OldPlayer item);
	}
}

class PlayerDialog extends Dialog {
	private ListView list;
	private PlayerAdapter adapter;
	
	public PlayerDialog(final Context context){
		super(context);
		setContentView(R.layout.players);
		setTitle("Players");
		
		adapter = new PlayerAdapter(context, PlayerAdapter.LISTVIEW);
		
		((TextView) findViewById(R.id.players_lista_nenhumPonto)).setVisibility(
			ddb.players.count(null) == 0 ? View.VISIBLE : View.GONE
		);
		
		list = (ListView) findViewById(R.id.players_lista);
		list.setAdapter(adapter);
		
		((Button) findViewById(R.id.players_btn_new)).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				new EditPlayerDialog(context)
					.setOnSaveListener(new EditPlayerDialog.OnSaveListener(){
						@Override
						public void onSave(OldPlayer item){
							PlayerDialog.this.adapter.addItem(item);
						}
					}).show();
			}
		});
	}
}

class PlayerAdapter extends BaseAdapter implements AdapterBaseStruct<OldPlayer> {
	private Context context;
	private List<OldPlayer> list;
	private int mode = 0;
	
	public static int LISTVIEW = 0, SPINNER = 1;
	
	public PlayerAdapter(Context context){ // To dialog
		this(context, LISTVIEW);
	}

	public PlayerAdapter(Context context, int mode){ // To dialog
		this();
		this.context = context;
		this.mode = mode;
	}

	public PlayerAdapter(){ // To anonymous
		// Query load
		list = ddb.players.select(null);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public OldPlayer getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public void addItem(OldPlayer item){
		// Add on arraylist
		list.add(item);
		
		// Insert on database
		//ddb_old.players.insert("name, score", String.format("%s, %s", item.getName(), item.getMaxScore()));
		ddb.players.insert(item);
		
		onAdd(item);
		onUpdate();
	}
	
	@Override
	public void setItem(int i, OldPlayer item){
		list.set(i, item);
		ddb.players.update(item);
		onUpdate();
	}

	@Override
	public void removeItem(int i){
		removeItem(list.get(i));
		onUpdate();
	}
	
	@Override
	public void removeItem(OldPlayer item){
		if(item.getName().equals("anonymous")){
			ErrorDialog.show(context, "Erro ao remover", "Não é possivel remover esse player");
			return;
		}
		// Delete from database
		ddb.players.deleteById(item.getId());
		
		// Remove from arraylist
		list.remove(item);
		onRemove(item);
		onUpdate();
	}

	@Override
	public long getItemId(int i) {
		return list.get(i).getId();
	}

	@Override
	public View getView(final int i, View arg1, ViewGroup arg2) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.player_list, null);

		TextView name = (TextView) layout.findViewById(R.id.player_list_name);
		TextView maxPontos = (TextView) layout.findViewById(R.id.player_list_maxPontos);
		//TextView acertos = (TextView) layout.findViewById(R.id.pontos_list_acertos);
		
		Button remover = (Button) layout.findViewById(R.id.player_list_remove);
		
		name.setText(list.get(i).getName());
		maxPontos.setText("" + list.get(i).getMaxScore());
		
		if(mode == SPINNER){
			remover.setVisibility(View.GONE);
			maxPontos.setVisibility(View.GONE);
			//acertos.setVisibility(View.GONE);
			
			name.setTextSize(Utils.dpToPixel(context, 10));
		}

		remover.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				if(getItem(i).getName().equals("anonymous")){
					ErrorDialog.show(context, "Acesso negado", "Não é possivel remover esse player");
					return;
				}
				else {
					new AlertDialog.Builder(PlayerAdapter.this.context)
						.setTitle("Apagar player")
						.setMessage("Esse player será apagado.\nClique em 'ok' para continuar")
						.setNegativeButton("Cancelar", null)
						.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface p1, int p2){
								removeItem(i);
							}
						})
						.setCancelable(false)
						.show();
				}
			}
		});

		return layout;
	}

	public void onAdd(OldPlayer pl){}

	public void onRemove(OldPlayer pl){}
	
	public void onUpdate(){
		this.notifyDataSetChanged();
	}
}

class OldPlayer {
	private int id = 0;
	//private static DCode dcode = new DCode(DCode.NORMAL);
	private String name;
	private int maxScore;
	
	public OldPlayer(){ }
	
	public OldPlayer(int id){
		this.id = id;
	}
	
	public OldPlayer(int id, String name, int maxScore){
		this.id = id;
		this.name = name;
		this.maxScore = maxScore;
	}

	public OldPlayer(String name, int maxScore){
		this.name = name; this.maxScore = maxScore;
	}
	
	public int getId(){
		return this.id;
	}

	public String getName(){ return name; }
	public void setName(String name){
		this.name = name;
	}
	
	public int getMaxScore(){ return maxScore; }
	public void updateMaxScore(Score pt){
		int pontos = pt.getPontos();
	}
}

class Utils {
	public static int dpToPixel(Context ctx, float dps){
		final float scale = ctx.getResources().getDisplayMetrics().density;
		return (int) (dps * (scale + 0.1f));
	}
}

class Level {
	// Properties
	public boolean charMode;
	public int velocity;
	//public int pointsToUp
	
	public Level(boolean charMode, int velocity){
		this.charMode = charMode;
		this.velocity = velocity;
	}
}

interface AdapterBaseStruct <T> {
	void addItem(T item);
	void setItem(int i, T item);
	void removeItem(T item);
	void removeItem(int i);
}

abstract class DDBTableBase <T> {
	protected DDBTableFile table;
	
	protected DDBTableBase(Context ctx, String fileName){
		this(new File(ctx.getExternalFilesDir("database"), fileName));
	}
	
	protected DDBTableBase(File file){
		table = new DDBTableFile(file);
	}
	
	public abstract boolean setup() throws Exception;
	
	public abstract boolean insert(T item);
	public abstract boolean update(T item);
	public abstract List<T> select(String where, String order);
	
	public List<T> select(String where){
		return this.select(where, null);
	}
	
	public int count(String where){
		return table.count("*", where);
	}
	
	public T selectById(int id){
		return this.select(String.format("id = %s", id)).get(0);
	}
	
	public boolean delete(String where){
		table.delete(where);
		return true;
	}
	
	public boolean deleteById(int id){
		return this.delete(String.format("id = %s", id));
	}
}

class PlayersDB extends DDBTableBase <OldPlayer> {

	public PlayersDB(Context ctx){
		super(ctx, "players.ddb");
	}

	@Override
	public boolean setup(){
		// Create the player table file
		table.createIfNotExists(new DDBTableColumn[]{
				new DDBTableColumn("id", ColumnTypes.INTEGER, true, false, true, 1),
				new DDBTableColumn("name", ColumnTypes.STRING, false, false, true, 0),
				new DDBTableColumn("mscore", ColumnTypes.FLOAT, false, true, false, 0),
				new DDBTableColumn("lastPlay", ColumnTypes.DATETIME, false, true, false, 0)
			});

		// Add the anonymous user case not exists
		if(ddb.players.count("name = anonymous") == 0){
			ddb.players.insert(new OldPlayer("anonymous", 0));
		}
		return true;
	}

	@Override
	public boolean insert(OldPlayer item){
		table.insert("name, mscore", String.format("%s, %s", item.getName(), item.getMaxScore()));
		return true;
	}

	@Override
	public boolean update(OldPlayer item){
		table.update("name, mscore", String.format("%s, %s", item.getName(), item.getMaxScore()), "id =" + item.getId());
		return true;
	}

	@Override
	public List<OldPlayer> select(String where, String order){
		DDBTableData query = table.select("*", where, order);
		List<OldPlayer> result = new ArrayList<>();
		
		for(DDBTableRow row : query.rows){
			OldPlayer item = new OldPlayer(
				row.get("id").toInteger(),
				row.get("name").toString(),
				row.get("mscore").toInteger()
			);
			
			result.add(item);
		}
		
		return result;
	}
}

class ScoreDB extends DDBTableBase<Score> {
	// private int id, user, acertos, erros, pontos;
	public ScoreDB(Context ctx){
		super(ctx, "score.ddb");
	}

	@Override
	public boolean setup() throws Exception{
		// Create the score table file
		table.createIfNotExists(new DDBTableColumn[]{
				new DDBTableColumn("id", ColumnTypes.INTEGER, true, false, true, 1),
				new DDBTableColumn("player", ColumnTypes.INTEGER, false, true, false, 0),
				new DDBTableColumn("acertos", ColumnTypes.INTEGER, false, true, false, 0),
				new DDBTableColumn("erros", ColumnTypes.INTEGER, false, true, false, 0),
				new DDBTableColumn("pontos", ColumnTypes.INTEGER, false, true, false, 0)
			});
		return true;
	}

	@Override
	public boolean insert(Score item){
		table.insert("player, acertos, erros, pontos", String.format("%s, %s, %s, %s", 
					item.getPlayer().getId(), item.getAcertos(), item.getErros(), item.getPontos()));
		return true;
	}

	@Override
	public boolean update(Score item){
		table.update("player. acertos, erros, pontos", String.format("%s, %s, %s, %s", 
					item.getPlayer().getId(), item.getAcertos(), item.getErros(), item.getPontos()),
					String.format("id = %s", item.getId()));
		return true;
	}

	@Override
	public List<Score> select(String where, String order){
		DDBTableData query = table.select("*", where, order);
		List<Score> result = new ArrayList<>();

		for(DDBTableRow row : query.rows){
			Score item = new Score(
				row.get("id").toInteger(),
				row.get("player").toInteger(),
				row.get("acertos").toInteger(),
				row.get("erros").toInteger(),
				row.get("pontos").toInteger()
			);

			result.add(item);
		}

		return result;
	}
}

class ddb {
	
	public static PlayersDB players;
	public static ScoreDB scores;
	
	
	public static void setup(Context context) throws Exception {
		ddb.players = new PlayersDB(context);
		ddb.scores = new ScoreDB(context);
		
		players.setup();
		scores.setup();
	}
}*/

/*@Entity
class Player {
	@PrimaryKey
	public int uid;
}*/
