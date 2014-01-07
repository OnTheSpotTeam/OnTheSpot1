package com.swat.onthespot;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;


public class NewsFeedFragment extends Fragment {
	private String mParam;

	
	/* Peng: For interaction with container activity. Not needed right now */
	//private OnFragmentInteractionListener mListener;

	public NewsFeedFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get the string argument that we inserted in MainActivity.
		if (getArguments() != null) {
			//
			try {
				mParam = getArguments().getString("key");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// get the parent of this fragment view:
		FrameLayout parent = (FrameLayout) getActivity().findViewById(R.id.content_frame);
				
		// Inflate the layout for this fragment
		View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_news_feed,
				parent, false);
		
		TextView text = (TextView) v.findViewById(R.id.news_feed_fragment_text);

		text.setText(mParam);
		
		return v;
	}

	// TODO: Rename method, update argument and hook method into UI event
	/* Peng: For interaction with container activity. Not needed right now */
	/*
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}
	*/

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		/* Peng: For interaction with container activity. Not needed right now */
		/*
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
		*/
	}

	@Override
	public void onDetach() {
		super.onDetach();
		/* Peng: For interaction with container activity. Not needed right now */
		//mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	/* Peng: For interaction with container activity. Not needed right now */
	/*
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}
	*/

}
