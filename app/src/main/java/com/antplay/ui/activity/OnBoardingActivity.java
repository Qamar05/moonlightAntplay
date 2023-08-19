package com.antplay.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import com.antplay.R;
import com.antplay.ui.adapter.OnBoardingPagerAdapter;
import com.antplay.ui.adapter.ZoomOutPageTransformer;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;
import com.antplay.utils.SharedPreferenceUtils;
import com.google.android.material.tabs.TabLayout;

public class  OnBoardingActivity extends AppCompatActivity implements View.OnClickListener {
    ViewPager viewPager;
    TabLayout tabLayout;
    TextView txtSkip;
    Button txtNext;
    int currentItem;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
        txtNext = findViewById(R.id.txtNext);
        txtSkip = findViewById(R.id.txtSkip);
        context = this;

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new OnBoardingPagerAdapter(getSupportFragmentManager()));
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.getCurrentItem();

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        setOnClickListener();
        onPageChangeListener();

       // mathscalculation();


    }

    private void mathscalculation() {
        int num1 = 10/3;
        float num2 = 10f/3;
        double num3 = 10d/3;

        Log.d("DOUBLE_NUM","Int : "+num1+" Float : "+num2+" Double : "+num3);
    }

    private void onPageChangeListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentItem = position;
                Log.d("PAGER", "Current Position: " + position);
                if (position == 6) {
                    txtNext.setText(getResources().getString(R.string.finish));
                    txtSkip.setVisibility(View.INVISIBLE);
                } else {
                    txtNext.setText(getResources().getString(R.string.next));
                    txtSkip.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setOnClickListener() {
        txtNext.setOnClickListener(OnBoardingActivity.this);
        txtSkip.setOnClickListener(OnBoardingActivity.this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtSkip:
                SharedPreferenceUtils.saveBoolean(OnBoardingActivity.this, Const.IS_FIRST_TIME,false);
                AppUtils.navigateScreen(OnBoardingActivity.this, LoginSignUpActivity.class);
                break;
            case R.id.txtNext:
                movePagerToNextPage();
                break;
            default:
        }
    }


    private void movePagerToNextPage() {
        int currentItem = viewPager.getCurrentItem();
        currentItem++;
        Log.d("PAGER", "Current Item: " + currentItem);
        if (currentItem == 7) {
            SharedPreferenceUtils.saveBoolean(OnBoardingActivity.this, Const.IS_FIRST_TIME,true);
            AppUtils.navigateScreen(OnBoardingActivity.this, LoginSignUpActivity.class);
        } else {
            viewPager.setCurrentItem(currentItem);
        }

    }

    private void updateSharedPreferences(boolean isNotFirstTime) {

//        SharedPreferenceUtils.saveBoolean(OnBoardingActivity.this, Const.IS_FIRST_TIME,isNotFirstTime);
       /* SharedPreferences preferences = context.getSharedPreferences("com.vms.antplay",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(getString(R.string.is_first_time),isNotFirstTime);
        editor.apply();*/
    }

}