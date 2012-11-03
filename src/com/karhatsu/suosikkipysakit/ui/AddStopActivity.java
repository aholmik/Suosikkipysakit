package com.karhatsu.suosikkipysakit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.karhatsu.suosikkipysakit.R;
import com.karhatsu.suosikkipysakit.datasource.OnStopRequestReady;
import com.karhatsu.suosikkipysakit.datasource.StopRequest;
import com.karhatsu.suosikkipysakit.db.StopDao;
import com.karhatsu.suosikkipysakit.domain.Stop;

public class AddStopActivity extends Activity implements OnStopRequestReady {

	private ProgressDialog progressDialog;
	private StopRequest stopRequest;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_stop);
		Object retained = getLastNonConfigurationInstance();
		if (retained instanceof StopRequest) {
			stopRequest = (StopRequest) retained;
			stopRequest.setOnStopRequestReady(this);
		} else {
			stopRequest = new StopRequest(this);
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		stopRequest.setOnStopRequestReady(null);
		return stopRequest;
	}

	public void searchStop(View button) {
		String code = getCode();
		if (!Stop.isValidCode(code)) {
			showToast(R.string.activity_add_stop_invalid_code);
			return;
		}
		showPleaseWait();
		stopRequest.execute(getCode());
	}

	private void showPleaseWait() {
		progressDialog = new PleaseWaitDialog(this);
		progressDialog.show();
	}

	private void showToast(int resourceId) {
		Toast toast = Toast.makeText(this, resourceId, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 200);
		toast.show();
	}

	private String getCode() {
		return ((EditText) findViewById(R.id.add_stop_code)).getText()
				.toString();
	}

	private void showSaveDialog(final Stop stop) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_save_stop, null);
		builder.setView(view);
		final EditText stopName = (EditText) view
				.findViewById(R.id.dialog_save_stop_name);
		stopName.setText(stop.getNameFi());
		builder.setTitle(R.string.dialog_save_stop_title);
		builder.setPositiveButton(R.string.dialog_save_stop_save,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						stop.setNameByUser(stopName.getText().toString());
						dialog.dismiss();
						saveStopAndShowAll(stop);
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void saveStopAndShowAll(Stop stop) {
		new StopDao().save(this, stop);
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	@Override
	public void notifyStopRequested(Stop stop) {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		if (stop != null) {
			showSaveDialog(stop);
		} else {
			showToast(R.string.activity_add_stop_not_found);
		}
	}

}
