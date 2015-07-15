package com.itlipan.lee.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Lee on 2015/6/16.
 */
public class TimePickerFragment extends DialogFragment {
    public static final String EXTRA_TIME = "com.itlipan.android.criminalintent.time";
    private Date mDate;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDate = (Date) getArguments().getSerializable(EXTRA_TIME);

        // Use the current time as the default values for the picker
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_time_picker, null);

        TimePicker timePicker = (TimePicker)v.findViewById(R.id.dialog_time_TimePicker);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mDate = new GregorianCalendar(year, month, day, hour, minute).getTime();
                //Update argument to preserve selected value on rotation
                getArguments().putSerializable(EXTRA_TIME, mDate);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendresult(Activity.RESULT_FIRST_USER);
                    }
                })
                .create();

    }

    //fragment data Exchange
    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TIME, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    //send date to CrimeFragment
    //use  different resultcode to distinguish updatetime or updatedate
    private  void sendresult(int resultCode){
        if (getTargetFragment()==null){
            return ;
        }

        Intent i = new Intent();
        i.putExtra(EXTRA_TIME,mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);

    }

}
