package com.swat.onthespot.support;

import com.swat.onthespot.ProfileFragmentFavorites;
import com.swat.onthespot.ProfileFragmentItins;
import com.swat.onthespot.ProfileFragmentPhotos;
import com.swat.onthespot.ProfileFragmentReviews;
import com.swat.onthespot.R;
import com.swat.onthespot.R.array;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class ProfileTabsAdapter extends FragmentPagerAdapter {
	private static final String TAG = "OTS_ProfileTabsAdapter";
	private String[] mPageTitles;

	public ProfileTabsAdapter(FragmentManager fm, Context context) {
		super(fm);
		Resources res = context.getResources();
		mPageTitles = res.getStringArray(R.array.profile_tab_titles);
	}

	@Override
	public Fragment getItem(int position) {
		//Log.d(TAG, "getItem() called with position = " + position);
		switch (position) {
		case 0:
			return new ProfileFragmentItins();
		case 1:
			return new ProfileFragmentFavorites();
		case 2:
			return new ProfileFragmentReviews();
		case 3:
			return new ProfileFragmentPhotos();
		default:
			return null;
		}
	}


	@Override
	public CharSequence getPageTitle(int position) {
		return mPageTitles[position];
	}

	@Override
	public int getCount() {
		return mPageTitles.length;
	}

}
