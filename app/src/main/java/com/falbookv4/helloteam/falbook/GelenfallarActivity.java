package com.falbookv4.helloteam.falbook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.falbookv4.helloteam.falbook.Activities.AnasayfaActivity;
import com.falbookv4.helloteam.falbook.Activities.FalbookhakkindaActivity;
import com.falbookv4.helloteam.falbook.Activities.KafeActivity;
import com.falbookv4.helloteam.falbook.Activities.ProfilActivity;
import com.falbookv4.helloteam.falbook.Activities.SifredegistirActivity;

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

    public void init(){

        toolbarGelenfal = (Toolbar) findViewById(R.id.toolbarGelenFallar);

        fbFalGonder = (FloatingActionButton) findViewById(R.id.fbFalGonder);
        botToolbar = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.anaDrawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.anaNavView);

        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        gelenFalRecyclerView = (RecyclerView) findViewById(R.id.gelenFalRecyclerView);

    }

    private void menuleriHazirla(){

        fbFalGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent anasayfaToKafe = new Intent(GelenfallarActivity.this, KafeActivity.class);
                startActivity(anasayfaToKafe);
            }
        });

        botToolbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.menuAnasafaButon:
                        Intent gelenfalToAnasayfa = new Intent(GelenfallarActivity.this, AnasayfaActivity.class);
                        finish();
                        startActivity(gelenfalToAnasayfa);
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


    public void handler(){

        menuleriHazirla();

        layoutManager = new LinearLayoutManager(this);
        gelenFalRecyclerView.setLayoutManager(layoutManager);

        adapter = new GelenFalAdapter();
        gelenFalRecyclerView.setAdapter(adapter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gelenfallar);
        init();
        handler();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.navProfiliniDuzenle:
                Intent fallarToProfil = new Intent(GelenfallarActivity.this, ProfilActivity.class);
                startActivity(fallarToProfil);
                break;

            case R.id.navSifreDegistir:
                Intent fallarToSifredegistir = new Intent(GelenfallarActivity.this, SifredegistirActivity.class);
                startActivity(fallarToSifredegistir);
                break;

            case R.id.navFalbookHk:
                Intent fallarToFalbookhk = new Intent(GelenfallarActivity.this, FalbookhakkindaActivity.class);
                startActivity(fallarToFalbookhk);
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
