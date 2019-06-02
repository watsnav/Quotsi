package com.watsnav.quotsi;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.*;
import android.view.*;
import android.view.animation.Animation;

import com.watsnav.quotsi.utils.FetchRandomQuoteTask;

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

}

