package com.trademarked.trumptimer;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;

import com.trademarked.trumptimer.databinding.ActivityMainBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final long UPDATE_INTERVAL_MS = 500;

    private ActivityMainBinding mBinding;
    private DateCalculator mCalculator;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Set the target date timezone.
        TimeZone tz = TimeZone.getTimeZone("America/New_York");
        if (tz == null) {
            Timber.d("Failed to load timezone for EST");
            finish();
            return;
        }

        // Set up the date calculator.
        mCalculator = new DateCalculator(this, "2021-01-20-12-00", tz);
        if (!mCalculator.hasTarget()) {
            Timber.d("Unexpected failure to parse target date");
            finish();
            return;
        }

        // Bind the calculator to the view.
        int screenSize = getShortestScreenSize();
        mBinding.victoryIcon.getLayoutParams().height = screenSize;
        mBinding.victoryIcon.getLayoutParams().width = screenSize;
        mBinding.setCalculator(mCalculator);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateCalculator();
            }
        }, 0 /* delay */, UPDATE_INTERVAL_MS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public int getShortestScreenSize() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return dm.widthPixels < dm.heightPixels ? dm.widthPixels : dm.heightPixels;
    }

    void updateCalculator() {
        if (mTimer == null) {
            return;
        }

        mCalculator.updateCurrentDate();
    }
}
