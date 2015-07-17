package com.itlipan.lee.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Lee on 2015/6/23.
 */
public class CrimeCameraActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeCameraFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Hide the Window tittle
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Hide the status bar and other os-level chrome
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
    }
}
