package com.swat.onthespot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileFragmentReviews extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.profile_tab_reviews, container, false);
        TextView text = (TextView) rootView.findViewById(R.id.profile_tab_reviews_text);
        text.setText("This is the reviews tab");
         
        return rootView;
    }
}
