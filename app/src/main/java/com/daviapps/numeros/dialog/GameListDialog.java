package com.daviapps.numeros.dialog;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.View.*;
import android.view.*;
import android.widget.AdapterView.*;
import android.widget.*;
import com.daviapps.numeros.*;
import com.daviapps.numeros.adapters.*;
import com.daviapps.numeros.domain.*;
import com.daviapps.numeros.database.*;
import android.content.DialogInterface.*;

public class GameListDialog extends Dialog implements OnClickListener, OnItemClickListener {
	// Layout
	private ListView list;
	private Button btnAdd;
	
	// Data
	private GameAdapter adapter;
	
	// Listener
	private OnSelectListener onSelectListener;
	
	public GameListDialog(Context context){
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_list_game);
		setCanceledOnTouchOutside(false);
		//setTitle("Partidas");
		
		// Layout
		this.list = findViewById(R.id.dialog_list_player_list);
		this.btnAdd = findViewById(R.id.dialog_list_player_add);
		
		// Listener
		this.list.setOnItemClickListener(this);
		this.btnAdd.setOnClickListener(this);
		
		// Data
		this.adapter = new GameAdapter(getContext());
		
		this.list.setAdapter(this.adapter);
		
		//this.adapter.load();
	}

	@Override
	protected void onStart(){
		super.onStart();
		
		this.btnAdd.setText(this.onSelectListener == null ? "Adicionar" : "Nova partida");
		this.setTitle(this.onSelectListener == null ? "Partidas" : "Partidas em andamento");
		
		if(onSelectListener == null)
			this.adapter.load();
		else
			this.adapter.load("status != \"" + EnvironmentGame.STOPPED + "\"");
	}

	@Override
	public void onClick(View v){
		switch(v.getId()){
			case R.id.dialog_list_player_add:
				Player p = new PlayerDB(getContext()).select("nickname = \"anonymous\"").get(0);
				
				Game g = adapter.addItem(new Game(p.getId()));
				
				if(onSelectListener != null){
					this.onSelectListener.onSelect(g);
					this.dismiss();
				}
				
				//Toast.makeText(getContext(), "Add: " + g, Toast.LENGTH_SHORT).show();
				

				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> p1, View v, int i, long p4){
		if(this.onSelectListener == null){
			//this.adapter.removeItem(adapter.getItem(i));
			Toast.makeText(getContext(), adapter.getItem(i).toString(), Toast.LENGTH_SHORT).show();
		}
		else {
			this.onSelectListener.onSelect(adapter.getItem(i));
			this.dismiss();
		}
		
		//Toast.makeText(getContext(), "Click (" + i + ")", Toast.LENGTH_SHORT).show();
	}
	
	public void setOnSelectListener(OnSelectListener listener){
		this.onSelectListener = listener;
	}
	
	public interface OnSelectListener {
		void onSelect(Game game);
	}

	public static class Builder {
		private GameListDialog dialog;
		
		public Builder(Context context){
			this.dialog = new GameListDialog(context);
		}
		
		public Builder setOnSelectListener(OnSelectListener listener){
			this.dialog.setOnSelectListener(listener);
			return this;
		}

		public Builder setOnCancelListener(DialogInterface.OnCancelListener listener){
			this.dialog.setOnCancelListener(listener);
			return this;
		}
		
		public GameListDialog build(){
			return dialog;
		}
	}
}
