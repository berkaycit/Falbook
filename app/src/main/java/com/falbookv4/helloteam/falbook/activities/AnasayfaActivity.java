package com.falbookv4.helloteam.falbook.activities;


import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

import org.greenrobot.eventbus.EventBus;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AnasayfaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
    private TextView txtTelveSayisi;
    private String strFalSayisi;

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


        //->Firebase
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){

            String uid = mAuth.getCurrentUser().getUid();
            mDatabaseKullanici = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid);
            mDatabaseKullanici.keepSynced(true);
        }

    }

    public void handler() {

        anaBtnFonk();

        if(mAuth.getCurrentUser()!= null){

            navBarDataYerlestir();
        }

        fbFalGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent anasayfaToKafe = new Intent(AnasayfaActivity.this, KafeActivity.class);
                anasayfaToKafe.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(anasayfaToKafe);
                finish();
            }
        });


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
                startActivity(anasayfaToKafe);
                finish();
            }
        });

        anaBtnTelveSatinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent anasayfaToSatinal = new Intent(AnasayfaActivity.this, SatinalActivity.class);
                startActivity(anasayfaToSatinal);
            }
        });

        anaBtnSSS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent anasayfaToSSS = new Intent(AnasayfaActivity.this, SSSActivity.class);
                startActivity(anasayfaToSSS);
            }
        });

        anaBtnIletisim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent anasayfaToIletisim = new Intent(AnasayfaActivity.this, IletisimActivity.class);
                startActivity(anasayfaToIletisim);
            }
        });

        anaBtnTelveKazan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

            mDatabaseKullanici.addValueEventListener(new ValueEventListener() {
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
                    txtTelveSayisi.setText(strFalSayisi);

                    EventBus.getDefault().postSticky(new TelveEvent(telveSayisi));

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


}