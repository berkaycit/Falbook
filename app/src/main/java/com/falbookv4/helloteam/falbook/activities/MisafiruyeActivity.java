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
import com.falbookv4.helloteam.falbook.classes.Sabitler;
import com.falbookv4.helloteam.falbook.classes.SecurePreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MisafiruyeActivity extends AppCompatActivity {

    private ConstraintLayout misafirUyeKayitLayout;
    private FirebaseAuth mAuth;
    private FirebaseUser mBulunanKullanici;
    private TextInputEditText misafirKayitTxtSifre, misafirKayitTxtMail;
    private TextInputLayout textInputMisafirSifre, textInputMisafirSifreOnay, textInputMisafirMail;
    private String sifre, mail, profilfoto;
    private SweetAlertDialog mProgressMisafirKayit;
    private Button btnMisafirUyeOlustur;
    private DatabaseReference mDatabaseKullanici;
    private String strAd, strSoyad;

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

        btnMisafirUyeOlustur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kayitOlustur();
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

                //current user ın id sini al
                final String uid = mAuth.getCurrentUser().getUid();

                //anonim üyeyi kalıcı üyelikle değiştiriyoruz
                AuthCredential credential = EmailAuthProvider.getCredential(mail, sifre);
                mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            mDatabaseKullanici = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid).child("mail");

                            mDatabaseKullanici.setValue(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    SecurePreferences preferences = new SecurePreferences(getApplicationContext(), "difs", "150", true);
                                    preferences.put(Sabitler.KULLANICI_MAIL, mail);
                                    preferences.put(Sabitler.KULLANICI_SIFRE, sifre);

                                    mProgressMisafirKayit.dismiss();
                                    Intent regToProfil = new Intent(MisafiruyeActivity.this, ProfilActivity.class);
                                    regToProfil.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(regToProfil);
                                    finish();
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
                            } catch (Exception e) {
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
