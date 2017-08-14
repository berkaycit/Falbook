package com.falbookv4.helloteam.falbook.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.falbookv4.helloteam.falbook.R;
import com.falbookv4.helloteam.falbook.classes.FontCache;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SifredegistirActivity extends AppCompatActivity {

    private CoordinatorLayout genelLayout;
    private Toolbar sifreDegistirToolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser mBulunanKullanici;
    private TextView txtBilgilendirme;
    private TextInputEditText txtSifreDegistir, txtSifreDegistirOnay;
    private Button btnGonder;
    private String strYeniSifre, strYeniSifreOnay;
    private FloatingActionButton fbFalGonder;
    private BottomNavigationView botToolbar;
    private String kullaniciMaili;
    private TextInputLayout textInputSifre, textInputYeniSifre;
    private TextView toolbarBaslik, txtMailKontrol;


    public void init(){

        toolbarBaslik = (TextView) findViewById(R.id.sifredegistir_toolbar_baslik);
        txtMailKontrol = (TextView) findViewById(R.id.txtMailKontrolEt);

        mAuth = FirebaseAuth.getInstance();
        mBulunanKullanici = mAuth.getCurrentUser();

        genelLayout = (CoordinatorLayout) findViewById(R.id.sifreDegistirGenelLayout);
        sifreDegistirToolbar = (Toolbar) findViewById(R.id.toolbarSifreDegistir);

        txtBilgilendirme = (TextView) findViewById(R.id.txtSifreDegistirBilgilendirme);
        txtSifreDegistir = (TextInputEditText) findViewById(R.id.txtSifreDegistirSifre);
        txtSifreDegistirOnay = (TextInputEditText) findViewById(R.id.txtSifreDegistirSifreOnay);
        textInputSifre = (TextInputLayout) findViewById(R.id.textInputSifreDegistir);
        textInputYeniSifre = (TextInputLayout) findViewById(R.id.textInputSifreDegistirOnay);

        btnGonder = (Button) findViewById(R.id.btnSifreYenile);

        fbFalGonder = (FloatingActionButton) findViewById(R.id.fbFalGonder);
        botToolbar = (BottomNavigationView) findViewById(R.id.bottom_navigation);

    }

    private void fontHandler(){

        Typeface typeFace= FontCache.get("fonts/MyriadPro.ttf", this);
        Typeface typeFaceBold= FontCache.get("fonts/MyriadProBold.ttf", this);

        toolbarBaslik.setTypeface(typeFaceBold);
        txtMailKontrol.setTypeface(typeFace);
        txtSifreDegistir.setTypeface(typeFace);
        txtSifreDegistir.setTypeface(typeFace);
        btnGonder.setTypeface(typeFace);
    }

    private void menuleriHazirla() {

        fbFalGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent anasayfaToKafe = new Intent(SifredegistirActivity.this, KafeActivity.class);
                anasayfaToKafe.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(anasayfaToKafe);
                finish();
            }
        });

        botToolbar.setSelectedItemId(R.id.menuBosButon);

        botToolbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.menuAnasafaButon:
                        Intent intentToAnasayfa = new Intent(SifredegistirActivity.this, AnasayfaActivity.class);
                        intentToAnasayfa.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentToAnasayfa);
                        finish();
                        break;
                    case R.id.menuGelenfalButon:
                        Intent intentToFallarim = new Intent(SifredegistirActivity.this, GelenfallarActivity.class);
                        intentToFallarim.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentToFallarim);
                        finish();
                        break;
                }
                return true;
            }
        });
    }


    public void handler(){

        fontHandler();
        menuleriHazirla();

        setSupportActionBar(sifreDegistirToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        if(mAuth.getCurrentUser().getEmail() != null){
            kullaniciMaili = mAuth.getCurrentUser().getEmail();
            String strBildirim = "Mail adresinize bildirimde bulunacağız.\nMail adresiniz: " + kullaniciMaili;
            txtBilgilendirme.setText(strBildirim);
        }else{
            txtBilgilendirme.setText("Şifrenizi değiştirmek için lütfen öncelikle profil sayfasından kayıt olunuz.");
        }


        btnGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strYeniSifre = txtSifreDegistir.getText().toString();
                strYeniSifreOnay = txtSifreDegistirOnay.getText().toString();

                if(mAuth.getCurrentUser().getEmail() != null){

                    sifreGuncelle(strYeniSifre, strYeniSifreOnay);
                }else{

                    Snackbar snacBasariliSifre = Snackbar
                            .make(genelLayout, "Lütfen öncelikle profil sayfasından kayıt olunuz!", Snackbar.LENGTH_LONG);
                    snacBasariliSifre.show();
                }

            }
        });

    }

    private boolean sifreDogrula(String eskiSifre, String yeniSifre){

        String parola = txtSifreDegistirOnay.getText().toString().trim();

        if(parola.length()<6){
            textInputYeniSifre.setError("Şifreniz 6 karakterden daha az!");
            return false;
        }else if(eskiSifre.equals(yeniSifre)){
            textInputYeniSifre.setError("Eski şifreniz ile yeni şifreniz aynı olamaz");
            return false;
        }
        else{
            textInputYeniSifre.setErrorEnabled(false);
            return true;
        }
    }

    //sifre yi güncelleme
    private void sifreGuncelle(final String eskiSifre, final String yeniSifre){

        if(mBulunanKullanici.getEmail() != null){

            AuthCredential credential = EmailAuthProvider.getCredential(mBulunanKullanici.getEmail()
                    ,eskiSifre);

            mBulunanKullanici.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){

                        textInputSifre.setErrorEnabled(false);
                        if(!(eskiSifre.equals(yeniSifre)) && sifreDogrula(eskiSifre, yeniSifre) ) {
                            mBulunanKullanici.updatePassword(yeniSifre).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Snackbar snacSifreHata = Snackbar
                                                .make(genelLayout, "Şifreniz güncellenemedi", Snackbar.LENGTH_LONG);
                                        snacSifreHata.show();

                                        Log.d("sifrede hata", task.getException().toString());

                                    } else if (task.isSuccessful()) {

                                        textInputSifre.setErrorEnabled(false);
                                        if(!isFinishing()){

                                            new SweetAlertDialog(SifredegistirActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                                    .setTitleText("Başarılı!")
                                                    .setConfirmText("Tamam")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            sweetAlertDialog.cancel();
                                                        }
                                                    })
                                                    .setContentText("Şifre başarıyla değiştirildi!")
                                                    .show();
                                        }
                                    }
                                }
                            });
                        }

                    }else{

                        textInputSifre.setError("Eski şifrenizi yanlış girdiniz");

                        Snackbar snacBasariliSifre = Snackbar
                                .make(genelLayout, "Eski şifrenizi yanlış girdiniz.", Snackbar.LENGTH_LONG);
                        snacBasariliSifre.show();

                    }
                }
            });

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sifredegistir);
        init();
        handler();
        menuleriHazirla();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fbFalGonder.setOnClickListener(null);
    }
}
