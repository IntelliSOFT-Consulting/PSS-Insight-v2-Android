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

import org.w3c.dom.Text;

public class FragmentAbout extends Fragment {
    private TextView textView;
    private String about = "";

    public FragmentAbout() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        about = getAbout();
        textView = rootView.findViewById(R.id.tv_about);
        about=getAbout();
        Spanned spanned = Html.fromHtml(about);
        textView.setText(spanned);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        return rootView;
    }

    private String getAbout() {
        return "App Version: v2<br>" +
                "SDK Version: 1.1.1<br><br>" +
                "More about this app: read <a href='#'>what's supported</a> and what's not supported</a><br><br>" +
                "Connected to: $$<br>" +
                "Current user: @@<br><br>" +
                "<a href='#'>Click here</a> to check our Privacy Policy<br><br>" +
                "This and previous versions of this application, as well as the source code are available at <a href='#'>Github</a>.<br>" +
                "PSS V2 Android Capture is Licensed under the terms of the <a href='https://www.gnu.org/licenses/gpl-3.0.en.html'>GNU General Public License</a> as published by the Free Software Foundation<br><br><br>" +
                "Developed and maintained by the IntelliSOFT team.<br><br>" +
                "For more information or questions about this application, please write to <a href='mailto:apps@intellisoftkenya.com'>apps@intellisoftkenya.com</a>";
    }

}