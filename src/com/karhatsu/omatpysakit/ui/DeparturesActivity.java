package com.karhatsu.omatpysakit.ui;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ListView;

import com.karhatsu.omatpysakit.datasource.OnStopRequestReady;
import com.karhatsu.omatpysakit.datasource.StopRequest;
import com.karhatsu.omatpysakit.domain.Stop;

public class DeparturesActivity extends ListActivity implements
		OnStopRequestReady {

	private ProgressDialog progressDialog;
	private StopRequest stopRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Object retained = getLastNonConfigurationInstance();
		if (retained instanceof StopRequest) {
			stopRequest = (StopRequest) retained;
			stopRequest.setOnStopRequestReady(this);
		} else {
			stopRequest = new StopRequest(this);
			queryDepartures();
		}
	}

	private void queryDepartures() {
		String stopCode = getIntent().getStringExtra(Stop.CODE_KEY);
		showProgressDialog();
		stopRequest.execute(stopCode);
	}

	private void showProgressDialog() {
		progressDialog = new PleaseWaitDialog(this);
		progressDialog.show();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		stopRequest.setOnStopRequestReady(null);
		return stopRequest;
	}

	@Override
	public void notifyStopRequested(Stop stop) {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		ListView departuresListView = getListView();
		DepartureListAdapter adapter = new DepartureListAdapter(this,
				stop.getDepartures());
		departuresListView.setAdapter(adapter);
	}
}
