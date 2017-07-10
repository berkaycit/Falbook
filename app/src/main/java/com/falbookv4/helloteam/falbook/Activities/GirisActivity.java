package com.falbookv4.helloteam.falbook.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.falbookv4.helloteam.falbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class GirisActivity extends AppCompatActivity {

    private ConstraintLayout girisMainLayout;
    private Button btnMisafirGirisi, btnKullaniciGirisi, btnKayitOl;
    private SweetAlertDialog mProgressMisafir;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseKullanicilar;
    private FirebaseUser mBulunanKullanici;
    private boolean misafirGirisDurumu = true;

    public void init(){

        girisMainLayout = (ConstraintLayout) findViewById(R.id.girisMainLayout);
        btnMisafirGirisi = (Button) findViewById(R.id.btnMisafirGiris);
        btnKullaniciGirisi = (Button) findViewById(R.id.btnGirisKullaniciGiris);
        btnKayitOl = (Button) findViewById(R.id.btnGirisKayitOl);


        mProgressMisafir = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        mAuth = FirebaseAuth.getInstance();
    }


    public void handler(){

        //misafir giriş yap butonuna basıldığı zaman
        btnMisafirGirisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressMisafir.getProgressHelper().setBarColor(Color.parseColor("#795548"));
                mProgressMisafir.setTitleText("Misafir girişiniz oluşturuluyor");
                mProgressMisafir.setCancelable(false);
                mProgressMisafir.show();

                new MisafirKullaniciUyelik().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
            taskResult.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    if(taskResult.isSuccessful()){

                        //(current user)
                        mBulunanKullanici = mAuth.getCurrentUser();
                        String uid = mBulunanKullanici.getUid();

                        mDatabaseKullanicilar = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid);
                        //offline olduğu durumlar için
                        mDatabaseKullanicilar.keepSynced(true);

                        //kullanıcı bilgileri
                        HashMap<String, String> userMap = new HashMap<>();
                        userMap.put("isim", "");
                        userMap.put("soyisim", "");
                        userMap.put("mail", "");
                        userMap.put("cinsiyet", "");
                        userMap.put("dogum", "");
                        userMap.put("iliski", "");
                        userMap.put("telve", "50");
                        userMap.put("profilfoto", "default");

                        mDatabaseKullanicilar.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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


                    }

                }
            });


            return misafirGirisDurumu;
        }
    }




}



