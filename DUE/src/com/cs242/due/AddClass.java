package com.cs242.due;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.cs242.due.AddAssignment.AssSubmmitTask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddClass extends Activity {

	private String _username;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_class);
		Intent intent = getIntent();
		_username = intent.getStringExtra("username");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_class, menu);
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
	
	public void submmit(View view) {
		AssSubmmitTask task = new AssSubmmitTask(
				((EditText)findViewById(R.id.addcourse)).getText().toString(),
				_username
				);
		task.execute((Void) null);
	}
	
	public class AssSubmmitTask extends AsyncTask<Void, Void, Boolean> {

		private final String mCourse;
		private final String mUsername;
		private JSONObject feedback;

		AssSubmmitTask(String course,String username) {
			mCourse = course;
			mUsername = username;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://104.236.34.81/addClass/");
				System.err.println(mUsername+","+mCourse);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("username", mUsername));
				nameValuePairs.add(new BasicNameValuePair("code", mCourse));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response;

				response = httpclient.execute(httppost);
				// HttpResponse response = httpclient.execute(request);
				ResponseHandler<String> handler = new BasicResponseHandler();
				String res = handler.handleResponse(response);
				feedback = new JSONObject(res);

			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				
				e1.printStackTrace();
				return false;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {


			if (success) {
				try {
					boolean loginSuccess = feedback.getBoolean("status");
					
					if(loginSuccess){
						
						
						Toast.makeText(getApplicationContext(), "Class Added",
								Toast.LENGTH_SHORT).show();
						finish();
						
						
					}else{
						
						Toast.makeText(getApplicationContext(), feedback.getString("error"),
								Toast.LENGTH_SHORT).show();
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else {
				
				Toast.makeText(getApplicationContext(), "Network Error",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			
		}
	}
}
