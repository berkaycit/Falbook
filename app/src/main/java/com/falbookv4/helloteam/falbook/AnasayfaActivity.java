package com.falbookv4.helloteam.falbook;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.media.Image;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class AnasayfaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbarAnasayfa, toolbar;
    private FloatingActionButton fbFalGonder;
    private ConstraintLayout consToolBar;
    private ImageButton btnAnasayfaButon, btnFalBaktir;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle drawerToggle;
    private BottomNavigationView botToolbar;
    private ImageView anaBtnFalBaktir, anaBtnTelveSatinal, anaBtnTelveKazan, anaBtnSSS, anaBtnIletisim, anaBtnKullanim;


    public void init(){
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

    }

    public void anaBtnFonk(){
        anaBtnFalBaktir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent anasayfaToKafe = new Intent(AnasayfaActivity.this, KafeActivity.class);
                startActivity(anasayfaToKafe);
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

    public void handler(){

        anaBtnFonk();

        fbFalGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent anasayfaToKafe = new Intent(AnasayfaActivity.this, KafeActivity.class);
                finish();
                startActivity(anasayfaToKafe);
            }
        });



        botToolbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.menuAnasafaButon:
                        break;
                    case R.id.menuGelenfalButon:
                        Intent anasayfaToFallarim = new Intent(AnasayfaActivity.this, GelenfallarActivity.class);
                        startActivity(anasayfaToFallarim);
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

        switch (item.getItemId()){

            case R.id.navProfiliniDuzenle:
                Intent anasayfaToProfil = new Intent(AnasayfaActivity.this, ProfilActivity.class);
                startActivity(anasayfaToProfil);
                break;

            case R.id.navSifreDegistir:
                Intent anasayfaToSifredegistir = new Intent(AnasayfaActivity.this, SifredegistirActivity.class);
                startActivity(anasayfaToSifredegistir);
                break;

            case R.id.navFalbookHk:
                Intent anasayfaToFalbookhk = new Intent(AnasayfaActivity.this, FalbookhakkindaActivity.class);
                startActivity(anasayfaToFalbookhk);
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
        else
            super.onBackPressed();
    }



}