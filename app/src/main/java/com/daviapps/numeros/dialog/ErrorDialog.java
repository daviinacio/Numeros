package com.daviapps.numeros.dialog;
import android.content.*;
import android.app.*;

public class ErrorDialog {

	/*public static void show(Context context, Exception ex){
	 ErrorDialog.show(context, ex.getClass().getSimpleName(), ex.getMessage());
	 }*/

	public static void show(Context context, String title, String mensage){
		ErrorDialog.show(context, title, mensage, null);
	}


	public static void show(Context context, String title, String mensage, DialogInterface.OnDismissListener onDismiss){
		new AlertDialog.Builder(context)
			.setTitle(title)
			.setMessage(mensage)
			.setPositiveButton("Ok", null)
			.setOnDismissListener(onDismiss)
			.show();
	}
}
