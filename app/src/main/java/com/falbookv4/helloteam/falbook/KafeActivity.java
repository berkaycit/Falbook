package com.falbookv4.helloteam.falbook;

import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class KafeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ImageButton imgAnasayfa;
    private Toolbar toolbar, toolbarKafe;
    private Button btnFalGonder;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle drawerToggle;
    private BottomNavigationView botToolbar;

    public void init(){

        botToolbar = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.anaDrawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.anaNavView);

        toolbarKafe = (Toolbar) findViewById(R.id.toolbarKafe); //->actionbar
        toolbar = (Toolbar) findViewById(R.id.genelToolbar);
        //imgAnasayfa = (ImageButton) toolbar.findViewById(R.id.anasafaButon);
        btnFalGonder = (Button) findViewById(R.id.btnGonderFal);
    }


    private void menuleriHazirla(){

        btnFalGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent kafeToFalcilar = new Intent(KafeActivity.this, FalcilarActivity.class);
                startActivity(kafeToFalcilar);
            }
        });

        botToolbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.menuAnasafaButon:
                        Intent kafeToAnasayfa = new Intent(KafeActivity.this, AnasayfaActivity.class);
                        startActivity(kafeToAnasayfa);
                        break;
                    case R.id.menuGelenfalButon:
                        Intent anasayfaToFallarim = new Intent(KafeActivity.this, GelenfallarActivity.class);
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

    public void handler(){

        menuleriHazirla();



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

        switch (item.getItemId()){

            case R.id.navProfiliniDuzenle:
                Intent kafeToProfil = new Intent(KafeActivity.this, ProfilActivity.class);
                startActivity(kafeToProfil);
                break;

            case R.id.navSifreDegistir:
                Intent kafeToSifredegistir = new Intent(KafeActivity.this, SifredegistirActivity.class);
                startActivity(kafeToSifredegistir);
                break;

            case R.id.navFalbookHk:
                Intent kafeToFalbookhk = new Intent(KafeActivity.this, FalbookhakkindaActivity.class);
                startActivity(kafeToFalbookhk);
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
