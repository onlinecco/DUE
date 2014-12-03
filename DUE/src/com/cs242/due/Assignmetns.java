package com.cs242.due;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Assignmetns extends ActionBarActivity {

	private MyAdapter mAdapter;
	private ExpandableListView expand;
	private String _username;
	private String _password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assignmetns);
		ActionBar actionBar = getSupportActionBar();
		actionBar.show();
		Intent intent = getIntent();
		String message1 = intent.getStringExtra("bundle");
		_username = intent.getStringExtra("username");
		_password = intent.getStringExtra("password");
		expand = (ExpandableListView) this.findViewById(android.R.id.list);

		renderExpandablelist(message1);

	}

	/**
	 * @param message1
	 */
	private void renderExpandablelist(String message1) {
		JSONObject feedback;
		try {
			feedback = new JSONObject(message1);
			JSONArray jArray = feedback.getJSONArray("courses");

			List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
			List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();

			for (int i = 0; i < jArray.length(); i++) {

				JSONObject oneObject = jArray.getJSONObject(i);
				// Pulling items from the array
				String oneObjectsItem = oneObject.getString("course");
				JSONArray jArray2 = oneObject.getJSONArray("assignments");

				Map<String, String> curGroupMap = new HashMap<String, String>();
				groupData.add(curGroupMap);
				curGroupMap.put("course", oneObjectsItem);

				List<Map<String, String>> childList = new ArrayList<Map<String, String>>();
				for (int j = 0; j < jArray2.length(); j++) {
					Map<String, String> curChildMap = new HashMap<String, String>();
					childList.add(curChildMap);
					JSONObject aObject = jArray2.getJSONObject(j);

					// curChildMap.put(NAME, "Child " + j);
					curChildMap.put("aid", aObject.getInt("aid") + "");
					curChildMap.put("title", aObject.getString("title"));
					curChildMap.put("content", aObject.getString("content"));
					curChildMap.put("date", aObject.getString("duedate"));
					curChildMap.put("finished",
							String.valueOf(aObject.getBoolean("finished")));
				}
				childData.add(childList);
			}

			// Set up our adapter
			mAdapter = new MyAdapter(groupData, childData);
			expand.setAdapter(mAdapter);

			expand.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupPosition, int childPosition, long id) {

					Intent intent = new Intent(getApplicationContext(),
							ViewAssignment.class);
					intent.putExtra("title", ((TextView) v
							.findViewById(R.id.title)).getText().toString());
					intent.putExtra("content", ((TextView) v
							.findViewById(R.id.content)).getText().toString());
					intent.putExtra("date", ((TextView) v
							.findViewById(R.id.date)).getText().toString());
					intent.putExtra("aid", (( CheckBox) findViewById(R.id.cbx1)).getTag().toString());
					intent.putExtra("uid", _username);
					startActivity(intent);

					return true;
				}

			});
			mAdapter.notifyDataSetChanged();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.err.println("JSON file is corrupted.");
			e.printStackTrace();
		}
	}

	static class TestViewHolder {
		public TextView tvCounter;
	}

	public class MyAdapter extends BaseExpandableListAdapter {

		public List<Map<String, String>> groupItem;
		public List<List<Map<String, String>>> childItem;
		private HashMap<TextView, CountDownTimer> counters;
		public LayoutInflater minflater;
		public Activity activity;

		public MyAdapter(List<Map<String, String>> grList,
				List<List<Map<String, String>>> childItem) {
			groupItem = grList;
			this.childItem = childItem;
			counters = new HashMap<TextView, CountDownTimer>();
		}

		public void setInflater(LayoutInflater mInflater, Activity act) {
			this.minflater = mInflater;
			activity = act;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return childItem.get(groupPosition).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			List<Map<String, String>> tempChild = (List<Map<String, String>>) childItem
					.get(groupPosition);

			if (convertView == null) {

				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				convertView = inflater.inflate(R.layout.children_item, parent,
						false);

				final TestViewHolder viewHolder = new TestViewHolder();
				viewHolder.tvCounter = (TextView) convertView
						.findViewById(R.id.timeleft);

				convertView.setTag(viewHolder);

			}
			try {
				TestViewHolder holder = (TestViewHolder) convertView.getTag();
				final TextView tv = holder.tvCounter;

				CountDownTimer cdt = counters.get(holder.tvCounter);
				if (cdt != null) {
					cdt.cancel();
					cdt = null;
				}
				String dateTime = tempChild.get(childPosition).get("date");
				dateTime = dateTime.substring(0, dateTime.indexOf("+"));

				// final TextView tv = ((TextView) convertView
				// .findViewById(R.id.timeleft));

				Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
						Locale.ENGLISH).parse(dateTime);

				long milliseconds = date.getTime();
				long millisecondsFromNow = milliseconds
						- (new Date()).getTime();
				cdt = new CountDownTimer(millisecondsFromNow, 1000) {
					@Override
					public void onTick(long millisUntilFinished) {
						long savedTime = millisUntilFinished;
						int days = 0;
						int hours = 0;
						int minutes = 0;
						int seconds = 0;
						String sDate = "";

						if (millisUntilFinished > DateUtils.DAY_IN_MILLIS) {
							days = (int) (millisUntilFinished / DateUtils.DAY_IN_MILLIS);
							sDate += days + "d";
						}

						millisUntilFinished -= (days * DateUtils.DAY_IN_MILLIS);

						if (millisUntilFinished > DateUtils.HOUR_IN_MILLIS) {
							hours = (int) (millisUntilFinished / DateUtils.HOUR_IN_MILLIS);
						}

						millisUntilFinished -= (hours * DateUtils.HOUR_IN_MILLIS);

						if (millisUntilFinished > DateUtils.MINUTE_IN_MILLIS) {
							minutes = (int) (millisUntilFinished / DateUtils.MINUTE_IN_MILLIS);
						}

						millisUntilFinished -= (minutes * DateUtils.MINUTE_IN_MILLIS);

						if (millisUntilFinished > DateUtils.SECOND_IN_MILLIS) {
							seconds = (int) (millisUntilFinished / DateUtils.SECOND_IN_MILLIS);
						}

						sDate += " " + String.format("%02d", hours) + ":"
								+ String.format("%02d", minutes) + ":"
								+ String.format("%02d", seconds);
						tv.setText(sDate.trim());
						tv.setTextColor(Color.BLACK);
						if(savedTime/1000 < 3600*3) tv.setTextColor(Color.RED);
						if(savedTime/1000 == 3600 || savedTime/1000 == 1800 || savedTime/1000 == 900 || savedTime/1000 == 450 || savedTime/1000 == 60){
							
						NotificationCompat.Builder mBuilder =
						        new NotificationCompat.Builder(getApplicationContext())
						        .setSmallIcon(R.drawable.impo)
						        .setContentTitle("DUE Notification")
						        .setContentText("You have an assignment is about to due!");
						// Creates an explicit intent for an Activity in your app
						Intent resultIntent = new Intent(getApplicationContext(), Main.class);

						// The stack builder object will contain an artificial back stack for the
						// started Activity.
						// This ensures that navigating backward from the Activity leads out of
						// your application to the Home screen.
						TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
						// Adds the back stack for the Intent (but not the Intent itself)
						stackBuilder.addParentStack(Main.class);
						// Adds the Intent that starts the Activity to the top of the stack
						stackBuilder.addNextIntent(resultIntent);
						PendingIntent resultPendingIntent =
						        stackBuilder.getPendingIntent(
						            0,
						            PendingIntent.FLAG_UPDATE_CURRENT
						        );
						mBuilder.setContentIntent(resultPendingIntent);
						NotificationManager mNotificationManager =
						    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
						// mId allows you to update the notification later on.
						mNotificationManager.notify(1555, mBuilder.build());
						}
					}

					@Override
					public void onFinish() {
						tv.setText("DUE");
						tv.setTextColor(Color.RED);
					}
				};

				// counters.put(tv, cdt);
				cdt.start();
				counters.put(tv, cdt);
				cdt.start();

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				System.err.println("Datetime parsing error");
				e.printStackTrace();
			}

			CheckBox ckb = (CheckBox) convertView.findViewById(R.id.cbx1);

			TextView title = (TextView) convertView.findViewById(R.id.title);
			TextView content = (TextView) convertView
					.findViewById(R.id.content);
			TextView date = (TextView) convertView.findViewById(R.id.date);

			title.setText(tempChild.get(childPosition).get("title"));
			content.setText(tempChild.get(childPosition).get("content"));
			date.setText(tempChild.get(childPosition).get("date"));

			ckb.setTag(tempChild.get(childPosition).get("aid"));
			String finished = tempChild.get(childPosition).get("finished");
			if (finished.compareTo("true") == 0) {
				ckb.setChecked(true);
			} else if (finished.compareTo("false") == 0) {
				ckb.setChecked(false);
			}
			// System.err.println(((TextView) convertView
			// .findViewById(R.id.timeleft)).getText().toString());
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return ((List<Map<String, String>>) childItem.get(groupPosition))
					.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public int getGroupCount() {
			return groupItem.size();
		}

		@Override
		public void onGroupCollapsed(int groupPosition) {
			super.onGroupCollapsed(groupPosition);
		}

		@Override
		public void onGroupExpanded(int groupPosition) {
			super.onGroupExpanded(groupPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.groupitem, parent,
						false);
			}
			TextView title = (TextView) convertView.findViewById(R.id.text1);
			title.setText(groupItem.get(groupPosition).get("course"));
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

	public void onCheckboxClicked(View view) {

		boolean checked = ((CheckBox) view).isChecked();

		CheckboxTask task = new CheckboxTask(_username,
				(String) ((CheckBox) view).getTag(), checked);
		task.execute((Void) null);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.assignmetns, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_logout) {
			SharedPreferences mySharedPreferences = getApplicationContext()
					.getSharedPreferences("USER_PREFS", Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = mySharedPreferences.edit();
			editor.putString("USERNAME", null);
			editor.putString("PASSWORD", null);
			editor.commit();
			Intent intent = new Intent(getApplicationContext(), Main.class);
			startActivity(intent);
			Toast.makeText(getApplicationContext(), "Successfully Logout",
					Toast.LENGTH_SHORT).show();
			finish();
			return true;
		}
		if (id == R.id.action_add_class) {
			Intent intent = new Intent(getApplicationContext(), AddClass.class);
			intent.putExtra("username", _username);
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_add_assign) {
			Intent intent = new Intent(getApplicationContext(),
					AddAssignment.class);
			intent.putExtra("username", _username);
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_refresh) {

			UserLoginTask mAuthTask = new UserLoginTask(_username, _password);
			mAuthTask.execute((Void) null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private JSONObject feedback;

		UserLoginTask(String email, String password) {
			mEmail = email;
			mPassword = password;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://104.236.34.81/login/");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("username", mEmail));
				nameValuePairs
						.add(new BasicNameValuePair("password", mPassword));
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

						renderExpandablelist(feedback.toString());
						Toast.makeText(getApplicationContext(),
								"Assignments Updated", Toast.LENGTH_SHORT)
								.show();

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

				// fresh error
				Toast.makeText(getApplicationContext(), "Network Error",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	public class CheckboxTask extends AsyncTask<Void, Void, Boolean> {

		private final String mUsername;
		private final String mAid;
		private final boolean mCheck;
		private JSONObject feedback;

		CheckboxTask(String username, String aid, boolean check) {
			mUsername = username;
			mAid = aid;
			mCheck = check;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(
						"http://104.236.34.81/updateAssignment/");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs
						.add(new BasicNameValuePair("username", mUsername));
				nameValuePairs.add(new BasicNameValuePair("aid", mAid));
				if (mCheck) {
					nameValuePairs.add(new BasicNameValuePair("finish", "1"));
				} else {
					nameValuePairs.add(new BasicNameValuePair("finish", "0"));
				}
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

						Toast.makeText(getApplicationContext(),
								"Assignments Status Synced", Toast.LENGTH_SHORT)
								.show();

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

				// fresh error
				Toast.makeText(getApplicationContext(), "Network Error",
						Toast.LENGTH_SHORT).show();
			}
		}

	}
}
