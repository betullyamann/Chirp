package com.example.betulyaman.chirp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.betulyaman.chirp.R.id;
import com.example.betulyaman.chirp.R.layout;
import com.example.betulyaman.chirp.containers.SimplifiedTweet;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TweetFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;

    public TweetFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflater-sisirmek
        View view = inflater.inflate(layout.fragment_tweet, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(id.swipe_refresh_layout);
        listView = (ListView) view.findViewById(id.list_view);
        return view;
    }

    public void setListToView(ArrayList<SimplifiedTweet> tweets) {
        listView.setAdapter(new ArrayAdapter<SimplifiedTweet>(getActivity().getApplicationContext(), layout.layout_tweet, tweets));
    }

}
