package com.falbookv4.helloteam.falbook.falcisec;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnSuccessListener;
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
import org.greenrobot.eventbus.Subscribe;

public class Falci2 extends Fragment{

    private FrameLayout genelLayout;
    private Button btnGonder;
    String gonderenKisininIsmi = "";
    private int falciBedel = 100;
    private TextView falciIsmi, falciAciklamasi, falciTelveSayisi;

    private StorageReference mStorage;
    private DatabaseReference mDatabaseFalcilar, mFalci2, mFalci2Foto;

    public void init(){

        btnGonder = (Button) genelLayout.findViewById(R.id.btnFalciGonder);
        falciIsmi = (TextView) genelLayout.findViewById(R.id.txtFalciIsim);
        falciAciklamasi = (TextView) genelLayout.findViewById(R.id.txtFalciTanitim);
        falciTelveSayisi = (TextView) genelLayout.findViewById(R.id.txtFalciTelveSayisi);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabaseFalcilar = FirebaseDatabase.getInstance().getReference().child("Falcilar");
        mDatabaseFalcilar.keepSynced(true);
    }

    private void falciDataAtamasi(){

        mFalci2 = mDatabaseFalcilar.child("Falci2");
        mFalci2Foto = mFalci2.child("Falci_Foto");

        StorageReference filepath = mStorage.child("Falcilar_Fotolari").child("falcip2.jpg");

        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                mFalci2Foto.setValue(uri.toString());
            }
        });


        mFalci2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                falciIsmi.setText(dataSnapshot.child("Falci_Ismi").getValue().toString());
                falciAciklamasi.setText(dataSnapshot.child("Falci_Aciklamasi").getValue().toString());
                String strTelveFalci2 = dataSnapshot.child("Falci_Telve").getValue().toString() + " Telve";
                falciTelveSayisi.setText(strTelveFalci2);
                if(dataSnapshot.child("Falci_Foto").getValue() != null){

                    setFoto(getContext(), dataSnapshot.child("Falci_Foto").getValue().toString());
                }

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

                EventBus.getDefault().postSticky(new Falci2telveEvent(falciBedel));
                Intent falci2ToDilek = new Intent(getContext(), DilekActivity.class);
                startActivity(falci2ToDilek);

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
}
