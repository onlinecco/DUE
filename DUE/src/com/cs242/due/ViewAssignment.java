package com.cs242.due;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

public class ViewAssignment extends ActionBarActivity {

	private String aid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_assignment);
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		String content = intent.getStringExtra("content");
		String date = intent.getStringExtra("date");
		aid = intent.getStringExtra("aid");
		((TextView)findViewById(R.id.asstitle)).setText(title);
		((TextView)findViewById(R.id.asscontent)).setText(content);
		((TextView)findViewById(R.id.assdate)).setText(date);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_assignment, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(id == R.id.action_view_asspeers){
			
			Intent intent = new Intent(getApplicationContext(), Main.class);
			intent.putExtra("aid", aid);
			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
	}
}
