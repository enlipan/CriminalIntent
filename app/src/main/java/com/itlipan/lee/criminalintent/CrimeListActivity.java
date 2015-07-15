package com.itlipan.lee.criminalintent;

import android.annotation.TargetApi;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class CrimeListActivity extends SingleFragmentActivity {

    @TargetApi(11)
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();

    }
}
