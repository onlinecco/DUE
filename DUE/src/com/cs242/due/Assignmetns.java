package com.cs242.due;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cs242.due.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

public class Assignmetns extends ExpandableListActivity {
	
	private ExpandableListAdapter mAdapter;
	ExpandableListView expand;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assignmetns);
		Intent intent = getIntent();
		String message1 = intent.getStringExtra("bundle");
		expand = (ExpandableListView) this.findViewById(R.id.expandableListView1);
		mAdapter = (ExpandableListAdapter) expand.getExpandableListAdapter();
		JSONObject feedback;
		
		try {
			feedback = new JSONObject(message1);
			JSONArray jArray = feedback.getJSONArray("courses");
	        
	        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
	        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
	        
			for (int i=0; i < jArray.length(); i++)
			{

			        JSONObject oneObject = jArray.getJSONObject(i);
			        // Pulling items from the array
			        String oneObjectsItem = oneObject.getString("course");
			        JSONArray jArray2 = feedback.getJSONArray("assignments");

			        
			        Map<String, String> curGroupMap = new HashMap<String, String>();
			        groupData.add(curGroupMap);
			        curGroupMap.put("course", oneObjectsItem);
			            
			            List<Map<String, String>> children = new ArrayList<Map<String, String>>();
			            for (int j = 0; j < jArray2.length(); j++) {
			            	Map<String, String> curChildMap = new HashMap<String, String>();
			                children.add(curChildMap);
			            	JSONObject aObject = jArray2.getJSONObject(i);
			                
			               // curChildMap.put(NAME, "Child " + j);
			                curChildMap.put("title", aObject.getString("title"));
			                curChildMap.put("content", aObject.getString("content"));
			                curChildMap.put("date", aObject.getString("date"));
			            }
			            childData.add(children);
			        }
			        
			        // Set up our adapter
			        mAdapter = new SimpleExpandableListAdapter(
			                this,
			                groupData,
			                R.layout.groupitem,
			                new String[] { "course"},
			                new int[] { R.id.text1 },
			                childData,
			                R.layout.children_item,
			                new String[] { "title","content","date" },
			                new int[] { R.id.title, R.id.content,R.id.date }
			                );
			        setListAdapter(mAdapter);

			        expand.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

			            @Override
			            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
			                //Nothing here ever fires
			                System.err.println("child clicked");
			                Toast.makeText(getApplicationContext(), "child clicked", Toast.LENGTH_SHORT).show();
			                return true;
			            }

			        });

			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((BaseAdapter) mAdapter).notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.assignmetns, menu);
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
}
