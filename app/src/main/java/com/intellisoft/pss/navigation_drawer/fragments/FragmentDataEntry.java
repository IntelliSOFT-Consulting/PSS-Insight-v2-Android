package com.intellisoft.pss.navigation_drawer.fragments;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private Button saveDraft, submitSurvey;
    private EditText etPeriod;
    private RetrofitCalls retrofitCalls = new RetrofitCalls();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_data_entry, container, false);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        myViewModel = new PssViewModel(((Application) requireContext().getApplicationContext()));

        saveDraft = rootView.findViewById(R.id.saveDraft);
        submitSurvey = rootView.findViewById(R.id.submitSurvey);
        etPeriod = rootView.findViewById(R.id.etPeriod);

        saveDraft.setOnClickListener(view -> {
            saveSubmission(SubmissionsStatus.DRAFT.name());
            Intent intent = new Intent(requireContext(), MainActivity.class);
            startActivity(intent);
        });
        submitSurvey.setOnClickListener(view -> {
          //Create a entity of the date it was pressed
            saveSubmission(SubmissionsStatus.SUBMITTED.name());
            DbSaveDataEntry dataEntry = myViewModel.getSubmitData(requireContext());
            if (dataEntry != null){
                retrofitCalls.submitData(requireContext(), dataEntry);

                Intent intent = new Intent(requireContext(), MainActivity.class);
                startActivity(intent);
            }else {
                Toast.makeText(requireContext(), "Please try again.", Toast.LENGTH_SHORT).show();
            }

        });


        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveSubmission(String status){

        String period = etPeriod.getText().toString();
        if (!TextUtils.isEmpty(period)){

            //Create a entity of the date it was pressed
            String userId = formatterClass.getSharedPref("username", requireContext());
            if (userId != null){
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

        }else {
            etPeriod.setError("Field cannot be empty..");
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        IndicatorsData indicatorsData = myViewModel.getAllMyData(requireContext());
        if (indicatorsData != null){

            int indicatorSize = 0;
            ArrayList<DbDataEntryForm> dbDataEntryFormList = new ArrayList<>();
            String jsonData = indicatorsData.getJsonData();

            Converters converters = new Converters();
            DbDataEntry dataEntry = converters.fromJson(jsonData);
            List<DbDataEntryDetails> detailsList = dataEntry.getDetails();
            for (int j = 0; j < detailsList.size(); j++){

                String categoryName = detailsList.get(j).getCategoryName();
                List<DbIndicatorsDetails> indicators = detailsList.get(j).getIndicators();
                for (int i = 0; i< indicators.size(); i++){

                    String indicatorCode = indicators.get(i).getCode();
                    String indicatorId = indicators.get(i).getIndicatorId();
                    String indicatorName = indicators.get(i).getIndicatorName();
                    ArrayList<DbIndicators> indicatorsList = (ArrayList<DbIndicators>) indicators.get(i).getIndicators();

                    indicatorSize = indicatorSize + indicatorsList.size();

                    DbDataEntryForm dbDataEntryForm = new DbDataEntryForm(
                            indicatorCode, indicatorName, indicatorId, indicatorsList);
                    dbDataEntryFormList.add(dbDataEntryForm);
                }
            }
            DataEntryAdapter dataEntryAdapter = new DataEntryAdapter(dbDataEntryFormList, requireContext());
            mRecyclerView.setAdapter(dataEntryAdapter);

            formatterClass.saveSharedPref("indicatorSize",
                    String.valueOf(indicatorSize), requireContext());
        }
    }
}