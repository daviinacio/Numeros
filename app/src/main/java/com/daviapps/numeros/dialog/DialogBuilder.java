package com.daviapps.numeros.dialog;
import android.app.*;
import android.content.*;

public abstract class DialogBuilder<E extends Dialog> {
    protected E dialog;

    public DialogBuilder(E dialog){
        if(dialog == null)
            throw new IllegalArgumentException("The dialog can't be null");
        else
            this.dialog = dialog;
    }

    public DialogBuilder setOnCancelListener(DialogInterface.OnCancelListener listener){
        this.dialog.setOnCancelListener(listener);
        return this;
    }

    public DialogBuilder setOnDismissListener(DialogInterface.OnDismissListener listener){
        this.dialog.setOnDismissListener(listener);
        return this;
    }

    public DialogBuilder setOnShowListener(DialogInterface.OnShowListener listener){
        this.dialog.setOnShowListener(listener);
        return this;
    }

    public DialogBuilder setOnKeyListener(DialogInterface.OnKeyListener listener){
        this.dialog.setOnKeyListener(listener);
        return this;
    }

    public DialogBuilder setCancelable(boolean cancelable){
        this.dialog.setCancelable(cancelable);
        return this;
    }

    public DialogBuilder setsetCanceledOnTouchOutside(boolean canceledOnTouchOutSide){
        this.dialog.setCanceledOnTouchOutside(canceledOnTouchOutSide);
        return this;
    }

    public E build(){
        return this.dialog;
    }

    public void show(){
        this.dialog.show();
    }
}
 
