package com.swat.onthespot.support;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.swat.onthespot.ItinMapFragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;

public class DrawArrowsTask extends AsyncTask<Void, Void, Bitmap> {
 
  private GoogleMap map;
  private Activity context;
  private LatLng from;
  private LatLng to;
  private final double degreesPerRadian = 180.0 / Math.PI;
  private double bearing;

  public DrawArrowsTask(GoogleMap map, Activity context, LatLng from, LatLng to) {
      this.map = map;
      this.context = context;
      this.from = from;
      this.to = to;
      
  }
  
  @Override
  protected void onPreExecute() {
      super.onPreExecute();
     
  }

  @Override
  protected Bitmap doInBackground(Void... params) {
  	return getArrowHead(from, to);
  }
  /**
  * After completing background task Dismiss the progress dialog
  * **/
  @Override
  protected void onPostExecute(Bitmap image) {
     Log.i("MapSave", "onPost");
     DrawArrowHead(image);
  }
  
	private Bitmap getArrowHead(LatLng from, LatLng to){
    // obtain the bearing between the last two points
    bearing = GetBearing(from, to);

    // round it to a multiple of 3 and cast out 120s
    double adjBearing = Math.round(bearing / 3) * 3;
    while (adjBearing >= 120) {
        adjBearing -= 120;
    }
    // Get the corresponding triangle marker from Google        
    URL url;
    Bitmap image = null;

    try {
        url = new URL("http://www.google.com/intl/en_ALL/mapfiles/dir_" + String.valueOf((int)adjBearing) + ".png");
        try {
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    } catch (MalformedURLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }

    return image;
}

private double GetBearing(LatLng from, LatLng to){
    double lat1 = from.latitude * Math.PI / 180.0;
    double lon1 = from.longitude * Math.PI / 180.0;
    double lat2 = to.latitude * Math.PI / 180.0;
    double lon2 = to.longitude * Math.PI / 180.0;

    // Compute the angle.
    double angle = - Math.atan2( Math.sin( lon1 - lon2 ) * Math.cos( lat2 ), Math.cos( lat1 ) * Math.sin( lat2 ) - Math.sin( lat1 ) * Math.cos( lat2 ) * Math.cos( lon1 - lon2 ) );

    if (angle < 0.0)
        angle += Math.PI * 2.0;

    // And convert result to degrees.
    angle = angle * degreesPerRadian;

    return angle;
}

private void DrawArrowHead(Bitmap image)
{
	if (image != null){

    // Anchor is ratio in range [0..1] so value of 0.5 on x and y will center the marker image on the lat/long
    float anchorX = 0.5f;
    float anchorY = 0.5f;

    int offsetX = 0;
    int offsetY = 0;

    // images are 24px x 24px
    // so transformed image will be 48px x 48px

    //315 range -- 22.5 either side of 315
    if (bearing >= 292.5 && bearing < 335.5){
        offsetX = 24;
        offsetY = 24;
    }
    //270 range
    else if (bearing >= 247.5 && bearing < 292.5){
        offsetX = 24;
        offsetY = 12;
    }
    //225 range
    else if (bearing >= 202.5 && bearing < 247.5){
        offsetX = 24;
        offsetY = 0;
    }
    //180 range
    else if (bearing >= 157.5 && bearing < 202.5){
        offsetX = 12;
        offsetY = 0;
    }
    //135 range
    else if (bearing >= 112.5 && bearing < 157.5){
        offsetX = 0;
        offsetY = 0;
    }
    //90 range
    else if (bearing >= 67.5 && bearing < 112.5){
        offsetX = 0;
        offsetY = 12;
    }
    //45 range
    else if (bearing >= 22.5 && bearing < 67.5){
        offsetX = 0;
        offsetY = 24;
    }
    //0 range - 335.5 - 22.5
    else {
        offsetX = 12;
        offsetY = 24;
    }

    Bitmap wideBmp;
    Canvas wideBmpCanvas;
    Rect src, dest;

    // Create larger bitmap 4 times the size of arrow head image
    wideBmp = Bitmap.createBitmap(image.getWidth() * 2, image.getHeight() * 2, image.getConfig());

    wideBmpCanvas = new Canvas(wideBmp); 

    src = new Rect(0, 0, image.getWidth(), image.getHeight());
    dest = new Rect(src); 
    dest.offset(offsetX, offsetY); 

    wideBmpCanvas.drawBitmap(image, src, dest, null);

    map.addMarker(new MarkerOptions()
    .position(new LatLng((from.latitude + to.latitude) / 2, (from.longitude + to.longitude) / 2))
    .icon(BitmapDescriptorFactory.fromBitmap(wideBmp))
    .anchor(anchorX, anchorY));
}
}
  
}
	