package com.falbookv4.helloteam.falbook.falcisec;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.Toast;

import com.falbookv4.helloteam.falbook.activities.DilekActivity;
import com.falbookv4.helloteam.falbook.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

public class Falci1 extends Fragment{

    private FrameLayout genelLayout;
    private Button btnGonder;
    String gonderenKisininIsmi = "";
    private int falciBedel, kullaniciTelveSayisi, farkTelveSayisi;
    private TextView falciIsmi, falciAciklamasi, falciTelveSayisi;
    private StorageReference mStorage;
    private DatabaseReference mDatabaseFalcilar, mFalci1, mFalci1Foto, mKullanici;
    private ValueEventListener mListener;
    private FirebaseAuth mAuth;
    private boolean falGonderecek = true;
    private ImageView imageFalci;
    private Bitmap bm1;


    public void init(){

        mAuth = FirebaseAuth.getInstance();

        imageFalci = (ImageView) genelLayout.findViewById(R.id.imgFalci);
        btnGonder = (Button) genelLayout.findViewById(R.id.btnFalciGonder);
        falciIsmi = (TextView) genelLayout.findViewById(R.id.txtFalciIsim);
        falciAciklamasi = (TextView) genelLayout.findViewById(R.id.txtFalciTanitim);
        falciTelveSayisi = (TextView) genelLayout.findViewById(R.id.txtFalciTelveSayisi);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabaseFalcilar = FirebaseDatabase.getInstance().getReference().child("Falcilar");
        String uid = mAuth.getCurrentUser().getUid();
        mKullanici = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid);
        mDatabaseFalcilar.keepSynced(true);
        mKullanici.keepSynced(true);

    }

    private void falciDataAtamasi(){

        bm1 = BitmapFactory.decodeResource(getResources(), R.drawable.falcifotogbir);
        imageFalci.setImageBitmap(bm1);

        mFalci1 = mDatabaseFalcilar.child("Falci1");
        mFalci1Foto = mFalci1.child("Falci_Foto");

        /*
        StorageReference filepath = mStorage.child("Falcilar_Fotolari").child("falcip1.jpg");

        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                mFalci1Foto.setValue(uri.toString());

            }
        });
        */

        mKullanici.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                kullaniciTelveSayisi = ((Long)dataSnapshot.child("telve").getValue()).intValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mFalci1.addValueEventListener(mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                falciBedel = ((Long)dataSnapshot.child("Falci_Telve").getValue()).intValue();

                falciIsmi.setText(dataSnapshot.child("Falci_Ismi").getValue().toString());
                falciAciklamasi.setText(dataSnapshot.child("Falci_Aciklamasi").getValue().toString());
                String strTelveFalci1 = dataSnapshot.child("Falci_Telve").getValue().toString() + " Telve";
                falciTelveSayisi.setText(strTelveFalci1);
                /*
                if(dataSnapshot.child("Falci_Foto").getValue() != null){

                    setFoto(getContext(), dataSnapshot.child("Falci_Foto").getValue().toString());
                }
                */

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setFoto(final Context ctx, final String image) {

        final ImageView imageFalci2 = (ImageView) genelLayout.findViewById(R.id.imgFalci);

        //picasso framework ü kullanarak imajı yükle
        Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.falcifotogbir).into(imageFalci2, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

                Picasso.with(ctx).load(image).into(imageFalci2);

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

                    EventBus.getDefault().postSticky(new FalcitelveEvent(falciBedel, false));
                    Intent falci1ToDilek = new Intent(getContext(), DilekActivity.class);
                    startActivity(falci1ToDilek);
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
        bm1.recycle();
        bm1 = null;
        super.onDestroyView();
    }
}
