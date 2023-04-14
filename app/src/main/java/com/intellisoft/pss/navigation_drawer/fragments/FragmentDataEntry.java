package com.intellisoft.pss.navigation_drawer.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import com.intellisoft.pss.widgets.CustomRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentDataEntry extends Fragment {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 102;
    private PssViewModel myViewModel;
    private static final int REQUEST_IMAGE_PICKER = 1001;
    private FormatterClass formatterClass = new FormatterClass();
    private RecyclerView mRecyclerView;

    private MaterialButton saveDraft, submitSurvey, btnCancel, btnNext;
    private AutoCompleteTextView etPeriod;
    private TextView progressLabel, progressText;
    private RetrofitCalls retrofitCalls = new RetrofitCalls();
    private LinearLayoutManager linearLayoutManager;

    private Map<String, String> stringMap;
    private String organizationsCode;

    private List<Organizations> organizationsList;
    private List<String> stringList;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter adapter;
    private String submissionId = "";
    private ProgressBar progressBar;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_data_entry, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

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

        saveDraft.setOnClickListener(view -> {
            String period = etPeriod.getText().toString();
            if (TextUtils.isEmpty(period)) {
                etPeriod.setError("Field cannot be empty..");
                etPeriod.requestFocus();
                return;
            }
            String organizationsCode = autoCompleteTextView.getText().toString();
            if (TextUtils.isEmpty(organizationsCode)) {
                autoCompleteTextView.setError("Field cannot be empty..");
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
                autoCompleteTextView.setError("Field cannot be empty..");
                autoCompleteTextView.requestFocus();
                return;
            }
            saveSubmission(SubmissionsStatus.SUBMITTED.name(), period, organizationsCode);
            DbSaveDataEntry dataEntry = myViewModel.getSubmitData(requireContext());
            if (dataEntry != null) {
                submissionDialog();
            } else {
                Toast.makeText(requireContext(), "Please try again.", Toast.LENGTH_SHORT).show();
            }

        });
        loadYears();

        loadInitial();

        return rootView;
    }

    private void loadYears() {
        ArrayList<String> stringList = new ArrayList<>(generateYears());
        adapter = new ArrayAdapter(requireContext(),
                android.R.layout.simple_list_item_1, stringList);
        etPeriod.setAdapter(adapter);

    }

    private Collection<String> generateYears() {
        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i <= 20; i++) {
            years.add(Integer.toString(currentYear - i));
        }
        return years;
    }

    private void submissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_custom, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        TextView titleTextView = dialogView.findViewById(R.id.tv_title);
        ImageView okButton = dialogView.findViewById(R.id.dialog_cancel_image);

        okButton.setOnClickListener(v -> {

            Intent intent = new Intent(requireContext(), MainActivity.class);
            startActivity(intent);
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

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
            int lastVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            int totalItemCount = mRecyclerView.getAdapter().getItemCount();
            if (lastVisibleItemPosition < totalItemCount) {
                lastVisibleItemPosition--;
                mRecyclerView.smoothScrollToPosition(lastVisibleItemPosition);
            }

            handleButtonClicks();
        });
        btnNext.setOnClickListener(v -> {
            LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            int totalItemCount = mRecyclerView.getAdapter().getItemCount();
            if (lastVisibleItemPosition < totalItemCount) {
                lastVisibleItemPosition++;
                mRecyclerView.smoothScrollToPosition(lastVisibleItemPosition);
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
            DataEntryAdapter dataEntryAdapter = new DataEntryAdapter(dbDataEntryFormList, requireContext(), submissionId, FragmentDataEntry.this);
            mRecyclerView.setAdapter(dataEntryAdapter);

            formatterClass.saveSharedPref("indicatorSize",
                    String.valueOf(indicatorSize), requireContext());

            controlPagination(dataEntry.getCount());
        }
    }

    private void controlPagination(int count) {
        if (count == 0) {
            btnCancel.setVisibility(View.INVISIBLE);
            btnNext.setVisibility(View.GONE);
        }
        if (count == 1) {
            btnCancel.setVisibility(View.INVISIBLE);
            btnNext.setVisibility(View.GONE);
            saveDraft.setVisibility(View.VISIBLE);
            submitSurvey.setVisibility(View.VISIBLE);
        } else {
            handleButtonClicks();
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
            progressText.setText(percentInt + "% done");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleButtonClicks() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        int totalItemCount = mRecyclerView.getAdapter().getItemCount();

        int center = mRecyclerView.getWidth() / 2;
        int closestToCenter = Integer.MAX_VALUE;
        int activeItemPosition = -1;
        for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
            View itemView = layoutManager.findViewByPosition(i);
            if (itemView != null) {
                int itemCenter = (itemView.getLeft() + itemView.getRight()) / 2;
                int distance = Math.abs(center - itemCenter);
                if (distance < closestToCenter) {
                    closestToCenter = distance;
                    activeItemPosition = i + 1;
                }
            }
        }
        if (activeItemPosition == -1) {
            activeItemPosition = 1;
        }

        String activeItemText = "Page " + activeItemPosition + " / " + totalItemCount;
        progressLabel.setText(activeItemText);
        if (activeItemPosition == totalItemCount) {
            btnNext.setVisibility(View.GONE);
            submitSurvey.setVisibility(View.VISIBLE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            submitSurvey.setVisibility(View.GONE);
        }

        if (activeItemPosition > 1 && activeItemPosition <= totalItemCount) {
            btnCancel.setVisibility(View.VISIBLE);
        } else {
            btnCancel.setVisibility(View.INVISIBLE);
        }
    }


    public void uploadImage(@NotNull String userId, @NotNull String indicatorId, @NotNull String submissionId) {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.selectImage(userId, indicatorId, submissionId);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == Activity.RESULT_OK && data != null) {
            // Get the selected image URI
            Uri selectedImageUri = data.getData();
            Log.e("Selected", "Selected Image");

        }
    }

}