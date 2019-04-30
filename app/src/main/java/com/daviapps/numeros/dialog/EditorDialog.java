package com.daviapps.numeros.dialog;
import android.app.*;
import android.content.*;

class EditorDialog<E> extends Dialog {
	private OnDoneListener<E> onDoneListener;
	
	public EditorDialog(Context context){
		super(context);
	}
	
	public EditorDialog(Context context, int theme){
		super(context, theme);
	}
	
	// Listener method
	protected void done(E item){
		if(this.onDoneListener != null){
			this.onDoneListener.onDone(item);
			this.dismiss();
		}
	}
	
	// Listener setter
	public void setOnDoneListener(OnDoneListener listener){
		this.onDoneListener = listener;
	}
	
	// Inteface
	public interface OnDoneListener<E> {
		void onDone(E item);
	}
}
