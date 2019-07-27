package com.daviapps.numeros.dialog;
import android.app.*;
import android.content.*;
import android.os.*;

public class SettingsDialog extends Dialog {
	public SettingsDialog(Context context){
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
	}

	public static class Builder extends DialogBuilder<SettingsDialog> {
		public Builder(Context context){
			super(new SettingsDialog(context));
		}
	}
}
