package com.liviu.apps.iasianunta.utils;

import android.util.Log;

public class Console {
	private static boolean isDev = true;
	public static void debug(String TAG, String txt){
		if(isDev){			
			Log.e(TAG, txt);
		}
	}
}
