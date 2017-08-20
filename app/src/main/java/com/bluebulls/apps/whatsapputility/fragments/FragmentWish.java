package com.bluebulls.apps.whatsapputility.fragments;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bluebulls.apps.whatsapputility.R;
import com.bluebulls.apps.whatsapputility.adapters.MyViewPager;
import com.mxn.soul.slidingcard_core.ContainerView;
import com.mxn.soul.slidingcard_core.SlidingCard;

import java.util.ArrayList;

import gun0912.tedbottompicker.TedBottomPicker;
import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;

/**
 * Created by dell on 8/20/2017.
 */

public class FragmentWish extends Fragment implements ContainerView.ContainerInterface {
    private static FragmentManager manager;
    private Button edit,undo,finish,clear,pick;
    private DrawableViewConfig config;
    DrawableView drawableView;
    private ArrayList<Uri> uriArrayList = new ArrayList<>();
    private ContainerView containerView;
    private static MyViewPager viewPager;
    private int selectedIndex = 0;
    public static FragmentWish newInstance(FragmentManager man, MyViewPager pager) {
        Bundle args = new Bundle();
        FragmentWish fragment = new FragmentWish();
        fragment.setArguments(args);
        manager = man;
        viewPager = pager;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wish_fragment, null);
        pick = (Button)v.findViewById(R.id.pick);
        containerView = (ContainerView)v.findViewById(R.id.container);
        containerView.setScrollableGroups(viewPager);
        drawableView=(DrawableView)v.findViewById(R.id.draw);
        edit=(Button)v.findViewById(R.id.edit);
        undo=(Button)v.findViewById(R.id.undo);
        clear=(Button)v.findViewById(R.id.clear);
        finish=(Button)v.findViewById(R.id.finish);
        pick=(Button)v.findViewById(R.id.pick);
        config=new DrawableViewConfig();
        config.setStrokeColor(Color.BLACK);
        config.setShowCanvasBounds(true);
        config.setStrokeWidth(10.0f);
        config.setMinZoom(1.0f);
        config.setMaxZoom(1.5f);
        config.setCanvasHeight(1080);
        config.setCanvasWidth(1920);
        drawableView.setConfig(config);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawableView.setVisibility(View.VISIBLE);
                undo.setVisibility(View.VISIBLE);
                edit.setVisibility(View.GONE);
                clear.setVisibility(View.VISIBLE);
                finish.setVisibility(View.VISIBLE);
                viewPager.setPagingEnabled(false);
            }
        });
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawableView.undo();
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawableView.setVisibility(View.GONE);
                undo.setVisibility(View.GONE);
                finish.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                clear.setVisibility(View.INVISIBLE);
                viewPager.setPagingEnabled(true);
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawableView.clear();
            }
        });
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
        selectedIndex = index;
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
