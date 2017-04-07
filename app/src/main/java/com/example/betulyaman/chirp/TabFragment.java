package com.example.betulyaman.chirp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.betulyaman.chirp.R.id;
import com.example.betulyaman.chirp.R.layout;

public class TabFragment extends Fragment {


    private static final int int_items = 3;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate tab_layout and setup Views.
        View x = inflater.inflate(layout.layout_tab, null);
        tabLayout = (TabLayout) x.findViewById(id.layout_tab);
        viewPager = (ViewPager) x.findViewById(id.pager_view);


        // Set an Apater for the View Pager
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new HomeFragment(), "Home");
        viewPagerAdapter.addFragment(new TopFreeFragment(), "Top Free");
        viewPagerAdapter.addFragment(new TopPaidFragment(), "Top Paid");
        viewPager.setAdapter(viewPagerAdapter);

        // Now, this is a workaround. The setupWithViewPager dose't works without the runnable. Maybe a Support Library Bug.
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        return x;
    }
}
