package com.daviapps.numeros.adapters;
import android.widget.*;
import com.daviapps.numeros.interfaces.*;
import com.daviapps.numeros.domain.*;
import android.content.*;
import android.view.*;
import java.util.*;
import com.daviapps.numeros.database.*;
import com.daviapps.numeros.*;
import java.text.*;
import android.graphics.*;

public class GameAdapter extends BaseAdapter implements GenericBaseAdapter<Game> {
	private List<Game> items;
	private DataSet<Game> db;
	private DataSet<Player>  pdb;
	
	private Context context;
	
	private SimpleDateFormat dateFormat;
	
	public GameAdapter(Context context){
		this.items = new ArrayList<>();
		this.db = new GameDB(context);
		this.pdb = new PlayerDB(context);
		
		this.context = context;
		this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	}

	@Override
	public int getCount(){
		return this.items.size();
	}

	@Override
	public long getItemId(int i){
		return this.getItem(i).getId();
	}

	@Override
	public View getView(int i, View v, ViewGroup p3){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.adapter_game, null);
		
		// Layout
		//TextView player = layout.findViewById(R.id.adapter_game_player);
		TextView average = layout.findViewById(R.id.adapter_game_average);
		TextView score = layout.findViewById(R.id.adapter_game_score);
		TextView date = layout.findViewById(R.id.adapter_game_date);
		
		// Data
		Game item = this.items.get(i);
		
		//player.setText("" + pdb.selectById(item.getPlayer()).getNickname());
		
		try {
			average.setText(String.format("%s%%", item.getAverage()));
		}
		catch(ArithmeticException ex){
			average.setText("0%");
		}
		
		score.setText(Integer.toString(item.getScore()));
		date.setText(dateFormat.format(item.getDateStart()));
		
		if(item.getStatus() == EnvironmentGame.STOPPED){
			//layout.setBackgroundColor(Color.rgb(128, 32, 32));
			layout.setBackgroundColor(Color.argb(50, 255, 0, 0));
		}
		
		return layout;
	}

	@Override
	public Game addItem(Game e){
		e = this.db.insert(e);
		this.items.add(e);
		this.notifyDataSetChanged();
		return e;
	}

	@Override
	public Game getItem(int i){
		return this.items.get(i);
	}

	@Override
	public void setItem(Game e){
		this.db.update(e);
		this.items.set(this.items.indexOf(e), e);
		this.notifyDataSetChanged();
	}

	@Override
	public void removeItem(Game e){
		if(e.getStatus() != EnvironmentGame.STOPPED && e.getScore() > 1){
			Toast.makeText(context, "Somente partidas encerradas podem ser deletadas", Toast.LENGTH_SHORT).show();
			return;
		}
		
		this.db.delete(e);
		this.items.remove(e);
		this.notifyDataSetChanged();
	}

	@Override
	public void load(){
		this.load("1 = 1");
	}

	@Override
	public void load(String where){
		this.items = this.db.select(where);
		this.notifyDataSetChanged();
	}
}
