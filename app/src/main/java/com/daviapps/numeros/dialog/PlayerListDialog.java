package com.daviapps.numeros.dialog;
import android.app.*;
import android.content.*;
import android.os.*;
import com.daviapps.numeros.*;
import android.view.View.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.daviapps.numeros.database.*;
import com.daviapps.numeros.domain.*;
import com.daviapps.numeros.adapters.*;

public class PlayerListDialog extends Dialog implements OnClickListener, OnItemClickListener {
	
	// Layout
	private Button btnAdd;
	private ListView lvList;
	
	// Data
	private PlayerAdapter adapter;
	
	
	public PlayerListDialog(Context context){
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_list_player);
		
		// Dialog
		this.setTitle("Players");
		this.setCanceledOnTouchOutside(false);
		
		// Layout
		this.btnAdd = findViewById(R.id.dialog_list_player_add);
		this.lvList = findViewById(R.id.dialog_list_player_list);
		
		// Listener
		this.btnAdd.setOnClickListener(this);
		this.lvList.setOnItemClickListener(this);
		
		// Data
		this.adapter = new PlayerAdapter(this.getContext());
		
		// Adapter
		this.lvList.setAdapter(this.adapter);
		
		// Setup
		this.adapter.load();
		
	}

	@Override
	public void onClick(View view){
		switch(view.getId()){
			case R.id.dialog_list_player_add:
				//Toast.makeText(this.getContext(), "Add", Toast.LENGTH_SHORT).show();
				
				PlayerDB db = new PlayerDB(this.getContext());
				
				Player p = new Player("player" + (db.count() + 1));
				
				db.insert(p);
				
				Toast.makeText(this.getContext(), p.toString(), Toast.LENGTH_SHORT).show();
				
				this.adapter.load();
				
				break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> p1, View v, int i, long p4){
		adapter.removeItem(adapter.getItem(i));
	}
}
