package com.watsnav.quotsi.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.widget.*;


import com.watsnav.quotsi.utils.FetchQuoteTask;
import com.watsnav.quotsi.R;


public class QuotsiWidgetProvider extends AppWidgetProvider {
	@Override
	public void onUpdate(Context ctx, AppWidgetManager awm, int[] widgetIds) {
		// Get all ids
		ComponentName thisWidget = new ComponentName(ctx,
				QuotsiWidgetProvider.class);
		int[] allWidgetIds = awm.getAppWidgetIds(thisWidget);
		for (int widgetId : allWidgetIds) {
			Intent intent = new Intent(ctx, QuotsiWidgetProvider.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,allWidgetIds);
			PendingIntent pIntent = PendingIntent.getBroadcast(ctx,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
			RemoteViews rviews = new RemoteViews(ctx.getPackageName(), R.layout.quotsi_widget);
			rviews.setOnClickPendingIntent(R.id.ibtn_widget, pIntent);
			new FetchQuoteTask(rviews, awm, widgetId).execute();
		}
	}
}

