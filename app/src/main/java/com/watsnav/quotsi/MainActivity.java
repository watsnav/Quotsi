package com.watsnav.quotsi;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.*;
import android.view.*;
import android.view.animation.Animation;

import com.watsnav.quotsi.utils.FetchQuoteTask;
import com.watsnav.quotsi.utils.NetUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import static com.watsnav.quotsi.utils.NetUtils.getHttpResponse;

public class MainActivity extends AppCompatActivity {
    private TextView tv;
    private TextView tvauth;
    private ImageButton ibtn;
    private Context ctx;
    private HashSet<String> hash;
    private boolean permsOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permsOK = false;
        hash = new HashSet<>();
        hashFileContents();
        setContentView(R.layout.activity_main);
        ctx = this;
        tv = findViewById(R.id.tvquote);
        tvauth = findViewById(R.id.tvauthor);
        ibtn = findViewById(R.id.ibtn);


        Handler handler = new Handler();
        CompletableFuture<String> fetchedQuote = fetchQuote(handler);

        //setFetchedQuote();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else permsOK = true;
        ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation ranim = AnimationUtils.loadAnimation(ctx, R.anim.rotate);
                ibtn.setAnimation(ranim);
                if (permsOK) {
                    setRandomHashedQuote();
                }
            }
        });
    }

    public void onJsonResponse(Handler handler, String json) {
        if (json == null) return;
        //parse JSON and update tv
        try {
            JSONObject jsonObj = new JSONObject(json);
            String newQuote = jsonObj.getString("quote");
            String newAuthor = jsonObj.getString("name");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tv.setText(newQuote);
                    tvauth.setText(newAuthor);
                    cacheLatestQuote();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onRequestPermissionsResult(int req, String[] perms, int[] res) {
        super.onRequestPermissionsResult(req, perms, res);
        if (req == 1) {
            if (res.length > 0 && res[0] == PackageManager.PERMISSION_GRANTED && res[1] == PackageManager.PERMISSION_GRANTED) {
                permsOK = true;
            }
        }
    }

    public void hashFileContents() {
        try {
            String tmpQuote;
            File dir = new File(this.getFilesDir(), "Quotsi");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, "quotes.json");
            if (file.exists()) {
                FileReader reader = new FileReader(file);
                char[] buffer = new char[(int) file.length()];
                reader.read(buffer);
                reader.close();
                String fileContents = new String(buffer);
                JSONObject jsonRoot = new JSONObject(fileContents);
                JSONArray jsonArr = jsonRoot.getJSONArray("quotes");
                for (int i = 0; i < jsonArr.length(); i++) {
                    tmpQuote = jsonArr.getJSONObject(i).toString();
                    if (!hash.contains(tmpQuote)) {
                        hash.add(tmpQuote);
                    }
                }
            }
        } catch (IOException e) {
            Log.e("hashFileContents", "IOException");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e("setRandomHashedQuote", "JSONException");
            e.printStackTrace();
        }
    }

    public void cacheLatestQuote() {
        try {
            File dir = new File(this.getFilesDir(), "Quotsi");
            if (!dir.exists()) {
                dir.mkdir();
            }
            if (tv.getText().length() >= 1) {
                String latestQuote = "{\"quote\":\"" + tv.getText() + "\",\"name\":\"" + tvauth.getText() + "\"}";
                if (hash.contains(latestQuote)) {
                    return;
                } else {
                    File file = new File(dir, "quotes.json");
                    String header = "{quotes:[";
                    hash.add(latestQuote);
                    StringBuilder fullStr = new StringBuilder();
                    fullStr.append(header);
                    Iterator it = hash.iterator();
                    while (it.hasNext()) {
                        fullStr.append(it.next());
                        if (it.hasNext()) fullStr.append(',');
                        //Log.e("fileContents", fileContents.toString());
                    }
                    fullStr.append("]}");
                    FileWriter writer = new FileWriter(file);
                    writer.write(fullStr.toString());
                    writer.flush();
                    writer.close();
                }
            } else return;
        } catch (IOException e) {
            Log.e("cacheLatestQuote", "IOException");
            e.printStackTrace();
        }
    }

    public void setRandomHashedQuote() {
        try {
            Random rand = new Random();
            int random = rand.nextInt(hash.size());
            int i = 0;
            for (String tmpStr : hash) {
                if (i == random) {
                    //String tmpStr = it.next().toString();
                    JSONObject jsonObj = new JSONObject(tmpStr);
                    tv.setText(jsonObj.getString("quote"));
                    tvauth.setText(jsonObj.getString("name"));
                }
                i++;
            }
        } catch (
                JSONException e) {
            Log.e("setRandomHashedQuote", "JSONException");
            e.printStackTrace();
        }

    }

    //Fetches quote from webservice
    public void setFetchedQuote() {
        Context ctx = this;
        FetchQuoteTask fetchTask = new FetchQuoteTask(ctx);
        fetchTask.execute();
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

}

