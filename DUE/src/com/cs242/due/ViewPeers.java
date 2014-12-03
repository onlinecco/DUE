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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_peers);
		Intent intent = getIntent();
		String aid = intent.getStringExtra("aid");
		expand = (ListView) this.findViewById(R.id.asspeers_listview);
		GetAssPeersTask mtask = new GetAssPeersTask(aid);
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

	}

	private void renderExpandablelist(JSONObject feedback) {


		try {
			JSONArray jArray = feedback.getJSONArray("peers");

			List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();

			for (int i = 0; i < jArray.length(); i++) {

				JSONObject oneObject = jArray.getJSONObject(i);
				// Pulling items from the array
				String oneObjectsItem = oneObject.getString("Name");
				String twoObjectsItem = oneObject.getString("Aid");

				Map<String, String> curGroupMap = new HashMap<String, String>();
				groupData.add(curGroupMap);
				curGroupMap.put("Name", oneObjectsItem);
				curGroupMap.put("Aid", twoObjectsItem);

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

	public class MySimpleArrayAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final List<Map<String, String>> values;

		public MySimpleArrayAdapter(Context context,
				List<Map<String, String>> values) {
			super(context, R.layout.list_item);
			this.context = context;
			this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.list_item, parent, false);

			TextView textView = (TextView) rowView.findViewById(R.id.text1);
			Button buttonView = (Button) rowView.findViewById(R.id.duebutton);

			textView.setText(values.get(position).get("Name"));
			// Change the icon for Windows and iPhone
			buttonView.setText(values.get(position).get("Aid"));

			return rowView;
		}
	}

	public class GetAssPeersTask extends AsyncTask<Void, Void, Boolean> {

		private final String aid;
		private JSONObject feedback;

		GetAssPeersTask(String aid) {
			this.aid = aid;
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
				nameValuePairs.add(new BasicNameValuePair("aid", aid));
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

}
