package com.trademarked.trumptimer;

import android.content.Context;
import android.databinding.BaseObservable;
import android.support.annotation.IntDef;
import android.text.format.DateUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static com.trademarked.trumptimer.DateCalculator.DateUnit.DAYS;
import static com.trademarked.trumptimer.DateCalculator.DateUnit.HOURS;
import static com.trademarked.trumptimer.DateCalculator.DateUnit.MINUTES;
import static com.trademarked.trumptimer.DateCalculator.DateUnit.SECONDS;
import static com.trademarked.trumptimer.DateCalculator.DateUnit.WEEKS;
import static com.trademarked.trumptimer.DateCalculator.DateUnit.YEARS;

/**
 * Helper class to compute date differences.
 */
public final class DateCalculator extends BaseObservable {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-DD-HH-mm",
            Locale.getDefault());

    public @interface DateUnit {
        int YEARS = 0;
        int MONTHS = 1;
        int WEEKS = 2;
        int DAYS = 3;
        int HOURS = 4;
        int MINUTES = 5;
        int SECONDS = 6;
    }

    private final Context mContext;
    private final Date mTargetDate;
    private Date mCurrentDate;

    public DateCalculator(Context context, String targetDate, TimeZone timezone) {
        mContext = context;
        DATE_FORMAT.setTimeZone(timezone);
        mTargetDate = getDate(targetDate, DATE_FORMAT);
        mCurrentDate = new Date();
    }

    public boolean hasTarget() {
        return mTargetDate != null;
    }

    public boolean isPastTarget() {
        return mCurrentDate.after(mTargetDate);
    }

    public void updateCurrentDate() {
        mCurrentDate = new Date();
        notifyChange();
    }

    public String getValue(@DateUnit int unit) {
        // Compute the difference.
        long difference = mTargetDate.getTime() - mCurrentDate.getTime();

        // Compute the number of years.
        long numYears = difference / DateUtils.YEAR_IN_MILLIS;
        if (unit == YEARS) {
            return Long.toString(numYears);
        }

        // CAN WE DO MONTHS?!?

        // Compute the number of weeks.
        difference = difference % DateUtils.YEAR_IN_MILLIS;
        long numWeeks = difference / DateUtils.WEEK_IN_MILLIS;
        if (unit == WEEKS) {
            return Long.toString(numWeeks);
        }

        // Compute the number of days.
        difference = difference % DateUtils.WEEK_IN_MILLIS;
        long numDays = difference / DateUtils.DAY_IN_MILLIS;
        if (unit == DAYS) {
            return Long.toString(numDays);
        }

        // Compute the number of hours.
        difference = difference % DateUtils.DAY_IN_MILLIS;
        long numHours = difference / DateUtils.HOUR_IN_MILLIS;
        if (unit == HOURS) {
            return Long.toString(numHours);
        }

        // Compute the number of minutes.
        difference = difference % DateUtils.HOUR_IN_MILLIS;
        long numMinutes = difference / DateUtils.MINUTE_IN_MILLIS;
        if (unit == MINUTES) {
            return Long.toString(numMinutes);
        }

        // Compute the number of seconds.
        difference = difference % DateUtils.MINUTE_IN_MILLIS;
        long numSeconds = difference / DateUtils.SECOND_IN_MILLIS;
        if (unit == SECONDS) {
            return Long.toString(numSeconds);
        }

        // WTF - shouldn't get here.
        throw new IllegalArgumentException("Unknown date unit " + unit);
    }

    public String getLabel(@DateUnit int unit) {
        switch (unit) {
            case YEARS:
                return mContext.getString(R.string.unit_years);
            case WEEKS:
                return mContext.getString(R.string.unit_weeks);
            case DAYS:
                return mContext.getString(R.string.unit_days);
            case HOURS:
                return mContext.getString(R.string.unit_hours);
            case MINUTES:
                return mContext.getString(R.string.unit_minutes);
            case SECONDS:
                return mContext.getString(R.string.unit_seconds);
        }

        // WTF - shouldn't get here.
        throw new IllegalArgumentException("Unknown date unit " + unit);
    }

    private Date getDate(String dateStr, DateFormat inputFormat) {
        Date date;
        try {
            date = inputFormat.parse(dateStr);
        } catch (ParseException pe) {
            Timber.d("Couldn't parse change deadline %s", dateStr);
            return null;
        }
        return date;
    }
}
