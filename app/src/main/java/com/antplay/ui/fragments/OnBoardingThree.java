package com.antplay.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.antplay.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnBoardingThree#} factory method to
 * create an instance of this fragment.
 */
public class OnBoardingThree extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_on_boarding_three, container, false);
    }
}