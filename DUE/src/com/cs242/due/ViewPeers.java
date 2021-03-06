package com.cs242.due;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewPeers extends ActionBarActivity {

	private ListAdapter mAdapter;
	private ListView expand;
	private String aid;
	private String uid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_peers);
		Intent intent = getIntent();
		aid = intent.getStringExtra("aid");
		uid = intent.getStringExtra("uid");
		expand = (ListView) this.findViewById(R.id.asspeers_listview);
		GetAssPeersTask mtask = new GetAssPeersTask(aid,uid);
		mtask.execute((Void) null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_peers, menu);
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

	public void onDueButtonClicked(View view) {
		// TODO
		String tuid = (String)((Button) view).getTag();
		DUETask haha = new DUETask(aid,uid,tuid);
		haha.execute((Void) null);

	}

	private void renderExpandablelist(JSONObject feedback) {


		try {
			JSONArray jArray = feedback.getJSONArray("user");

			List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();

			for (int i = 0; i < jArray.length(); i++) {

				JSONObject oneObject = jArray.getJSONObject(i);
				// Pulling items from the array
				String oneObjectsItem = oneObject.getString("name");
				String twoObjectsItem = oneObject.getString("uid");
				Map<String, String> curGroupMap = new HashMap<String, String>();
				groupData.add(curGroupMap);
				curGroupMap.put("name", oneObjectsItem);
				curGroupMap.put("uid", twoObjectsItem);

			}

			// Create ArrayAdapter using the planet list.
			mAdapter = new MySimpleArrayAdapter(this, groupData);
			expand.setAdapter(mAdapter);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.err.println("JSON file is corrupted.");
			e.printStackTrace();
		}
	}

	public class MySimpleArrayAdapter extends ArrayAdapter<Map<String, String>> {
		private final Context context;
		private final List<Map<String, String>> values;

		public MySimpleArrayAdapter(Context context,
				List<Map<String, String>> values) {
			super(context, R.layout.list_item, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item, parent, false);

			TextView textView = (TextView) convertView.findViewById(R.id.text1);
			Button buttonView = (Button) convertView.findViewById(R.id.duebutton);
			System.err.println(values.get(position).get("name"));
			System.err.println(values.get(position).get("uid"));
			textView.setText(values.get(position).get("name"));
			// Change the icon for Windows and iPhone
			buttonView.setTag(values.get(position).get("uid"));

			return convertView;
		}
	}

	public class GetAssPeersTask extends AsyncTask<Void, Void, Boolean> {

		private final String aid;
		private final String uid;
		private JSONObject feedback;

		GetAssPeersTask(String aid, String uid) {
			this.aid = aid;
			this.uid = uid;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(
						"http://104.236.34.81/assignmentUsers/");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("aid", aid));
				nameValuePairs.add(new BasicNameValuePair("username", uid));
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
						renderExpandablelist(feedback);
						System.err.println(feedback.toString());
						Toast.makeText(getApplicationContext(),
								"Peers list updated", Toast.LENGTH_SHORT)
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
	
	public class DUETask extends AsyncTask<Void, Void, Boolean> {

		private final String mUsername;
		private final String mAid;
		private final String mUid;
		private JSONObject feedback;

		DUETask(String aid, String username, String uid) {
			this.mAid = aid;
			this.mUid = uid;
			this.mUsername = username;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(
						"http://104.236.34.81/due/");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("suname", mUsername));
				nameValuePairs.add(new BasicNameValuePair("tuid", mUid));
				nameValuePairs.add(new BasicNameValuePair("aid", mAid));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				System.err.println(mUsername+mUid +mAid);
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
				/*
				try {
					boolean loginSuccess = feedback.getBoolean("status");

					if (loginSuccess) {
						System.err.println(feedback.toString());
						Toast.makeText(getApplicationContext(),
								"Peers list updated", Toast.LENGTH_SHORT)
								.show();

					} else {
						Toast.makeText(getApplicationContext(),
								feedback.getString("error"), Toast.LENGTH_SHORT)
								.show();

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				Toast.makeText(getApplicationContext(), "DUE!",
						Toast.LENGTH_SHORT).show();

			} else {

				// fresh error
				Toast.makeText(getApplicationContext(), "Network Error",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

}
