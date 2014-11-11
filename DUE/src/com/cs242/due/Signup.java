package com.cs242.due;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class Signup extends ActionBarActivity {

	public final static String EXTRA_MESSAGE1 = "com.example.myfirstapp.MESSAGE1";
	public final static String EXTRA_MESSAGE2 = "com.example.myfirstapp.MESSAGE2";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		// Remember that you should never show the action bar if the
		// status bar is hidden, so hide that too if necessary.

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.signup, menu);
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
	public void sendMessage(View view){
		Intent intent = new Intent(this, Signup_sub.class);
		EditText username = (EditText) findViewById(R.id.username);
	    String message = username.getText().toString();
		EditText password = (EditText) findViewById(R.id.password);
	    String message2 = password.getText().toString();
	    EditText firstname = (EditText) findViewById(R.id.firstname);
	    String message3 = firstname.getText().toString();
		EditText lastname = (EditText) findViewById(R.id.lastname);
	    String message4 = lastname.getText().toString();
	    intent.putExtra("username", message);
	    intent.putExtra("password", message2);
	    intent.putExtra("firstname", message3);
	    intent.putExtra("lastname", message4);
	    startActivity(intent);
	}
}
