package com.daviapps.numeros.dialog;
import android.app.*;
import android.content.*;
import android.os.*;

public class AboutDialog extends Dialog {
	public AboutDialog(Context context){
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
	}
	
	public static class Builder extends DialogBuilder<AboutDialog> {
		public Builder(Context context){
			super(new AboutDialog(context));
		}
	}
}
