package com.intellisoft.pss.navigation_drawer.fragments;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.resources.MaterialAttributes;
import com.intellisoft.pss.DbDataEntry;
import com.intellisoft.pss.DbDataEntryDetails;
import com.intellisoft.pss.DbDataEntryForm;
import com.intellisoft.pss.DbIndicators;
import com.intellisoft.pss.DbIndicatorsDetails;
import com.intellisoft.pss.DbSaveDataEntry;
import com.intellisoft.pss.NavigationValues;
import com.intellisoft.pss.helper_class.FormatterClass;
import com.intellisoft.pss.R;
import com.intellisoft.pss.SubmissionsStatus;
import com.intellisoft.pss.adapter.DataEntryAdapter;
import com.intellisoft.pss.navigation_drawer.MainActivity;
import com.intellisoft.pss.network_request.RetrofitCalls;
import com.intellisoft.pss.room.Converters;
import com.intellisoft.pss.room.IndicatorsData;
import com.intellisoft.pss.room.PssViewModel;
import com.intellisoft.pss.room.Submissions;

import java.util.ArrayList;
import java.util.List;

public class FragmentDataEntry extends Fragment {
    private PssViewModel myViewModel;

    private FormatterClass formatterClass = new FormatterClass();
    private RecyclerView mRecyclerView;

    private MaterialButton saveDraft, submitSurvey, btnCancel, btnNext;
    private EditText etPeriod;
    private RetrofitCalls retrofitCalls = new RetrofitCalls();
    private LinearLayoutManager linearLayoutManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_data_entry, container, false);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);




        myViewModel = new PssViewModel(((Application) requireContext().getApplicationContext()));

        saveDraft = rootView.findViewById(R.id.saveDraft);
        submitSurvey = rootView.findViewById(R.id.submitSurvey);
        btnCancel = rootView.findViewById(R.id.btn_cancel);
        btnNext = rootView.findViewById(R.id.btn_next);

        etPeriod = rootView.findViewById(R.id.etPeriod);

        saveDraft.setOnClickListener(view -> {
            String period = etPeriod.getText().toString();
            if (TextUtils.isEmpty(period)) {
                etPeriod.setError("Field cannot be empty..");
                etPeriod.requestFocus();
                return;
            }
            saveSubmission(SubmissionsStatus.DRAFT.name(),period);
            Intent intent = new Intent(requireContext(), MainActivity.class);
            startActivity(intent);

        });
        submitSurvey.setOnClickListener(view -> {
            //Create a entity of the date it was pressed
            String period = etPeriod.getText().toString();
            if (TextUtils.isEmpty(period)) {
                etPeriod.setError("Field cannot be empty..");
                etPeriod.requestFocus();
                return;
            }
            saveSubmission(SubmissionsStatus.SUBMITTED.name(),period);
            DbSaveDataEntry dataEntry = myViewModel.getSubmitData(requireContext());
            if (dataEntry != null) {
                retrofitCalls.submitData(requireContext(), dataEntry);
                Intent intent = new Intent(requireContext(), MainActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(requireContext(), "Please try again.", Toast.LENGTH_SHORT).show();
            }

        });


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e("Current", "Current State" + newState);
                handleButtonClicks();
            }
        });
        btnCancel.setOnClickListener(v -> {
            LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            int currentPosition = layoutManager.findFirstVisibleItemPosition();
            int newPosition = currentPosition - 1;

            if (newPosition >= 0) {
                // Scroll to the previous item
                layoutManager.scrollToPosition(newPosition);
            }
            handleButtonClicks();
        });
        btnNext.setOnClickListener(v -> {
            LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            int currentPosition = layoutManager.findLastVisibleItemPosition();
            int newPosition = currentPosition + 1;

            if (newPosition < mRecyclerView.getAdapter().getItemCount()) {
                // Scroll to the next item
                layoutManager.scrollToPosition(newPosition);
            }
            handleButtonClicks();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveSubmission(String status,String period) {

        //Create a entity of the date it was pressed
        String userId = formatterClass.getSharedPref("username", requireContext());
        if (userId != null) {
            String date = formatterClass.getCurrentDate();
            Submissions submissions = new Submissions(
                    date,
                    status,
                    userId,
                    period
            );
            myViewModel.addSubmissions(submissions);
        }
        formatterClass.saveSharedPref(NavigationValues.NAVIGATION.name(),
                NavigationValues.SUBMISSION.name(), requireContext());


    }

    @Override
    public void onStart() {
        super.onStart();

        IndicatorsData indicatorsData = myViewModel.getAllMyData(requireContext());
        if (indicatorsData != null) {

            int indicatorSize = 0;
            ArrayList<DbDataEntryForm> dbDataEntryFormList = new ArrayList<>();
            String jsonData = indicatorsData.getJsonData();

            Converters converters = new Converters();
            DbDataEntry dataEntry = converters.fromJson(jsonData);
            List<DbDataEntryDetails> detailsList = dataEntry.getDetails();
            for (int j = 0; j < detailsList.size(); j++) {

                String categoryName = detailsList.get(j).getCategoryName();
                List<DbIndicatorsDetails> indicators = detailsList.get(j).getIndicators();

                for (int i = 0; i < indicators.size(); i++) {
                    String categoryId = indicators.get(i).getCategoryId();
                    String categoryCode = indicators.get(i).getCategoryName();
                    String indicatorName = indicators.get(i).getIndicatorName();

                    ArrayList<DbIndicators> indicatorsList = (ArrayList<DbIndicators>) indicators.get(i).getIndicatorDataValue();
                    indicatorSize = indicatorSize + indicatorsList.size();

                    DbDataEntryForm dbDataEntryForm = new DbDataEntryForm(
                            categoryCode, indicatorName, categoryId, indicatorsList);
                    dbDataEntryFormList.add(dbDataEntryForm);

                }
            }
            DataEntryAdapter dataEntryAdapter = new DataEntryAdapter(dbDataEntryFormList, requireContext());
            mRecyclerView.setAdapter(dataEntryAdapter);

            formatterClass.saveSharedPref("indicatorSize",
                    String.valueOf(indicatorSize), requireContext());

            handleButtonClicks();
        }
    }

    private void handleButtonClicks() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

        if (firstVisibleItemPosition == 0) {
            // The first visible item is active, disable the back button
            btnCancel.setVisibility(View.INVISIBLE);
            submitSurvey.setVisibility(View.GONE);
        } else {
            // The first visible item is not active, show the back button
            btnCancel.setVisibility(View.VISIBLE);
            submitSurvey.setVisibility(View.GONE);
        }

        if (lastVisibleItemPosition == mRecyclerView.getAdapter().getItemCount() - 1) {
            // The last visible item is active, remove the next button
            btnNext.setVisibility(View.GONE);
            submitSurvey.setVisibility(View.VISIBLE);
        } else {
            // The last visible item is not active, show the next button
            btnNext.setVisibility(View.VISIBLE);
            submitSurvey.setVisibility(View.GONE);
        }



    }
}