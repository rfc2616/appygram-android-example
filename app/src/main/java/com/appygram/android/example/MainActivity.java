package com.appygram.android.example;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appygram.java.Appygram;
import com.appygram.java.AppygramConfig;
import com.appygram.java.AppygramMessage;
import com.appygram.java.AppygramTopic;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class MainActivity extends Activity {

	private final static String APPYGRAM_API_KEY = "your-api-key-here";
	
	List<AppygramTopic> topics = new ArrayList<AppygramTopic>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        Appygram.configure(new AppygramConfig(APPYGRAM_API_KEY));

		new GetTopicsTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
		
	private class GetTopicsTask extends AsyncTask<URL, Integer, Long> {

		protected GetTopicsTask(){
			super();
		}

		protected Long doInBackground(URL... urls) {
            topics.clear();
            for (AppygramTopic topic : Appygram.Global.topics())
                topics.add(topic);
			return 0L;
		}

		protected void onPostExecute(Long result) {
			ArrayList<String> names = new ArrayList<String>();
			for(AppygramTopic topic: topics){
				names.add(topic.getName());
			}
			Spinner spinner = (Spinner) findViewById(R.id.editTopic);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,names.toArray(new String[0]));
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
		}

	}

	private class SendAppygramTask extends AsyncTask<URL, Integer, Long> {
		private AppygramMessage message;

		protected SendAppygramTask(AppygramMessage message){
			super();
			this.message = message;
		}

		protected Long doInBackground(URL... urls) {
            Appygram.Global.send(message);
			return 200L;
		}

		protected void onPostExecute(Long result) {
			// TODO: show that something happened
            Toast.makeText(getApplicationContext(), R.string.confirmation, Toast.LENGTH_LONG).show();
		}
	}

	private String getUIString(int id){
		return ((TextView) findViewById(id)).getText().toString();
	}

	public void send(View source) {
        AppygramMessage message = Appygram.Global.create();

		// Collect parameters from the UI
        message.setTopic(topics.get(((Spinner) findViewById(R.id.editTopic)).getSelectedItemPosition()).getName());
        message.setName(getUIString(R.id.editName));
        message.setEmail(getUIString(R.id.editEmail));
        message.setMessage(getUIString(R.id.editMessage));

		// Send them to Appygram
		new SendAppygramTask(message).execute();
	}

}
