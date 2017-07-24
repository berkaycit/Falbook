package com.falbookv4.helloteam.falbook.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.falbookv4.helloteam.falbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SifredegistirActivity extends AppCompatActivity {

    private CoordinatorLayout genelLayout;
    private Toolbar sifreDegistirToolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser mBulunanKullanici;
    private EditText txtSifreDegistir, txtSifreDegistirOnay;
    private Button btnGonder;
    private String strYeniSifre, strYeniSifreOnay;
    private FloatingActionButton fbFalGonder;
    private BottomNavigationView botToolbar;


    public void init(){
        mAuth = FirebaseAuth.getInstance();
        mBulunanKullanici = mAuth.getCurrentUser();

        genelLayout = (CoordinatorLayout) findViewById(R.id.sifreDegistirGenelLayout);
        sifreDegistirToolbar = (Toolbar) findViewById(R.id.toolbarSifreDegistir);

        txtSifreDegistir = (EditText) findViewById(R.id.txtSifreDegistirSifre);
        txtSifreDegistirOnay = (EditText) findViewById(R.id.txtSifreDegistirSifreOnay);

        btnGonder = (Button) findViewById(R.id.btnSifreYenile);


        fbFalGonder = (FloatingActionButton) findViewById(R.id.fbFalGonder);
        botToolbar = (BottomNavigationView) findViewById(R.id.bottom_navigation);

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

        menuleriHazirla();

        setSupportActionBar(sifreDegistirToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);


        btnGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strYeniSifre = txtSifreDegistir.getText().toString();
                strYeniSifreOnay = txtSifreDegistirOnay.getText().toString();

                //sifreGuncelle();

            }
        });

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

                        if(!(eskiSifre.equals(yeniSifre))) {
                            mBulunanKullanici.updatePassword(yeniSifre).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Snackbar snacSifreHata = Snackbar
                                                .make(genelLayout, "Şifreniz güncellenemedi", Snackbar.LENGTH_LONG);
                                        snacSifreHata.show();

                                        Log.d("sifrede hata", task.getException().toString());

                                    } else if (task.isSuccessful()) {

                                        Snackbar snacBasariliSifre = Snackbar
                                                .make(genelLayout, "Şifreniz başarıyla güncellendi", Snackbar.LENGTH_LONG);
                                        snacBasariliSifre.show();
                                    }
                                }
                            });
                        }

                    }
                }
            });

        }


    }

}
