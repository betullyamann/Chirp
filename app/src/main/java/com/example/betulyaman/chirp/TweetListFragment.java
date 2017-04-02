package com.example.betulyaman.chirp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class TweetListFragment extends Fragment {

    private LinearLayout linearLayout;


    public TweetListFragment() {
        linearLayout = (LinearLayout) getActivity().findViewById(R.id.linear_layout);
    }

    public void addView(View view) {
        linearLayout.addView(view);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tweet_list, container, false);
    }

}
