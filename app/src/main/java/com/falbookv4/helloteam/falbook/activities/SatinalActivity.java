package com.falbookv4.helloteam.falbook.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.adcolony.sdk.AdColony;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.falbookv4.helloteam.falbook.R;
import com.falbookv4.helloteam.falbook.falcisec.TelveEvent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jirbo.adcolony.AdColonyAdapter;
import com.jirbo.adcolony.AdColonyBundleBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class SatinalActivity extends AppCompatActivity implements RewardedVideoAdListener, BillingProcessor.IBillingHandler {

    private Toolbar toolbarSatinal;
    private RelativeLayout dusukTelve, izleKazan;
    private FirebaseAuth mAuth;
    private FirebaseUser mBulunanKullanici;
    private DatabaseReference mDatabaseKullanici;
    private int bulunanTelve, uzerineEklenenTelve;
    private AlertDialog.Builder alertSatinal;
    private FloatingActionButton fbFalGonder;
    private BottomNavigationView botToolbar;
    private RewardedVideoAd mAd;
    private InterstitialAd interstitialAd;
    private String base64EncodedPublicKey;
    private BillingProcessor billingProcessor;

    public void init(){

        //base64EncodedPublicKey = getString(R.string.base64encodedpublickey);

        //MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        //AdColony.configure()

        //ikinci parametreye key girilecek.
        billingProcessor = new BillingProcessor(this, null, this);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-9010511052067778/9309985844");

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                telveSatinAl(35);
            }
        });

        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(this);

        izleKazan = (RelativeLayout) findViewById(R.id.satinalIzleKazan);

        toolbarSatinal = (Toolbar) findViewById(R.id.toolbarSatinal);
        dusukTelve = (RelativeLayout) findViewById(R.id.dusukTelveSatinAl);
        mAuth = FirebaseAuth.getInstance();
        mBulunanKullanici = mAuth.getCurrentUser();
        mDatabaseKullanici = FirebaseDatabase.getInstance().getReference().child("Kullanicilar")
                .child(mBulunanKullanici.getUid());

        alertSatinal = new AlertDialog.Builder(this);

        fbFalGonder = (FloatingActionButton) findViewById(R.id.fbFalGonder);
        botToolbar = (BottomNavigationView) findViewById(R.id.bottom_navigation);

    }

    private void menuleriHazirla() {

        fbFalGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent anasayfaToKafe = new Intent(SatinalActivity.this, KafeActivity.class);
                startActivity(anasayfaToKafe);
                finish();
            }
        });

        botToolbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.menuAnasafaButon:
                        Intent intentToAnasayfa = new Intent(SatinalActivity.this, AnasayfaActivity.class);
                        startActivity(intentToAnasayfa);
                        finish();
                        break;
                    case R.id.menuGelenfalButon:
                        Intent intentToFallarim = new Intent(SatinalActivity.this, GelenfallarActivity.class);
                        startActivity(intentToFallarim);
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    public void telveSatinAl(final int eklenecekTelveSayisi){

        mDatabaseKullanici.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                bulunanTelve = ((Long) dataSnapshot.child("telve").getValue()).intValue();
                uzerineEklenenTelve = bulunanTelve + eklenecekTelveSayisi;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        alertSatinal.setTitle("" + eklenecekTelveSayisi + " Telve Satın Al");
        alertSatinal.setMessage("Satın al sayfasına yönlendirileceksin");
        alertSatinal.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Map<String, Object> telveSatinAlMap = new HashMap<>();
                telveSatinAlMap.put("telve",uzerineEklenenTelve);

                mDatabaseKullanici.updateChildren(telveSatinAlMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            //satın alım başarılı
                        }
                    }
                });

                /*
                mDatabaseKullanici.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Map<String, Object> telveSatinAlMap = new HashMap<>();
                        telveSatinAlMap.put("telve",uzerineEklenenTelve);

                        mDatabaseKullanici.updateChildren(telveSatinAlMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    //satın alım başarılı
                                }
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
                */

            }
        });

        alertSatinal.show();

    }

    public void satinAlimHandler(){

        dusukTelve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                billingProcessor.purchase(SatinalActivity.this, "android.test.purchased");

            }
        });


        loadAd();
        izleKazan.setEnabled(false);
        izleKazan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                if(interstitialAd.isLoaded()){

                    interstitialAd.show();
                }
                */
                if(mAd.isLoaded()){
                    mAd.show();
                }
            }
        });

    }



    public void handler(){

        menuleriHazirla();
        satinAlimHandler();

        setSupportActionBar(toolbarSatinal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satinal);
        init();
        handler();
    }


    private void loadAd(){
/*
        AdColonyBundleBuilder.setUserId("USER_ID");
        AdColonyBundleBuilder.setShowPrePopup(true);
        AdColonyBundleBuilder.setShowPostPopup(true);
        AdRequest request = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdColonyAdapter.class, AdColonyBundleBuilder.build())
                .build();
        interstitialAd.loadAd(request);
        */

        if(!mAd.isLoaded()){

            mAd.loadAd("ca-app-pub-9010511052067778/9309985844", new AdRequest.Builder().build());
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        izleKazan.setEnabled(true);
        izleKazan.setBackgroundColor(Color.BLUE);
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        izleKazan.setEnabled(false);
        loadAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        telveSatinAl(55);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onResume() {
        mAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mAd.destroy(this);

        if(billingProcessor != null){
            billingProcessor.release();
        }

        super.onDestroy();
    }


    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

        telveSatinAl(200);
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(!billingProcessor.handleActivityResult(requestCode, resultCode, data)){

            super.onActivityResult(requestCode, resultCode, data);
        }


    }


}
