package com.cs242.due;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class Signup_sub extends ActionBarActivity {
	
	private TextView textView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup_sub);
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();

		textView = (TextView) findViewById(R.id.hello);
		Intent intent = getIntent();
		String message1 = intent.getStringExtra("username");
		String message2 = intent.getStringExtra("password");
		String message3 = intent.getStringExtra("firstname");
		String message4 = intent.getStringExtra("lastname");
		new MyAsyncTask().execute(message1,message2,message3,message4);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.signup_sub, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private class MyAsyncTask extends AsyncTask<String, Integer, Double>{
		String res = "Loading...";
		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
			postData(params);
			return null;
		}
 
		protected void onPostExecute(Double result){
			//textView.setVisibility(View.GONE);
				try {
					JSONObject feedback = new JSONObject(res);
					boolean loginSuccess = feedback.getBoolean("status");
					
					if(loginSuccess){
						
						
						textView.setText("Success!");
						
					}else{
						
						textView.setText("Failed!");
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			
			//Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
		}
		protected void onProgressUpdate(Integer... progress){
			//pb.setProgress(progress[0]);
		}
 
		public void postData(String... params){
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://104.236.34.81/signup/");
			/*HttpGet request = new HttpGet();
            try {
				request.setURI(new URI("http://104.236.34.81/?username=haha&password=123&firstName=123&lastName=34"));
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			
			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("username", params[0]));
				nameValuePairs.add(new BasicNameValuePair("password", params[1]));
				nameValuePairs.add(new BasicNameValuePair("firstName", params[2]));
				nameValuePairs.add(new BasicNameValuePair("lastName", params[3]));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				
				
				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				//HttpResponse response = httpclient.execute(request);
				ResponseHandler<String> handler = new BasicResponseHandler();
				res = handler.handleResponse(response);

		    
 
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
 
	}
}

/*
 * 
 * 
 * Intent intent = getIntent();
		    		String message1 = intent.getStringExtra(Signup.EXTRA_MESSAGE1);
		    		String message2 = intent.getStringExtra(Signup.EXTRA_MESSAGE2);
		    		
		    		HttpClient client = new DefaultHttpClient();
		    		HttpPost post = new HttpPost("http://104.236.34.81");
		    		HttpGet request = new HttpGet();
		            request.setURI(new URI("https://www.google.com"));
		            
		    		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		    		pairs.add(new BasicNameValuePair("username", message1));
		    		pairs.add(new BasicNameValuePair("password", message2));
		    		try {
		    			//post.setEntity(new UrlEncodedFormEntity(pairs));
		    			//HttpResponse response = client.execute(post);
		    			HttpResponse response = client.execute(request);
		    			 // Create the text view
		    			

		    		    TextView textView = new TextView();
		    		    		//(TextView)findViewById(R.id.hello);
		    		    textView.setTextSize(40);
		    		    textView.setText(response.getEntity().toString());
		    		    
		    		    // Set the text view as the activity layout
		    		    setContentView(textView);
		    		} catch (UnsupportedEncodingException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		} catch (ClientProtocolException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		} catch (IOException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}*/
