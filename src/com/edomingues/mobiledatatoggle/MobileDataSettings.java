package com.edomingues.mobiledatatoggle;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MobileDataSettings {
	
	private static final String TAG = "MobileDataSettings";
	
	private Context context;
	
	private MobileDataSettings(Context context) {
		this.context = context;
	}
	
	public boolean toggleMobileData() throws ClassNotFoundException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
        iConnectivityManagerField.setAccessible(true);
        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
        final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        WifiManager wirelessManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		boolean isEnabled = telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED;
		
		Log.d(TAG, "dataState isEnabled="+isEnabled);

		if (isEnabled) {

            Toast.makeText(context, "Disabling Mobile Data", Toast.LENGTH_LONG).show();
            setMobileDataEnabledMethod.invoke(iConnectivityManager, false);
            Toast.makeText(context, "Enabling Wifi", Toast.LENGTH_LONG).show();
            wirelessManager.setWifiEnabled(true);

		} else {
            Toast.makeText(context, "Enabling Mobile Data", Toast.LENGTH_LONG).show();
            setMobileDataEnabledMethod.invoke(iConnectivityManager, true);
            Toast.makeText(context, "Disabling Wifi", Toast.LENGTH_LONG).show();
            wirelessManager.setWifiEnabled(false);
		}

        Toast.makeText(context, "Data Connection Toggled",
                Toast.LENGTH_LONG).show();

		return !isEnabled;
	}
	
	public boolean isEnabled() {
		TelephonyManager telephonyManager = (TelephonyManager) context
		        .getSystemService(Context.TELEPHONY_SERVICE);

		boolean isEnabled = telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED;
		
		return isEnabled;
	}
	
	public static MobileDataSettings getInstance(Context context) {
		return new MobileDataSettings(context);
	}
}
