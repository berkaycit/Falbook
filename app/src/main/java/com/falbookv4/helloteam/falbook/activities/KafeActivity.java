package com.falbookv4.helloteam.falbook.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.falbookv4.helloteam.falbook.Manifest;
import com.falbookv4.helloteam.falbook.classes.RuntimeIzinler;
import com.falbookv4.helloteam.falbook.falcisec.FalcilarActivity;
import com.falbookv4.helloteam.falbook.falcisec.GelenfalEvent;
import com.falbookv4.helloteam.falbook.R;
import com.falbookv4.helloteam.falbook.falcisec.TelveEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class KafeActivity extends RuntimeIzinler implements NavigationView.OnNavigationItemSelectedListener, com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

    private static final String TAG = "KafeActivity";
    private static final int KAFE_IZIN_REQUEST_CODE = 300;
    private static final int KAMERA_IZIN_REQUEST_CODE = 305;
    private DrawerLayout genelLayout;
    private ImageButton imgAnasayfa;
    private Toolbar toolbar, toolbarKafe;
    private Button btnFalGonder;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle drawerToggle;
    private BottomNavigationView botToolbar;
    private EditText gondereninAdi, kafeTxtIliski, kafeTxtDogum, kafeTxtCinsiyet;
    private String strAd, strCinsiyet, strDogum, strIliski;
    private final CharSequence iliskiDurumlari[] = {"İlişkisi Yok", "Evli", "Boşanmış", "Dul", "İlişkisi Var", "Nişanlı", "Platonik"};
    private final CharSequence arrCinsiyet[] = {"Kadın", "Erkek"};
    private SelectableRoundedImageView imgKam1, imgKam2, imgKam3;
    private Boolean kam1Durum = false, kam2Durum = false, kam3Durum = false;
    private byte[] kuculmusFoto1_byte, kuculmusFoto2_byte, kuculmusFoto3_byte;
    private Uri kuculmusFoto1_uri, kuculmusFoto2_uri, kuculmusFoto3_uri;
    private Bitmap kucukProfilFoto1 = null, kucukProfilFoto2 = null, kucukProfilFoto3 = null;
    private boolean iliskiCooldown = true, cinsiyetCoolDown = true;
    private Handler handlerIliski, handlerCinsiyet;
    private Runnable runnableIliski, runnableCinsiyet;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseKullanici;
    private TextView navKullaniciIsmi, navKullaniciMail;
    private SelectableRoundedImageView navKullaniciProfilFoto;
    private FloatingActionButton fbFalGonder;
    private boolean fotoYerlesti1, fotoYerlesti2 , fotoYerlesti3;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //kullanıcının giriş yapıp yapmadığını kontrol et
        if (currentUser == null && mDatabaseKullanici == null) {
            giriseGonder();
        }
    }

    private void giriseGonder() {
        Intent anasayfaToGiris = new Intent(KafeActivity.this, GirisActivity.class);
        startActivity(anasayfaToGiris);
        finish();
    }

    public void init(){

        botToolbar = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.anaDrawerLayoutKafe);
        mNavigationView = (NavigationView) findViewById(R.id.anaNavView);

        toolbarKafe = (Toolbar) findViewById(R.id.toolbarKafe); //->actionbar
        toolbar = (Toolbar) findViewById(R.id.genelToolbar);
        //imgAnasayfa = (ImageButton) toolbar.findViewById(R.id.anasafaButon);
        btnFalGonder = (Button) findViewById(R.id.btnGonderFal);

        gondereninAdi = (EditText) findViewById(R.id.gondereninAdi);
        kafeTxtIliski = (EditText) findViewById(R.id.txtKafeiliski);
        kafeTxtCinsiyet = (EditText) findViewById(R.id.txtKafeCinsiyet);
        kafeTxtDogum = (EditText) findViewById(R.id.txtKafeDogum);

        imgKam1 = (SelectableRoundedImageView) findViewById(R.id.kamera1);
        imgKam2 = (SelectableRoundedImageView) findViewById(R.id.kamera2);
        imgKam3 = (SelectableRoundedImageView) findViewById(R.id.kamera3);

        handlerIliski = new Handler();
        handlerCinsiyet = new Handler();

        fbFalGonder = (FloatingActionButton) findViewById(R.id.fbFalGonder);


        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){

            String uid = mAuth.getCurrentUser().getUid();
            mDatabaseKullanici = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid);
            mDatabaseKullanici.keepSynced(true);
        }

    }


    private void menuleriHazirla(){

        fbFalGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!fotoYerlesti1){
                    kam1Durum = true;
                }else if(!fotoYerlesti2){
                    kam2Durum = true;
                }else if(!fotoYerlesti3){
                    kam3Durum = true;
                }

                kameraIzin();
            }
        });

        btnFalGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                strAd = gondereninAdi.getText().toString();
                strCinsiyet = kafeTxtCinsiyet.getText().toString();
                strDogum = kafeTxtDogum.getText().toString();
                strIliski = kafeTxtIliski.getText().toString();

                //fragment ın alabilmesi için yayın açıyorum
                //&& kuculmusFoto1_uri != null && kucukProfilFoto2 !=null && kuculmusFoto3_uri != null
                if(!TextUtils.isEmpty(strAd) && !TextUtils.isEmpty(strCinsiyet) && !TextUtils.isEmpty(strDogum)
                        && !TextUtils.isEmpty(strIliski)) {

                    EventBus.getDefault().postSticky(new GelenfalEvent(strAd, strCinsiyet, strDogum,
                            strIliski, kuculmusFoto1_byte, kuculmusFoto2_byte, kuculmusFoto3_byte));

                    Intent kafeToFalcilar = new Intent(KafeActivity.this, FalcilarActivity.class);
                    startActivity(kafeToFalcilar);
                }else{

                    Snackbar snacBilgiGuncellenemedi = Snackbar
                            .make(mDrawerLayout, "Bilgileriniz EKSİK", Snackbar.LENGTH_LONG);
                    snacBilgiGuncellenemedi.show();
                }
            }
        });

        botToolbar.setSelectedItemId(R.id.menuBosButon);

        botToolbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.menuAnasafaButon:
                        Intent kafeToAnasayfa = new Intent(KafeActivity.this, AnasayfaActivity.class);
                        kafeToAnasayfa.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(kafeToAnasayfa);
                        break;
                    case R.id.menuGelenfalButon:
                        Intent anasayfaToFallarim = new Intent(KafeActivity.this, GelenfallarActivity.class);
                        anasayfaToFallarim.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(anasayfaToFallarim);
                        break;
                }
                return true;
            }
        });

        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbarKafe, R.string.drawer_acilis, R.string.drawer_kapanis); //butonu oluşturuyoruz
        mDrawerLayout.addDrawerListener(drawerToggle); //bu sayede artık ikon senkronize bir şekilde biz menüyü açtıkça kayacak(animasyonlu olarak)
        drawerToggle.syncState(); //unutursan butonu ekranda göstermez

        //tıklanma olayları için navigation view a listener veriyoruz
        mNavigationView.setNavigationItemSelectedListener(this);

    }

    private void kameraIzin(){

        String[] istenilenIzinler = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        KafeActivity.super.izinIste(istenilenIzinler, KAMERA_IZIN_REQUEST_CODE);
    }

    private void kafeIzni(){

        String[] istenilenIzinler = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        KafeActivity.super.izinIste(istenilenIzinler, KAFE_IZIN_REQUEST_CODE);
    }

    @Override
    public void izinVerildi(int requestCode) {
        if(requestCode == KAFE_IZIN_REQUEST_CODE){

            EasyImage.openChooserWithGallery(KafeActivity.this, "Telve Fotoğrafınız", 0);
        }

        if(requestCode == KAMERA_IZIN_REQUEST_CODE){

            EasyImage.openCamera(KafeActivity.this, 0);
        }
    }

    private void kameraHandler(){

        imgKam1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard(KafeActivity.this);
                kafeIzni();
                kam1Durum = true;
            }
        });

        imgKam2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard(KafeActivity.this);
                kafeIzni();
                kam2Durum = true;
            }
        });

        imgKam3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard(KafeActivity.this);
                kafeIzni();
                kam3Durum = true;
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

    private void textHandler(){

        kafeTxtIliski.setFocusable(false);
        kafeTxtIliski.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard(KafeActivity.this);

                //hızlıca iki kere basarsa 2-3 tane açılmaması için
                if (iliskiCooldown) {

                    iliskiCooldown = false;

                    new AlertDialog.Builder(KafeActivity.this, R.style.DialogThemeBg)
                            .setSingleChoiceItems(iliskiDurumlari, 0, null)
                            .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    int seciliDurum = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                    kafeTxtIliski.setText(iliskiDurumlari[seciliDurum]);
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

        kafeTxtDogum.setFocusable(false);
        kafeTxtDogum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard(KafeActivity.this);

                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        KafeActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAccentColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                dpd.show(getFragmentManager(), "Doğum Tarihiniz");

            }
        });

        kafeTxtCinsiyet.setFocusable(false);
        kafeTxtCinsiyet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard(KafeActivity.this);

                //hızlıca iki kere basarsa 2-3 tane açılmaması için
                if (cinsiyetCoolDown) {

                    cinsiyetCoolDown = false;

                    new AlertDialog.Builder(KafeActivity.this, R.style.DialogThemeBg)
                            .setSingleChoiceItems(arrCinsiyet, 0, null)
                            .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    int seciliDurum = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                    kafeTxtCinsiyet.setText(arrCinsiyet[seciliDurum]);
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

    }

    public void handler(){

        kameraHandler();
        menuleriHazirla();
        textHandler();
        navBarDataYerlestir();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_kafe);
        init();
        handler();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.navProfiliniDuzenle:
                Intent anasayfaToProfil = new Intent(KafeActivity.this, ProfilActivity.class);
                startActivity(anasayfaToProfil);
                break;

            case R.id.navSifreDegistir:
                Intent anasayfaToSifredegistir = new Intent(KafeActivity.this, SifredegistirActivity.class);
                startActivity(anasayfaToSifredegistir);
                break;

            case R.id.navFalbookHk:
                Intent anasayfaToFalbookhk = new Intent(KafeActivity.this, FalbookhakkindaActivity.class);
                startActivity(anasayfaToFalbookhk);
                break;

            case R.id.navCikis:

                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Çıkış yapmak istiyor musunuz?")
                        .setContentText("Üye olmadıysanız bütün bilgilerinizi kaybedebilirsiniz!")
                        .setCancelText("Evet, Çıkış Yap")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                mAuth.signOut();
                                giriseGonder();
                            }
                        })
                        .setConfirmText("Hayır")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.cancel();
                            }
                        })
                        .show();

                break;


            default:
                return true;
        }

        //itemlerin üzerine basınca otomatik olarak navigation view ın kapanmasını istiyoruz
        navigationViewKapat();

        return false;
    }

    private void navigationViewKapat(){
        mDrawerLayout.closeDrawer(GravityCompat.START); // başlangıç yönene doğru kapat
    }

    private void navigationViewAc(){
        mDrawerLayout.openDrawer(GravityCompat.START); //başlangıç yönünden itibaren aç
    }

    @Override
    public void onBackPressed(){

        if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
            navigationViewKapat();
            //açık değilse bildiği işlemi yapsın
        else{
            super.onBackPressed();
            Intent kafeToAnasayfaBack = new Intent(KafeActivity.this, AnasayfaActivity.class);
            kafeToAnasayfaBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(kafeToAnasayfaBack);
            finish();
        }
    }

    //Fotoğraflarla sonuca göre işlem yapıyoruz
    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //fotoğrafı alırken hata
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {

                if(kam1Durum) {

                    kam1Durum = false;

                    if (resultCode != RESULT_CANCELED) {

                        kucukProfilFoto1 = null;

                        try {
                            kucukProfilFoto1 = new Compressor(KafeActivity.this)
                                    .setMaxWidth(128)
                                    .setMaxHeight(128)
                                    .setQuality(50)
                                    .compressToBitmap(imageFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //fotografi seçtikten sonra image ata
                        imgKam1.setImageBitmap(Bitmap.createScaledBitmap(kucukProfilFoto1, 128, 128, true));
                        fotoYerlesti1 = true;

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        kucukProfilFoto1.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        kuculmusFoto1_byte = baos.toByteArray();
                    }
                }

                if(kam2Durum) {

                    kam2Durum = false;

                    if (resultCode != RESULT_CANCELED) {

                        kucukProfilFoto2 = null;
                        try {
                            kucukProfilFoto2 = new Compressor(KafeActivity.this)
                                    .setMaxWidth(128)
                                    .setMaxHeight(128)
                                    .setQuality(50)
                                    .compressToBitmap(imageFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //fotografi seçtikten sonra image ata
                        imgKam2.setImageBitmap(Bitmap.createScaledBitmap(kucukProfilFoto2, 128, 128, true));
                        fotoYerlesti2 = true;

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        kucukProfilFoto2.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        kuculmusFoto2_byte = baos.toByteArray();
                    }
                }

                if(kam3Durum) {

                    kam3Durum = false;

                    if (resultCode != RESULT_CANCELED) {

                        kucukProfilFoto3 = null;
                        try {
                            kucukProfilFoto3 = new Compressor(KafeActivity.this)
                                    .setMaxWidth(128)
                                    .setMaxHeight(128)
                                    .setQuality(50)
                                    .compressToBitmap(imageFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //fotografi seçtikten sonra image ata
                        imgKam3.setImageBitmap(Bitmap.createScaledBitmap(kucukProfilFoto3, 128, 128, true));
                        fotoYerlesti3 = true;

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        kucukProfilFoto3.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        kuculmusFoto3_byte = baos.toByteArray();
                    }
                }

            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //cancel edilirse çekilen fotoğrafı sil
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(KafeActivity.this);
                    if (photoFile != null) photoFile.delete();
                    kam1Durum = false; kam2Durum = false; kam3Durum = false;
                    fotoYerlesti1 = false; fotoYerlesti2 = false; fotoYerlesti3 = false;
                }

                if(source == EasyImage.ImageSource.GALLERY){
                    kam1Durum = false; kam2Durum = false; kam3Durum = false;
                    fotoYerlesti1 = false; fotoYerlesti2 = false; fotoYerlesti3 = false;
                }

            }

        });

    }

    //Kütüphanenin Tarih ile ilgili metodu
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String tarihDogum = "" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        kafeTxtDogum.setText(tarihDogum);
    }

    private void navBarDataYerlestir() {

        View header=mNavigationView.getHeaderView(0);
        /*View view=navigationView.inflateHeaderView(R.layout.nav_header_main);*/
        navKullaniciIsmi = (TextView)header.findViewById(R.id.navKullaniciIsim);
        navKullaniciMail = (TextView)header.findViewById(R.id.navKullaniciMail);
        navKullaniciProfilFoto = (SelectableRoundedImageView) header.findViewById(R.id.navProfilePhoto);

        new NavDataYerlestir().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class NavDataYerlestir extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            mDatabaseKullanici.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final String image;
                    String kullaniciIsmi, kullaniciMail;

                    kullaniciIsmi = (String) dataSnapshot.child("isim").getValue();
                    kullaniciMail = (String) dataSnapshot.child("mail").getValue();

                    if(dataSnapshot.child("telve").getValue() == null){

                        giriseGonder();
                        return;
                    }

                    image = dataSnapshot.child("profilfoto").getValue().toString();

                    if (!kullaniciIsmi.isEmpty())
                        navKullaniciIsmi.setText(kullaniciIsmi);
                    if (!kullaniciMail.isEmpty())
                        navKullaniciMail.setText(kullaniciMail);

                    if(!image.equals("default")){
                        Picasso.with(KafeActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.cat_profile)
                                .into(navKullaniciProfilFoto, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {

                                        //tekrardan indir
                                        Picasso.with(KafeActivity.this).load(image).placeholder(R.drawable.cat_profile)
                                                .into(navKullaniciProfilFoto);
                                    }
                                });
                    }



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;
        }
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static File getCameraPicLocation(Context context) throws IOException {

        File cacheDir = context.getCacheDir();

        if (isExternalStorageWritable()) {
            cacheDir = context.getExternalCacheDir();
        }

        File dir = new File(cacheDir, "EasyImage");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static void clearCameraPic(Context context) {
        List<File> tempFiles = new ArrayList<>();
        File[] files = new File[0];
        try {
            files = getCameraPicLocation(context).listFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (File file : files) {
            file.delete();
        }
    }


    @Override
    protected void onDestroy() {
        clearCameraPic(this);
        //EasyImage.clearPublicTemp(getApplicationContext());
        super.onDestroy();
    }
}
