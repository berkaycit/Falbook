package com.falbookv4.helloteam.falbook.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.falbookv4.helloteam.falbook.R;
import com.falbookv4.helloteam.falbook.falcisec.TelveEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
    private FloatingActionButton fbFalGonder;
    private BottomNavigationView botToolbar;

    public void init(){

        toolbarSatinal = (Toolbar) findViewById(R.id.toolbarSatinal);
        dusukTelve = (RelativeLayout) findViewById(R.id.dusukTelveSatinAl);
        mAuth = FirebaseAuth.getInstance();
        mBulunanKullanici = mAuth.getCurrentUser();
        mDatabaseKullanici = FirebaseDatabase.getInstance().getReference().child("Kullanicilar")
                .child(mBulunanKullanici.getUid());

        alertSatinal = new AlertDialog.Builder(this);

        fbFalGonder = (FloatingActionButton) findViewById(R.id.fbFalGonder);
        botToolbar = (BottomNavigationView) findViewById(R.id.bottom_navigation);

    }

    private void menuleriHazirla() {

        fbFalGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent anasayfaToKafe = new Intent(SatinalActivity.this, KafeActivity.class);
                startActivity(anasayfaToKafe);
                finish();
            }
        });

        botToolbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.menuAnasafaButon:
                        Intent intentToAnasayfa = new Intent(SatinalActivity.this, AnasayfaActivity.class);
                        startActivity(intentToAnasayfa);
                        finish();
                        break;
                    case R.id.menuGelenfalButon:
                        Intent intentToFallarim = new Intent(SatinalActivity.this, GelenfallarActivity.class);
                        startActivity(intentToFallarim);
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    public void telveSatinAl(final int eklenecekTelveSayisi){

        mDatabaseKullanici.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                bulunanTelve = ((Long) dataSnapshot.child("telve").getValue()).intValue();
                uzerineEklenenTelve = bulunanTelve + eklenecekTelveSayisi;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        alertSatinal.setTitle("" + eklenecekTelveSayisi + " Telve Satın Al");
        alertSatinal.setMessage("Satın al sayfasına yönlendirileceksin");
        alertSatinal.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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

                /*
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
                */

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

        menuleriHazirla();

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
