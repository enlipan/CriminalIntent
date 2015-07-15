package com.itlipan.lee.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    public static final String EXTRA_CRIME_ID = "com.itlipan.android.criminalintent";
    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_CONTECT =2;
    private static final String TAG = "CrimeFragment";
    private static final String DIALOG_IMAGE = "image";

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Button mSuspectButton;

    private  String getCrimeReport(){
        String solvedString = null;
        if (mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
         }else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE,MMM dd";
        String dateString = android.text.format.DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect== null){
            suspect = getString(R.string.crime_report_no_suspect);
        }else {
            suspect = getString(R.string.crime_report_suspect);
        }

        String report  = getString(R.string.crime_report,mCrime.getTitle(),dateString,solvedString,suspect);
        return report;
    }

    private void updateDate() {
//        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
//        DateFormat.getDateTimeInstance();
        mDateButton.setText(DateFormat.getDateInstance().format(mCrime.getDate()));
    }

    private void updateTime() {
//        SimpleDateFormat formatter = new SimpleDateFormat ("hh:mm");
        mTimeButton.setText(DateFormat.getTimeInstance().format(mCrime.getDate()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        mCrime = new Crime();
//        UUID crimeId = (UUID)getActivity().getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            case R.id.menu_item_delete_in:
                Toast.makeText(getActivity(), "Crime:" + mCrime.getTitle() + " Delete", Toast.LENGTH_SHORT).show();
                CrimeLab.get(getActivity()).delete(mCrime);
                Intent i = new Intent(getActivity(), CrimeListActivity.class);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showPhoto(){
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;
        if (p!=null){
            String path = getActivity().getFileStreamPath(p.getFileName()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(),path);
        }
        mPhotoView.setImageDrawable(b);
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageview(mPhotoView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
//                startActivity(i);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

        //check if camera is available,if not  disable camera functionality
        PackageManager pm = getActivity().getPackageManager();
        boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
                pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
                Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD ||
                Camera.getNumberOfCameras() > 0;
        if (!hasACamera) {
            mPhotoButton.setEnabled(false);
        }

        mPhotoView = (ImageView) v.findViewById(R.id.crime_imageView );
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo p = mCrime.getPhoto();
                if (p ==null){
                    return;
                }
                FragmentManager fm = getActivity().getSupportFragmentManager();
                String path = getActivity().getFileStreamPath(p.getFileName()).getAbsolutePath();
                ImageFragment.newInstance(path).show(fm,DIALOG_IMAGE);
            }
        });

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mCrime.setTitle(c.toString());
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
//        mDateButton.setText(mCrime.getDate().toString());
//        mDateButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
//                DatePickerFragment dialog = new DatePickerFragment();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });
        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(fm, DIALOG_DATE);


            }
        });
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // set the crime's solved property
                mCrime.setSolved(isChecked);
            }
        });
        Button reportButton = (Button) v.findViewById(R.id.crime_reportButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                i = Intent.createChooser(i,getString(R.string.send_report));
                startActivity(i);
            }
        });

        mSuspectButton = (Button) v.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i,REQUEST_CONTECT);
            }
        });
        if (mCrime.getSuspect()!=null){
            mSuspectButton.setText(mCrime.getSuspect());
        }
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK && resultCode != Activity.RESULT_FIRST_USER) {
            return;
        }
        if (requestCode == REQUEST_DATE && resultCode == Activity.RESULT_OK) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        } else if (requestCode ==REQUEST_PHOTO&&resultCode==Activity.RESULT_OK) {
            //create a new photo obj and attach it to the crime
            String filename =data.getStringExtra(CrimeCameraFragment.EXTRA_PHTOTO_FILENAME);
            if (filename!=null){
//                Log.i(TAG, "filename:" + filename);
                Photo p =new Photo(filename);
                mCrime.setPhoto(p);
                showPhoto();
                Log.i(TAG,"Crime:"+mCrime.getTitle()+" has a photo");
            }
        }else if (requestCode == REQUEST_CONTECT){
            Uri contactUri = data.getData();

            //specify which fields you want your query to return
            //values for
            String [] querFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
            //perform your query - the contacturi is like a "where"
            //clause here
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri,querFields,null,null,null);
            //double check that you actually got results
            if (c.getCount()==0){
                c.close();
                return;
            }

            //pull out the first column of the first row of data
            //that is your suspect's name
            c.moveToFirst();
            String suspect = c.getString(0);
            mCrime.setSuspect(suspect);
            mSuspectButton.setText(suspect);
            c.close();
        }else {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(date);
            updateTime();
        }
//            mDateButton.setText(mCrime.getDate().toString());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list_in, menu);
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
