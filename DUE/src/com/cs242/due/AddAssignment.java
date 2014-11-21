package com.cs242.due;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddAssignment extends ActionBarActivity {

	private String _username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_assignment);
		Intent intent = getIntent();
		_username = intent.getStringExtra("username");
		GetUserClassTask mTask = new GetUserClassTask(_username);
		mTask.execute((Void) null);
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



	public void submmit(View view) {
		AssSubmmitTask task = new AssSubmmitTask(
				((EditText) findViewById(R.id.addtitle)).getText().toString(),
				((Spinner) findViewById(R.id.addcourse)).getSelectedItem().toString().replace(" ", ""),
				((EditText) findViewById(R.id.adddate)).getText().toString()+"-"+((EditText) findViewById(R.id.addtime)).getText().toString(),
				((EditText) findViewById(R.id.addcontent)).getText().toString(),
				_username);
		task.execute((Void) null);
	}

	public class AssSubmmitTask extends AsyncTask<Void, Void, Boolean> {

		private final String mTitle;
		private final String mCourse;
		private final String mDate;
		private final String mContent;
		private final String mUsername;
		private JSONObject feedback;

		AssSubmmitTask(String title, String course, String date,
				String content, String username) {
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
				HttpPost httppost = new HttpPost(
						"http://104.236.34.81/addAssignment/");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				System.err.println(mUsername + "," + mTitle + "," + mDate + ","
						+ mContent + "," + mCourse);
				nameValuePairs
						.add(new BasicNameValuePair("username", mUsername));
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

					if (loginSuccess) {

						Toast.makeText(getApplicationContext(), "Assignment Added",
								Toast.LENGTH_SHORT).show();
						finish();

					} else {

						Toast.makeText(getApplicationContext(),
								feedback.getString("error"), Toast.LENGTH_SHORT)
								.show();

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

	public class TimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// Do something with the time chosen by the user
			((EditText) findViewById(R.id.addtime)).setText(new StringBuilder()
			// Month is 0 based, just add 1
			.append(hourOfDay).append("-").append(minute));
		}
	}

	public class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
			((EditText) findViewById(R.id.adddate)).setText(new StringBuilder()
			// Month is 0 based, just add 1
			.append(month + 1).append("-").append(day).append("-")
			.append(year));
		}
	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}

	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getSupportFragmentManager(), "timePicker");
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class GetUserClassTask extends AsyncTask<Void, Void, Boolean> {

		private final String mUsername;
		private JSONObject feedback;

		GetUserClassTask(String username) {
			mUsername = username;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://104.236.34.81/classList/");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("username", mUsername));
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

						JSONArray jArray2 = feedback.getJSONArray("courses");
						Spinner spinner = (Spinner) findViewById(R.id.addcourse);
						List<String> list = new ArrayList<String>();
						for (int j = 0; j < jArray2.length(); j++) {



							list.add(jArray2.getString(j));
						}

						ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(),
							R.layout.course_item, list);
						spinner.setAdapter(dataAdapter);
					}else{
						Toast.makeText(getApplicationContext(), feedback.getString("error"),
								Toast.LENGTH_SHORT).show();
						
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else {

				//fresh error
				Toast.makeText(getApplicationContext(), "Network Error",
						Toast.LENGTH_SHORT).show();
			}
		}


	}
}
