package com.falbookv4.helloteam.falbook.activities;

import android.content.Context;
import android.content.Intent;
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
import com.falbookv4.helloteam.falbook.classes.Kullanicilar;
import com.falbookv4.helloteam.falbook.classes.Sabitler;
import com.falbookv4.helloteam.falbook.classes.SecurePreferences;
import com.falbookv4.helloteam.falbook.classes.Utils;
import com.falbookv4.helloteam.falbook.falcisec.TelveEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class KayitActivity extends AppCompatActivity {

    private ConstraintLayout misafirUyeKayitLayout;
    private FirebaseAuth mAuth;
    private FirebaseUser mBulunanKullanici;
    private TextInputLayout textInputMisafirSifre, textInputMisafirSifreOnay, textInputMisafirMail;
    private String sifre, mail, profilfoto;
    private SweetAlertDialog mProgressMisafirKayit;
    private Button btnMisafirUyeOlustur;
    private DatabaseReference mDatabaseKullanici, mDatabaseKullanicilar;
    private String strAd, strSoyad, strTelveSayisi, strMail;
    private int girisTelve = 50, dahaOncedenBulunanTelve = -1, verilecekTelveSayisi;
    private TextInputEditText misafirKayitTxtSifre, misafirKayitTxtMail;

    public void init(){

        mAuth = FirebaseAuth.getInstance();

        mBulunanKullanici = mAuth.getCurrentUser();

        misafirKayitTxtMail = (TextInputEditText) findViewById(R.id.txtMisafirMail);
        misafirKayitTxtSifre = (TextInputEditText) findViewById(R.id.txtMisafirSifre);

        textInputMisafirMail = (TextInputLayout) findViewById(R.id.textInputMisafirMail);
        textInputMisafirSifre = (TextInputLayout) findViewById(R.id.textInputMisafirSifre);

        btnMisafirUyeOlustur = (Button) findViewById(R.id.kayitMisafirBtnKaydol);

        misafirUyeKayitLayout = (ConstraintLayout) findViewById(R.id.misafirUyeKayitLayout);

        mProgressMisafirKayit = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

    }

    public void misafirTelveSayisiYukle(){

        //secure olarak shared pref i al, -> string geliyor
        SecurePreferences preferences = new SecurePreferences(getApplicationContext(), "difs", "150", true);
        strTelveSayisi = preferences.getString(Sabitler.MISAFIR_SAYISI);
        strMail = preferences.getString(Sabitler.KULLANICI_MAIL);

        //int e dönüştürerek kullan
        if(strTelveSayisi != null && strMail == null){

            dahaOncedenBulunanTelve = Integer.parseInt(strTelveSayisi);
        }else if(Utils.isFileExist("fbb")){
            dahaOncedenBulunanTelve = 0;
        } else if(strMail == null){
            //ilk giriş durumu
            dahaOncedenBulunanTelve = -1;
        }else{
            //kullanıcının daha önceden giriş yaptığı ve mail inin girili olduğu durum
            dahaOncedenBulunanTelve = 0;
        }
    }


    private boolean mailDogrula(){

        if(misafirKayitTxtMail.getText().toString().isEmpty()){

            textInputMisafirMail.setError("Mail adresi boş bırakılamaz");
            return false;
        }

        else{
            textInputMisafirMail.setErrorEnabled(false);
            return true;
        }
    }

    private boolean sifreDogrula(){

        String parola = misafirKayitTxtSifre.getText().toString().trim();

        if(parola.length()<6){
            textInputMisafirSifre.setError("Şifreniz 6 karakterden daha az!");
            return false;
        }
        else{
            textInputMisafirSifre.setErrorEnabled(false);
            return true;
        }
    }

    public void handler(){

        misafirTelveSayisiYukle();
        btnMisafirUyeOlustur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mailDogrula() && sifreDogrula()){

                    kayitOlustur();
                }else{

                    Snackbar snacMisafirGirisiYapilamadi = Snackbar
                            .make(misafirUyeKayitLayout, "Boş bırakmayınız", Snackbar.LENGTH_LONG);
                    snacMisafirGirisiYapilamadi.show();
                }

            }
        });

    }


    private void kayitOlustur() {

        sifre = misafirKayitTxtSifre.getText().toString();
        mail = misafirKayitTxtMail.getText().toString();

        if (mailDogrula() && sifreDogrula()) {
            {
                mProgressMisafirKayit.getProgressHelper().setBarColor(Color.parseColor("#795548"));
                mProgressMisafirKayit.setTitleText("Kaydınız oluşturuluyor");
                mProgressMisafirKayit.setCancelable(false);
                mProgressMisafirKayit.show();

                mAuth.createUserWithEmailAndPassword(mail, sifre).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            //(current user)
                            mBulunanKullanici = mAuth.getCurrentUser();
                            String uid = mBulunanKullanici.getUid();

                            mDatabaseKullanicilar = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid);

                            String kullaniciToken = FirebaseInstanceId.getInstance().getToken();

                            //kullanıcı bilgileri

                            if(dahaOncedenBulunanTelve != -1){
                                verilecekTelveSayisi = dahaOncedenBulunanTelve;
                            }else{
                                verilecekTelveSayisi = girisTelve;
                            }

                            Kullanicilar girisKullanici = new Kullanicilar("", "", mail, "", "", "", verilecekTelveSayisi, "default", kullaniciToken);

                            mDatabaseKullanicilar.setValue(girisKullanici).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        SecurePreferences preferences = new SecurePreferences(getApplicationContext(), "difs", "150", true);
                                        preferences.put(Sabitler.KULLANICI_MAIL, mail);
                                        preferences.put(Sabitler.KULLANICI_SIFRE, sifre);

                                        mProgressMisafirKayit.dismiss();
                                        Intent kayitToAnasayfa = new Intent(KayitActivity.this, AnasayfaActivity.class);
                                        kayitToAnasayfa.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(kayitToAnasayfa);
                                        finish();
                                    }

                                    else{

                                        mProgressMisafirKayit.hide();

                                        Snackbar snacMisafirGirisiYapilamadi = Snackbar
                                                .make(misafirUyeKayitLayout, "Giriş Yapılamadı", Snackbar.LENGTH_LONG);
                                        snacMisafirGirisiYapilamadi.show();
                                    }
                                }
                            });

                        }else{

                            mProgressMisafirKayit.hide();

                            String errorMisafirKayit = "";
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                errorMisafirKayit = "ZAYIF Şifre, Lütfen daha güçlü şifre giriniz!";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                errorMisafirKayit = "Hata, GEÇERSİZ mail adresi girdiniz!";
                            } catch (FirebaseAuthUserCollisionException e) {
                                errorMisafirKayit = "Hata, Böyle bir kullanıcı BULUNMAKTA!";
                            }
                            catch (Exception e) {
                                errorMisafirKayit = "İnternetinizi kontrol edin!";
                                e.printStackTrace();
                            }

                            Snackbar snacMisafirUyelikHata = Snackbar
                                    .make(misafirUyeKayitLayout, errorMisafirKayit, Snackbar.LENGTH_LONG);
                            snacMisafirUyelikHata.show();

                        }

                    }
                });

            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misafiruye);
        init();
        handler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btnMisafirUyeOlustur.setOnClickListener(null);
    }

}
