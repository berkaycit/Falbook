package com.falbookv4.helloteam.falbook.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MaildegistirActivity extends AppCompatActivity {

    private CoordinatorLayout genelLayout;
    private Button btnGonder;
    private TextInputEditText txtSifre, txtMail;
    private TextInputLayout txtInputMail, txtInputSifre;
    private FirebaseAuth mAuth;
    private FirebaseUser mBulunanKullanici;
    private DatabaseReference mDatabaseKullanici;
    private TextView toolbarBaslik, txtMailDegistirBilgilendirme;

    private Toolbar toolbarFalbookHakkinda;

    private FloatingActionButton fbFalGonder;
    private BottomNavigationView botToolbar;

    public void init(){

        mAuth = FirebaseAuth.getInstance();
        mBulunanKullanici = mAuth.getCurrentUser();
        String uid = mBulunanKullanici.getUid();
        mDatabaseKullanici = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid);

        toolbarBaslik = (TextView) findViewById(R.id.maildegistir_toolbar_baslik);
        genelLayout = (CoordinatorLayout) findViewById(R.id.mailDegistirGenelLayout);
        btnGonder = (Button) findViewById(R.id.btnMailDegistir);
        txtSifre = (TextInputEditText) findViewById(R.id.txtMailDegistirSifre);
        txtMail = (TextInputEditText) findViewById(R.id.txtMailDegistirMail);
        txtInputMail = (TextInputLayout) findViewById(R.id.textInputMailDegistir);
        txtInputSifre = (TextInputLayout) findViewById(R.id.txtInputMailDegistirSifre);
        txtMailDegistirBilgilendirme = (TextView) findViewById(R.id.txtMailDegistirBilgilendirme);

        toolbarFalbookHakkinda = (Toolbar) findViewById(R.id.toolbarFalbookHakkinda);

        fbFalGonder = (FloatingActionButton) findViewById(R.id.fbFalGonder);
        botToolbar = (BottomNavigationView) findViewById(R.id.bottom_navigation);

    }

    private void fontHandler(){

        Typeface typeFace= FontCache.get("fonts/MyriadPro.ttf", this);
        Typeface typeFaceBold= FontCache.get("fonts/MyriadProBold.ttf", this);

        toolbarBaslik.setTypeface(typeFaceBold);
        txtMailDegistirBilgilendirme.setTypeface(typeFace);
        txtSifre.setTypeface(typeFace);
        txtMail.setTypeface(typeFace);
        btnGonder.setTypeface(typeFace);

    }

    private void menuleriHazirla() {

        fbFalGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent anasayfaToKafe = new Intent(MaildegistirActivity.this, KafeActivity.class);
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
                        Intent intentToAnasayfa = new Intent(MaildegistirActivity.this, AnasayfaActivity.class);
                        intentToAnasayfa.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentToAnasayfa);
                        finish();
                        break;
                    case R.id.menuGelenfalButon:
                        Intent intentToFallarim = new Intent(MaildegistirActivity.this, GelenfallarActivity.class);
                        intentToFallarim.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentToFallarim);
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    private boolean mailDogrula(){

        if(txtMail.getText().toString().isEmpty()){

            txtInputMail.setError("Mail adresi boş bırakılamaz");
            return false;
        }

        else{
            txtInputMail.setErrorEnabled(false);
            return true;
        }
    }

    public void handler(){

        fontHandler();
        menuleriHazirla();

        btnGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String strSifre, strMail;
                strSifre = txtSifre.getText().toString();
                strMail = txtMail.getText().toString();

                if(mailDogrula()){

                    AuthCredential credential = EmailAuthProvider.getCredential(mBulunanKullanici.getEmail(), strSifre);

                    mBulunanKullanici.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                txtInputMail.setErrorEnabled(false);

                                if(mailDogrula()){

                                    mBulunanKullanici.updateEmail(strMail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                mDatabaseKullanici.child("mail").setValue(strMail);

                                                if(!isFinishing()){

                                                    new SweetAlertDialog(MaildegistirActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                                            .setTitleText("Başarılı!")
                                                            .setConfirmText("Tamam")
                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                @Override
                                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                    sweetAlertDialog.cancel();
                                                                }
                                                            })
                                                            .setContentText("Mailiniz başarıyla değiştirildi!")
                                                            .show();
                                                }
                                            }else{

                                                Snackbar snacSifreHata = Snackbar
                                                        .make(genelLayout, "Mailiniz güncellenemedi", Snackbar.LENGTH_LONG);
                                                snacSifreHata.show();

                                            }
                                        }
                                    });
                                }
                            }else{

                                Snackbar snacBasariliSifre = Snackbar
                                        .make(genelLayout, "Bağlantı kurulamadı.", Snackbar.LENGTH_LONG);
                                snacBasariliSifre.show();

                            }
                        }
                    });


                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maildegistir);
        init();
        handler();
    }
}
