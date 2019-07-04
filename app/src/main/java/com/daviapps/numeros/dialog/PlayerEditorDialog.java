package com.daviapps.numeros.dialog;
import com.daviapps.numeros.domain.*;
import android.content.*;
import android.os.*;
import com.daviapps.numeros.*;
import android.widget.*;
import android.view.*;
import com.daviapps.numeros.database.*;

public class PlayerEditorDialog extends EditorDialog<Player> {
	// Visual
	private Button btnSave, btnCancel, btnDelete;
	private TextView labelPassword1, labelPassword2;
	private EditText editName, editPassword1, editPassword2;
	private boolean state = false;
	
	// Layout
	public PlayerEditorDialog(Context context){
		super(context);
	}
	
	public PlayerEditorDialog(Context context, Player player){
		super(context, player);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		setContentView(R.layout.dialog_editor_player);
		setTitle(R.string.title_player_editor);
		
		// Button
		this.btnSave = findViewById(R.id.dialog_editor_save);
		this.btnCancel = findViewById(R.id.dialog_editor_cancel);
		this.btnDelete = findViewById(R.id.dialog_editor_delete);
		
		// TextView
		this.labelPassword1 = findViewById(R.id.dialog_editor_player_label_passwd1);
		this.labelPassword2 = findViewById(R.id.dialog_editor_player_label_passwd2);
		
		// EditText
		this.editName = findViewById(R.id.dialog_editor_player_edit_nickname);
		this.editPassword1 = findViewById(R.id.dialog_editor_player_edit_passwd1);
		this.editPassword2 = findViewById(R.id.dialog_editor_player_edit_passwd2);
		
		this.btnSave.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				save();
			}
		});
		
		this.btnCancel.setOnClickListener(new View.OnClickListener(){	
			@Override
			public void onClick(View v){
				cancel();
			}
		});
			
		this.btnDelete.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				delete();
			}
		});
		
	}

	@Override
	protected Player onNew(){
		this.state = true;
		
		this.btnDelete.setVisibility(View.GONE);
		
		this.labelPassword1.setText("Senha");
		this.labelPassword2.setText("Confirme a senha");
		
		//this.editPassword2.setVisibility(View.GONE);
		
		return new Player();
	}

	@Override
	protected void onEdit(Player item){
		this.state = false;
		
		if(!item.getPassword().isEmpty()){
			this.labelPassword1.setText("Senha atual");
			this.labelPassword2.setText("Nova senha");
		}
		
		this.editName.setText(item.getNickname());
	}

	@Override
	protected void putValuesOnObject(Player item) throws Exception {	
		if(this.editName.getText().toString().isEmpty())
			throw new IllegalArgumentException("O campo de nickname não pode estar vazio!");
		
		item.setNickname(this.editName.getText().toString());
		
		// New item
		if(state){
			if(!this.editPassword1.getText().toString().isEmpty()){
				if(this.editPassword1.getText().toString().equals(this.editPassword2.getText().toString())){
					item.setPassword(this.editPassword1.getText().toString());
				} 
				else
					throw new IllegalArgumentException("A senha não confere");
			}
		}
		// Edit item
		else {
			
		}
	}
	
	public static final class Builder extends DialogBuilder<PlayerEditorDialog> {
		public Builder(Context context){
			super(new PlayerEditorDialog(context));
		}
		
		public Builder(Context context, Player player){
			super(new PlayerEditorDialog(context, player));
		}
		
		public Builder setOnSaveListener(OnSaveListener<Player> listener){
			this.dialog.setOnSaveListener(listener);
			return this;
		}

		public Builder setOnDeleteListener(OnDeleteListener<Player> listener){
			this.dialog.setOnDeleteListener(listener);
			return this;
		}
	}
}
