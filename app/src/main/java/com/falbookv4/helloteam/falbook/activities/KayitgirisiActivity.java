package com.falbookv4.helloteam.falbook.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.falbookv4.helloteam.falbook.R;
import com.falbookv4.helloteam.falbook.classes.Sabitler;
import com.falbookv4.helloteam.falbook.classes.SecurePreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class KayitgirisiActivity extends AppCompatActivity {

    private ConstraintLayout kayitGirisLayout;
    private FirebaseAuth mAuth;
    private FirebaseUser mBulunanKullanici;
    private TextInputEditText girisTxtSifre, girisTxtMail;
    private TextInputLayout textInputGirisSifre, textInputGirisMail;
    private String sifre, mail;
    private SweetAlertDialog mProgressGiris;
    private Button btnKayitGiris;
    private DatabaseReference mDatabaseKullanici;
    private String strAd, strSoyad, strMail, strSifre;
    private FirebaseAuth.AuthStateListener mAuthListener;


    public void init(){

        mAuth = FirebaseAuth.getInstance();

        mBulunanKullanici = mAuth.getCurrentUser();

        girisTxtMail = (TextInputEditText) findViewById(R.id.txtKayitGirisMail);
        girisTxtSifre = (TextInputEditText) findViewById(R.id.txtKayitGirisSifre);

        textInputGirisMail = (TextInputLayout) findViewById(R.id.textInputKayitGirisMail);
        textInputGirisSifre = (TextInputLayout) findViewById(R.id.textInputKayitGirisSifre);

        btnKayitGiris = (Button) findViewById(R.id.kayitGirisBtnGiris);

        kayitGirisLayout = (ConstraintLayout) findViewById(R.id.kayitGirisLayout);

        mProgressGiris = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mAuth.removeAuthStateListener(mAuthListener);
    }

    private boolean mailDogrula(){

        if(girisTxtMail.getText().toString().isEmpty()){

            textInputGirisMail.setError("Mail adresi boş bırakılamaz");
            return false;
        }

        else{
            textInputGirisMail.setErrorEnabled(false);
            return true;
        }
    }

    private boolean sifreDogrula(){


        String parola = girisTxtSifre.getText().toString().trim();

        if(parola.length()<6){
            textInputGirisSifre.setError("Şifreniz 6 karakterden daha az!");
            return false;
        }
        else{
            textInputGirisSifre.setErrorEnabled(false);
            return true;
        }

    }


    public void handler(){


        SecurePreferences preferences = new SecurePreferences(getApplicationContext(), "difs", "150", true);
        strMail = preferences.getString(Sabitler.KULLANICI_MAIL);
        strSifre = preferences.getString(Sabitler.KULLANICI_SIFRE);

        if(strMail !=  null && strSifre != null){

            girisTxtMail.setText(strMail);
            girisTxtSifre.setText(strSifre);
        }


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() != null){

                    String uid = firebaseAuth.getCurrentUser().getUid();
                    mDatabaseKullanici = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid);

                    String kullaniciTokenGiris = FirebaseInstanceId.getInstance().getToken();

                    Map<String, Object> updateKullaniciGirisMap = new HashMap<>();
                    updateKullaniciGirisMap.put("cihazID", kullaniciTokenGiris);

                    mDatabaseKullanici.updateChildren(updateKullaniciGirisMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){

                                mProgressGiris.hide();
                                Snackbar snacBilgiGuncellenemedi = Snackbar
                                        .make(kayitGirisLayout, "Tekrar Deneyiniz", Snackbar.LENGTH_LONG);
                                snacBilgiGuncellenemedi.show();
                            }else{

                                Intent girisToAnasayfa = new Intent(KayitgirisiActivity.this, AnasayfaActivity.class);
                                girisToAnasayfa.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(girisToAnasayfa);
                                finish();
                            }
                        }
                    });


                }

            }
        };


        btnKayitGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mailDogrula() && sifreDogrula()){

                    girisYap();
                }else{

                    Snackbar snacMisafirUyelikHata = Snackbar
                            .make(kayitGirisLayout, "Boş bırakmayınız", Snackbar.LENGTH_LONG);
                    snacMisafirUyelikHata.show();
                }

            }
        });

    }

    private void girisYap() {

        mProgressGiris.getProgressHelper().setBarColor(Color.parseColor("#795548"));
        mProgressGiris.setTitleText("Giriş Yapılıyor");
        mProgressGiris.setCancelable(false);
        mProgressGiris.show();

        sifre = girisTxtSifre.getText().toString();
        mail = girisTxtMail.getText().toString();

        if(mailDogrula() && sifreDogrula()){

            mAuth.signInWithEmailAndPassword(mail, sifre).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        SecurePreferences preferences = new SecurePreferences(getApplicationContext(), "difs", "150", true);
                        strMail = girisTxtMail.getText().toString();
                        strSifre = girisTxtSifre.getText().toString();
                        preferences.put(Sabitler.KULLANICI_MAIL, strMail);
                        preferences.put(Sabitler.KULLANICI_SIFRE, strSifre);

                        mProgressGiris.dismiss();

                    }else{

                        //Giriş yapılamadı

                        mProgressGiris.hide();

                        String errorMisafirKayit = "";
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthException e) {
                            errorMisafirKayit = "Böyle bir kullanıcı bulunmamakta.";
                        } catch (Exception e) {
                            errorMisafirKayit = "İnternetinizi kontrol edin!";
                            e.printStackTrace();
                        }

                        Snackbar snacMisafirUyelikHata = Snackbar
                                .make(kayitGirisLayout, errorMisafirKayit, Snackbar.LENGTH_LONG);
                        snacMisafirUyelikHata.show();


                    }
                }
            });

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayitgirisi);
        init();
        handler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btnKayitGiris.setOnClickListener(null);

    }
}
