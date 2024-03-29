package com.falbookv4.helloteam.falbook.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.util.DateInterval;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.falbookv4.helloteam.falbook.R;
import com.falbookv4.helloteam.falbook.classes.FontCache;
import com.falbookv4.helloteam.falbook.classes.RandomString;
import com.falbookv4.helloteam.falbook.classes.Sabitler;
import com.falbookv4.helloteam.falbook.classes.SecurePreferences;
import com.falbookv4.helloteam.falbook.classes.Utils;
import com.falbookv4.helloteam.falbook.falcisec.FalcitelveEvent;
import com.falbookv4.helloteam.falbook.falcisec.GelenfalEvent;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import pl.aprilapps.easyphotopicker.EasyImage;

public class DilekActivity extends AppCompatActivity {

    private CoordinatorLayout dilekGenelLayout;
    private Toolbar dilekToolbar;
    private EditText txtDilek;
    private Button btnGonder;
    String dilekIsim, dilekIliski, dilekDogum, dilekCinsiyet, falGonderilmeTarihi, fal_aciklamasi = "", strFalciIsmi, strOncekiTarih;
    byte[] dilekKucukFoto1, dilekKucukFoto2, dilekKucukFoto3;
    boolean falGonderSonuc = false, falciDilekAktif;
    private FirebaseAuth mAuth;
    private FirebaseUser mBulunanKullanici;
    private StorageReference mStorageKahve;
    private DatabaseReference mDatabaseFal, mDatabaseBakilmamisFal, mDatabaseKullanici;
    private Uri uriKuculmusFoto1, uriKuculmusFoto2, uriKuculmusFoto3;
    private SweetAlertDialog mProgress, mProgressBasariliGonderme;
    private String strDilek = "";
    private boolean gonderCooldown = true, asyncDonenSonuc = false, falGonderebilir = true;
    private int farkTelveSayisi, telveBedeli, yeniTelveSayiniz, bulunanTelve, telveKontrolSayi;
    private DatabaseReference connectedRef;
    private ValueEventListener mListener1, mListener2, mListener3;
    private TextView toolbarBaslik, txtDilekBilgilendirme, txtOdenecekUcret;
    private boolean bedavaGonder;
    private String strOdenecekBilgilendirme;

    public void init(){

        toolbarBaslik = (TextView) findViewById(R.id.dilek_toolbar_baslik);
        txtDilekBilgilendirme = (TextView) findViewById(R.id.txtDilekBilgilendirme);
        txtOdenecekUcret = (TextView) findViewById(R.id.txtOdenecekUcret);

        dilekGenelLayout = (CoordinatorLayout) findViewById(R.id.dilek_layout);

        mAuth = FirebaseAuth.getInstance();
        mBulunanKullanici = mAuth.getCurrentUser();
        mStorageKahve = FirebaseStorage.getInstance().getReference();
        mDatabaseFal = FirebaseDatabase.getInstance().getReference().child("Fal"); //database ref
        mDatabaseBakilmamisFal = FirebaseDatabase.getInstance().getReference().child("BakilmamisFal");

        mDatabaseKullanici = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").
                child(mBulunanKullanici.getUid()); //database ref -> kullanıcıların altına kullanıcı id leri

        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

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

    private void fontHandler(){

        Typeface typeFaceBold= FontCache.get("fonts/MyriadProBold.ttf", this);
        Typeface typeFace= FontCache.get("fonts/MyriadPro.ttf", this);

        toolbarBaslik.setTypeface(typeFaceBold);
        txtDilekBilgilendirme.setTypeface(typeFace);
        txtDilek.setTypeface(typeFace);
        btnGonder.setTypeface(typeFace);

    }


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
    }

    @Subscribe(sticky = true)
    public void onFalcitelveEvent(FalcitelveEvent event){
        //kullanıcının telvesini alıyoruz
        telveBedeli = event.getFalcitelveBedeli();
        falciDilekAktif = event.isFalciDilekAktif();

        strOdenecekBilgilendirme = "Bu falcının telve bedeli: " + "" + telveBedeli + "";
        txtOdenecekUcret.setText(strOdenecekBilgilendirme);
    }


    public void falGonder(){

        strDilek = txtDilek.getText().toString();

        connectedRef.addValueEventListener(mListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

                    mDatabaseKullanici.addValueEventListener(mListener2 = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            bulunanTelve = ((Long) dataSnapshot.child("telve").getValue()).intValue();

                            if(bulunanTelve>0){

                                farkTelveSayisi = bulunanTelve - telveBedeli;
                            }

                            if(farkTelveSayisi>=0 && falGonderebilir && bulunanTelve>0 && telveBedeli>0){
                                falGonderebilir = false;
                                yeniTelveSayiniz = farkTelveSayisi;
                                new FalGonderAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dilekIsim,
                                        dilekDogum, dilekCinsiyet, dilekIliski, fal_aciklamasi, strDilek);
                            }
                            else if(bulunanTelve<=0 && farkTelveSayisi<=0){
                                Snackbar snacButunBilgi = Snackbar
                                        .make(dilekGenelLayout, "Telve sayınız TÜKENDİ", Snackbar.LENGTH_LONG);
                                snacButunBilgi.show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            mProgress.hide();

                            Snackbar snacGonderilemedi = Snackbar
                                    .make(dilekGenelLayout, "Falınız GÖNDERİLEMEDİ.", Snackbar.LENGTH_LONG);
                            snacGonderilemedi.show();

                        }
                    });



                } else {
                    if(!isFinishing()){

                        Snackbar snacGonderilemedi = Snackbar
                                .make(dilekGenelLayout, "İnternetinizi kontrol ediniz.", Snackbar.LENGTH_LONG);
                        snacGonderilemedi.show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

    }

    public static long getDateDiff(long timeUpdate, long timeNow, TimeUnit timeUnit)
    {
        long diffInHours = Math.abs(timeNow - timeUpdate);
        return timeUnit.convert(diffInHours, TimeUnit.HOURS);
    }

    public void handler(){

        fontHandler();

        setSupportActionBar(dilekToolbar);
        getSupportActionBar().setTitle(null);

        txtDilek.setFocusable(false);
        txtDilek.setFocusableInTouchMode(false);

        if(telveBedeli > 100){
            txtDilek.setFocusable(true);
            txtDilek.setFocusableInTouchMode(true);
        }

        txtDilek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(telveBedeli > 100){
                    txtDilek.setFocusable(true);
                    txtDilek.setFocusableInTouchMode(true);
                }

                if(telveBedeli < 100){

                    //txtDilek.setFocusable(false);
                    //txtDilek.getText().clear();
                    Snackbar snacGonderilemedi = Snackbar
                            .make(dilekGenelLayout, "Bu falcı niyet kabul etmiyor.(Başka falcı seçebilirsiniz)", Snackbar.LENGTH_LONG);
                    snacGonderilemedi.show();
                }
            }
        });

        btnGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(telveBedeli > 100) {

                    if (gonderCooldown) {

                        falGonder();
                        btnGonder.setEnabled(false);

                    } else {
                        btnGonder.setEnabled(false);
                    }

                    gonderCooldown = false;
                }else{

                    SecurePreferences preferences = new SecurePreferences(getApplicationContext(), "difs", "550", true);
                    strOncekiTarih = preferences.getString(Sabitler.GUNDE_MAX);

                    if (strOncekiTarih == null || strOncekiTarih.equals("")) {
                        bedavaGonder = true;
                    } else {
                        long diffTime = -1;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String strCurrentTime  = dateFormat.format(new Date().getTime());
                        try {
                            Date dateCurrent = dateFormat.parse(strCurrentTime);
                            long msCurrent = dateCurrent.getTime();
                            Date dateBefore = dateFormat.parse(strOncekiTarih);
                            long msBefore = dateBefore.getTime();

                            Log.d("SimdikiZaman", String.valueOf(msCurrent));
                            Log.d("OncekiZaman", String.valueOf(msBefore));

                            diffTime = msCurrent - msBefore;

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if(diffTime > 72000000){
                            bedavaGonder = true;
                        }else{
                            bedavaGonder = false;
                        }

                    }

                    if(bedavaGonder){
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String strOncekiTarih  = dateFormat.format(new Date().getTime());
                        preferences.put(Sabitler.GUNDE_MAX, strOncekiTarih);
                        falGonder();
                    }else{
                        Snackbar snacGonderilemedi = Snackbar
                                .make(dilekGenelLayout, "Bu falcıya günde en fazla 1 defa gönderebilirsiniz.", Snackbar.LENGTH_LONG);
                        snacGonderilemedi.show();
                    }

                }
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

    @Override
    protected void onDestroy() {

        //EventBus.clearCaches();
        Utils.clearCameraPic(this);
        //Utils.deleteCache(this);
        EasyImage.clearPublicTemp(getApplicationContext());

        super.onDestroy();

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
            final String kullaniciID = mAuth.getCurrentUser().getUid();
            Random random = new Random();
            int randomSayi = random.nextInt(30);
            RandomString randomDizi = new RandomString(5);

            StorageReference filepath1 = mStorageKahve.child("Fal_Fotolari1").child(kullaniciID + "" + randomDizi);
            StorageReference filepath2 = mStorageKahve.child("Fal_Fotolari2").child(kullaniciID + "" + randomDizi);
            StorageReference filepath3 = mStorageKahve.child("Fal_Fotolari3").child(kullaniciID + "" + randomDizi);

            //database de fal ın altında->uid-> push yaparak(random id) oluştur
            final DatabaseReference yeniPost = mDatabaseFal.child(kullaniciID).push();

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
                        yeniBakilmamisFalPost.child("uid").setValue(kullaniciID);
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
                        yeniPost.child("uid").setValue(kullaniciID);
                        yeniPost.child("fal_yorumu").setValue(fal_aciklamasi);
                        yeniPost.child("fal_dilek").setValue(dilek_val);
                        yeniPost.child("harcanan_telve").setValue(telveBedeli);


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

            if(!isFinishing()){

                mProgress.getProgressHelper().setBarColor(Color.parseColor("#795548"));
                mProgress.setTitleText("Fal Gönderiliyor!");
                mProgress.setCancelable(false);
                mProgress.show();
            }

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

