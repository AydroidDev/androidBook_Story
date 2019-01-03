package com.startingandroid.sqlitedatabasetutorial.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.startingandroid.sqlitedatabasetutorial.R;



public class AboutUs extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.about_us,null);
    }

    @Override
    public void onViewCreated (View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("About Us");
    }

}