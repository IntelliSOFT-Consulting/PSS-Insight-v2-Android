package com.intellisoft.pss.navigation_drawer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.intellisoft.pss.R;

public class FragmentHelpDesk extends Fragment {

    public FragmentHelpDesk() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_help_desk, container, false);

        return rootView;
    }

}