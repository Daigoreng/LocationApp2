package com.example.locationapp;

import android.content.Context;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;


public class AsyncNetworkTask extends AsyncTask<String,Void,String> {

	public AsyncNetworkTask(Context context){
		super();
	}

	@Override
	protected String doInBackground(String...params){
		StringBuilder builder = new StringBuilder();
		try{
			URL url = new URL(params[0]);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type","text/plain; charset=utf-8");
			con.setDoOutput(true);

			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(),"Shift-JIS"));
			String line;
			while((line = reader.readLine()) != null){
				builder.append(line);
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return builder.toString();
	}

}
