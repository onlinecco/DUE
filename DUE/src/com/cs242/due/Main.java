package com.cs242.due;


import com.cs242.due.Login.UserLoginTask;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class Main extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    SharedPreferences mySharedPreferences = getApplicationContext().getSharedPreferences("USER_PREFS", Activity.MODE_PRIVATE);
	    String password = mySharedPreferences.getString("PASSWORD", null);
	    
	    if(password != null){
			Intent intent = new Intent(this, Login.class);
			startActivity(intent);
			finish();
	    }
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		View decorView = getWindow().getDecorView();
		// Hide the status bar.
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		// Remember that you should never show the action bar if the
		// status bar is hidden, so hide that too if necessary.


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	public void signup(View view){
		Intent intent = new Intent(this, Signup.class);
	    intent.putExtra("haha", "haha");
		startActivity(intent);
	}
	
	public void login(View view){
		Intent intent = new Intent(this, Login.class);
		startActivity(intent);
		finish();
	}
}
