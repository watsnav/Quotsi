package com.watsnav.quotsi.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import android.view.*;

import com.watsnav.quotsi.utils.FetchRandomQuoteTask;
import com.watsnav.quotsi.R;
import com.watsnav.quotsi.utils.NetUtils;
import org.json.JSONObject;
import org.json.JSONException;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;


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
			new FetchRandomQuoteTask(rviews, awm, widgetId).execute();
			//rviews.setTextViewText(R.id.tvquote_widget, R.id.tvquote)

			//awm.updateAppWidget(widgetId, rviews);
		}
	}
}

