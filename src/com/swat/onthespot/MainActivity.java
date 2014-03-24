package com.swat.onthespot;


import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.swat.onthespot.support.ProfileTabsAdapter;
import com.viewpagerindicator.TabPageIndicator;


public class MainActivity extends FragmentActivity {
	public static final String USER_NAME = "Sam Green";
	private static final String TAG = "OTS_MainActivity";
	
	// For Navigation Drawer
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPageNames;
    View mProfileView = null;
    View mNearMeView = null;
    View mNewsFeedView = null;
    
    // For Tabs in Profile Page
    private ProfileTabsAdapter mProfileAdapter;
    private ViewPager mProfileViewPager;
    private TabPageIndicator mProfileIndicator;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Record the initial title (app name)
	    mTitle = mDrawerTitle = getTitle();
        mPageNames = getResources().getStringArray(R.array.fragment_names);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);	
        
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.list_item_drawer, mPageNames));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);

        
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
        
	}

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		//Setting up search configuration
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		
		SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
		searchView.setSearchableInfo(info);

		// Change the maximum width of the searchView. Original one is not wide enough
		// setMaxWidth() function takes pixel value. Need to convert from 
		// resolution-independent unit dp;
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float dp = 1000f; // 6 inches in a 160 dpi screen is probably enough.
		int pixels = (int) (metrics.density * dp + 0.5f);
		searchView.setMaxWidth(pixels);
		
		// Display the submit button while searching.
		// searchView.setSubmitButtonEnabled(true);
		 	
		return true;
	}
	
    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	
    	// TODO: Peng: This will be useful when we handle option menu buttons.
        // If the nav drawer is open, hide action items related to the content view
    	/*
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        */
        return super.onPrepareOptionsMenu(menu);
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        
		switch (item.getItemId()){
		case R.id.menu_action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
        // TODO: Peng: This will be useful when we handle option menu buttons.
        /*
        switch(item.getItemId()) {
        case R.id.action_websearch:
            // create intent to perform web search for this planet
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
            // catch event that there's no activity to handle intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
        */
    }
    
    private void selectItem(int position) {
        // update the main content by replacing fragments
    	//Log.d(TAG, "Selected " + position);
        
        //FragmentManager fragmentManager = getFragmentManager();
        //Bundle args = new Bundle();
        //Fragment fragment;

    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
    	ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
    	contentFrame.removeAllViews();
        switch (position){
        case 0: // Profile Tab pressed
        	fillProfilePage(inflater, contentFrame);
        	break;
        case 1:
        	fillNearMePage(inflater, contentFrame);
        	break;
        case 2:
        	fillNewsFeedPage(inflater, contentFrame);
        	break;
        default:
        	Log.e(TAG, "Drawer selection out of range!");
        }
        
        mDrawerList.setItemChecked(position, true);
        setTitle(mPageNames[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    private void fillProfilePage(LayoutInflater inflater, ViewGroup contentFrame){
    	if (mProfileView == null){
    		mProfileView = inflater.inflate(R.layout.drawer_profile, null);
    		//TextView text = (TextView)mProfileView.findViewById(R.id.profile_text);
    		//text.setText("This is the profile page");
    	}
    	contentFrame.addView(mProfileView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));	

        // For tabs in Profile Page.
        mProfileAdapter = new ProfileTabsAdapter(getSupportFragmentManager(), this);
        mProfileAdapter.notifyDataSetChanged();
        mProfileViewPager = (ViewPager) findViewById(R.id.profile_pager);
        mProfileViewPager.setAdapter(mProfileAdapter);
        mProfileIndicator = (TabPageIndicator)findViewById(R.id.indicator);
        mProfileIndicator.setViewPager(mProfileViewPager);
    }
    
    
    private void fillNearMePage(LayoutInflater inflater, ViewGroup contentFrame){
    	if (mNearMeView == null){
    		mNearMeView = inflater.inflate(R.layout.drawer_near_me, null);
    		TextView text = (TextView)mNearMeView.findViewById(R.id.near_me_text);
    		text.setText("This is the near me page");
    	}
    	contentFrame.addView(mNearMeView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));	
    }
    
    private void fillNewsFeedPage(LayoutInflater inflater, ViewGroup contentFrame){
    	if (mNewsFeedView == null){
    		mNewsFeedView = inflater.inflate(R.layout.drawer_news_feed, null);
    		TextView text = (TextView)mNewsFeedView.findViewById(R.id.news_feed_text);
    		text.setText("This is the news feed page");
    	}
    	contentFrame.addView(mNewsFeedView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));	
    }
    


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
 
    
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }   

}
