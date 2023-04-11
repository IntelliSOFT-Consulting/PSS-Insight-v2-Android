package com.intellisoft.pss.navigation_drawer.fragments;

import static android.view.SoundEffectConstants.CLICK;

import android.app.Application;
import android.os.Bundle;
import android.os.RecoverySystem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.intellisoft.pss.R;
import com.intellisoft.pss.adapter.ExpandableRecyclerAdapter;
import com.intellisoft.pss.helper_class.SettingItem;
import com.intellisoft.pss.helper_class.SettingItemChild;
import com.intellisoft.pss.room.PssViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class FragmentSettings extends Fragment {
    ArrayList<SettingItem> settingItemArrayList;
    ArrayList<SettingItemChild> settingItemChildArrayList;
    ExpandableRecyclerAdapter expandableRecyclerAdapter;
    private RecyclerView recyclerView;
    private PssViewModel myViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        settingItemArrayList = new ArrayList<>();
        settingItemChildArrayList = new ArrayList<>();
        myViewModel = new PssViewModel(((Application) requireContext().getApplicationContext()));
        recyclerView = rootView.findViewById(R.id.recyclerView);
        initData();
        return rootView;
    }


    private void initData() {
        SettingItemChild settingItemChild = new SettingItemChild("Syncing period: " + getSyncingPeriod(), "Last Sync on: " + getLastSync(), true, "Sync Data Now");
        SettingItemChild settingItemChild1 = new SettingItemChild("Syncing period: " + getSyncingPeriod(), "Last Sync on: " + getLastSync(), true, "Sync Configuration Now");
        SettingItemChild settingItemChild2 = new SettingItemChild(getReserved() + " Reserved values downloaded per TEI attribute", "", true, "Manage Reserved Values");
        SettingItemChild settingItemChild3 = new SettingItemChild("All Capture App data stored in your device will be deleted",
                "Data which is not synced to the server will be lost", false, "Accept");

        SettingItem settingItem = new SettingItem("Sync Data", settingItemChild, true,0);
        SettingItem settingItem1 = new SettingItem("Sync Configuration", settingItemChild1, false,1);
        SettingItem settingItem2 = new SettingItem("Reserved Values", settingItemChild2, false,2);
        SettingItem settingItem3 = new SettingItem("Delete Local Data", settingItemChild3, false,3);


        settingItemArrayList.addAll(Arrays.asList(settingItem, settingItem1, settingItem2, settingItem3));

        expandableRecyclerAdapter = new ExpandableRecyclerAdapter(settingItemArrayList, requireContext(),myViewModel);
        recyclerView.setAdapter(expandableRecyclerAdapter);
        recyclerView.setHasFixedSize(true);


    }

    private String getReserved() {
        return "10";
    }

    private String getLastSync() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, -12);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        String formattedDate = dateFormat.format(cal.getTime());
        return formattedDate;
    }

    private String getSyncingPeriod() {
        String sync = "Manual";
        return sync;
    }

}