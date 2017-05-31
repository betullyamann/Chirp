package info.chirpapp.chirp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import info.chirpapp.chirp.R.id;
import info.chirpapp.chirp.R.layout;
import info.chirpapp.chirp.containers.SimplifiedTweet;

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
