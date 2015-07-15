package com.itlipan.lee.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by Lee on 2015/6/23.
 */
public class CrimeCameraActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeCameraFragment();
    }
}
