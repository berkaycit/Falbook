package com.falbookv4.helloteam.falbook.falcisec;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.falbookv4.helloteam.falbook.R;
import com.falbookv4.helloteam.falbook.activities.DilekActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class Falci3 extends Fragment{

    private FrameLayout genelLayout;
    private Button btnGonder;
    String gonderenKisininIsmi = "";
    private int falciBedel, kullaniciTelveSayisi, farkTelveSayisi;
    private TextView falciIsmi, falciAciklamasi, falciTelveSayisi;
    private StorageReference mStorage;
    private DatabaseReference mDatabaseFalcilar, mFalci3, mFalci3Foto, mKullanici;
    private ValueEventListener mListener;
    private boolean falGonderecek = true;
    private ImageView imageFalci;
    private FirebaseAuth mAuth;
    private Bitmap bm3;

    public void init(){

        mAuth = FirebaseAuth.getInstance();

        imageFalci = (ImageView) genelLayout.findViewById(R.id.imgFalci);
        btnGonder = (Button) genelLayout.findViewById(R.id.btnFalciGonder);
        falciIsmi = (TextView) genelLayout.findViewById(R.id.txtFalciIsim);
        falciAciklamasi = (TextView) genelLayout.findViewById(R.id.txtFalciTanitim);
        falciTelveSayisi = (TextView) genelLayout.findViewById(R.id.txtFalciTelveSayisi);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabaseFalcilar = FirebaseDatabase.getInstance().getReference().child("Falcilar");
        mDatabaseFalcilar.keepSynced(true);

        String uid = mAuth.getCurrentUser().getUid();
        mKullanici = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid);
        mKullanici.keepSynced(true);
    }

    private void falciDataAtamasi(){

        bm3 = BitmapFactory.decodeResource(getResources(), R.drawable.vika);
        imageFalci.setImageBitmap(bm3);

        mFalci3 = mDatabaseFalcilar.child("Falci3");

        mKullanici.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                kullaniciTelveSayisi = ((Long)dataSnapshot.child("telve").getValue()).intValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mFalci3.addValueEventListener(mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                falciBedel = ((Long)dataSnapshot.child("Falci_Telve").getValue()).intValue();

                falciIsmi.setText(dataSnapshot.child("Falci_Ismi").getValue().toString());
                falciAciklamasi.setText(dataSnapshot.child("Falci_Aciklamasi").getValue().toString());
                String strTelveFalci3 = dataSnapshot.child("Falci_Telve").getValue().toString() + " Telve";
                falciTelveSayisi.setText(strTelveFalci3);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void handler(){

        falciDataAtamasi();

        btnGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(kullaniciTelveSayisi > 0 && falGonderecek){

                    falGonderecek = false;
                    farkTelveSayisi = kullaniciTelveSayisi - falciBedel;
                }

                if(farkTelveSayisi >= 0 && kullaniciTelveSayisi > 0){

                    EventBus.getDefault().postSticky(new FalcitelveEvent(falciBedel, true));
                    Intent falci3ToDilek = new Intent(getContext(), DilekActivity.class);
                    startActivity(falci3ToDilek);
                }else{
                    Snackbar snacYetersiz = Snackbar.make(genelLayout, "Telve Sayınız YETERSİZ!", Snackbar.LENGTH_LONG);
                    snacYetersiz.show();
                }



            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        genelLayout = (FrameLayout) inflater.inflate(R.layout.pager_falcilar, container, false);

        init();
        handler();

        return genelLayout;
    }

    @Override
    public void onDestroyView() {
        bm3.recycle();
        bm3 = null;
        super.onDestroyView();
    }
}
