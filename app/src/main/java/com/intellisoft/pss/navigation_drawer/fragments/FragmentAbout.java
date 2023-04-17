package com.intellisoft.pss.navigation_drawer.fragments;

import static org.apache.commons.lang3.SystemUtils.getUserName;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.intellisoft.pss.R;
import com.intellisoft.pss.adapter.ViewPagerAdapter;
import com.intellisoft.pss.helper_class.FormatterClass;
import com.intellisoft.pss.helper_class.SettingsQueue;
import com.intellisoft.pss.navigation_drawer.fragments.child.FragmentAboutApp;
import com.intellisoft.pss.navigation_drawer.fragments.child.FragmentAboutPss;

import org.w3c.dom.Text;

public class FragmentAbout extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter pagerAdapter;

    public FragmentAbout() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        viewPager = rootView.findViewById(R.id.viewPager);
        tabLayout = rootView.findViewById(R.id.tabLayout);
        pagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        pagerAdapter.addFragment(new FragmentAboutPss(), "About PSS");
        pagerAdapter.addFragment(new FragmentAboutApp(), "About App");
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(requireContext().getResources().getColor(R.color.secondaryColor),requireContext().getResources().getColor(R.color.secondaryColor));
        tabLayout.setSelectedTabIndicatorColor(requireContext().getResources().getColor(R.color.secondaryColor));
        return rootView;
    }


}