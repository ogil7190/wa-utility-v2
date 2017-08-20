package com.bluebulls.apps.whatsapputility.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluebulls.apps.whatsapputility.R;

/**
 * Created by dell on 8/20/2017.
 */

public class FragmentWish extends Fragment {
    public static FragmentWish newInstance() {

        Bundle args = new Bundle();

        FragmentWish fragment = new FragmentWish();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wish_fragment, null);
        return v;
    }
}
