package com.falbookv4.helloteam.falbook.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.falbookv4.helloteam.falbook.R;
import com.falbookv4.helloteam.falbook.classes.FontCache;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class IletisimActivity extends AppCompatActivity {

    private CoordinatorLayout genelLayout;
    private Toolbar toolbarIletisim;
    private EditText txtKullaniciSorun;
    private Button btnSorunGonder;
    private String sorun, uid;
    private DatabaseReference mDatabaseSorun, connectedRef;
    private FirebaseAuth mAuth;
    private TextView toolbarBaslik, txtIletisimBilgilendirme;

    public void init(){

        genelLayout = (CoordinatorLayout) findViewById(R.id.iletisimGenelLayout);

        toolbarBaslik = (TextView) findViewById(R.id.iletisim_toolbar_baslik);
        toolbarIletisim = (Toolbar) findViewById(R.id.toolbarIletisim);
        txtKullaniciSorun = (EditText) findViewById(R.id.txtIletisimSorun);
        btnSorunGonder = (Button) findViewById(R.id.btnIletisimSorunGonder);
        txtIletisimBilgilendirme = (TextView) findViewById(R.id.txtIletisimBilgilendirme);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        mDatabaseSorun = FirebaseDatabase.getInstance().getReference().child("Iletisim").child(uid);
    }

    private void fontHandler(){

        Typeface typeFace= FontCache.get("fonts/MyriadPro.ttf", this);
        Typeface typeFaceBold= FontCache.get("fonts/MyriadProBold.ttf", this);

        toolbarBaslik.setTypeface(typeFaceBold);
        txtIletisimBilgilendirme.setTypeface(typeFace);
        txtKullaniciSorun.setTypeface(typeFace);
        btnSorunGonder.setTypeface(typeFace);

    }

    public void handler(){

        fontHandler();

        setSupportActionBar(toolbarIletisim);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);


        btnSorunGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                connectedRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean connected = dataSnapshot.getValue(Boolean.class);
                        if(connected){

                            sorun = txtKullaniciSorun.getText().toString();

                            if (mAuth.getCurrentUser().getEmail() != null && !TextUtils.isEmpty(sorun)) {
                                mDatabaseSorun.setValue(sorun);


                                Snackbar snacGonderilemedi = Snackbar
                                        .make(genelLayout, "Mesajınız ALDIK!", Snackbar.LENGTH_LONG);
                                snacGonderilemedi.show();

                            } else {

                                Snackbar snacGonderilemedi = Snackbar
                                        .make(genelLayout, "Mesajınız GÖNDERİLEMEDİ! (mail adresinizi " +
                                                "profil sayfasında belirttiniz mi?)", Snackbar.LENGTH_LONG);
                                snacGonderilemedi.show();

                            }


                        }else{

                            if(!isFinishing()){

                                new SweetAlertDialog(IletisimActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Hata")
                                        .setConfirmText("Tamam")
                                        .setContentText("İnternetinizi kontrol ediniz!")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.cancel();
                                            }
                                        })
                                        .show();
                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iletisim);
        init();
        handler();
    }
}
