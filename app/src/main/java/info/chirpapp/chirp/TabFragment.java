package info.chirpapp.chirp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import info.chirpapp.chirp.R.id;
import info.chirpapp.chirp.R.layout;
import info.chirpapp.chirp.containers.Category;
import info.chirpapp.chirp.handlers.CategorizationHandler;
import info.chirpapp.chirp.handlers.DatabaseHandler;

public class TabFragment extends Fragment {

    ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CategorizationHandler categorizationHandler;
    private HashMap<String, Category> categories;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate tab_layout and setup Views.
        View view = inflater.inflate(layout.layout_tab, null);
        tabLayout = (TabLayout) view.findViewById(id.layout_tab);
        viewPager = (ViewPager) view.findViewById(id.pager_view);
        categorizationHandler = new CategorizationHandler(getContext());
        categories = new HashMap<>();

        tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                if (!categories.isEmpty()) {
                    viewPagerAdapter.getTitleFragment(tab.getText().toString()).setListToView(new ArrayList<>(categories.get(tab.getText().toString()).getTweets().keySet()));
                }
            }

            @Override
            public void onTabUnselected(Tab tab) {

            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });

        // Set an Adapter for the View Pager
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        for (String category : new DatabaseHandler(getActivity().getApplicationContext()).getCategoryNames()) {
            viewPagerAdapter.addFragment(new TweetFragment(), category);
        }

        viewPager.setAdapter(viewPagerAdapter);

        // Now, this is a workaround. The setupWithViewPager doesn't works without the runnable. Maybe a Support Library Bug.
        tabLayout.post(() -> tabLayout.setupWithViewPager(viewPager));

        Thread thread = new Thread() {
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
        categories.clear();
        for (Category category : categorizationHandler.start()) {
            categories.put(category.getName(), category);
            viewPagerAdapter.getTitleFragment(category.getName()).setListToView(new ArrayList<>(category.getTweets().keySet()));
        }
    }
}
