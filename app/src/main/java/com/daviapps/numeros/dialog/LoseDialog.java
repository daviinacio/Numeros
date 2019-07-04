package com.daviapps.numeros.dialog;
import android.app.*;
import android.os.*;
import android.content.*;
import com.daviapps.numeros.*;
import com.daviapps.numeros.domain.*;

public class LoseDialog extends Dialog {
	private OnTryAgainListener onTryAgainListener;
	private Game game;
	
	public LoseDialog(Context context){
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_lose);
	}

	@Override
	protected void onStart(){
		super.onStart();
		
		
	}
	
	public void setOnTryAgain(OnTryAgainListener listener){
		this.onTryAgainListener = listener;
	}
	
	public interface OnTryAgainListener {
		void onTryAgain();
	}
	
	public static class Builder {
		private LoseDialog dialog;
	}
}
