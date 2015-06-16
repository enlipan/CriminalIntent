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
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Created by Lee on 2015/6/16.
 */
public class DatePickerFragment extends DialogFragment {
    public static final String EXTRA_DATE = "com.itlipan.android.criminalintent.date";
    private Date mDate;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDate = (Date) getArguments().getSerializable(EXTRA_DATE);

        //Create a Calendar to get the year month and day
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_picker, null);

        DatePicker datePicker = (DatePicker) v.findViewById(R.id.dialog_date_datePicker);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mDate = new GregorianCalendar(year, month, day).getTime();
                //Update argument to preserve selected value on rotation
                getArguments().putSerializable(EXTRA_DATE, mDate);
            }
        });


        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendresult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    //fragment data Exchange
    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    //send date to CrimeFragment
    private  void sendresult(int resultCode){
        if (getTargetFragment()==null){
            return ;
        }

        Intent i = new Intent();
        i.putExtra(EXTRA_DATE,mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,i);

    }
}
