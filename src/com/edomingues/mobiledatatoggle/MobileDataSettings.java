package com.edomingues.mobiledatatoggle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MobileDataSettings {
	
	private static final String TAG = "MobileDataSettings";
	
	private Context context;
	
	private MobileDataSettings(Context context) {
		this.context = context;
	}
	
	public boolean toggleMobileData() throws ClassNotFoundException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method dataConnSwitchmethod;
		Class<?> telephonyManagerClass;
		Object ITelephonyStub;
		Class<?> ITelephonyClass;

		TelephonyManager telephonyManager = (TelephonyManager) context
		        .getSystemService(Context.TELEPHONY_SERVICE);

		boolean isEnabled = (telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED); 
		
		Log.d(TAG, "dataState isEnabled="+isEnabled);

		telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
		Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
		getITelephonyMethod.setAccessible(true);
		ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
		ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());

		if (isEnabled) {
		    dataConnSwitchmethod = ITelephonyClass
		            .getDeclaredMethod("disableDataConnectivity");
		} else {
		    dataConnSwitchmethod = ITelephonyClass
		            .getDeclaredMethod("enableDataConnectivity");   
		}
		dataConnSwitchmethod.setAccessible(true);
		dataConnSwitchmethod.invoke(ITelephonyStub);
		
		return !isEnabled;
	}
	
	public boolean isEnabled() {
		TelephonyManager telephonyManager = (TelephonyManager) context
		        .getSystemService(Context.TELEPHONY_SERVICE);

		boolean isEnabled = (telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED);
		
		return isEnabled;
	}
	
	public static MobileDataSettings getInstance(Context context) {
		return new MobileDataSettings(context);
	}
}
