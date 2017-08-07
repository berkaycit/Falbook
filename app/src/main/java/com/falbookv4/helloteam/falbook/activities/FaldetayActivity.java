package com.falbookv4.helloteam.falbook.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.falbookv4.helloteam.falbook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FaldetayActivity extends AppCompatActivity {


    private CollapsingToolbarLayout colToolbar;
    private Toolbar faldetayToolbar;
    private DatabaseReference mDatabaseFal;
    private FirebaseAuth mAuth;
    private FirebaseUser mBulunanKullanici;
    private String strFalKey;
    private TextView bakilaninIsmi, falDetayYorum, falDetayTarih;
    private ImageView falDetayFoto;
    private String baktiranKisi, strCinsiyet = "", strBaktiran, strFalFoto1, falAciklamasi, strTarih;
    private FloatingActionButton fbFalGonder;
    private BottomNavigationView botToolbar;
    private ValueEventListener mListener;
    private FloatingActionButton fbPaylas;

    public void init(){


        Typeface typeFace= Typeface.createFromAsset(getAssets(),"fonts/MyriadPro.ttf");
        Typeface typeFaceBold= Typeface.createFromAsset(getAssets(),"fonts/MyriadProBold.ttf");

        colToolbar = (CollapsingToolbarLayout)findViewById(R.id.faldetayColToolbar);
        colToolbar.setTitle("FALLARIM");
        fbPaylas = (FloatingActionButton) findViewById(R.id.fbPaylas);

        faldetayToolbar = (Toolbar) findViewById(R.id.faldetayToolbar);

        falDetayFoto = (ImageView) findViewById(R.id.faldetayFalFoto);
        bakilaninIsmi = (TextView) findViewById(R.id.faldetayContainBakilanIsmi);
        falDetayYorum = (TextView) findViewById(R.id.faldetayContainFalYorumu);
        falDetayTarih = (TextView) findViewById(R.id.txtFalDetayTarih);

        falDetayYorum.setTypeface(typeFace);
        bakilaninIsmi.setTypeface(typeFaceBold);

        //kullanıcı yönetmek için
        mAuth = FirebaseAuth.getInstance();

        //(current user)
        mBulunanKullanici = mAuth.getCurrentUser();

        mDatabaseFal = FirebaseDatabase.getInstance().getReference().child("Fal");

        //hangi fal a bastığını anlamak için yönlendiren activity den key bilgisini alıcaz
        strFalKey = getIntent().getExtras().getString("fal_id");

        fbFalGonder = (FloatingActionButton) findViewById(R.id.fbFalGonder);
        botToolbar = (BottomNavigationView) findViewById(R.id.bottom_navigation);


    }

    private void menuleriHazirla() {

        fbPaylas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, falAciklamasi);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));

            }
        });

        fbFalGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent anasayfaToKafe = new Intent(FaldetayActivity.this, KafeActivity.class);
                anasayfaToKafe.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(anasayfaToKafe);
                finish();
            }
        });

        botToolbar.setSelectedItemId(R.id.menuBosButon);

        botToolbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.menuAnasafaButon:
                        Intent intentToAnasayfa = new Intent(FaldetayActivity.this, AnasayfaActivity.class);
                        intentToAnasayfa.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentToAnasayfa);
                        finish();
                        break;
                    case R.id.menuGelenfalButon:
                        Intent intentToFallarim = new Intent(FaldetayActivity.this, GelenfallarActivity.class);
                        intentToFallarim.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentToFallarim);
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    public void handler(){

        menuleriHazirla();

        //toolbar ı action bar ın özelliklerinden faydalanmasını sağlıyoruz
        setSupportActionBar(faldetayToolbar);
        //parent ı na geri tuşu koymuş oluyoruz, action bar özelliği sayesinde->toolbar a
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String kullaniciID = mBulunanKullanici.getUid();

        mDatabaseFal.child(kullaniciID).child(strFalKey).addValueEventListener(mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                strBaktiran = (String) dataSnapshot.child("isim").getValue();
                strFalFoto1 = (String) dataSnapshot.child("foto1").getValue();
                falAciklamasi = (String) dataSnapshot.child("fal_yorumu").getValue();
                strCinsiyet = (String) dataSnapshot.child("cinsiyet").getValue();
                strTarih = (String) dataSnapshot.child("gonderilme_tarihi").getValue();

                /*
                if(strCinsiyet.equals("Erkek")){
                    baktiranKisi = strBaktiran + " Bey;";
                }else if(strCinsiyet.equals(("Kadın"))){
                    baktiranKisi = strBaktiran + " Hanım;";
                }else{
                    baktiranKisi = strBaktiran + ";";
                }
                */

                //database deki fal açıklamasını set et
                bakilaninIsmi.setText("Sevgili " + strBaktiran + ";");
                falDetayYorum.setText(falAciklamasi);

                falDetayTarih.setText(strTarih);

                //fal fotoğrafını sayfaya yükle
                Picasso.with(FaldetayActivity.this).load(strFalFoto1).into(falDetayFoto);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faldetay);
        init();
        handler();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if(mDatabaseFal != null){

            mDatabaseFal.removeEventListener(mListener);
        }

        fbFalGonder.setOnClickListener(null);
        fbFalGonder.setImageDrawable(null);

    }
}
