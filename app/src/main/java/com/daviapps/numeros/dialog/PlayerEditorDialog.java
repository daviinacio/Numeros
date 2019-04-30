package com.daviapps.numeros.dialog;
import com.daviapps.numeros.domain.*;
import android.content.*;
import android.os.*;
import com.daviapps.numeros.*;

public class PlayerEditorDialog extends EditorDialog<Player> {
	// Variables
	private Player item;
	
	
	// Layout
	
	
	
	public PlayerEditorDialog(Context context){
		this(context, new Player());
	}
	
	public PlayerEditorDialog(Context context, Player player){
		super(context);
		this.item = player;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		setContentView(R.layout.dialog_editor_player);
	}
}
