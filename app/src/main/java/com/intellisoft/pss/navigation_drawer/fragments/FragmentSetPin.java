package com.intellisoft.pss.navigation_drawer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.intellisoft.pss.R;
import com.intellisoft.pss.pinlockview.IndicatorDots;
import com.intellisoft.pss.pinlockview.PinLockListener;
import com.intellisoft.pss.pinlockview.PinLockView;

public class FragmentSetPin extends Fragment {
    public static final String TAG = "PinLockView";

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;

    public FragmentSetPin() {
    }

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            Log.d(TAG, "Pin complete: " + pin);
            Toast.makeText(requireContext(), "In Progress", Toast.LENGTH_SHORT).show();
            navigateBackHome();
        }

        @Override
        public void onEmpty() {
            Log.d(TAG, "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };

    private void navigateBackHome() {
        requireActivity().onBackPressed();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Enable back press handling in this fragment
  /*      requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back press event
                if (shouldHandleBackPress()) {
                    // Handle back press event in this fragment
                    handleBackPress();
                } else {
                    // Pass the back press event to the hosting activity
                    requireActivity().onBackPressed();
                }
            }
        });*/
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_set_pin, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        mPinLockView = rootView.findViewById(R.id.pin_lock_view);
        mIndicatorDots = rootView.findViewById(R.id.indicator_dots);

        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);
        mPinLockView.setPinLength(4);
        mPinLockView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);
        return rootView;
    }

}