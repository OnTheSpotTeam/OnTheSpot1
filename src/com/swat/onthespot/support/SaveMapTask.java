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

public class SaveMapTask extends AsyncTask<Void, Void, Void> {
 
  private GoogleMap map;
  private Activity context;
	private String FILE_NAME;
	private ProgressDialog progressDialog;
  public SaveMapTask(String FILE_NAME, GoogleMap map, ProgressDialog dialog, Activity context) {
      this.map = map;
      this.context = context;
      this.FILE_NAME = FILE_NAME;
      progressDialog = dialog;
  }
  
  @Override
  protected void onPreExecute() {
      super.onPreExecute();
     
  }

  @Override
  protected Void doInBackground(Void... params) {
  	try
    {
	    Thread.sleep(2000);
    } catch (InterruptedException e1)
    {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
    }
  	try{
  		progressDialog.show();
  		SnapshotReadyCallback callback = new SnapshotReadyCallback() {
  			Bitmap bitmap;
  			@Override
  			public void onSnapshotReady(Bitmap snapshot) {
  				// TODO Auto-generated method stub
  				try {
  					bitmap = snapshot;
  					Log.i("FILENAME", FILE_NAME);
  					FileOutputStream out = context.openFileOutput(FILE_NAME +".png", Context.MODE_PRIVATE);
  					bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
  					out.flush();
  					out.close();
  					progressDialog.dismiss();
  					((Activity)context).finish();
  					Log.i("MapSave", "onSnap");
  				} catch (Exception e) {
  					e.printStackTrace();
  				}
  			}
  		};

  		map.snapshot(callback);
  		return null;
  	} catch (Exception e) {
  		e.printStackTrace();
  		return null;
  	}
  	
  }
  /**
  * After completing background task Dismiss the progress dialog
  * **/
  @Override
  protected void onPostExecute(Void v) {
     Log.i("MapSave", "onPost");
  }
}
	