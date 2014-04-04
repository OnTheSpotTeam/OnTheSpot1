package com.swat.onthespot.support;

import java.io.FileOutputStream;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.swat.onthespot.ItinMapFragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

public class SaveMapTask extends AsyncTask<String, Void, Void> {
 
  private GoogleMap map;
  private Context context;
	private String FILE_NAME;
	private ProgressDialog progressDialog;
  public SaveMapTask(String FILE_NAME, GoogleMap map, Context context) {
      this.map = map;
      this.context = context;
      this.FILE_NAME = FILE_NAME;
  }
  
  @Override
  protected void onPreExecute() {
      super.onPreExecute();
      progressDialog = ProgressDialog.show(context, "Wait", "Saving...");
  }

  @Override
  protected Void doInBackground(String... params) {
  	SnapshotReadyCallback callback = new SnapshotReadyCallback() {
			Bitmap bitmap;

			@Override
			public void onSnapshotReady(Bitmap snapshot) {
				// TODO Auto-generated method stub
				bitmap = snapshot;
				try {
					Log.i("FILENAME", FILE_NAME);
					FileOutputStream out = context.openFileOutput(FILE_NAME +".png", Context.MODE_PRIVATE);
					bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
					out.flush();
					out.close();
					progressDialog.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

	 map.snapshot(callback);
   return null;
  }

  /**
  * After completing background task Dismiss the progress dialog
  * **/
  @Override
  protected void onPostExecute(Void v) {
      ((Activity)context).finish();
     
  }
}
	