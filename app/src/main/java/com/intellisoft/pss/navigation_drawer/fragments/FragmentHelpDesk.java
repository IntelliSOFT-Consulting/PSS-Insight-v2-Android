package com.intellisoft.pss.navigation_drawer.fragments;

import android.app.Application;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.intellisoft.pss.R;
import com.intellisoft.pss.helper_class.DbDataEntry;
import com.intellisoft.pss.helper_class.FormatterClass;
import com.intellisoft.pss.helper_class.Information;
import com.intellisoft.pss.helper_class.NationalInformation;
import com.intellisoft.pss.room.Converters;
import com.intellisoft.pss.room.IndicatorsData;
import com.intellisoft.pss.room.PssViewModel;

import java.util.List;

public class FragmentHelpDesk extends Fragment {
    private TextView tv_contact_email;
    private FormatterClass formatterClass;
    private PssViewModel myViewModel;

    public FragmentHelpDesk() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_help_desk, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        myViewModel = new PssViewModel(((Application) requireContext().getApplicationContext()));
        formatterClass = new FormatterClass();
        tv_contact_email = rootView.findViewById(R.id.tv_contact_email);
        updateText(tv_contact_email, loadData());

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        IndicatorsData indicatorsData = myViewModel.getAllMyData(requireContext());
        if (indicatorsData != null) {
            try {
                String jsonData = indicatorsData.getJsonData();

                Converters converters = new Converters();
                DbDataEntry dataEntry = converters.fromJson(jsonData);
                String aboutUs = dataEntry.getAboutUs();
                String contactUs = dataEntry.getContactUs();
                formatterClass.saveSharedPref(Information.ABOUT.name(),
                        aboutUs, requireContext());
                formatterClass.saveSharedPref(Information.CONTACT.name(),
                        contactUs, requireContext());
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Experience problems, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String loadData() {
        String data = "";
        String about = formatterClass.getSharedPref(Information.CONTACT.name(), requireContext());
        if (about != null) {
            data = about;
        }
        return data;
    }

    private void updateText(TextView tv_contact_email, String s) {
        Spanned spanned = Html.fromHtml(s);
        tv_contact_email.setText(spanned);
        tv_contact_email.setMovementMethod(LinkMovementMethod.getInstance());
    }

}