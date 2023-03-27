package com.intellisoft.pss.navigation_drawer.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.intellisoft.pss.helper_class.FormatterClass;
import com.intellisoft.pss.NavigationValues;
import com.intellisoft.pss.R;
import com.intellisoft.pss.navigation_drawer.MainActivity;

public class FragmentHome extends Fragment {

    private CardView cardViewSubmission;

    private FormatterClass formatterClass = new FormatterClass();


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        cardViewSubmission = rootView.findViewById(R.id.cardViewSubmission);
        cardViewSubmission.setOnClickListener(view -> {

            formatterClass.saveSharedPref(NavigationValues.NAVIGATION.name(),
                    NavigationValues.SUBMISSION.name(), requireContext());
            Intent intent = new Intent(requireContext(), MainActivity.class);
            startActivity(intent);

        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}