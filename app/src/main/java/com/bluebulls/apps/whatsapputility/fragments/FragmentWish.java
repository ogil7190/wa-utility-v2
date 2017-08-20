package com.bluebulls.apps.whatsapputility.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.activities.HomeActivity;
import com.mxn.soul.slidingcard_core.ContainerView;
import com.mxn.soul.slidingcard_core.SlidingCard;

import java.util.ArrayList;

import gun0912.tedbottompicker.TedBottomPicker;

/**
 * Created by dell on 8/20/2017.
 */

public class FragmentWish extends Fragment implements ContainerView.ContainerInterface {
    private static FragmentManager manager;
    private Button pick;
    private ArrayList<Uri> uriArrayList = new ArrayList<>();
    private ContainerView containerView;
    private static ViewPager viewPager;

    public static FragmentWish newInstance(FragmentManager man, ViewPager pager) {
        Bundle args = new Bundle();
        FragmentWish fragment = new FragmentWish();
        fragment.setArguments(args);
        manager = man;
        viewPager = pager;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wish_fragment, null);
        pick = (Button)v.findViewById(R.id.pick);
        containerView = (ContainerView)v.findViewById(R.id.contentview);
        containerView.setScrollableGroups(viewPager);
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(getContext())
                        .setOnMultiImageSelectedListener(new TedBottomPicker.OnMultiImageSelectedListener() {
                            @Override
                            public void onImagesSelected(ArrayList<Uri> uriList) {
                                uriArrayList.addAll(uriList);
                                containerView.initCardView(FragmentWish.this, R.layout.single_wish_card, R.id
                                        .sliding_card_content_view);
                            }
                        })
                        .setPeekHeight(1600)
                        .showTitle(false)
                        .setCompleteButtonText("Done")
                        .setEmptySelectionText("No Select")
                        .create();

                bottomSheetDialogFragment.show(manager);
            }
        });
        return v;
    }

    @Override
    public void initCard(SlidingCard card, int index) {
        Log.d("App","Position:"+index);
        ImageView imageView = (ImageView)card.findViewById(R.id.card_image);
        if(uriArrayList.size()>0){
            imageView.setImageURI(uriArrayList.get(index));
        }
    }

    @Override
    public void exChangeCard() {
        Uri uri = uriArrayList.get(0);
        uriArrayList.remove(0);
        uriArrayList.add(uri);
    }
}
