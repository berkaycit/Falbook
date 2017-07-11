package com.falbookv4.helloteam.falbook.falcisec;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.falbookv4.helloteam.falbook.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class Falci3 extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        return inflater.inflate(R.layout.pager_falcilar, container, false);
    }



    @Subscribe (sticky = true)
    public void onGelenfalEvent(GelenfalEvent event){

        String gonderenKisininIsmi = event.getIsim();
        Log.d("Fragmentler", gonderenKisininIsmi);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }
}
