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

import info.chirpapp.chirp.containers.Category;
import info.chirpapp.chirp.handlers.CategorizationHandler;

public class TabFragment extends Fragment {

    ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate tab_layout and setup Views.
        View view = inflater.inflate(info.chirpapp.betulyaman.chirp.R.layout.layout_tab, null);
        tabLayout = (TabLayout) view.findViewById(info.chirpapp.betulyaman.chirp.R.id.layout_tab);
        viewPager = (ViewPager) view.findViewById(info.chirpapp.betulyaman.chirp.R.id.pager_view);

        // Set an Adapter for the View Pager
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        //for(String category : new DatabaseHandler(getActivity().getApplicationContext()).getCategoryNames()){
        //    viewPagerAdapter.addFragment(new TweetFragment(), category);
        //}

        viewPager.setAdapter(viewPagerAdapter);

        // Now, this is a workaround. The setupWithViewPager doesn't works without the runnable. Maybe a Support Library Bug.
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        populateFragments();

        return view;
    }

    public void populateFragments() {
        ArrayList<Category> categories = CategorizationHandler.start(getActivity().getApplicationContext());

        for (Category category : categories) {
            viewPagerAdapter.getTitleFragment(category.getName()).setListToView(category.getTweets());
        }
    }
}
