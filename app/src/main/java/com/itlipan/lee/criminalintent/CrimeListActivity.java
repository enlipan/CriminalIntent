package com.itlipan.lee.criminalintent;

import android.support.v4.app.Fragment;

import java.util.UUID;

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();

    }
}
