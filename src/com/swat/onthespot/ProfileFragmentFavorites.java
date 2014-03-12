package com.swat.onthespot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileFragmentFavorites extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.profile_tab_fovorites, container, false);
        TextView text = (TextView) rootView.findViewById(R.id.profile_tab_favorites_text);
        text.setText("This is the favorites tab");
        
        return rootView;
    }
}
