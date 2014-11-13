package com.cs242.due;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.cs242.due.Login.UserLoginTask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class AddAssignment extends Activity {
	private EditText tvDisplayDate;
	private DatePicker dpResult;
	private int year;
	private int month;
	private int day;
	private String _username;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_assignment);
		setCurrentDateOnView();
		Intent intent = getIntent();
		_username = intent.getStringExtra("username");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_assignment, menu);
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

	public void setCurrentDateOnView() {

		tvDisplayDate = (EditText) findViewById(R.id.adddate);
		dpResult = (DatePicker) findViewById(R.id.datePicker1);

		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		// set current date into textview
		tvDisplayDate.setText(new StringBuilder()
				// Month is 0 based, just add 1
				.append(month + 1).append("-").append(day).append("-")
				.append(year).append(" "));

		// set current date into datepicker
		dpResult.init(year, month, day, null);

	}



	public void submmit(View view) {
		AssSubmmitTask task = new AssSubmmitTask(((EditText)findViewById(R.id.addtitle)).getText().toString(),
				((EditText)findViewById(R.id.addcourse)).getText().toString(),
				((EditText)findViewById(R.id.adddate)).getText().toString(),
				((EditText)findViewById(R.id.addcontent)).getText().toString(),
				_username
				);
		task.execute((Void) null);
	}
	
	public class AssSubmmitTask extends AsyncTask<Void, Void, Boolean> {

		private final String mTitle;
		private final String mCourse;
		private final String mDate;
		private final String mContent;
		private final String mUsername;
		private JSONObject feedback;

		AssSubmmitTask(String title, String course,String date,String content,String username) {
			mTitle = title;
			mCourse = course;
			mDate = date;
			mContent = content;
			mUsername = username;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://104.236.34.81/addAssignment/");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				System.err.println(mUsername+","+mTitle+","+mDate+","+mContent+","+mCourse);
				nameValuePairs.add(new BasicNameValuePair("username", mUsername));
				nameValuePairs.add(new BasicNameValuePair("title", mTitle));
				nameValuePairs.add(new BasicNameValuePair("date", mDate));
				nameValuePairs.add(new BasicNameValuePair("content", mContent));
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
