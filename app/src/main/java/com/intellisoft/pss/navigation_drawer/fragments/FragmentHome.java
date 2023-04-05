package com.intellisoft.pss.navigation_drawer.fragments;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.intellisoft.pss.helper_class.FormatterClass;
import com.intellisoft.pss.helper_class.NavigationValues;
import com.intellisoft.pss.R;
import com.intellisoft.pss.navigation_drawer.MainActivity;
import com.intellisoft.pss.room.PssViewModel;
import com.intellisoft.pss.room.Submissions;

import java.util.List;

public class FragmentHome extends Fragment {

    private CardView cardViewSubmission;
    private PssViewModel myViewModel;
    private FormatterClass formatterClass = new FormatterClass();


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
//        setHasOptionsMenu(true);
        myViewModel = new PssViewModel(((Application) requireContext().getApplicationContext()));
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sync_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_1:
                selectAllSubmittedSubmissions();
                return true;
            // Add more cases for other menu items as needed
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectAllSubmittedSubmissions() {


    }

    @Override
    public void onStart() {
        super.onStart();

    }
}