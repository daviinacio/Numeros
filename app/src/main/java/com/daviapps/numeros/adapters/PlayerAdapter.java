package com.daviapps.numeros.adapters;
import android.widget.*;
import android.view.*;
import com.daviapps.numeros.database.*;
import com.daviapps.numeros.domain.*;
import android.content.*;
import java.util.*;
import com.daviapps.numeros.*;
import com.daviapps.numeros.interfaces.*;

public class PlayerAdapter extends BaseAdapter implements GenericBaseAdapter<Player> {
	// Model
	private DataSet<Player> db;
	private List<Player> items;
	
	private Context context;
	
	public PlayerAdapter(Context context){
		this.context = context;
		this.db = new PlayerDB(context);
		this.items = new ArrayList<>();
	}

	@Override
	public int getCount(){
		return items.size();
	}

	@Override
	public long getItemId(int i){
		return items.get(i).getId();
	}

	@Override
	public View getView(int i, View v, ViewGroup gv){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.adapter_player, null);
		
		// Layout
		TextView nickname = layout.findViewById(R.id.adapter_player_nickname);
		TextView maxscore = layout.findViewById(R.id.adapter_player_maxscore);
		
		// Data
		Player player = this.items.get(i);
		
		nickname.setText(player.getNickname());
		maxscore.setText(Integer.toString(player.getMaxScore()));
		
		return layout;
	}
	
	@Override
	public Player addItem(Player e){
		e = this.db.insert(e);
		this.items.add(e);
		this.notifyDataSetChanged();
		return e;
	}
	
	@Override
	public Player getItem(int i){
		return this.items.get(i);
	}

	@Override
	public void setItem(Player e){
		this.items.set(this.items.indexOf(e), e);
		this.db.update(e);
		this.notifyDataSetChanged();
	}

	@Override
	public void removeItem(Player e){
		if(e.getNickname().equals("anonymous")){
			Toast.makeText(context, "Esse player não pode ser removido", Toast.LENGTH_SHORT).show();
			return;
		}
		
		this.items.remove(e);
		this.db.delete(e);
		this.notifyDataSetChanged();
	}
	
	@Override
	public void load(){
		this.load("1 = 1");
	}
	
	@Override
	public void load(String where){
		this.items = db.select(where);
		this.notifyDataSetChanged();
	}
}
