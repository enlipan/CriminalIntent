package com.itlipan.lee.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Created by Lee on 2015/7/16.
 *
 * Class Work for check if there has one more activity
 * corresponding to this intent
 */
public  class CheckIsIntentCanBeResponse {
    private Context mContext;

   public static  CheckIsIntentCanBeResponse newInstance(Context c){
       return new CheckIsIntentCanBeResponse(c);
   }

   private CheckIsIntentCanBeResponse(Context c){
        mContext = c;
    }

    public   boolean isIntentCanBeResponse(Intent intent){
        PackageManager pm = mContext.getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size()>0;
        return  isIntentSafe;
    }

}
