package com.falbookv4.helloteam.falbook.activities;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MaildegistirActivity extends AppCompatActivity {

    private CoordinatorLayout genelLayout;
    private Button btnGonder;
    private EditText txtSifre, txtMail;
    private TextInputLayout txtInputMail, txtInputSifre;
    private FirebaseAuth mAuth;
    private FirebaseUser mBulunanKullanici;
    private DatabaseReference mDatabaseKullanici;

    public void init(){

        mAuth = FirebaseAuth.getInstance();
        mBulunanKullanici = mAuth.getCurrentUser();
        String uid = mBulunanKullanici.getUid();
        mDatabaseKullanici = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid);

        genelLayout = (CoordinatorLayout) findViewById(R.id.mailDegistirGenelLayout);
        btnGonder = (Button) findViewById(R.id.btnMailDegistir);
        txtSifre = (EditText) findViewById(R.id.txtMailDegistirSifre);
        txtMail = (EditText) findViewById(R.id.txtMailDegistirMail);
        txtInputMail = (TextInputLayout) findViewById(R.id.textInputMailDegistir);
        txtInputSifre = (TextInputLayout) findViewById(R.id.txtInputMailDegistirSifre);

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