package com.antplay.ui.adapter;



import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.antplay.ui.fragments.OnBoardingFive;
import com.antplay.ui.fragments.OnBoardingFour;
import com.antplay.ui.fragments.OnBoardingOne;
import com.antplay.ui.fragments.OnBoardingSeven;
import com.antplay.ui.fragments.OnBoardingSix;
import com.antplay.ui.fragments.OnBoardingThree;
import com.antplay.ui.fragments.OnBoardingTwo;


public class OnBoardingPagerAdapter extends FragmentStatePagerAdapter {

    int PAGE_COUNT = 7;




    public OnBoardingPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OnBoardingOne();
            case 1:
                return new OnBoardingTwo();
            case 2:
                return new OnBoardingThree();
            case 3:
                return new OnBoardingFour();
            case 4:
                return new OnBoardingFive();
            case 5:
                return new OnBoardingSix();
            case 6:
                return new OnBoardingSeven();

        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
