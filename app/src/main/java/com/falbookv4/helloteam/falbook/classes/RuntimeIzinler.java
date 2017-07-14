package com.falbookv4.helloteam.falbook.classes;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.audiofx.EnvironmentalReverb;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

public abstract class RuntimeIzinler extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void izinIste(final String[] istenilenIzinler, final int requestCode){

        int izinKontrol = PackageManager.PERMISSION_GRANTED;
        boolean mazeretGoster = false;

        for(String izin : istenilenIzinler) {
            //belirtilen izinler için ->izin verilip verilmediği kontrol ediliyor
            //izinkontol = 0 ise izin verilmiş -1 ise izin verilmemiştir
            izinKontrol = izinKontrol + ContextCompat.checkSelfPermission(this, izin);
            //mazereti eğer kullanıcı bir defa izini reddederse göstereceğiz bu yüzde aşağıdaki gibi bir boolean yaptık
            //ilk başta mazeretGoster = false, diğer taraf da ilk başta false-> reddederse 1 dönecek.
            mazeretGoster = mazeretGoster || ActivityCompat.shouldShowRequestPermissionRationale(this, izin);
        }

            if(izinKontrol != PackageManager.PERMISSION_GRANTED){

                //ikinci defada bir mazeret göstereceğiz
                if(mazeretGoster){

                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("İzin istememizin nedeni!")
                            .setContentText("İşlemlere başka türlü devam edemiyoruz")
                            .setCancelText("Onay Vermiyorum")
                            .setConfirmText("Tamam")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    ActivityCompat.requestPermissions(RuntimeIzinler.this, istenilenIzinler, requestCode);
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .show();

                }else {
                    //kullanıcıya ilk defa izin isteğimizi soruyoruz.

                    ActivityCompat.requestPermissions(RuntimeIzinler.this, istenilenIzinler, requestCode);

                }


            }else{
                //burası izinlerin kullanıcı tarafından verildiği durum

                izinVerildi(requestCode);
            }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int izinKontrol = PackageManager.PERMISSION_GRANTED;


        for(int izinDurumu : grantResults){

            //tek bir izin bile verilmemiş olursa uygulama çalışmayacak. -1 + 0 + 0 ---> -1 verilmeyen izin
            izinKontrol = izinKontrol + izinDurumu;
        }

        //tüm izinlerin verildiği durum
        if((grantResults.length>0) && izinKontrol == PackageManager.PERMISSION_GRANTED){

            izinVerildi(requestCode);

        }else{
            //tek bir izinin bile verilmediği durum

            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("İzin Gerekiyor!")
                    .setContentText("Ayarlardan izinleri vermen gerekiyor")
                    .setCancelText("Onay Vermiyorum")
                    .setConfirmText("Tamam")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                            sweetAlertDialog.dismissWithAnimation();

                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setData(Uri.parse("package" + getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivity(intent);

                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                    .show();


        }

    }

    public abstract void izinVerildi(int requestCode);

}
