package com.falbookv4.helloteam.falbook.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.adcolony.sdk.AdColony;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.falbookv4.helloteam.falbook.R;
import com.falbookv4.helloteam.falbook.classes.FontCache;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.adcolony.sdk.*;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SatinalActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler, RewardedVideoAdListener {

    private Toolbar toolbarSatinal;
    private CardView dusukTelve, normalTelve, buyukTelve, cokbuyukTelve, izleKazan;
    private FirebaseAuth mAuth;
    private FirebaseUser mBulunanKullanici;
    private DatabaseReference mDatabaseKullanici, mDatabaseSiparis;
    private int bulunanTelve, uzerineEklenenTelve;
    private AlertDialog.Builder alertSatinal;
    private FloatingActionButton fbFalGonder;
    private BottomNavigationView botToolbar;
    private RewardedVideoAd mAd;
    private InterstitialAd interstitialAd;
    private String base64EncodedPublicKey;
    private BillingProcessor billingProcessor;
    static final String kucukTelve_id = "com.falbookv4.telve200yeni1";
    static final String normalTelve_id = "com.falbookv4.telve300yeni1";
    static final String buyukTelve_id = "com.falbookv4.telve500yeni1";
    static final String cokbuyukTelve_id = "com.falbookv4.telve1000yeni1";
    private boolean telveSatinalmaAktif = false;
    private ValueEventListener mListener;
    private TextView toolbarBaslik, txtTelveSayisi1, txtTelveParasi1, txtTelveSayisi2, txtTelveParasi2,
                        txtTelveSayisi3, txtTelveParasi3, txtTelveSayisi4, txtTelveParasi4, txtIzleKazan;
    private SweetAlertDialog pDialog;
    final private String APP_ID = "app76409d884f2b4690be";
    final private String ZONE_ID = "vzb4be69d9b1194e7287";
    private AdColonyInterstitial mColonyAd;
    private AdColonyInterstitialListener listener;
    private AdColonyAdOptions adOptions;

    private boolean admobAktif = false;

    public void init(){

        toolbarBaslik = (TextView) findViewById(R.id.satinal_toolbar_baslik);
        txtTelveSayisi1 = (TextView) findViewById(R.id.txtTelveSayisi);
        txtTelveParasi1 = (TextView) findViewById(R.id.txtTelveParasi);
        txtTelveSayisi2 = (TextView) findViewById(R.id.txtTelveSayisi300);
        txtTelveParasi2 = (TextView) findViewById(R.id.txtTelveParasi300);
        txtTelveSayisi3 = (TextView) findViewById(R.id.txtTelveSayisi500);
        txtTelveParasi3 = (TextView) findViewById(R.id.txtTelveParasi500);
        txtTelveSayisi4 = (TextView) findViewById(R.id.txtTelveSayisi1000);
        txtTelveParasi4 = (TextView) findViewById(R.id.txtTelveParasi1000);
        txtIzleKazan = (TextView) findViewById(R.id.txtIzleyerekKazan);

        base64EncodedPublicKey = getString(R.string.base64encodedpublickey);


        //ikinci parametreye key girilecek.
        billingProcessor = new BillingProcessor(this, base64EncodedPublicKey, this);
        billingProcessor.initialize();

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-9010511052067778/9309985844");

        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(this);

        izleKazan = (CardView) findViewById(R.id.card_izle_kazan);

        toolbarSatinal = (Toolbar) findViewById(R.id.toolbarSatinal);
        dusukTelve = (CardView) findViewById(R.id.card_dusuk_telve);
        normalTelve = (CardView) findViewById(R.id.card_normal_telve);
        buyukTelve = (CardView) findViewById(R.id.card_buyuk_telve);
        cokbuyukTelve = (CardView) findViewById(R.id.card_cokbuyuk_telve);

        mAuth = FirebaseAuth.getInstance();
        mBulunanKullanici = mAuth.getCurrentUser();
        mDatabaseKullanici = FirebaseDatabase.getInstance().getReference().child("Kullanicilar")
                .child(mBulunanKullanici.getUid());
        mDatabaseSiparis = FirebaseDatabase.getInstance().getReference().child("Siparisler")
                .child(mBulunanKullanici.getUid());

        alertSatinal = new AlertDialog.Builder(this);

        fbFalGonder = (FloatingActionButton) findViewById(R.id.fbFalGonder);
        botToolbar = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        MobileAds.initialize(this, "ca-app-pub-9010511052067778/9309985844");



        AdColonyAppOptions app_options = new AdColonyAppOptions().setUserID(mBulunanKullanici.getUid());
        AdColony.configure(this, app_options, APP_ID, ZONE_ID);

        adOptions = new AdColonyAdOptions()
                .enableConfirmationDialog(false)
                .enableResultsDialog(false);

        AdColony.setRewardListener(new AdColonyRewardListener() {
            @Override
            public void onReward(AdColonyReward reward) {
                telveSatinalmaAktif = true;
                telveSatinAl(7);
            }
        });


        listener = new AdColonyInterstitialListener() {
            @Override
            public void onRequestFilled(AdColonyInterstitial ad) {
                mColonyAd = ad;
                if(pDialog!=null){
                    pDialog.dismissWithAnimation();
                    pDialog = null;
                    mColonyAd.show();
                }
            }

            @Override
            public void onRequestNotFilled(AdColonyZone zone) {
                super.onRequestNotFilled(zone);

                AdColony.requestInterstitial(ZONE_ID, listener, adOptions);
                /*
                admobAktif = true;
                if(!mAd.isLoaded()){
                    mAd.loadAd("ca-app-pub-9010511052067778/9309985844", new AdRequest.Builder().build());
                }
                */

                Toast.makeText(SatinalActivity.this, "Reklam yüklenemedi.", Toast.LENGTH_SHORT).show();

                if(pDialog!=null){
                    if(mAd.isLoaded()){
                        mAd.show();
                    }
                }
            }

            @Override
            public void onClosed(AdColonyInterstitial ad) {
                super.onClosed(ad);
                loadAd();
            }

            @Override
            public void onAudioStarted(AdColonyInterstitial ad) {
                super.onAudioStarted(ad);
                if(pDialog != null){
                    pDialog.dismissWithAnimation();
                    pDialog = null;
                }
            }


            @Override
            public void onOpened(AdColonyInterstitial ad) {
                super.onOpened(ad);

                if(pDialog != null){
                    pDialog.dismissWithAnimation();
                    pDialog = null;
                }
            }
        };

        AdColony.requestInterstitial(ZONE_ID, listener, adOptions);


    }

    private void initAlert(){
            pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
    }

    private void fontHandler(){

        Typeface typeFace= FontCache.get("fonts/MyriadPro.ttf", this);
        Typeface typeFaceBold= FontCache.get("fonts/MyriadProBold.ttf", this);

        toolbarBaslik.setTypeface(typeFaceBold);
        txtTelveSayisi1.setTypeface(typeFace);
        txtTelveParasi1.setTypeface(typeFace);
        txtTelveSayisi2.setTypeface(typeFace);
        txtTelveParasi2.setTypeface(typeFace);
        txtTelveSayisi3.setTypeface(typeFace);
        txtTelveParasi3.setTypeface(typeFace);
        txtTelveSayisi4.setTypeface(typeFace);
        txtTelveParasi4.setTypeface(typeFace);
        txtIzleKazan.setTypeface(typeFace);

        txtTelveParasi1.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        txtTelveParasi2.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        txtTelveParasi3.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        txtTelveParasi4.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private void menuleriHazirla() {

        fbFalGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent anasayfaToKafe = new Intent(SatinalActivity.this, KafeActivity.class);
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
                        Intent intentToAnasayfa = new Intent(SatinalActivity.this, AnasayfaActivity.class);
                        intentToAnasayfa.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentToAnasayfa);
                        finish();
                        break;
                    case R.id.menuGelenfalButon:
                        Intent intentToFallarim = new Intent(SatinalActivity.this, GelenfallarActivity.class);
                        intentToFallarim.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentToFallarim);
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    public void telveSatinAl(final int eklenecekTelveSayisi){

        mDatabaseKullanici.addValueEventListener(mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                bulunanTelve = ((Long) dataSnapshot.child("telve").getValue()).intValue();
                uzerineEklenenTelve = bulunanTelve + eklenecekTelveSayisi;

                if(telveSatinalmaAktif){
                    telveSatinalmaAktif = false;

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

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void satinAlimHandler(){

        dusukTelve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle extraParams = new Bundle();
                extraParams.putString("accountId", "MY_ACCOUNT_ID");

                billingProcessor.consumePurchase(kucukTelve_id);
                billingProcessor.purchase(SatinalActivity.this, kucukTelve_id, null, extraParams);

            }
        });

        normalTelve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                billingProcessor.consumePurchase(normalTelve_id);
                billingProcessor.purchase(SatinalActivity.this, normalTelve_id);
            }
        });

        buyukTelve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                billingProcessor.consumePurchase(buyukTelve_id);
                billingProcessor.purchase(SatinalActivity.this, buyukTelve_id);
            }
        });

        cokbuyukTelve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                billingProcessor.consumePurchase(cokbuyukTelve_id);
                billingProcessor.purchase(SatinalActivity.this, cokbuyukTelve_id);
            }
        });

        loadAd();
        //izleKazan.setEnabled(false);


        izleKazan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mColonyAd!=null){
                    mColonyAd.show();
                }

                if(mAd.isLoaded()){
                    mAd.show();
                }

                initAlert();

                if(pDialog != null){

                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Reklam yükleniyor");
                    pDialog.setCancelable(true);
                    pDialog.show();
                }


            }
        });


    }

    public void handler(){

        fontHandler();
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

        mAd.loadAd("ca-app-pub-9010511052067778/9309985844", new AdRequest.Builder().build());

        if (mColonyAd == null || mColonyAd.isExpired()) {
            AdColony.requestInterstitial(ZONE_ID, listener, adOptions);
        } else{
            if(pDialog != null){

                pDialog.dismiss();
                pDialog = null;
            }
        }
        /*
        else if(!mAd.isLoaded()) {

            //"8B171216A18A2C889ADF71BBCC97E39A"
            mAd.loadAd("ca-app-pub-9010511052067778/9309985844", new AdRequest.Builder().build());
        }


        if(!mAd.isLoaded()){
            mAd.loadAd("ca-app-pub-9010511052067778/9309985844", new AdRequest.Builder().build());
        }
        */
    }


    @Override
    public void onRewardedVideoAdLoaded() {
        //izleKazan.setEnabled(true);

        if(pDialog != null){
            pDialog.dismissWithAnimation();
            pDialog = null;
            mAd.show();
        }

        /*
        if(admobAktif){
            mAd.show();
        }
        */

    }

    @Override
    public void onRewardedVideoAdOpened() {

        if(pDialog != null){
            pDialog.dismissWithAnimation();
            pDialog = null;
        }
    }

    @Override
    public void onRewardedVideoStarted() {

        if(pDialog != null){
            pDialog.dismissWithAnimation();
            pDialog = null;
        }
    }

    @Override
    public void onRewardedVideoAdClosed() {
        //izleKazan.setEnabled(false); //sorun buradan olabilir
        loadAd();
        admobAktif = false;
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        telveSatinalmaAktif = true;
        telveSatinAl(7);
        admobAktif = false;
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }



    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

        if(pDialog!=null){
            pDialog.dismissWithAnimation();
            pDialog = null;
        }

        Toast.makeText(SatinalActivity.this, "Reklam yüklenemedi.", Toast.LENGTH_SHORT).show();

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

        fbFalGonder.setOnClickListener(null);
        dusukTelve.setOnClickListener(null);
        normalTelve.setOnClickListener(null);
        buyukTelve.setOnClickListener(null);
        cokbuyukTelve.setOnClickListener(null);
        izleKazan.setOnClickListener(null);
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

        final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
        DatabaseReference yeniSatinAlim = mDatabaseSiparis.child(currentDate);

        if(billingProcessor.isValidTransactionDetails(details)){

            if(productId.equals(kucukTelve_id)){

                telveSatinalmaAktif = true;
                telveSatinAl(200);
                yeniSatinAlim.setValue(200);


            }
            if(productId.equals(normalTelve_id)){

                telveSatinalmaAktif = true;
                telveSatinAl(300);
                yeniSatinAlim.setValue(300);

            }
            if(productId.equals(buyukTelve_id)){

                telveSatinalmaAktif = true;
                telveSatinAl(500);
                yeniSatinAlim.setValue(500);


            }
            if(productId.equals(cokbuyukTelve_id)){

                telveSatinalmaAktif = true;
                telveSatinAl(1000);
                yeniSatinAlim.setValue(1000);

            }
        }



    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Toast.makeText(this, "Satın alınamadı, TEKRAR deneyin!", Toast.LENGTH_LONG).show();
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
