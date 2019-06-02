package com.watsnav.quotsi;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.animation.AnimationUtils;
import android.widget.*;
import android.view.*;
import android.view.animation.Animation;

import com.watsnav.quotsi.utils.FetchRandomQuoteTask;
import com.watsnav.quotsi.utils.NetUtils;
import org.json.JSONObject;
import org.json.JSONException;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
	private TextView tv;
	private TextView tvauth;
	private ImageButton ibtn;
	private Context ctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ctx = this;
		tv = findViewById(R.id.tvquote);
		tvauth = findViewById(R.id.tvauthor);
		ibtn = findViewById(R.id.ibtn);
		set_random_quote();
		ibtn.setOnClickListener( new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			Animation ranim = AnimationUtils.loadAnimation(ctx, R.anim.rotate);
			ibtn.setAnimation(ranim);
			set_random_quote();
		}
	});
	}

	//Fetches random quote
	public void set_random_quote() {
		Context ctx = this;
		new FetchRandomQuoteTask(ctx).execute();
	}
/*
	public static class FetchRandomQuoteTask extends AsyncTask<Void, Void, String> {
		private WeakReference<Context> ctxref;
		public FetchRandomQuoteTask(Context ctx) {
			ctxref = new WeakReference<>(ctx);
		}
		@Override
		public void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		public void onPostExecute(String json) {
			super.onPostExecute(json);
			if(json==null) return;
			Context ctx = ctxref.get();
			tvq = ctx.findViewById(R.id.tvquote);
			tva = ctx.findViewById(R.id.tvauthor);
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
*/
}

