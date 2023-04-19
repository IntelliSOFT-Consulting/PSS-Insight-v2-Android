package com.intellisoft.pss.navigation_drawer.fragments;

import static org.apache.commons.lang3.SystemUtils.getUserName;

import android.app.Application;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
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
import com.intellisoft.pss.helper_class.DbDataEntry;
import com.intellisoft.pss.helper_class.DbDataEntryDetails;
import com.intellisoft.pss.helper_class.DbDataEntryForm;
import com.intellisoft.pss.helper_class.FormatterClass;
import com.intellisoft.pss.helper_class.Information;
import com.intellisoft.pss.helper_class.NationalInformation;
import com.intellisoft.pss.helper_class.PublishedNationalInformation;
import com.intellisoft.pss.helper_class.SettingsQueue;
import com.intellisoft.pss.navigation_drawer.fragments.child.FragmentAboutApp;
import com.intellisoft.pss.navigation_drawer.fragments.child.FragmentAboutPss;
import com.intellisoft.pss.room.Converters;
import com.intellisoft.pss.room.IndicatorsData;
import com.intellisoft.pss.room.PssViewModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class FragmentAbout extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter pagerAdapter;

    public FragmentAbout() {
    }

    private PssViewModel myViewModel;
    private FormatterClass formatterClass = new FormatterClass();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        myViewModel = new PssViewModel(((Application) requireContext().getApplicationContext()));
        viewPager = rootView.findViewById(R.id.viewPager);
        tabLayout = rootView.findViewById(R.id.tabLayout);
        pagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        pagerAdapter.addFragment(new FragmentAboutPss(), "About PSS");
        pagerAdapter.addFragment(new FragmentAboutApp(), "About App");
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(requireContext().getResources().getColor(R.color.secondaryColor), requireContext().getResources().getColor(R.color.secondaryColor));
        tabLayout.setSelectedTabIndicatorColor(requireContext().getResources().getColor(R.color.secondaryColor));
        loadData();
        return rootView;
    }

    private void loadData() {
        IndicatorsData indicatorsData = myViewModel.getAllMyData(requireContext());
        if (indicatorsData != null) {
            String jsonData = indicatorsData.getJsonData();

            Converters converters = new Converters();
            DbDataEntry dataEntry = converters.fromJson(jsonData);
            List<NationalInformation> detailsList = dataEntry.getNationalInformation();
            for (int j = 0; j < detailsList.size(); j++) {
                String aboutUs = detailsList.get(j).getAboutUs();
                String contactUs = detailsList.get(j).getContactUs();

                formatterClass.saveSharedPref(Information.ABOUT.name(),
                        aboutUs, requireContext());
                formatterClass.saveSharedPref(Information.CONTACT.name(),
                        contactUs, requireContext());
            }

        }
    }


}