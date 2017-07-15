package com.falbookv4.helloteam.falbook.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.falbookv4.helloteam.falbook.R;
import com.falbookv4.helloteam.falbook.falcisec.Falci1telveEvent;
import com.falbookv4.helloteam.falbook.falcisec.Falci2telveEvent;
import com.falbookv4.helloteam.falbook.falcisec.Falci3telveEvent;
import com.falbookv4.helloteam.falbook.falcisec.GelenfalEvent;
import com.falbookv4.helloteam.falbook.falcisec.TelveEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DilekActivity extends AppCompatActivity {

    private CoordinatorLayout dilekGenelLayout;
    private Toolbar dilekToolbar;
    private EditText txtDilek;
    private Button btnGonder;
    String dilekIsim, dilekIliski, dilekDogum, dilekCinsiyet, falGonderilmeTarihi, fal_aciklamasi = "";
    byte[] dilekKucukFoto1, dilekKucukFoto2, dilekKucukFoto3;
    boolean falGonderSonuc = false;
    private FirebaseAuth mAuth;
    private FirebaseUser mBulunanKullanici;
    private StorageReference mStorageKahve;
    private DatabaseReference mDatabaseFal, mDatabaseBakilmamisFal, mDatabaseKullanici;
    private Uri uriKuculmusFoto1, uriKuculmusFoto2, uriKuculmusFoto3;
    private SweetAlertDialog mProgress, mProgressBasariliGonderme;
    private String strDilek = "";
    private boolean gonderCooldown = true, asyncDonenSonuc = false;
    private int toplamTelveSayisi, farkTelveSayisi, telveBedeli, yeniTelveSayiniz;


    //TODO: telve eksiltmeyi ekle.
    @Subscribe(sticky = true)
    public void onGelenfalEvent(GelenfalEvent event){
        //kullanıcı gönderilerini alıyoruz
        dilekIsim = event.getIsim();
        dilekIliski = event.getIliski();
        dilekDogum = event.getDogum();
        dilekCinsiyet = event.getCinsiyet();
        dilekKucukFoto1 = event.getKucukFoto1();
        dilekKucukFoto2 = event.getKucukFoto2();
        dilekKucukFoto3 = event.getKucukFoto3();
        uriKuculmusFoto1 = event.getUriKucukFoto1();
        uriKuculmusFoto2 = event.getUriKucukFoto2();
        uriKuculmusFoto3 = event.getUriKucukFoto3();
    }

    @Subscribe(sticky = true)
    public void onTelveEvent(TelveEvent event){
        //kullanıcının telvesini alıyoruz
        toplamTelveSayisi = event.getTelveEventSayisi();
    }

    @Subscribe(sticky = true)
    public void onFalci1telveEvent(Falci1telveEvent event){
        //kullanıcının telvesini alıyoruz
        telveBedeli = event.getFalci1telveBedeli();
    }

    @Subscribe(sticky = true)
    public void onFalci2telveEvent(Falci2telveEvent event){
        //kullanıcının telvesini alıyoruz
        telveBedeli = event.getFalci2telveBedeli();
    }

    @Subscribe(sticky = true)
    public void onFalci3telveEvent(Falci3telveEvent event){
        //kullanıcının telvesini alıyoruz
        telveBedeli = event.getFalci3telveBedeli();
    }


    public void falGonder(){

        strDilek = txtDilek.getText().toString();
        farkTelveSayisi = toplamTelveSayisi - telveBedeli;

        if(farkTelveSayisi >= 0) {

            yeniTelveSayiniz = farkTelveSayisi;
            new FalGonderAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dilekIsim,
                    dilekDogum, dilekCinsiyet, dilekIliski, fal_aciklamasi, strDilek);
        }


        else{
            Snackbar snacButunBilgi = Snackbar
                    .make(dilekGenelLayout, "Telve sayınız YETERSİZ", Snackbar.LENGTH_LONG);
            snacButunBilgi.show();
        }


    }


    public void init(){

        dilekGenelLayout = (CoordinatorLayout) findViewById(R.id.dilek_layout);

        mAuth = FirebaseAuth.getInstance();
        mBulunanKullanici = mAuth.getCurrentUser();
        mStorageKahve = FirebaseStorage.getInstance().getReference();
        mDatabaseFal = FirebaseDatabase.getInstance().getReference().child("Fal"); //database ref
        mDatabaseBakilmamisFal = FirebaseDatabase.getInstance().getReference().child("BakilmamisFal");

        mDatabaseKullanici = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").
                child(mBulunanKullanici.getUid()); //database ref -> kullanıcıların altına kullanıcı id leri

        dilekToolbar = (Toolbar) findViewById(R.id.toolbarDilek);

        txtDilek = (EditText) findViewById(R.id.txtDilekSoru);
        btnGonder = (Button) findViewById(R.id.btnDilekGonder);

        mProgress = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        mProgressBasariliGonderme = new SweetAlertDialog(DilekActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        mProgressBasariliGonderme.setTitleText("Fal Gönderildi!");
        mProgressBasariliGonderme.setContentText("Falınız başarılı bir şekilde gönderildi!");
        mProgressBasariliGonderme.setConfirmText("Tamam");
        mProgressBasariliGonderme.setCancelable(false);
        mProgressBasariliGonderme.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                mProgressBasariliGonderme.dismiss();
                Intent dilekToAnasayfa = new Intent(DilekActivity.this, AnasayfaActivity.class);
                dilekToAnasayfa.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(dilekToAnasayfa);
                finish();
            }
        });



    }

    public void handler(){

        setSupportActionBar(dilekToolbar);
        getSupportActionBar().setTitle(null);

        btnGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(gonderCooldown){
                    falGonder();
                }else{
                    btnGonder.setEnabled(false);
                }

                gonderCooldown = false;

            }
        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dilek);
        init();
        handler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    //parametre-progress-result
    private class  FalGonderAsync extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {

            falGonderSonuc = false;

            final String isim_val = strings[0];
            final String dogum_val = strings[1];
            final String cinsiyet_val = strings[2];
            final String iliski_val = strings[3];
            final String fal_aciklamasi = strings[4];
            final String dilek_val = strings[5];

            //storage da 'fotograflarin' altına cekilen fotograflarin id

            StorageReference filepath1 = mStorageKahve.child("Fal_Fotolari1").child(uriKuculmusFoto1.getLastPathSegment());
            StorageReference filepath2 = mStorageKahve.child("Fal_Fotolari2").child(uriKuculmusFoto2.getLastPathSegment());
            StorageReference filepath3 = mStorageKahve.child("Fal_Fotolari3").child(uriKuculmusFoto3.getLastPathSegment());

            //database de fal ın altında->uid-> push yaparak(random id) oluştur
            final DatabaseReference yeniPost = mDatabaseFal.child(mBulunanKullanici.getUid()).push();

            filepath3.putBytes(dilekKucukFoto3).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    //firebase e yüklenen fotografin url sini almak için
                    String kucukFotoDownloadUrl3 = task.getResult().getDownloadUrl().toString();

                    if(task.isSuccessful()){

                        //güncel bilgileri yerleştir
                        //başarıyla yüklendi.
                        yeniPost.child("foto3").setValue(kucukFotoDownloadUrl3);
                    }
                }
            });

            filepath2.putBytes(dilekKucukFoto2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    //firebase e yüklenen fotografin url sini almak için
                    String kucukFotoDownloadUrl2 = task.getResult().getDownloadUrl().toString();

                    if(task.isSuccessful()){

                        //güncel bilgileri yerleştir
                        //başarıyla yüklendi.
                        yeniPost.child("foto2").setValue(kucukFotoDownloadUrl2);
                    }
                }
            });

            filepath1.putBytes(dilekKucukFoto1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    //firebase e yüklenen fotografin url sini almak için
                    final String kucukFotoDownloadUrl1 = task.getResult().getDownloadUrl().toString();

                    if(task.isSuccessful()){

                        //güncel bilgileri yerleştir
                        //başarıyla yüklendi.
                        yeniPost.child("foto1").setValue(kucukFotoDownloadUrl1);

                        final DatabaseReference yeniBakilmamisFalPost = mDatabaseBakilmamisFal.child(yeniPost.getKey());


                        //bakılmamış fal database
                        yeniBakilmamisFalPost.child("uid").setValue(mBulunanKullanici.getUid());
                        yeniBakilmamisFalPost.child("date").setValue(ServerValue.TIMESTAMP);
                        yeniBakilmamisFalPost.child("fid").setValue(yeniPost.getKey());

                        //tarih
                        //Long timestamp = (Long) dataSnapshot.getValue();

                        //SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        //falGonderilmeTarihi = sfd.format(new Date(timestamp));

                        //yeniPost.child("gonderilme_tarihi").setValue(ServerValue.TIMESTAMP);

                        String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                        yeniPost.child("gonderilme_tarihi").setValue(currentDate);

                        //database e degerleri al
                        yeniPost.child("isim").setValue(isim_val);
                        yeniPost.child("dogum").setValue(dogum_val);
                        yeniPost.child("cinsiyet").setValue(cinsiyet_val);
                        yeniPost.child("iliski").setValue(iliski_val);
                        yeniPost.child("uid").setValue(mBulunanKullanici.getUid());
                        yeniPost.child("fal_yorumu").setValue(fal_aciklamasi);
                        yeniPost.child("fal_dilek").setValue(dilek_val);

/*
                        mDatabaseKullanici.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Map<String, Object> updateTelveMap = new HashMap<>();
                                updateTelveMap.put("telve", yeniTelveSayiniz);

                                mDatabaseKullanici.updateChildren(updateTelveMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            falGonderSonuc = true;
                                            mProgress.dismiss();
                                            if(!isFinishing()){

                                                mProgressBasariliGonderme.show();
                                            }
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        */


                        mDatabaseFal.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                falGonderSonuc = true;
                                mProgress.dismiss();
                                if(!isFinishing()){

                                    mProgressBasariliGonderme.show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                    }else{

                        mProgress.hide();

                        Snackbar snacGonderilemedi = Snackbar
                                .make(dilekGenelLayout, "Falınız GÖNDERİLEMEDİ.", Snackbar.LENGTH_LONG);
                        snacGonderilemedi.show();

                        falGonderSonuc = false;
                    }
                }
            });


            return falGonderSonuc;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgress.getProgressHelper().setBarColor(Color.parseColor("#795548"));
            mProgress.setTitleText("Fal Gönderiliyor!");
            mProgress.setCancelable(false);
            mProgress.show();

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }


}
