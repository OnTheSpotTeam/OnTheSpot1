package com.swat.onthespot;

import java.io.IOException;
import java.util.List;

import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.swat.onthespot.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class ItinMapFragment extends FragmentActivity implements RoutingListener
{
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after user
	 * interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
  GoogleMap map;
  LatLng start;
  LatLng end;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_itin_map_fragment);
    setTitle("Map View");
    SupportMapFragment fm = (SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map);
    map = fm.getMap();
    CameraUpdate center=CameraUpdateFactory.newLatLng(new LatLng(39.907036, -75.352040));
    CameraUpdate zoom=  CameraUpdateFactory.zoomTo(15);

    map.moveCamera(center);
    map.animateCamera(zoom);
    LatLng start = null;
    LatLng end= null;
    Geocoder coder = new Geocoder(this);
    String startAddress = "500 College Avenue, Swarthmore, PA 19081";
    String endAddress = "101 N Merion Ave, Bryn Mawr, PA 19010";
    List<Address> address;
 
  	try
    {
      //Convert Start Address into LatLng.
  		address = coder.getFromLocationName(startAddress,5);
      Address location = address.get(0);
    	location.getLatitude();
    	location.getLongitude();
    	start = new LatLng((int) (location.getLatitude() * 1E6),
      (int) (location.getLongitude() * 1E6));
    	
    	//Convert End Address into LatLng
      address = coder.getFromLocationName(endAddress,5);
      location = address.get(0);
    	location.getLatitude();
    	location.getLongitude();
    	end = new LatLng((int) (location.getLatitude() * 1E6),
      (int) (location.getLongitude() * 1E6));
    	
      Routing routing = new Routing(Routing.TravelMode.DRIVING);
      routing.registerListener(this);
      routing.execute(start, end);
    } catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
        	

        
    
    

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.map);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
		    .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener()
		    {
			    // Cached values.
			    int mControlsHeight;
			    int mShortAnimTime;

			    @Override
			    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
			    public void onVisibilityChange(boolean visible)
			    {
				    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
				    {
					    // If the ViewPropertyAnimator API is available
					    // (Honeycomb MR2 and later), use it to animate the
					    // in-layout UI controls at the bottom of the
					    // screen.
					    if (mControlsHeight == 0)
					    {
						    mControlsHeight = controlsView.getHeight();
					    }
					    if (mShortAnimTime == 0)
					    {
						    mShortAnimTime = getResources().getInteger(
						        android.R.integer.config_shortAnimTime);
					    }
					    controlsView.animate()
					        .translationY(visible ? 0 : mControlsHeight)
					        .setDuration(mShortAnimTime);
				    } else
				    {
					    // If the ViewPropertyAnimator APIs aren't
					    // available, simply show or hide the in-layout UI
					    // controls.
					    controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
				    }

				    if (visible && AUTO_HIDE)
				    {
					    // Schedule a hide().
					    delayedHide(AUTO_HIDE_DELAY_MILLIS);
				    }
			    }
		    });

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (TOGGLE_ON_CLICK)
				{
					mSystemUiHider.toggle();
				} else
				{
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the system
	 * UI. This is to prevent the jarring behavior of controls going away while
	 * interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener()
	{
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent)
		{
			if (AUTO_HIDE)
			{
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis)
	{
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	public void exitItin(View v)
	{
		finish();
	}
	
	@Override 
	public void onRoutingFailure() {
     // The Routing request failed
   }

	 @Override
   public void onRoutingStart() {
     // The Routing Request starts
   }

	 @Override
   public void onRoutingSuccess(PolylineOptions mPolyOptions) {
     PolylineOptions polyoptions = new PolylineOptions();
     polyoptions.color(Color.BLUE);
     polyoptions.width(10);
     polyoptions.addAll(mPolyOptions.getPoints());
     map.addPolyline(polyoptions);

     // Start marker
     MarkerOptions options = new MarkerOptions();
     options.position(start);
     options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
     map.addMarker(options);

     // End marker
     options = new MarkerOptions();
     options.position(end);
     options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));  
     map.addMarker(options);
   }
}
