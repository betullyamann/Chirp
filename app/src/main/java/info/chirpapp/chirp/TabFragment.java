package info.chirpapp.chirp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import info.chirpapp.chirp.R.id;
import info.chirpapp.chirp.R.layout;
import info.chirpapp.chirp.containers.Category;
import info.chirpapp.chirp.handlers.CategorizationHandler;
import info.chirpapp.chirp.handlers.DatabaseHandler;

public class TabFragment extends Fragment {

    ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private  CategorizationHandler categorizationHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate tab_layout and setup Views.
        View view = inflater.inflate(layout.layout_tab, null);
        tabLayout = (TabLayout) view.findViewById(id.layout_tab);
        viewPager = (ViewPager) view.findViewById(id.pager_view);
        categorizationHandler = new CategorizationHandler(getContext());

        // Set an Adapter for the View Pager
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        for(String category : new DatabaseHandler(getActivity().getApplicationContext()).getCategoryNames()){
            viewPagerAdapter.addFragment(new TweetFragment(), category);
        }

        viewPager.setAdapter(viewPagerAdapter);

        // Now, this is a workaround. The setupWithViewPager doesn't works without the runnable. Maybe a Support Library Bug.
        tabLayout.post(() -> tabLayout.setupWithViewPager(viewPager));

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                populateFragments();
            }
        };

        thread.start();

        return view;
    }

    public void populateFragments() {
        ArrayList<Category> categories = categorizationHandler.start();

        for (Category category : categories) {
            viewPagerAdapter.getTitleFragment(category.getName()). setListToView(new ArrayList<>(category.getTweets().keySet()));
        }
    }
}
