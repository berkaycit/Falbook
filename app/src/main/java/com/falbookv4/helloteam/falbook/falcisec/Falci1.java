package com.falbookv4.helloteam.falbook.falcisec;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.falbookv4.helloteam.falbook.activities.DilekActivity;
import com.falbookv4.helloteam.falbook.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class Falci1 extends Fragment{

    private FrameLayout genelLayout;
    private Button btnGonder;
    String gonderenKisininIsmi = "";
    private byte[] kucukFoto1;

    @Subscribe (sticky = true)
    public void onGelenfalEvent(GelenfalEvent event){

        gonderenKisininIsmi = event.getIsim();
        kucukFoto1 = event.getKucukFoto1();


    }

    public void init(){


        btnGonder = (Button) genelLayout.findViewById(R.id.btnFalciGonder);
    }

    public void handler(){

        btnGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent falci1ToDilek = new Intent(getContext(), DilekActivity.class);
                startActivity(falci1ToDilek);

            }
        });


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        genelLayout = (FrameLayout) inflater.inflate(R.layout.pager_falcilar, container, false);

        init();
        handler();

        return genelLayout;
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
