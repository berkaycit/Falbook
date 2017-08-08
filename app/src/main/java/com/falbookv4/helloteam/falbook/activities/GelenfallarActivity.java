package com.falbookv4.helloteam.falbook.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.falbookv4.helloteam.falbook.R;
import com.falbookv4.helloteam.falbook.classes.Fal;
import com.falbookv4.helloteam.falbook.classes.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class GelenfallarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView gelenFalRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle drawerToggle;
    private BottomNavigationView botToolbar;
    private Toolbar toolbarGelenfal;
    private FloatingActionButton fbFalGonder;
    private CollapsingToolbarLayout colToolbar;
    private boolean falYorumlandi;
    private SweetAlertDialog progressFalSil;
    private DatabaseReference mDatabase, mDatabaseKullanici;
    private FirebaseUser mBulunanKullanici;
    private TextView navKullaniciIsmi, navKullaniciMail;
    private SelectableRoundedImageView navKullaniciProfilFoto;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ValueEventListener mListener;
    private TextView toolbarBaslik;

    public void init(){

        toolbarBaslik = (TextView) findViewById(R.id.fallarim_toolbar_baslik);
        progressFalSil = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);

        toolbarGelenfal = (Toolbar) findViewById(R.id.toolbarGelenFallar);

        fbFalGonder = (FloatingActionButton) findViewById(R.id.fbFalGonder);
        botToolbar = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.anaDrawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.anaNavView);

        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        gelenFalRecyclerView = (RecyclerView) findViewById(R.id.gelenFalRecyclerView);

        //kullanıcı yönetmek için
        mAuth = FirebaseAuth.getInstance();

        //(current user)
        mBulunanKullanici = mAuth.getCurrentUser();

        //database açmak,
        if(mAuth.getCurrentUser()!=null){

            String uid = mAuth.getCurrentUser().getUid();
            mDatabaseKullanici = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid);
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Fal");

            mDatabaseKullanici.keepSynced(true);

            //offline olduğu durumlar için
            mDatabaseKullanici.keepSynced(true);
            mDatabase.keepSynced(true);
        }

        //layout manager oluşturuyoruz
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        //recyclerview a istediğimiz kuralları uyguluyoruz
        gelenFalRecyclerView.setLayoutManager(layoutManager);

    }

    private void fontHandler(){

        Typeface typeFace= Typeface.createFromAsset(getAssets(),"fonts/MyriadPro.ttf");
        Typeface typeFaceBold= Typeface.createFromAsset(getAssets(),"fonts/MyriadProBold.ttf");

        toolbarBaslik.setTypeface(typeFaceBold);


    }

    @Override
    protected void onStart() {
        super.onStart();

        //kullanıcının giriş yapıp yapmadığını kontrol et
        if (mBulunanKullanici == null && mDatabaseKullanici == null) {
            giriseGonder();
        }

        //recycler view(falList) için adapter
        FirebaseRecyclerAdapter<Fal, FalViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Fal, FalViewHolder>(

                Fal.class,
                R.layout.card_gelenfal,
                FalViewHolder.class,
                mDatabase.child(mBulunanKullanici.getUid())
        ) {

            @Override
            protected void populateViewHolder(final FalViewHolder viewHolder, Fal model, final int position) {

                //postun id si ni string olarak alıyoruz (push ettiğimiz, bilmediğimiz id)
                final String post_key = getRef(position).getKey();

                //RW de ki bilgiler
                viewHolder.setKullanici(model.getIsim());
                if(model.getFal_yorumu() != null && model.getFal_yorumu().isEmpty() ){
                    viewHolder.setCardYorum("Falınız Yorumlanıyor");
                    falYorumlandi = false;
                }else{
                    viewHolder.setCardYorum(model.getFal_yorumu());
                    falYorumlandi = true;
                }
                viewHolder.setGonderilmeTarihi(model.getGonderilme_tarihi());
                viewHolder.setFoto(getApplicationContext(), model.getFoto1());

                //View ın üzerine basılınca;
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(GelenfalActivity.this, post_key, Toast.LENGTH_LONG).show();

                        //Fal detay sayfasına gitsin
                        Intent intentToFaldetay = new Intent(GelenfallarActivity.this, FaldetayActivity.class);
                        //fal detay sayfasına giderken post_key bilgisini de yanında götürsün
                        intentToFaldetay.putExtra("fal_id", post_key);
                        startActivity(intentToFaldetay);
                    }
                });

                viewHolder.cardBtnCop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(GelenfallarActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(GelenfallarActivity.this);
                        }
                        builder.setTitle("Falı SİL!")
                                .setMessage("Falı silmek istediğinize emin misiniz?")
                                .setPositiveButton(R.string.evet_sil, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //fal yorumlandıysa sil
                                        if(falYorumlandi){

                                            DatabaseReference itemRef = getRef(position);
                                            itemRef.removeValue();
                                        }
                                    }
                                })
                                .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    }
                });


            }

        };

        //adepter ı bağlıyoruz
        gelenFalRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    private void giriseGonder() {
        Intent anasayfaToGiris = new Intent(GelenfallarActivity.this, GirisActivity.class);
        startActivity(anasayfaToGiris);
        finish();
    }

    public static class FalViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private ImageButton cardBtnCop;

        //yapılandırıcı -> class ın içinde kullanabilmek için view
        public FalViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            cardBtnCop = (ImageButton) itemView.findViewById(R.id.cardBtnCop);

        }

        public void setCardYorum(String fal_yorumu){

            TextView post_cardYorum = (TextView) mView.findViewById(R.id.cardFalYorumu);
            post_cardYorum.setText(fal_yorumu);
        }

        public void setKullanici(String kullanici){

            TextView post_kullanici = (TextView) mView.findViewById(R.id.cardKullaniciIsmi);
            post_kullanici.setText(kullanici);

        }

        public void setGonderilmeTarihi(String gonderilmeTarihi){

            TextView post_gonderilmeTarihi = (TextView) mView.findViewById(R.id.cardTarihText);
            post_gonderilmeTarihi.setText(gonderilmeTarihi);
        }


        //fotografi bastir
        public void setFoto(final Context ctx, final String image) {

            final ImageView post_image = (ImageView) mView.findViewById(R.id.cardKahveFotografi);

            post_image.setFitsSystemWindows(true);
            post_image.setScaleType(ImageView.ScaleType.CENTER_CROP);

            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    Picasso.with(ctx).load(image).into(post_image);

                }
            });
        }
    }



    public void handler(){

        fontHandler();
        menuleriHazirla();
        navBarDataYerlestir();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gelenfallar);
        init();
        handler();
    }


    private void menuleriHazirla(){

        fbFalGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent anasayfaToKafe = new Intent(GelenfallarActivity.this, KafeActivity.class);
                anasayfaToKafe.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(anasayfaToKafe);
                finish();
            }
        });


        botToolbar.setSelectedItemId(R.id.menuGelenfalButon);

        botToolbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.menuAnasafaButon:
                        Intent gelenfalToAnasayfa = new Intent(GelenfallarActivity.this, AnasayfaActivity.class);
                        gelenfalToAnasayfa.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(gelenfalToAnasayfa);
                        finish();
                        break;
                    case R.id.menuGelenfalButon:
                        break;
                }
                return true;
            }
        });

        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbarGelenfal, R.string.drawer_acilis, R.string.drawer_kapanis); //butonu oluşturuyoruz
        mDrawerLayout.addDrawerListener(drawerToggle); //bu sayede artık ikon senkronize bir şekilde biz menüyü açtıkça kayacak(animasyonlu olarak)
        drawerToggle.syncState(); //unutursan butonu ekranda göstermez

        //tıklanma olayları için navigation view a listener veriyoruz
        mNavigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.navProfiliniDuzenle:
                Intent anasayfaToProfil = new Intent(GelenfallarActivity.this, ProfilActivity.class);
                startActivity(anasayfaToProfil);
                break;

            case R.id.navSifreDegistir:
                Intent anasayfaToSifredegistir = new Intent(GelenfallarActivity.this, SifredegistirActivity.class);
                startActivity(anasayfaToSifredegistir);
                break;

            case R.id.navFalbookHk:
                Intent anasayfaToFalbookhk = new Intent(GelenfallarActivity.this, FalbookhakkindaActivity.class);
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
                Intent anasayfaToIletisim = new Intent(GelenfallarActivity.this, IletisimActivity.class);
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
                                Utils.deleteCache(GelenfallarActivity.this);
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
            Intent fallarToAnasayfaBack = new Intent(GelenfallarActivity.this, AnasayfaActivity.class);
            fallarToAnasayfaBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(fallarToAnasayfaBack);
            finish();
        }
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

            mDatabaseKullanici.addValueEventListener(mListener = new ValueEventListener() {
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
                        Picasso.with(GelenfallarActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.cat_profile)
                                .into(navKullaniciProfilFoto, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {

                                        //tekrardan indir
                                        Picasso.with(GelenfallarActivity.this).load(image).placeholder(R.drawable.cat_profile)
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
    protected void onDestroy() {
        super.onDestroy();

        if(mDatabaseKullanici != null){

            mDatabaseKullanici.removeEventListener(mListener);
        }

        fbFalGonder.setOnClickListener(null);
        fbFalGonder.setImageDrawable(null);

    }
}
