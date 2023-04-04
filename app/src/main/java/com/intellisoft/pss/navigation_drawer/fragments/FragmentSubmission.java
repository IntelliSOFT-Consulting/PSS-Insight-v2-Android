package com.intellisoft.pss.navigation_drawer.fragments;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.intellisoft.pss.helper_class.SubmissionsStatus;
import com.intellisoft.pss.adapter.SubmissionAdapter;
import com.intellisoft.pss.helper_class.FormatterClass;
import com.intellisoft.pss.helper_class.NavigationValues;
import com.intellisoft.pss.R;
import com.intellisoft.pss.navigation_drawer.MainActivity;
import com.intellisoft.pss.room.PssViewModel;
import com.intellisoft.pss.room.Submissions;

import java.util.ArrayList;
import java.util.List;

public class FragmentSubmission extends Fragment {

    private Button btnAdd;
    private FormatterClass formatterClass = new FormatterClass();
    private PssViewModel myViewModel;
    private RecyclerView mRecyclerView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_submission, container, false);

        myViewModel = new PssViewModel(((Application) requireContext().getApplicationContext()));
        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        btnAdd = rootView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(view -> {

            formatterClass.saveSharedPref(NavigationValues.NAVIGATION.name(),
                    NavigationValues.DATA_ENTRY.name(), requireContext());

            saveSubmission(SubmissionsStatus.DRAFT.name());

            Intent intent = new Intent(requireContext(), MainActivity.class);
            startActivity(intent);

        });

        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveSubmission(String status){
        //Create a entity of the date it was pressed
        String userId = formatterClass.getSharedPref("username", requireContext());
        if (userId != null){
            String date = formatterClass.getCurrentDate();
            String year = formatterClass.getYear();
            Submissions submissions = new Submissions(
                    date,
                    "",
                    status,
                    userId,
                    year
            );
            myViewModel.addSubmissions(submissions);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        getSubmissions();
    }

    private void getSubmissions() {

        List<Submissions> submissionList = myViewModel.getSubmissions(requireContext());
        SubmissionAdapter dataEntryAdapter = new SubmissionAdapter((ArrayList<Submissions>) submissionList, requireContext());
        mRecyclerView.setAdapter(dataEntryAdapter);


    }
}