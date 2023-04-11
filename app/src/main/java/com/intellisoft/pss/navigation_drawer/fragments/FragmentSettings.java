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
import com.intellisoft.pss.helper_class.FormatterClass;
import com.intellisoft.pss.helper_class.SettingItem;
import com.intellisoft.pss.helper_class.SettingItemChild;
import com.intellisoft.pss.helper_class.SettingsQueue;
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
    FormatterClass formatterClass = new FormatterClass();

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
        SettingItemChild settingItemChild1 = new SettingItemChild("Syncing period: " + getSyncingConfPeriod(), "Last Sync on: " + getLastSync(), true, "Sync Configuration Now");
        SettingItemChild settingItemChild2 = new SettingItemChild(getReserved() + " Reserved values downloaded per TEI attribute", "", true, "Manage Reserved Values");
        SettingItemChild settingItemChild3 = new SettingItemChild("All Capture App data stored in your device will be deleted",
                "Data which is not synced to the server will be lost", false, "Accept");

        ArrayList<String> sync = new ArrayList<>(Arrays.asList("30 Minutes", "1 Hour", "6h", "12h", "1 Day (Default)", "Manual"));
        ArrayList<String> conf = new ArrayList<>(Arrays.asList("1 Day (Default)", "1 Week", "Manual"));

        SettingItem settingItem = new SettingItem("Sync Data", settingItemChild, true, 0,
                R.drawable.baseline_sync_24, sync,true);
        SettingItem settingItem1 = new SettingItem("Sync Configuration", settingItemChild1, false, 1,
                R.drawable.baseline_manage_history_24, conf,true);
        SettingItem settingItem2 = new SettingItem("Reserved Values", settingItemChild2, false, 2,
                R.drawable.baseline_menu_24, null,false);
        SettingItem settingItem3 = new SettingItem("Delete Local Data", settingItemChild3, false, 3,
                R.drawable.baseline_delete_24, null,false);


        settingItemArrayList.addAll(Arrays.asList(settingItem, settingItem1, settingItem2, settingItem3));

        expandableRecyclerAdapter = new ExpandableRecyclerAdapter(settingItemArrayList, requireContext(), myViewModel);
        recyclerView.setAdapter(expandableRecyclerAdapter);
        recyclerView.setHasFixedSize(true);

    }

    private String getSyncingConfPeriod() {
        String code = formatterClass.getSharedPref(SettingsQueue.CONFIGURATION.name(), requireContext());
        return code;
    }

    private String getReserved() {
        String code = formatterClass.getSharedPref(SettingsQueue.RESERVED.name(), requireContext());
        return code;
    }

    private String getLastSync() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, -12);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        String formattedDate = dateFormat.format(cal.getTime());
        return formattedDate;
    }

    private String getSyncingPeriod() {
        String code = formatterClass.getSharedPref(SettingsQueue.SYNC.name(), requireContext());
        return code;
    }

}