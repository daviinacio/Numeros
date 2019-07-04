package com.daviapps.numeros.dialog;
import android.app.*;
import android.content.*;
import android.widget.*;
import com.daviapps.numeros.*;

public abstract class EditorDialog<E> extends Dialog {
	// Model
	private E item;
	
	// Listener
	private OnSaveListener<E> onSaveListener;
	private OnDeleteListener<E> onDeleteListener;
	
	// Constructors
	public EditorDialog(Context context){
		super(context);
	}
	
	public EditorDialog(Context context, E item){
		super(context);
		this.item = item;
	}

	// Dialog event
	@Override
	protected void onStart(){
		
		if(this.item == null){
			this.item = this.onNew();
		}
		else {
			this.onEdit(this.item);
		}
		
		super.onStart();
	}
	
	// Methods to override
	protected abstract void onEdit(E item);
	protected abstract E onNew();
	protected abstract void putValuesOnObject(E item) throws Exception;
	
	// Listener caller
	protected void save(){
		try {
			// Fill the item object with the form data
			this.putValuesOnObject(this.item);
			
			// Call on save listener
			if(this.onSaveListener != null){
				if(!this.onSaveListener.onSave(this.item)){
					Toast.makeText(getContext(), "Error on save", Toast.LENGTH_SHORT).show();
					return;
				}
				this.dismiss();
			}
		} 
		catch(IllegalArgumentException ex){
			Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
		}
		catch(Exception ex){
			Toast.makeText(getContext(), "Check the inpputed data", Toast.LENGTH_SHORT).show();
		}
		
		
		/*if(!this.putValuesOnObject(this.item)){
			Toast.makeText(getContext(), "Check the inpputed data", Toast.LENGTH_SHORT).show();
			return;
		}*/
		
		
	}
	
	protected void delete(){
		new AlertDialog.Builder(getContext())
				.setTitle(R.string.title_editordialog_delete_confirm)
				.setMessage(R.string.message_editordialog_delete_confirm)
				.setNegativeButton(R.string.cancel, null)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface p1, int p2){
						// Call on delete listener
						if(EditorDialog.this.onDeleteListener != null){
							if(!EditorDialog.this.onDeleteListener.onDelete(EditorDialog.this.item)){
								Toast.makeText(getContext(), "Error on delete", Toast.LENGTH_SHORT).show();
								return;
							}
							EditorDialog.this.dismiss();
						}
					}
				})
				.show();
	}
	
	// Listener setter
	public void setOnSaveListener(OnSaveListener<E> listener){
		this.onSaveListener = listener;
	}
	
	public void setOnDeleteListener(OnDeleteListener<E> listener){
		this.onDeleteListener = listener;
	}
	
	// Listener
	public interface OnSaveListener<E extends Object> {
		boolean onSave(E item);
	}
	
	public interface OnDeleteListener<E extends Object> {
		boolean onDelete(E item);
	}
}
