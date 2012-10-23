package com.karhatsu.omatpysakit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	private Stops stops = new Stops();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ListView stopListView = (ListView) findViewById(R.id.stop_list);
		stopListView.setAdapter(getStopListAdapter());
	}

	private ListAdapter getStopListAdapter() {
		return new ArrayAdapter<String>(this, R.layout.stop_list_item,
				stops.getTitles());
	}

	public void addStop(View button) {
		Intent intent = new Intent(this, AddStopActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
