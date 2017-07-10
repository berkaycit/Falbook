package com.falbookv4.helloteam.falbook.Activities;

import com.falbookv4.helloteam.falbook.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joooonho.SelectableRoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class ProfilActivity extends AppCompatActivity implements com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

    private CoordinatorLayout profilLayout;
    private DatabaseReference mDatabaseKullanici, mDatabaseKullaniciIc;
    private Toolbar profilToolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser mBulunanKullanici;
    private StorageReference mResimStorage;
    private SelectableRoundedImageView kullaniciProfilFoto;
    private EditText profilTxtAd, profilTxtSoyad, profilTxtMail, profilTxtCinsiyet, profilTxtDogum, profilTxtIliski;
    private String strAd, strSoyad, strMail, strCinsiyet, strDogum, strIliski, strProfilFoto;
    private int telve = 50; //telve sayısı
    private SweetAlertDialog mProgressKayitGuncelle;
    private final CharSequence iliskiDurumlari[] = {"İlişkisi Yok", "Evli", "Boşanmış", "Dul", "İlişkisi Var", "Nişanlı", "Platonik"};
    private final CharSequence arrCinsiyet[] = {"Kadın", "Erkek"};
    private boolean iliskiCooldown = true, cinsiyetCoolDown = true;
    private Handler handlerIliski, handlerCinsiyet;
    private Runnable runnableIliski, runnableCinsiyet;

    public void init() {

        mAuth = FirebaseAuth.getInstance();
        mBulunanKullanici = mAuth.getCurrentUser();
        String uid = mBulunanKullanici.getUid();
        mDatabaseKullanici = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        mDatabaseKullaniciIc = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid);
        mResimStorage = FirebaseStorage.getInstance().getReference();
        //offline olduğu durumlar için
        mDatabaseKullaniciIc.keepSynced(true);

        profilLayout = (CoordinatorLayout) findViewById(R.id.profilLayout);

        profilToolbar = (Toolbar) findViewById(R.id.toolbarProfil);
        profilTxtMail = (EditText) findViewById(R.id.profilTxtMail);
        profilTxtAd = (EditText) findViewById(R.id.profilTxtAd);
        profilTxtSoyad = (EditText) findViewById(R.id.profilTxtSoyad);
        profilTxtCinsiyet = (EditText) findViewById(R.id.profilTxtCinsiyet);
        profilTxtDogum = (EditText) findViewById(R.id.profilTxtDogum);
        profilTxtIliski = (EditText) findViewById(R.id.profilTxtIliski);

        kullaniciProfilFoto = (SelectableRoundedImageView) findViewById(R.id.profilImageFotograf);

        handlerIliski = new Handler();
        handlerCinsiyet = new Handler();

    }

    public void handler() {

        setSupportActionBar(profilToolbar);
        getSupportActionBar().setTitle(null); //actionbarda falbook yazıyor, yazıyı silmek için
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //geri butonu koyuyoruz ->parent a

        new TextleriSet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //mail kısmına bastığında kayıtlı değilse kayıt activity sine gitsin.
        if (mAuth.getCurrentUser().getEmail() == null) {
            //içerisine bir şey yazmayı kapat -> kullanıcı kayıtlı değil
            profilTxtMail.setFocusable(false);
        } else {
            //içerisine bir şey yazmayı aç -> kullanıcı kayıtlı
            profilTxtMail.setFocusable(true);

            //new TextleriSet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        //eğer kullanıcı misafir girişi yapmışsa ve edittext e basıyorsa, geçiş yapıp kayıt olmasını sağlayacağız
        profilTxtMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser().getEmail() == null) {

                    Intent profilToMisafirkayit = new Intent(getApplicationContext(), MisafiruyeActivity.class);
                    startActivity(profilToMisafirkayit);
                    finish();
                }
            }
        });

        profilTxtIliski.setFocusable(false);
        profilTxtIliski.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //hızlıca iki kere basarsa 2-3 tane açılmaması için
                if (iliskiCooldown) {

                    iliskiCooldown = false;

                    new AlertDialog.Builder(ProfilActivity.this)
                            .setSingleChoiceItems(iliskiDurumlari, 0, null)
                            .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    int seciliDurum = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                    profilTxtIliski.setText(iliskiDurumlari[seciliDurum]);
                                }
                            })
                            .show();

                    handlerIliski.postDelayed(runnableIliski = new Runnable() {
                        @Override
                        public void run() {
                            iliskiCooldown = true;
                        }
                    }, 300);
                } else {

                    handlerIliski.removeCallbacksAndMessages(runnableIliski);
                }
            }
        });

        profilTxtDogum.setFocusable(false);

        profilTxtDogum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        ProfilActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAccentColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                dpd.show(getFragmentManager(), "Doğum Tarihiniz");

            }
        });

        profilTxtCinsiyet.setFocusable(false);
        profilTxtCinsiyet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //hızlıca iki kere basarsa 2-3 tane açılmaması için
                if (cinsiyetCoolDown) {

                    cinsiyetCoolDown = false;

                    new AlertDialog.Builder(ProfilActivity.this)
                            .setSingleChoiceItems(arrCinsiyet, 0, null)
                            .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    int seciliDurum = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                    profilTxtCinsiyet.setText(arrCinsiyet[seciliDurum]);
                                }
                            })
                            .show();

                    handlerCinsiyet.postDelayed(runnableCinsiyet = new Runnable() {
                        @Override
                        public void run() {
                            cinsiyetCoolDown = true;
                        }
                    }, 300);
                } else {

                    handlerCinsiyet.removeCallbacksAndMessages(runnableCinsiyet);
                }
            }
        });

        kullaniciProfilFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.openChooserWithGallery(ProfilActivity.this, "Profil Fotoğrafı", 0);

            }
        });

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String tarihDogum = "" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        profilTxtDogum.setText(tarihDogum);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        init();
        handler();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menuProfilKaydetButon) {

            if (!profilTxtAd.getText().toString().isEmpty() && !profilTxtSoyad.getText().toString().isEmpty() &&
                    !profilTxtMail.getText().toString().isEmpty() && !profilTxtCinsiyet.getText().toString().isEmpty() &&
                    !profilTxtDogum.getText().toString().isEmpty() && !profilTxtIliski.getText().toString().isEmpty()) {

                strAd = profilTxtAd.getText().toString();
                strSoyad = profilTxtSoyad.getText().toString();
                strMail = profilTxtMail.getText().toString();
                strCinsiyet = profilTxtCinsiyet.getText().toString();
                strDogum = profilTxtDogum.getText().toString();
                strIliski = profilTxtIliski.getText().toString();
                strProfilFoto = "default";

                new BilgileriDegistir().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        strAd, strSoyad, strMail, strCinsiyet, strDogum, strIliski, strProfilFoto);


            } else {
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Bilgiler Eksik")
                        .setContentText("Lütfen bütün bilgilerinizi doldurun!")
                        .setConfirmText("Tamam")
                        .show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_profilim, menu);
        return true;
    }


    //parametre-progress-result
    private class BilgileriDegistir extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressKayitGuncelle = new SweetAlertDialog(ProfilActivity.this, SweetAlertDialog.SUCCESS_TYPE);
            mProgressKayitGuncelle.setTitleText("Güncelleme Tamamlandı!");
            mProgressKayitGuncelle.setContentText("Bilgileriniz başarıyla güncellendi!");
            mProgressKayitGuncelle.setConfirmText("Tamam");

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            mProgressKayitGuncelle.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            final String kullaniciID = mBulunanKullanici.getUid();

            mDatabaseKullanici.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(kullaniciID)) {

                        //kullanıcı bilgilerini güncelle -> bu şekilde yapınca ve setValue diyince boş girdiğimiz bilgileri veri tabanından siliyor
                        //Kullanicilar kullanici = new Kullanicilar(strAd, strSoyad, strMail, strCinsiyet, strIliski, strDogum, null, null);

                        Map<String, Object> updateKullaniciProfilMap = new HashMap<>();
                        updateKullaniciProfilMap.put("isim", strAd);
                        updateKullaniciProfilMap.put("soyisim", strSoyad);
                        updateKullaniciProfilMap.put("mail", strMail);
                        updateKullaniciProfilMap.put("cinsiyet", strCinsiyet);
                        updateKullaniciProfilMap.put("iliski", strIliski);
                        updateKullaniciProfilMap.put("dogum", strDogum);

                        mDatabaseKullanici.child(kullaniciID).updateChildren(updateKullaniciProfilMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //eğer veriyi değiştiremezse
                                if (!task.isSuccessful()) {
                                    mProgressKayitGuncelle.hide();

                                    Snackbar snacBilgiGuncellenemedi = Snackbar
                                            .make(profilLayout, "Bilgileriniz GÜNCELLENEMEDİ", Snackbar.LENGTH_LONG);
                                    snacBilgiGuncellenemedi.show();
                                }
                            }
                        });

                        //güncel bilgileri editable textlere yerleştir
                        new TextleriSet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    } else {
                        mProgressKayitGuncelle.hide();

                        Snackbar snacBilgiKullaniciBulunamadi = Snackbar
                                .make(profilLayout, "Kullanıcı BULUNAMADI", Snackbar.LENGTH_LONG);
                        snacBilgiKullaniciBulunamadi.show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    mProgressKayitGuncelle.hide();
                }
            });

            return null;
        }
    }


    //parametre-progress-result
    private class TextleriSet extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            mDatabaseKullaniciIc.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {

                    //firebase in databese inden verileri oku
                    strAd = (String) dataSnapshot.child("isim").getValue();
                    strSoyad = (String) dataSnapshot.child("soyisim").getValue();
                    strIliski = (String) dataSnapshot.child("iliski").getValue();
                    strDogum = (String) dataSnapshot.child("dogum").getValue();
                    strCinsiyet = (String) dataSnapshot.child("cinsiyet").getValue();
                    strMail = mBulunanKullanici.getEmail(); //auth dan okuyor.

                    //fotoğrafı çekiyoruz.
                    final String image = dataSnapshot.child("profilfoto").getValue().toString();
                    if(!image.equals("default")){
                        Picasso.with(ProfilActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.cat_profile)
                                .into(kullaniciProfilFoto, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {

                                        //tekrardan indir
                                        Picasso.with(ProfilActivity.this).load(image).placeholder(R.drawable.cat_profile).into(kullaniciProfilFoto);
                                    }
                                });
                    }

                    //okuduğun verileri editable text lere yaz.
                    profilTxtAd.setText(strAd);
                    profilTxtSoyad.setText(strSoyad);
                    profilTxtMail.setText(strMail);
                    profilTxtDogum.setText(strDogum);
                    profilTxtIliski.setText(strIliski);
                    profilTxtCinsiyet.setText(strCinsiyet);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;
        }
    }


    @Override
    public void onBackPressed() {

        handlerIliski.removeCallbacksAndMessages(runnableIliski);
        handlerCinsiyet.removeCallbacksAndMessages(runnableCinsiyet);

        finish();

        super.onBackPressed();
    }

    @Override
    protected void onPause() {

        handlerIliski.removeCallbacksAndMessages(runnableIliski);
        handlerCinsiyet.removeCallbacksAndMessages(runnableCinsiyet);

        super.onPause();
    }

    @Override
    protected void onStop() {

        handlerIliski.removeCallbacksAndMessages(runnableIliski);
        handlerCinsiyet.removeCallbacksAndMessages(runnableCinsiyet);

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        handlerIliski.removeCallbacksAndMessages(runnableIliski);
        handlerCinsiyet.removeCallbacksAndMessages(runnableCinsiyet);

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }


            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {

                if (resultCode != RESULT_CANCELED) {

                    //inal Uri resultUriProfil = data.getData();


                    Bitmap kucukProfilFoto = null;
                    try {
                        kucukProfilFoto = new Compressor(ProfilActivity.this)
                                .setMaxWidth(128)
                                .setMaxHeight(128)
                                .setQuality(50)
                                .compressToBitmap(imageFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    Log.d("FOTOĞFAF", kucukProfilFoto.toString());

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    kucukProfilFoto.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] kuculmusFoto_byte = baos.toByteArray();

                    StorageReference filepathProfilFoto = mResimStorage.child("profil_fotograflari").child(mBulunanKullanici.getUid() + ".jpg");


                    filepathProfilFoto.putBytes(kuculmusFoto_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            //firebase e yüklenen fotografin url sini almak için
                            String kucukFotoDownloadUrl = task.getResult().getDownloadUrl().toString();
                            mDatabaseKullaniciIc.child("profilfoto").setValue(kucukFotoDownloadUrl);

                            if(task.isSuccessful()){

                                //güncel bilgileri yerleştir
                                new TextleriSet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                //başarıyla yüklendi.
                            }

                        }
                    });


                    /*
                    UploadTask uploadTask = filepathProfilFoto.putBytes(kuculmusFoto_byte);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Yükleme başarısız
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            mDatabaseKullaniciIc.child("profilfoto").setValue(downloadUrl.toString());
                        }
                    });
                    */

                }

            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //cancel edilirse çekilen fotoğrafı sil
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(ProfilActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }

        });


    }

}
