package com.falbookv4.helloteam.falbook.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.falbookv4.helloteam.falbook.Manifest;
import com.falbookv4.helloteam.falbook.R;
import com.falbookv4.helloteam.falbook.classes.Kullanicilar;
import com.falbookv4.helloteam.falbook.classes.RuntimeIzinler;
import com.falbookv4.helloteam.falbook.classes.Sabitler;
import com.falbookv4.helloteam.falbook.classes.SecurePreferences;
import com.falbookv4.helloteam.falbook.classes.Utils;
import com.falbookv4.helloteam.falbook.falcisec.FalcitelveEvent;
import com.falbookv4.helloteam.falbook.falcisec.TelveEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.security.Permission;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class GirisActivity extends AppCompatActivity {

    private static final int UYGULAMAYA_GIRIS_REQUEST_CODE = 100;
    private ConstraintLayout girisMainLayout;
    private Button btnMisafirGirisi, btnKullaniciGirisi, btnKayitOl;
    private SweetAlertDialog mProgressMisafir;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseKullanicilar;
    private FirebaseUser mBulunanKullanici;
    private boolean misafirGirisDurumu = true;
    private int girisTelve = 50, dahaOncedenBulunanTelve = -1, verilecekTelveSayisi;
    private TextView txtGizlilikPolitikasi;
    private String strTelveSayisi, strMail;

    public void init(){

        girisMainLayout = (ConstraintLayout) findViewById(R.id.girisMainLayout);
        btnMisafirGirisi = (Button) findViewById(R.id.btnMisafirGiris);
        btnKullaniciGirisi = (Button) findViewById(R.id.btnGirisKullaniciGiris);
        btnKayitOl = (Button) findViewById(R.id.btnGirisKayitOl);

        txtGizlilikPolitikasi = (TextView) findViewById(R.id.txtSozlesme);

        mProgressMisafir = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        mAuth = FirebaseAuth.getInstance();

    }

    private void fontHandler(){

        Typeface typeFace= Typeface.createFromAsset(getAssets(),"fonts/MyriadPro.ttf");
        Typeface typeFaceBold= Typeface.createFromAsset(getAssets(),"fonts/MyriadProBold.ttf");

        btnMisafirGirisi.setTypeface(typeFace);
        btnKullaniciGirisi.setTypeface(typeFace);
        btnKayitOl.setTypeface(typeFace);
        txtGizlilikPolitikasi.setTypeface(typeFace);

    }

    public void misafirTelveSayisiYukle(){

        //secure olarak shared pref i al, -> string geliyor
        SecurePreferences preferences = new SecurePreferences(getApplicationContext(), "difs", "150", true);
        strTelveSayisi = preferences.getString(Sabitler.TELVE_SAYISI_MISAFIR);
        strMail = preferences.getString(Sabitler.KULLANICI_MAIL);

        //int e dönüştürerek kullan
        if(strTelveSayisi != null && strMail == null){

            dahaOncedenBulunanTelve = Integer.parseInt(strTelveSayisi);
        }else if(strMail == null){
            //ilk giriş durumu
            dahaOncedenBulunanTelve = -1;
        }else{
            //kullanıcının daha önceden giriş yaptığı ve mail inin girili olduğu durum
            dahaOncedenBulunanTelve = 0;
        }
    }

    public void handler(){

        misafirTelveSayisiYukle();
        fontHandler();

        txtGizlilikPolitikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GirisActivity.this, GizlilikpolitikasiActivity.class));
            }
        });

        //misafir giriş yap butonuna basıldığı zaman
        btnMisafirGirisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressMisafir.getProgressHelper().setBarColor(Color.parseColor("#795548"));
                mProgressMisafir.setTitleText("Misafir girişiniz oluşturuluyor");
                mProgressMisafir.setCancelable(false);
                mProgressMisafir.show();

                new MisafirKullaniciUyelik().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                /*
                String[] istenilenIzinler = {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE };
                GirisActivity.super.izinIste(istenilenIzinler, UYGULAMAYA_GIRIS_REQUEST_CODE);
                */
            }
        });

        btnKullaniciGirisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GirisActivity.this, KayitgirisiActivity.class));
            }
        });

        btnKayitOl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GirisActivity.this, KayitActivity.class));
            }
        });



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);
        init();
        handler();
    }

    //parametre-progress-result
    private class  MisafirKullaniciUyelik extends AsyncTask<String, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            misafirGirisDurumu = false;

            //anonim olarak girişi sağlıyoruz.
            final Task<AuthResult> taskResult = mAuth.signInAnonymously();

            taskResult.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        //(current user)
                        mBulunanKullanici = mAuth.getCurrentUser();
                        String uid = mBulunanKullanici.getUid();

                        mDatabaseKullanicilar = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid);
                        //offline olduğu durumlar için
                        mDatabaseKullanicilar.keepSynced(true);

                        String kullaniciToken = FirebaseInstanceId.getInstance().getToken();

                        //kullanıcı bilgileri

                        if(dahaOncedenBulunanTelve != -1){
                            verilecekTelveSayisi = dahaOncedenBulunanTelve;
                        }else{
                            verilecekTelveSayisi = girisTelve;
                        }

                        Kullanicilar girisKullanici = new Kullanicilar("", "", "", "", "", "", verilecekTelveSayisi, "default", kullaniciToken);

                        mDatabaseKullanicilar.setValue(girisKullanici).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                    misafirGirisDurumu = true;
                                    mProgressMisafir.dismiss();

                                    Intent girisToAnasayfa = new Intent(GirisActivity.this, AnasayfaActivity.class);
                                    girisToAnasayfa.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(girisToAnasayfa);
                                    finish();
                                }

                                else{

                                    misafirGirisDurumu = false;
                                    mProgressMisafir.hide();

                                    Snackbar snacMisafirGirisiYapilamadi = Snackbar
                                            .make(girisMainLayout, "Giriş Yapılamadı", Snackbar.LENGTH_LONG);
                                    snacMisafirGirisiYapilamadi.show();
                                }
                            }
                        });

                    }else {

                        mProgressMisafir.hide();

                        String errorGirisHata = "";
                        try {
                            throw taskResult.getException();
                        } catch (Exception e) {

                            new SweetAlertDialog(GirisActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Hata")
                                    .setConfirmText("Tamam")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.cancel();
                                        }
                                    })
                                    .setContentText("İnternetinizi kontrol ediniz!")
                                    .show();

                            errorGirisHata = "İnternetinizi kontrol edin!";
                            e.printStackTrace();
                        }

                    }

                }
            });

            return misafirGirisDurumu;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        btnMisafirGirisi.setOnClickListener(null);
        btnKullaniciGirisi.setOnClickListener(null);
        btnKayitOl.setOnClickListener(null);
    }


}