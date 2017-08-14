package com.falbookv4.helloteam.falbook.activities;

import com.falbookv4.helloteam.falbook.Manifest;
import com.falbookv4.helloteam.falbook.R;
import com.falbookv4.helloteam.falbook.classes.FontCache;
import com.falbookv4.helloteam.falbook.classes.RuntimeIzinler;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joooonho.SelectableRoundedImageView;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class ProfilActivity extends RuntimeIzinler implements com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

    private static final int GALERIYE_ERISIM_REQUEST = 200;
    private CoordinatorLayout profilLayout;
    private DatabaseReference mDatabaseKullanici, mDatabaseKullaniciIc;
    private Toolbar profilToolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser mBulunanKullanici;
    private StorageReference mResimStorage;
    private SelectableRoundedImageView kullaniciProfilFoto;
    private EditText profilTxtAd, profilTxtSoyad, profilTxtMail, profilTxtCinsiyet, profilTxtDogum, profilTxtIliski;
    private String strAd, strSoyad, strMail, strCinsiyet, strDogum, strIliski, strProfilFoto;
    private int telve = 50; //telve sayısı
    private SweetAlertDialog mProgressKayitGuncelle;
    private final CharSequence iliskiDurumlari[] = {"İlişkisi Yok", "Evli", "Boşanmış", "Dul", "İlişkisi Var", "Nişanlı", "Platonik"};
    private final CharSequence arrCinsiyet[] = {"Kadın", "Erkek"};
    private boolean iliskiCooldown = true, cinsiyetCoolDown = true;
    private Handler handlerIliski, handlerCinsiyet;
    private Runnable runnableIliski, runnableCinsiyet;
    StorageReference filepathProfilFoto;
    AlertDialog.Builder fotoBeklemeDialog;
    boolean fotoYuklenmeSonuc;
    private FloatingActionButton fbFalGonder;
    private BottomNavigationView botToolbar;
    private ValueEventListener mListener1, mListener2;
    private boolean kullaniciKayitli = false;
    private TextView toolbarBaslik, txtProfilBilgilendirme;

    public void init() {

        mAuth = FirebaseAuth.getInstance();
        mBulunanKullanici = mAuth.getCurrentUser();
        String uid = mBulunanKullanici.getUid();
        mDatabaseKullanici = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        mDatabaseKullaniciIc = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid);
        mResimStorage = FirebaseStorage.getInstance().getReference();
        //offline olduğu durumlar için
        mDatabaseKullaniciIc.keepSynced(true);

        toolbarBaslik = (TextView) findViewById(R.id.profil_toolbar_baslik);
        txtProfilBilgilendirme = (TextView) findViewById(R.id.txtProfilBilgilendirme);

        profilLayout = (CoordinatorLayout) findViewById(R.id.profilLayout);

        profilToolbar = (Toolbar) findViewById(R.id.toolbarProfil);
        profilTxtMail = (EditText) findViewById(R.id.profilTxtMail);
        profilTxtAd = (EditText) findViewById(R.id.profilTxtAd);
        profilTxtSoyad = (EditText) findViewById(R.id.profilTxtSoyad);
        profilTxtCinsiyet = (EditText) findViewById(R.id.profilTxtCinsiyet);
        profilTxtDogum = (EditText) findViewById(R.id.profilTxtDogum);
        profilTxtIliski = (EditText) findViewById(R.id.profilTxtIliski);

        kullaniciProfilFoto = (SelectableRoundedImageView) findViewById(R.id.profilImageFotograf);
        fotoBeklemeDialog = new AlertDialog.Builder(this);

        handlerIliski = new Handler();
        handlerCinsiyet = new Handler();

        fbFalGonder = (FloatingActionButton) findViewById(R.id.fbFalGonder);
        botToolbar = (BottomNavigationView) findViewById(R.id.bottom_navigation);


    }

    private void fontHandler(){

        Typeface typeFace= FontCache.get("fonts/MyriadPro.ttf", this);
        Typeface typeFaceBold= FontCache.get("fonts/MyriadProBold.ttf", this);

        toolbarBaslik.setTypeface(typeFaceBold);
        txtProfilBilgilendirme.setTypeface(typeFace);
        profilTxtMail.setTypeface(typeFace);
        profilTxtAd.setTypeface(typeFace);
        profilTxtSoyad.setTypeface(typeFace);
        profilTxtCinsiyet.setTypeface(typeFace);
        profilTxtDogum.setTypeface(typeFace);
        profilTxtIliski.setTypeface(typeFace);
    }

    private void menuleriHazirla() {

        fbFalGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent anasayfaToKafe = new Intent(ProfilActivity.this, KafeActivity.class);
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
                        Intent intentToAnasayfa = new Intent(ProfilActivity.this, AnasayfaActivity.class);
                        intentToAnasayfa.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentToAnasayfa);
                        finish();
                        break;
                    case R.id.menuGelenfalButon:
                        Intent intentToFallarim = new Intent(ProfilActivity.this, GelenfallarActivity.class);
                        intentToFallarim.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentToFallarim);
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    public static void hideKeyboard(@NonNull Activity activity) {
        // focuslanıp-focuslanmadığını kontrol et
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void handler() {

        fontHandler();
        menuleriHazirla();

        setSupportActionBar(profilToolbar);
        getSupportActionBar().setTitle(null); //actionbarda falbook yazıyor, yazıyı silmek için
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //geri butonu koyuyoruz ->parent a

        new TextleriSet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        profilTxtMail.setFocusable(false);
        //mail kısmına bastığında kayıtlı değilse kayıt activity sine gitsin.
        if (mAuth.getCurrentUser().getEmail() == null) {
            kullaniciKayitli = false;
        } else {
            kullaniciKayitli = true;
        }


        if(!kullaniciKayitli){

            //eğer kullanıcı misafir girişi yapmışsa ve edittext e basıyorsa, geçiş yapıp kayıt olmasını sağlayacağız
            profilTxtMail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        Intent profilToMisafirkayit = new Intent(getApplicationContext(), MisafiruyeActivity.class);
                        startActivity(profilToMisafirkayit);
                        finish();
                }
            });
        }else{

            profilTxtMail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        Intent profilToMaildegistir = new Intent(getApplicationContext(), MaildegistirActivity.class);
                        startActivity(profilToMaildegistir);
                        finish();
                }
            });

        }


        profilTxtIliski.setFocusable(false);
        profilTxtIliski.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard(ProfilActivity.this);

                //hızlıca iki kere basarsa 2-3 tane açılmaması için
                if (iliskiCooldown) {

                    iliskiCooldown = false;

                    new AlertDialog.Builder(ProfilActivity.this, R.style.DialogThemeBg)
                            .setSingleChoiceItems(iliskiDurumlari, 0, null)
                            .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    int seciliDurum = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                    profilTxtIliski.setText(iliskiDurumlari[seciliDurum]);
                                }
                            })
                            .show();

                    handlerIliski.postDelayed(runnableIliski = new Runnable() {
                        @Override
                        public void run() {
                            iliskiCooldown = true;
                        }
                    }, 300);
                } else {

                    handlerIliski.removeCallbacksAndMessages(runnableIliski);
                }
            }
        });

        profilTxtDogum.setFocusable(false);

        profilTxtDogum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard(ProfilActivity.this);

                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        ProfilActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAccentColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                dpd.show(getFragmentManager(), "Doğum Tarihiniz");

            }
        });

        profilTxtCinsiyet.setFocusable(false);
        profilTxtCinsiyet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard(ProfilActivity.this);

                //hızlıca iki kere basarsa 2-3 tane açılmaması için
                if (cinsiyetCoolDown) {

                    cinsiyetCoolDown = false;

                    new AlertDialog.Builder(ProfilActivity.this, R.style.DialogThemeBg)
                            .setSingleChoiceItems(arrCinsiyet, 0, null)
                            .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    int seciliDurum = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                    profilTxtCinsiyet.setText(arrCinsiyet[seciliDurum]);
                                }
                            })
                            .show();

                    handlerCinsiyet.postDelayed(runnableCinsiyet = new Runnable() {
                        @Override
                        public void run() {
                            cinsiyetCoolDown = true;
                        }
                    }, 300);
                } else {

                    handlerCinsiyet.removeCallbacksAndMessages(runnableCinsiyet);
                }
            }
        });

        kullaniciProfilFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard(ProfilActivity.this);

                String[] istenilenIzinler = {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                ProfilActivity.super.izinIste(istenilenIzinler, GALERIYE_ERISIM_REQUEST);

            }
        });


    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String tarihDogum = "" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        profilTxtDogum.setText(tarihDogum);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        init();
        handler();

    }


    @Override
    public void izinVerildi(int requestCode) {

        if(requestCode == GALERIYE_ERISIM_REQUEST){

            EasyImage.openChooserWithGallery(ProfilActivity.this, "Profil Fotoğrafı", 0);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menuProfilKaydetButon) {

            if (!profilTxtAd.getText().toString().isEmpty() && !profilTxtSoyad.getText().toString().isEmpty() &&
                    !profilTxtMail.getText().toString().isEmpty() && !profilTxtCinsiyet.getText().toString().isEmpty() &&
                    !profilTxtDogum.getText().toString().isEmpty() && !profilTxtIliski.getText().toString().isEmpty()) {

                strAd = profilTxtAd.getText().toString();
                strSoyad = profilTxtSoyad.getText().toString();
                strMail = profilTxtMail.getText().toString();
                strCinsiyet = profilTxtCinsiyet.getText().toString();
                strDogum = profilTxtDogum.getText().toString();
                strIliski = profilTxtIliski.getText().toString();
                strProfilFoto = "default";

                new BilgileriDegistir().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        strAd, strSoyad, strMail, strCinsiyet, strDogum, strIliski, strProfilFoto);


            } else {
                if(!isFinishing()){

                    new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Bilgiler Eksik")
                            .setContentText("Lütfen bütün bilgilerinizi doldurun!")
                            .setConfirmText("Tamam")
                            .show();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_profilim, menu);
        return true;
    }

    //parametre-progress-result
    private class ProfilfotoYukle extends AsyncTask<byte[], Void, Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fotoBeklemeDialog.setTitle("Fotoğrafınız Yükleniyor");
            fotoBeklemeDialog.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            fotoBeklemeDialog.setCancelable(false);
            fotoBeklemeDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBool) {
            super.onPostExecute(aBool);
        }

        @Override
        protected Boolean doInBackground(byte[]... params) {

            filepathProfilFoto = mResimStorage.child("profil_fotograflari").child(mBulunanKullanici.getUid() + ".jpg");

            byte[] kuculmusFoto_byte = params[0];

            filepathProfilFoto.putBytes(kuculmusFoto_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    //firebase e yüklenen fotografin url sini almak için
                    String kucukFotoDownloadUrl = task.getResult().getDownloadUrl().toString();
                    mDatabaseKullaniciIc.child("profilfoto").setValue(kucukFotoDownloadUrl);

                    if(task.isSuccessful()){

                        fotoYuklenmeSonuc = true;
                        //güncel bilgileri yerleştir
                        new TextleriSet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        //başarıyla yüklendi.

                    }else{
                        fotoYuklenmeSonuc = false;

                        String errorProfilResmi = "";
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            errorProfilResmi = "İnternetinizi kontrol edin!";
                            e.printStackTrace();
                        }

                        Snackbar snacProfilResmiHata = Snackbar
                                .make(profilLayout, errorProfilResmi, Snackbar.LENGTH_LONG);
                        snacProfilResmiHata.show();
                    }

                }
            });

            return fotoYuklenmeSonuc;
        }
    }

    //parametre-progress-result
    private class BilgileriDegistir extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressKayitGuncelle = new SweetAlertDialog(ProfilActivity.this, SweetAlertDialog.SUCCESS_TYPE);
            mProgressKayitGuncelle.setTitleText("Güncelleme Tamamlandı!");
            mProgressKayitGuncelle.setContentText("Bilgileriniz başarıyla güncellendi!");
            mProgressKayitGuncelle.setConfirmText("Tamam");

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(!isFinishing()){

                mProgressKayitGuncelle.show();
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            final String kullaniciID = mBulunanKullanici.getUid();

            mDatabaseKullanici.addValueEventListener(mListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(kullaniciID)) {

                        //kullanıcı bilgilerini güncelle -> bu şekilde yapınca ve setValue diyince boş girdiğimiz bilgileri veri tabanından siliyor
                        //Kullanicilar kullanici = new Kullanicilar(strAd, strSoyad, strMail, strCinsiyet, strIliski, strDogum, null, null);

                        Map<String, Object> updateKullaniciProfilMap = new HashMap<>();
                        updateKullaniciProfilMap.put("isim", strAd);
                        updateKullaniciProfilMap.put("soyisim", strSoyad);
                        updateKullaniciProfilMap.put("mail", strMail);
                        updateKullaniciProfilMap.put("cinsiyet", strCinsiyet);
                        updateKullaniciProfilMap.put("iliski", strIliski);
                        updateKullaniciProfilMap.put("dogum", strDogum);

                        mDatabaseKullaniciIc.updateChildren(updateKullaniciProfilMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //eğer veriyi değiştiremezse
                                if (!task.isSuccessful()) {
                                    mProgressKayitGuncelle.hide();

                                    Snackbar snacBilgiGuncellenemedi = Snackbar
                                            .make(profilLayout, "Bilgileriniz GÜNCELLENEMEDİ", Snackbar.LENGTH_LONG);
                                    snacBilgiGuncellenemedi.show();
                                }
                            }
                        });

                        //güncel bilgileri editable textlere yerleştir
                        new TextleriSet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    } else {
                        mProgressKayitGuncelle.hide();

                        Snackbar snacBilgiKullaniciBulunamadi = Snackbar
                                .make(profilLayout, "Kullanıcı BULUNAMADI", Snackbar.LENGTH_LONG);
                        snacBilgiKullaniciBulunamadi.show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    mProgressKayitGuncelle.hide();
                }
            });

            return null;
        }
    }

    //parametre-progress-result
    private class TextleriSet extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            mDatabaseKullaniciIc.addValueEventListener(mListener2 = new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {

                    //firebase in databese inden verileri oku
                    strAd = (String) dataSnapshot.child("isim").getValue();
                    strSoyad = (String) dataSnapshot.child("soyisim").getValue();
                    strIliski = (String) dataSnapshot.child("iliski").getValue();
                    strDogum = (String) dataSnapshot.child("dogum").getValue();
                    strCinsiyet = (String) dataSnapshot.child("cinsiyet").getValue();
                    strMail = mBulunanKullanici.getEmail(); //auth dan okuyor.

                    //fotoğrafı çekiyoruz.
                    final String image = dataSnapshot.child("profilfoto").getValue().toString();
                    if(!image.equals("default")){
                        Picasso.with(ProfilActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.cat_profile)
                                .into(kullaniciProfilFoto, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {

                                        //tekrardan indir
                                        Picasso.with(ProfilActivity.this).load(image).placeholder(R.drawable.cat_profile).into(kullaniciProfilFoto);
                                    }
                                });
                    }

                    //okuduğun verileri editable text lere yaz.
                    profilTxtAd.setText(strAd);
                    profilTxtSoyad.setText(strSoyad);
                    profilTxtMail.setText(strMail);
                    profilTxtDogum.setText(strDogum);
                    profilTxtIliski.setText(strIliski);
                    profilTxtCinsiyet.setText(strCinsiyet);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;
        }
    }


    @Override
    public void onBackPressed() {

        //handlerIliski.removeCallbacksAndMessages(runnableIliski);
        //handlerCinsiyet.removeCallbacksAndMessages(runnableCinsiyet);

        super.onBackPressed();
    }

    @Override
    protected void onPause() {

        handlerIliski.removeCallbacksAndMessages(runnableIliski);
        handlerCinsiyet.removeCallbacksAndMessages(runnableCinsiyet);

        super.onPause();
    }

    @Override
    protected void onStop() {

        handlerIliski.removeCallbacksAndMessages(runnableIliski);
        handlerCinsiyet.removeCallbacksAndMessages(runnableCinsiyet);

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        handlerIliski.removeCallbacksAndMessages(runnableIliski);
        handlerCinsiyet.removeCallbacksAndMessages(runnableCinsiyet);

        super.onDestroy();

        if(mDatabaseKullaniciIc != null){

            mDatabaseKullaniciIc.removeEventListener(mListener2);
        }

        fbFalGonder.setOnClickListener(null);
        profilTxtMail.setOnClickListener(null);
        profilTxtCinsiyet.setOnClickListener(null);
        profilTxtDogum.setOnClickListener(null);
        profilTxtIliski.setOnClickListener(null);
        kullaniciProfilFoto.setOnClickListener(null);

    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }


            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {

                if (resultCode != RESULT_CANCELED) {

                    Bitmap kucukProfilFoto = null;
                    try {
                        kucukProfilFoto = new Compressor(ProfilActivity.this)
                                .setMaxWidth(128)
                                .setMaxHeight(128)
                                .setQuality(50)
                                .compressToBitmap(imageFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    kucukProfilFoto.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] kuculmusFoto_byte = baos.toByteArray();

                    new ProfilfotoYukle().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, kuculmusFoto_byte);

                    //StorageReference filepathProfilFoto = mResimStorage.child("profil_fotograflari").child(mBulunanKullanici.getUid() + ".jpg");

                    /*
                    filepathProfilFoto.putBytes(kuculmusFoto_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            //firebase e yüklenen fotografin url sini almak için
                            String kucukFotoDownloadUrl = task.getResult().getDownloadUrl().toString();
                            mDatabaseKullaniciIc.child("profilfoto").setValue(kucukFotoDownloadUrl);

                            if(task.isSuccessful()){

                                //güncel bilgileri yerleştir
                                new TextleriSet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                //başarıyla yüklendi.
                            }

                        }
                    });
                    */

                }

            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //cancel edilirse çekilen fotoğrafı sil
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(ProfilActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }

        });

    }



}
