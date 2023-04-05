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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.intellisoft.pss.helper_class.DbDataEntry;
import com.intellisoft.pss.helper_class.DbDataEntryDetails;
import com.intellisoft.pss.helper_class.DbDataEntryForm;
import com.intellisoft.pss.helper_class.DbIndicators;
import com.intellisoft.pss.helper_class.DbIndicatorsDetails;
import com.intellisoft.pss.helper_class.DbSaveDataEntry;
import com.intellisoft.pss.helper_class.NavigationValues;
import com.intellisoft.pss.helper_class.FormatterClass;
import com.intellisoft.pss.R;
import com.intellisoft.pss.helper_class.SubmissionQueue;
import com.intellisoft.pss.helper_class.SubmissionsStatus;
import com.intellisoft.pss.adapter.DataEntryAdapter;
import com.intellisoft.pss.navigation_drawer.MainActivity;
import com.intellisoft.pss.network_request.RetrofitCalls;
import com.intellisoft.pss.room.Converters;
import com.intellisoft.pss.room.IndicatorsData;
import com.intellisoft.pss.room.Organizations;
import com.intellisoft.pss.room.PssViewModel;
import com.intellisoft.pss.room.Submissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentDataEntry extends Fragment {
    private PssViewModel myViewModel;

    private FormatterClass formatterClass = new FormatterClass();
    private RecyclerView mRecyclerView;

    private MaterialButton saveDraft, submitSurvey, btnCancel, btnNext;
    private EditText etPeriod;
    private TextView progressLabel, progressText;
    private RetrofitCalls retrofitCalls = new RetrofitCalls();
    private LinearLayoutManager linearLayoutManager;

    private Map<String, String> stringMap;
    private String organizationsCode;

    private List<Organizations> organizationsList;
    private List<String> stringList;
    private AutoCompleteTextView autoCompleteTextView;
    private TextInputLayout textInputLayout;
    private ArrayAdapter adapter;
    private String submissionId = "";
    private ProgressBar progressBar;


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
        progressLabel = rootView.findViewById(R.id.progress_label);
        progressBar = rootView.findViewById(R.id.progressBar);
        progressText = rootView.findViewById(R.id.progressText);

        etPeriod = rootView.findViewById(R.id.etPeriod);
        autoCompleteTextView = rootView.findViewById(R.id.act_organization);
        textInputLayout = rootView.findViewById(R.id.til_organization);

        saveDraft.setOnClickListener(view -> {
            String period = etPeriod.getText().toString();
            if (TextUtils.isEmpty(period)) {
                etPeriod.setError("Field cannot be empty..");
                etPeriod.requestFocus();
                return;
            }
            String organizationsCode = autoCompleteTextView.getText().toString();
            if (TextUtils.isEmpty(organizationsCode)) {
                textInputLayout.setError("Field cannot be empty..");
                autoCompleteTextView.requestFocus();
                return;
            }
            saveSubmission(SubmissionsStatus.DRAFT.name(), period, organizationsCode);
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
            String organizationsCode = autoCompleteTextView.getText().toString();
            if (TextUtils.isEmpty(period)) {
                textInputLayout.setError("Field cannot be empty..");
                autoCompleteTextView.requestFocus();
                return;
            }
            saveSubmission(SubmissionsStatus.SUBMITTED.name(), period, organizationsCode);
            DbSaveDataEntry dataEntry = myViewModel.getSubmitData(requireContext());
            if (dataEntry != null) {
//                retrofitCalls.submitData(requireContext(), dataEntry);
                Intent intent = new Intent(requireContext(), MainActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(requireContext(), "Please try again.", Toast.LENGTH_SHORT).show();
            }

        });

        loadInitial();

        return rootView;
    }

    private void loadInitial() {
        String id = formatterClass.getSharedPref(SubmissionQueue.INITIATED.name(), requireContext());

        if (id != null) {
            Submissions submission = myViewModel.getSubmission(id, requireContext());
            if (submission != null) {
                autoCompleteTextView.setText(submission.getOrganization());
                etPeriod.setText(submission.getPeriod());
                submissionId = id;
            } else {
                Submissions submission1 = myViewModel.getLatestSubmission(requireContext());
                if (submission1 != null) {
                    submissionId = submission1.getId().toString();
                    formatterClass.saveSharedPref(SubmissionQueue.INITIATED.name(), submissionId, requireContext());
                }
            }
            updateProgress();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
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

        loadOrganizations();
    }

    private void loadOrganizations() {
        organizationsList = myViewModel.getOrganizations(requireContext());
        stringMap = new HashMap<>();
        stringList = new ArrayList<>();
        for (Organizations organization : organizationsList) {
            stringList.add(organization.getDisplayName());
            stringMap.put(organization.getIdcode(), organization.getDisplayName());
        }
        adapter = new ArrayAdapter(requireContext(),
                android.R.layout.simple_list_item_1, stringList);

        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            organizationsCode = getOrganizationsCode(autoCompleteTextView.getText().toString());
        });
    }

    private String getOrganizationsCode(String toString) {
        return stringMap.get(toString);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveSubmission(String status, String period, String organizationsCode) {

        //Create a entity of the date it was pressed
        String userId = formatterClass.getSharedPref("username", requireContext());
        if (userId != null) {
            String date = formatterClass.getCurrentDate();
            Submissions submissions = new Submissions(
                    date,
                    organizationsCode,
                    status,
                    userId,
                    period, false
            );
            Log.e("Data Entry", "Button Clicked...." + submissionId + "code" + organizationsCode);
            if (submissionId == null) {
                myViewModel.addSubmissions(submissions);
            } else {
                myViewModel.updateSubmissions(submissions, submissionId);
            }
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
            Log.e("Data Entry", "Submission Id" + submissionId);
            DataEntryAdapter dataEntryAdapter = new DataEntryAdapter(dbDataEntryFormList, requireContext(), submissionId,FragmentDataEntry.this);
            mRecyclerView.setAdapter(dataEntryAdapter);

            formatterClass.saveSharedPref("indicatorSize",
                    String.valueOf(indicatorSize), requireContext());
            Log.e("Data Entry", "indicatorSize::::::" + indicatorSize);
            handleButtonClicks();
            controlPagination(dataEntry.getCount());
        }
    }

    private void controlPagination(int count) {
        if (count == 1) {
            btnCancel.setVisibility(View.INVISIBLE);
            btnNext.setVisibility(View.GONE);
            saveDraft.setVisibility(View.VISIBLE);
            submitSurvey.setVisibility(View.VISIBLE);
        }
    }

    public void updateProgress() {
        String indicatorSize = formatterClass.getSharedPref("indicatorSize", requireContext());
        Integer answers = myViewModel.getSubmissionResponses(requireContext(), submissionId);
        try {
            double total = Double.parseDouble(indicatorSize);
            double answered = answers;

            double percent = (answered / total) * 100;
            int percentInt = (int) percent;
            progressBar.setProgress(percentInt);
            progressText.setText( percentInt+"% done");


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Data Entry", "indicatorSize:::::: error" + e.getMessage());
        }
    }

    private void handleButtonClicks() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        int totalItemCount = mRecyclerView.getAdapter().getItemCount();

        if (firstVisibleItemPosition == 0) {
            // The first visible item is active, disable the back button
            btnCancel.setVisibility(View.INVISIBLE);
            submitSurvey.setVisibility(View.GONE);
        } else {
            // The first visible item is not active, show the back button
            btnCancel.setVisibility(View.VISIBLE);
            submitSurvey.setVisibility(View.GONE);
        }

        if (lastVisibleItemPosition == totalItemCount - 1) {
            // The last visible item is active, remove the next button
            btnNext.setVisibility(View.GONE);
            submitSurvey.setVisibility(View.VISIBLE);
        } else {
            // The last visible item is not active, show the next button
            btnNext.setVisibility(View.VISIBLE);
            submitSurvey.setVisibility(View.GONE);
        }

        int activeItemCount;
        if (firstVisibleItemPosition == -1 || lastVisibleItemPosition == -1) {
            activeItemCount = 1;
        } else {
            activeItemCount = lastVisibleItemPosition - firstVisibleItemPosition + 1;
        }

        String activeItemText = "Page " + activeItemCount + " / " + totalItemCount;
        progressLabel.setText(activeItemText);
    }
}