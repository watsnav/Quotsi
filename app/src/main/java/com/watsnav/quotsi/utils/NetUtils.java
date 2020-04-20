package com.watsnav.quotsi.utils;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

import java.net.URL;
import java.util.Scanner;
import android.util.Log;

public final class NetUtils {
	private static final String TAG = NetUtils.class.getSimpleName();
	public static String getHttpResponse(URL url) throws IOException{
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		//con.connect();
		Log.v(TAG, "Connecting to:"+ url);
		try{
			/*con.setHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
			*/
			InputStream in = con.getInputStream();
			Scanner sc = new Scanner(in);
			sc.useDelimiter("\\A");
			boolean hasInput = sc.hasNext();
			if(hasInput) return sc.next();
			else return null;
		} catch (SSLPeerUnverifiedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		return null;
	}
}
