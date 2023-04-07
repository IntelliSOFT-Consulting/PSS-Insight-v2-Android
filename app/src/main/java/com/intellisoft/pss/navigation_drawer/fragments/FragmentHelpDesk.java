package com.intellisoft.pss.navigation_drawer.fragments;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.intellisoft.pss.R;

public class FragmentHelpDesk extends Fragment {
    private TextView tv_contact_email,tv_contact_phone;
    public FragmentHelpDesk() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_help_desk, container, false);

        tv_contact_email=  rootView.findViewById(R.id.tv_contact_email);
        tv_contact_phone=  rootView.findViewById(R.id.tv_contact_phone);
        updateText(tv_contact_email,"Email: <a href='mailto:apps@intellisoftkenya.com'>apps@intellisoftkenya.com</a>");
        updateText(tv_contact_phone,"Phone: <a href='tel:+254712345678'>+254 712 345 678</a>");

        return rootView;
    }

    private void updateText(TextView tv_contact_email, String s) {
        Spanned spanned = Html.fromHtml(s);
        tv_contact_email.setText(spanned);
        tv_contact_email.setMovementMethod(LinkMovementMethod.getInstance());
    }

}