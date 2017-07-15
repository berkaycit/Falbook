package com.falbookv4.helloteam.falbook.activities;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.falbookv4.helloteam.falbook.R;
import com.falbookv4.helloteam.falbook.falcisec.TelveEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class SatinalActivity extends AppCompatActivity {

    private Toolbar toolbarSatinal;
    private RelativeLayout dusukTelve;
    private FirebaseAuth mAuth;
    private FirebaseUser mBulunanKullanici;
    private DatabaseReference mDatabaseKullanici;
    private int bulunanTelve, uzerineEklenenTelve;
    private AlertDialog.Builder alertSatinal;

    public void init(){

        toolbarSatinal = (Toolbar) findViewById(R.id.toolbarSatinal);
        dusukTelve = (RelativeLayout) findViewById(R.id.dusukTelveSatinAl);
        mAuth = FirebaseAuth.getInstance();
        mBulunanKullanici = mAuth.getCurrentUser();
        mDatabaseKullanici = FirebaseDatabase.getInstance().getReference().child("Kullanicilar")
                .child(mBulunanKullanici.getUid());

        alertSatinal = new AlertDialog.Builder(this);

    }

    private int telveSayisiniBul(){

        mDatabaseKullanici.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                bulunanTelve = ((Long) dataSnapshot.child("telve").getValue()).intValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return bulunanTelve;
    }


    public void telveSatinAl(int eklenecekTelveSayisi){

        bulunanTelve = telveSayisiniBul();

        uzerineEklenenTelve = bulunanTelve + eklenecekTelveSayisi;

        alertSatinal.setTitle("" + eklenecekTelveSayisi + " Telve Satın Al");
        alertSatinal.setMessage("Satın al sayfasına yönlendirileceksin");
        alertSatinal.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mDatabaseKullanici.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Map<String, Object> telveSatinAlMap = new HashMap<>();
                        telveSatinAlMap.put("telve",uzerineEklenenTelve);

                        mDatabaseKullanici.updateChildren(telveSatinAlMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    //satın alım başarılı
                                }
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
            }
        });

        alertSatinal.show();


    }

    public void satinAlimHandler(){

        dusukTelve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: internetden çekmesi lazım telve sayılarını
                telveSatinAl(200);
            }
        });

    }



    public void handler(){

        setSupportActionBar(toolbarSatinal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        satinAlimHandler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satinal);
        init();
        handler();
    }



}
