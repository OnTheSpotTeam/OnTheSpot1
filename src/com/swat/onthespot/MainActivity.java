package com.swat.onthespot;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

public class MainActivity extends Activity {
	private static final String TAG = "OTS_MainActivity";
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    
    private String[] mFragmentNames;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Record the initial title (app name)
	    mTitle = mDrawerTitle = getTitle();
        mFragmentNames = getResources().getStringArray(R.array.fragment_names);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);	
        
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mFragmentNames));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // TODO: Peng: The following function is from the sample code. It's added in API level 14.
        // I didn't see any difference after commenting it.
        //getActionBar().setHomeButtonEnabled(true); 

        
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		String tag = "SearchConfig"; // for debugging
		//Setting up search configuration
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		
		SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
		searchView.setSearchableInfo(info);
		
		if (info == null) {
			Log.wtf("SearchableInfo", "is null!");
		}
		
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
		case R.id.action_settings:
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
    
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    
    private void selectItem(int position) {
        // update the main content by replacing fragments
    	
    	Log.d(TAG, "Selected " + position);
        
    	
    	/* TODO: Peng: This is a temporary solution. 
    	 * Creating new fragments and Bundles for each selection is really inefficient.
    	 * We will need to create corresponding fields in the class, and store them once we create them.
    	 * In this way, we only need to create them once.
    	 */
        FragmentManager fragmentManager = getFragmentManager();
        Bundle args = new Bundle();
        Fragment fragment;
        switch (position){
        case 0: // Profile Tab pressed
        	fragment = new ProfileFragment();
        	args.putString("key", "This is the profile tab.");
        	fragment.setArguments(args);
        	fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        	break;
        case 1:
        	fragment = new NearMeFragment();
        	args.putString("key", "This is the near me tab.");
        	fragment.setArguments(args);
        	fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        	break;
        case 2:
        	fragment = new SearchFragment();
        	args.putString("key", "This is the search tab.");
        	fragment.setArguments(args);
        	fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        	break;
        case 3:
        	fragment = new NewsFeedFragment();
        	args.putString("key", "This is the news feed tab.");
        	fragment.setArguments(args);
        	fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        	break;
        default:
        	Log.e(TAG, "Drawer selection out of range!");
        }
        
        
        mDrawerList.setItemChecked(position, true);
        setTitle(mFragmentNames[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
        
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
