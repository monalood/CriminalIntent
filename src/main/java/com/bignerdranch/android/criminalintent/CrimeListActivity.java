package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by yls on 15-5-15.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
