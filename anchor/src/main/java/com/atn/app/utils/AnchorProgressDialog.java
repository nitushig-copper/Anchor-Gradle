/**
 * @Copyright Coppermobile 2014.
 * 
 * */
package com.atn.app.utils;
import android.app.ProgressDialog;
import android.content.Context;


/*
 * Helper class for showing progress bar
 * **/
public class AnchorProgressDialog extends ProgressDialog {
	
	//can not create object
    private AnchorProgressDialog(Context context) {
        super(context);
    }

    //hold current Progress bar object reference
    private static ProgressDialog thisDialog = null;
    
    /**
     * @param context application context
     * @param resId string recourse id
     * */
    public static ProgressDialog show(Context context, int resId) {
       return show(context, context.getResources().getString(resId));	
    }
    
    /**
     * @param context application context
     * @param message Message string
     * */
    public static ProgressDialog show(Context context, String message) {
        conceal();
        thisDialog = AnchorProgressDialog.show(context, null, message);
        return thisDialog;
    }

    //hide visible progress bar.
    public static void conceal() {
        try {
            if (thisDialog != null && thisDialog.isShowing()) {
                thisDialog.dismiss();
                thisDialog = null;
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
}
