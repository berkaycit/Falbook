package com.falbookv4.helloteam.falbook.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.falbookv4.helloteam.falbook.R;
import com.falbookv4.helloteam.falbook.classes.Sabitler;
import com.falbookv4.helloteam.falbook.classes.Utils;
import com.falbookv4.helloteam.falbook.classes.SecurePreferences;
import com.falbookv4.helloteam.falbook.falcisec.TelveEvent;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;
import pl.aprilapps.easyphotopicker.EasyImage;

public class AnasayfaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int REQUEST_INVITE = 900;
    private Toolbar toolbarAnasayfa, toolbar;
    private FloatingActionButton fbFalGonder;
    private ConstraintLayout consToolBar;
    private ImageButton btnAnasayfaButon, btnFalBaktir;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle drawerToggle;
    private BottomNavigationView botToolbar;
    private SelectableRoundedImageView navKullaniciProfilFoto;
    private ImageView anaBtnFalBaktir, anaBtnTelveSatinal, anaBtnTelveKazan, anaBtnSSS, anaBtnIletisim, anaBtnKullanim;
    private FirebaseAuth mAuth;
    private TextView navKullaniciIsmi, navKullaniciMail;
    private int telveSayisi;
    private DatabaseReference mDatabaseKullanici;
    private TextView txtTelveSayisi, txtFalBaktir, txtTelveSatinal, txtTelveKazan, txtSSS, txtIletisim, txtKullanim, toolbarBaslik;
    private String strFalSayisi, strTelveSayisi;
    private ValueEventListener mListener;

    public void init() {


        botToolbar = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.anaDrawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.anaNavView);

        toolbarAnasayfa = (Toolbar) findViewById(R.id.toolbarAnasayfa); //->actionbar
        fbFalGonder = (FloatingActionButton) findViewById(R.id.fbFalGonder);

        toolbar = (Toolbar) findViewById(R.id.genelToolbar);

        anaBtnFalBaktir = (ImageView) findViewById(R.id.anaBtnFalBaktir);
        anaBtnTelveSatinal = (ImageView) findViewById(R.id.anaBtnTelveSatinal);
        anaBtnTelveKazan = (ImageView) findViewById(R.id.anaBtnTelveKazan);
        anaBtnSSS = (ImageView) findViewById(R.id.anaBtnSSS);
        anaBtnIletisim = (ImageView) findViewById(R.id.anaBtnIletisim);
        anaBtnKullanim = (ImageView) findViewById(R.id.anaBtnKullanim);

        txtTelveSayisi = (TextView) findViewById(R.id.telveSayisiTexts);
        txtFalBaktir = (TextView) findViewById(R.id.falbaktirText);
        txtTelveKazan = (TextView) findViewById(R.id.telvekazanText);
        txtSSS = (TextView) findViewById(R.id.sssText);
        txtIletisim = (TextView) findViewById(R.id.iletisimText);
        txtKullanim = (TextView) findViewById(R.id.kullanimText);
        toolbarBaslik = (TextView) findViewById(R.id.anasayfa_toolbar_baslik);


        //->Firebase
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){

            String uid = mAuth.getCurrentUser().getUid();
            mDatabaseKullanici = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid);
            mDatabaseKullanici.keepSynced(true);
        }

    }

    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder("Paylaş")
                .setMessage("Falbook uygulamasını denemeni tavsiye ederim!")
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                //.setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                .setCallToActionText("İndir")
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    private void fontHandler(){

        Typeface typeFace= Typeface.createFromAsset(getAssets(),"fonts/MyriadProBold.ttf");
        txtFalBaktir.setTypeface(typeFace);
        txtTelveSayisi.setTypeface(typeFace);
        txtTelveKazan.setTypeface(typeFace);
        txtSSS.setTypeface(typeFace);
        txtIletisim.setTypeface(typeFace);
        txtKullanim.setTypeface(typeFace);
        toolbarBaslik.setTypeface(typeFace);

    }

    public void handler() {

        anaBtnFonk();
        fontHandler();

        if(mAuth.getCurrentUser()!= null){

            navBarDataYerlestir();
        }

        //else{
        //    telveSayisi = -1;
        //}

        //telve sayısını gönder.
        //EventBus.getDefault().postSticky(new TelveEvent(telveSayisi));

        fbFalGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent anasayfaToKafe = new Intent(AnasayfaActivity.this, KafeActivity.class);
                anasayfaToKafe.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(anasayfaToKafe);
                finish();
            }
        });


        botToolbar.setSelectedItemId(R.id.menuAnasafaButon);

        botToolbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.menuAnasafaButon:
                        break;
                    case R.id.menuGelenfalButon:
                        Intent anasayfaToFallarim = new Intent(AnasayfaActivity.this, GelenfallarActivity.class);
                        anasayfaToFallarim.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(anasayfaToFallarim);
                        finish();
                        break;
                }
                return true;
            }
        });

        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbarAnasayfa, R.string.drawer_acilis, R.string.drawer_kapanis); //butonu oluşturuyoruz
        mDrawerLayout.addDrawerListener(drawerToggle); //bu sayede artık ikon senkronize bir şekilde biz menüyü açtıkça kayacak(animasyonlu olarak)
        drawerToggle.syncState(); //unutursan butonu ekranda göstermez

        //tıklanma olayları için navigation view a listener veriyoruz
        mNavigationView.setNavigationItemSelectedListener(this);

    }

    private void navBarDataYerlestir() {

            View header=mNavigationView.getHeaderView(0);
            /*View view=navigationView.inflateHeaderView(R.layout.nav_header_main);*/
            navKullaniciIsmi = (TextView)header.findViewById(R.id.navKullaniciIsim);
            navKullaniciMail = (TextView)header.findViewById(R.id.navKullaniciMail);
            navKullaniciProfilFoto = (SelectableRoundedImageView) header.findViewById(R.id.navProfilePhoto);

            new NavDataYerlestir().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //kullanıcının giriş yapıp yapmadığını kontrol et
        if (currentUser == null && mDatabaseKullanici == null) {
            giriseGonder();
        }else{

            SecurePreferences preferences = new SecurePreferences(getApplicationContext(), "difs", "150", true);
            preferences.put(Sabitler.TELVE_SAYISI_MISAFIR, strTelveSayisi);
        }
    }

    private void giriseGonder() {
        Intent anasayfaToGiris = new Intent(AnasayfaActivity.this, GirisActivity.class);
        startActivity(anasayfaToGiris);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //splash screen temasını belirtmiştik manifest de burada tekrardan normal temaya döndürüyoruz.
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_anasayfa);
        init();
        handler();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.navProfiliniDuzenle:
                Intent anasayfaToProfil = new Intent(AnasayfaActivity.this, ProfilActivity.class);
                anasayfaToProfil.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(anasayfaToProfil);
                break;

            case R.id.navSifreDegistir:
                Intent anasayfaToSifredegistir = new Intent(AnasayfaActivity.this, SifredegistirActivity.class);
                anasayfaToSifredegistir.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(anasayfaToSifredegistir);
                break;

            case R.id.navFalbookHk:
                Intent anasayfaToFalbookhk = new Intent(AnasayfaActivity.this, FalbookhakkindaActivity.class);
                anasayfaToFalbookhk.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(anasayfaToFalbookhk);
                break;

            case R.id.navPaylas:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.invitation_deep_link));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                //onInviteClicked();
                break;

            case R.id.navTeknik:
                Intent anasayfaToIletisim = new Intent(AnasayfaActivity.this, IletisimActivity.class);
                anasayfaToIletisim.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(anasayfaToIletisim);
                break;

            case R.id.navPuanVer:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.falbookv4.helloteam.falbook"));
                startActivity(intent);
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
                                //Utils.deleteCache(AnasayfaActivity.this);

                                //çıkarken telve sayısını gönderiyorum ki tekrar girdiğinde aynı telve sayısından devam etsin
                                //EventBus.getDefault().postSticky(new TelveEvent(telveSayisi));

                                SecurePreferences preferences = new SecurePreferences(getApplicationContext(), "difs", "150", true);
                                preferences.put(Sabitler.TELVE_SAYISI_MISAFIR, strTelveSayisi);

                                if(mAuth.getCurrentUser().getEmail() != null){

                                    preferences.put(Sabitler.KULLANICI_MAIL, mAuth.getCurrentUser().getEmail());
                                }

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

    private void navigationViewKapat() {
        mDrawerLayout.closeDrawer(GravityCompat.START); // başlangıç yönene doğru kapat
    }

    private void navigationViewAc() {
        mDrawerLayout.openDrawer(GravityCompat.START); //başlangıç yönünden itibaren aç
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            navigationViewKapat();
            //açık değilse bildiği işlemi yapsın
        else{
            super.onBackPressed();
            //bütün stackları sil ve programdan çık
            finishAffinity();
        }
    }

    public void anaBtnFonk() {
        anaBtnFalBaktir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent anasayfaToKafe = new Intent(AnasayfaActivity.this, KafeActivity.class);
                anasayfaToKafe.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(anasayfaToKafe);
                finish();
            }
        });

        anaBtnTelveSatinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent anasayfaToSatinal = new Intent(AnasayfaActivity.this, SatinalActivity.class);
                anasayfaToSatinal.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(anasayfaToSatinal);
            }
        });

        anaBtnSSS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent anasayfaToSSS = new Intent(AnasayfaActivity.this, SSSActivity.class);
                anasayfaToSSS.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(anasayfaToSSS);
            }
        });

        anaBtnIletisim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent anasayfaToIletisim = new Intent(AnasayfaActivity.this, IletisimActivity.class);
                anasayfaToIletisim.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(anasayfaToIletisim);
            }
        });

        anaBtnTelveKazan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent anasayfaToSatinal = new Intent(AnasayfaActivity.this, SatinalActivity.class);
                anasayfaToSatinal.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(anasayfaToSatinal);
            }
        });

        anaBtnKullanim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private class NavDataYerlestir extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {

            mDatabaseKullanici.addValueEventListener( mListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final String image;
                    String kullaniciIsmi, kullaniciMail;

                    kullaniciIsmi = (String) dataSnapshot.child("isim").getValue();
                    kullaniciMail = (String) dataSnapshot.child("mail").getValue();
                    //Sadece ANASAYFADA
                    if(dataSnapshot.child("telve").getValue() == null){

                        giriseGonder();
                        return;
                    }

                    telveSayisi = ((Long) dataSnapshot.child("telve").getValue()).intValue();
                    strFalSayisi = "" + telveSayisi + " TELVENİZ\nVAR" + "";
                    strTelveSayisi = "" + telveSayisi;
                    txtTelveSayisi.setText(strFalSayisi);

                    //EventBus.getDefault().postSticky(new TelveEvent(telveSayisi));

                    image = dataSnapshot.child("profilfoto").getValue().toString();

                    if (!kullaniciIsmi.isEmpty())
                        navKullaniciIsmi.setText(kullaniciIsmi);
                    if (!kullaniciMail.isEmpty())
                        navKullaniciMail.setText(kullaniciMail);

                    if(!image.equals("default")){
                        Picasso.with(AnasayfaActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.cat_profile)
                                .into(navKullaniciProfilFoto, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {

                                        //tekrardan indir
                                        Picasso.with(AnasayfaActivity.this).load(image).placeholder(R.drawable.cat_profile)
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d("AnasayfaActivity", "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }
    }



    @Override
    protected void onDestroy() {

        super.onDestroy();
        if(mDatabaseKullanici != null){
            mDatabaseKullanici.removeEventListener(mListener);
        }

        fbFalGonder.setOnClickListener(null);
        anaBtnFalBaktir.setOnClickListener(null);
        anaBtnTelveSatinal.setOnClickListener(null);
        anaBtnSSS.setOnClickListener(null);
        anaBtnIletisim.setOnClickListener(null);
        anaBtnTelveKazan.setOnClickListener(null);
        anaBtnKullanim.setOnClickListener(null);

        fbFalGonder.setImageDrawable(null);
        anaBtnTelveKazan.setImageDrawable(null);
        anaBtnTelveSatinal.setImageDrawable(null);
        anaBtnSSS.setImageDrawable(null);
        anaBtnIletisim.setImageDrawable(null);
        anaBtnTelveKazan.setImageDrawable(null);
        anaBtnKullanim.setImageDrawable(null);

    }
}