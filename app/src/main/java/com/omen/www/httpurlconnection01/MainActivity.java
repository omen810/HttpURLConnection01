package com.omen.www.httpurlconnection01;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText mUrlEditText;
    private TextView mResultTextView;
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUrlEditText = (EditText) findViewById(R.id.activity_main_et);
        mResultTextView = (TextView) findViewById(R.id.activity_main_tv);
    }

    public void myClickHandler(View view) {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
//            Toast.makeText(this, "正在下载……", Toast.LENGTH_SHORT).show();
            new DownloadWebpageTask().execute(mUrlEditText.getText().toString());

        } else {
            mResultTextView.setText("无法连接网络");
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                return downloadUrl(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return "Unable to retrieve web page. URL may be invalid.";
            }

        }

        private String downloadUrl(String url) throws IOException {
            InputStream is = null;
            int len = 500;
            URL myUrl = new URL(url);
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) myUrl.openConnection();
                conn.setReadTimeout(10000); /* milliseconds */
                conn.setConnectTimeout(15000);/* milliseconds */
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(TAG, "The Response is :" + response);
                is = conn.getInputStream();

                String contentAsString = readIt(is, len);
                return contentAsString;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    is.close();
                }
            }
            return  null;
        }

        private String readIt(InputStream is, int len) throws IOException,UnsupportedEncodingException {
            Reader reader=null;
            reader = new InputStreamReader(is, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        @Override
        protected void onPostExecute(String s) {
            mResultTextView.setText(s);
        }
    }
}
