package com.watsnav.quotsi.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.os.Handler;
import android.widget.*;


import com.watsnav.quotsi.utils.FetchQuoteTask;
import com.watsnav.quotsi.R;
import com.watsnav.quotsi.utils.NetUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;


public class QuotsiWidgetProvider extends AppWidgetProvider {
    private RemoteViews rviews;
    private AppWidgetManager awm;
	private int wid;

	@Override
	public void onUpdate(Context ctx, AppWidgetManager awm, int[] widgetIds) {
		// Get all ids
		ComponentName thisWidget = new ComponentName(ctx,
				QuotsiWidgetProvider.class);
		int[] allWidgetIds = awm.getAppWidgetIds(thisWidget);
		this.awm = awm;
		for (int widgetId : allWidgetIds) {
			Intent intent = new Intent(ctx, QuotsiWidgetProvider.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,allWidgetIds);
			PendingIntent pIntent = PendingIntent.getBroadcast(ctx,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
			rviews = new RemoteViews(ctx.getPackageName(), R.layout.quotsi_widget);
			rviews.setOnClickPendingIntent(R.id.ibtn_widget, pIntent);
			Handler handler = new Handler();
			this.wid = widgetId;
			CompletableFuture<String> fetchedQuote = fetchQuote(handler);
		}
	}
	public CompletableFuture<String> fetchQuote(Handler handler) {
		CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> {
			try {
				String base_url = "https://watsnav.github.io/quotsi";
				URL url = new URL(base_url);
				try {
					String json = NetUtils.getHttpResponse(url);
					onJsonResponse(handler, json);
					return json;
				} catch (IOException ie) {
					ie.printStackTrace();
					return null;
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
		});

		return result;
	}

	public void onJsonResponse(Handler handler, String json) {
		if (json == null) return;
		//parse JSON and update tv
		try {
			JSONObject jsonObj = new JSONObject(json);
			String newQuote = jsonObj.getString("quote");
			//String newAuthor = jsonObj.getString("name");
			handler.post(new Runnable() {
				@Override
				public void run() {
					rviews.setTextViewText(R.id.tvquote_widget, newQuote);
					awm.updateAppWidget(wid, rviews);
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}

