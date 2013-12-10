package com.edomingues.mobiledatatoggle;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class MobileToggleAppWidgetProvider extends AppWidgetProvider {
	
	private static final String TAG = "MobileToggleAppWidgetProvider";

	private static final ComponentName THIS_APPWIDGET =
			new ComponentName(MobileToggleAppWidgetProvider.class.getPackage().getName(),
					MobileToggleAppWidgetProvider.class.getName());

	private Boolean isEnabled = null; 

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		// Perform this loop procedure for each App Widget that belongs to this provider
		for (int i=0; i < appWidgetIds.length; i++) {
			int appWidgetId = appWidgetIds[i];

			RemoteViews views = buildUpdate(context);

			// Tell the AppWidgetManager to perform an update on the current app widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		try {
			if(intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
				isEnabled = MobileDataSettings.getInstance(context).toggleMobileData();

				updateWidget(context);
			}
		} catch (Exception e) {
			Log.e(TAG, "Exception caught on receive intent.", e);
		}
	}
	
	private RemoteViews buildUpdate(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, MobileToggleAppWidgetProvider.class);
		intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
		views.setOnClickPendingIntent(R.id.btn_sync, pendingIntent);

		if(isEnabled == null)
			isEnabled = MobileDataSettings.getInstance(context).isEnabled();
		
		if(isEnabled) {
			views.setImageViewResource(R.id.img_sync, getButtonImageId(true));
			views.setImageViewResource(
					R.id.ind_sync, R.drawable.appwidget_settings_ind_on_c_holo);
		}
		else {
			views.setImageViewResource(R.id.img_sync, getButtonImageId(false));
			views.setImageViewResource(
					R.id.ind_sync, R.drawable.appwidget_settings_ind_off_c_holo);
		}

		return views;
	}

	private int getButtonImageId(boolean on) {
		return on ? R.drawable.ic_appwidget_settings_sync_on_holo
				: R.drawable.ic_appwidget_settings_sync_off_holo;
	}

	private void updateWidget(Context context) {
		RemoteViews views = buildUpdate(context);
		// Update specific list of appWidgetIds if given, otherwise default to all
		final AppWidgetManager gm = AppWidgetManager.getInstance(context);
		gm.updateAppWidget(THIS_APPWIDGET, views);
	}
}