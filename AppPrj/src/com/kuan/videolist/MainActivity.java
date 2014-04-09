package com.kuan.videolist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	Button load;
	ListView content;
	Context context;
	TextView txtJson;
	ArrayAdapter<String> adapter;

	List<String> vlist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		load = (Button) findViewById(R.id.load);
		content = (ListView) findViewById(R.id.content);
		txtJson = (TextView) findViewById(R.id.txtjson);
		context = this;
		vlist = new ArrayList<String>();
		load.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				DataLoader dLdr = new DataLoader();
				try {
					dLdr.execute(new URI("http://kxing.info:3000/videolist"));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}

			}
		});

		adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_list_item_1, vlist);
		content.setAdapter(adapter);

	}

	private class DataLoader extends AsyncTask<URI, Integer, List<String>> {

		ProgressDialog wait = new ProgressDialog(context);

		@Override
		protected void onPostExecute(List<String> result) {
			super.onPostExecute(result);
			txtJson.setText((String) result.get(2));
			vlist.clear();
			for (int i = 0; i < result.size(); i++) {
				vlist.add(result.get(i));
			}
			adapter.notifyDataSetChanged();
			wait.dismiss();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			wait.setTitle("Loading...");
			wait.show();
		}

		@Override
		protected List<String> doInBackground(URI... params) {

			HttpClient client = new DefaultHttpClient();
			HttpGet getter = new HttpGet(params[0]);
			List<String> results = new ArrayList<String>();

			try {
				HttpResponse response = client.execute(getter);
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				InputStreamReader isRdr = new InputStreamReader(is);
				BufferedReader bfRdr = new BufferedReader(isRdr);
				StringBuilder strBldr = new StringBuilder();
				String line = null;

				while ((line = bfRdr.readLine()) != null) {
					strBldr.append(line);
				}

				String result = strBldr.toString();

				JSONObject jObj = new JSONObject(result);
				JSONArray jArray = jObj.getJSONArray("list");
				results = new ArrayList<String>();
				for (int i = 0; i < jArray.length(); i++) {
					results.add((String) jArray.getString(i));
				}

				return results;

			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

			return null;
		}
	}

}
