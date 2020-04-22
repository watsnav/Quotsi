package com.watsnav.quotsi.utils;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.widget.*;

import com.watsnav.quotsi.R;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import java.lang.ref.WeakReference;


public class FetchQuoteTask extends AsyncTask<Void, Void, String> {
	private WeakReference<Context> ctxref = null;
	private RemoteViews rviews = null;
	private AppWidgetManager awm;
	private int wid;

	//private final String base_url = "http://genelios.private/random.php";
	private final String base_url = "https://watsnav.github.io/quotsi";
	public FetchQuoteTask(Context ctx) {
		ctxref = new WeakReference<>(ctx);
	}
	public FetchQuoteTask(RemoteViews rv, AppWidgetManager awm, int wid) {
		rviews = rv;
		this.awm = awm;
		this.wid = wid;
	}
	@Override
	public void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	public void onPostExecute(String json) {
		super.onPostExecute(json);
		if(json==null) return;
		if(ctxref!=null) {//called from MainActivity
			AppCompatActivity ctx = (AppCompatActivity) ctxref.get();
			TextView tvq = ctx.findViewById(R.id.tvquote);
			TextView tva = ctx.findViewById(R.id.tvauthor);
			String previousQuote = tvq.getText().toString();
			String previousAuthor = tva.getText().toString();
			//parse JSON and update tv
			try {
				JSONObject jsobj = new JSONObject(json);
				String newQuote = jsobj.getString("quote");
				String newAuthor = jsobj.getString("name");
				tvq.setText(newQuote);
				tva.setText(newAuthor);
			} catch (JSONException e) {
				Toast.makeText(ctx, "Error while parsing JSON", Toast.LENGTH_SHORT).show();
				tvq.setText(previousQuote);
				tva.setText(previousAuthor);
			}
		}
		else {
			if(rviews!=null) {//called from remoteview
				//parse JSON and update tv
				try {
					JSONObject jsobj = new JSONObject(json);
					String newQuote = jsobj.getString("quote");
					//String newAuthor = jsobj.getString("name");
					rviews.setTextViewText(R.id.tvquote_widget, newQuote);
					awm.updateAppWidget(wid, rviews);
				} catch (JSONException e) {
					rviews.setTextViewText(R.id.tvquote_widget, "noConnection");
					awm.updateAppWidget(wid, rviews);
				}
			}
		}
	}

	@Override
	public String doInBackground(Void... voids) {
		try{
			URL url = new URL(base_url);
			try{
				return NetUtils.getHttpResponse(url);
			}catch (IOException ie) {
				return null;
			}
		} catch (MalformedURLException e) {
			return null;
		}
	}
}

