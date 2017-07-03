package com.falbookv4.helloteam.falbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

public class ProfilActivity extends AppCompatActivity {

    private Toolbar profilToolbar;

    public void init(){

        profilToolbar = (Toolbar) findViewById(R.id.toolbarProfil);
    }

    public void handler(){

        setSupportActionBar(profilToolbar);
        getSupportActionBar().setTitle(null); //actionbarda falbook yazıyor, yazıyı silmek için
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //geri butonu koyuyoruz ->parent a

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        init();
        handler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_profilim, menu);
        return true;
    }

}
