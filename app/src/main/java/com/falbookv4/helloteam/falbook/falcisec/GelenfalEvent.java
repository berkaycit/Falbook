package com.falbookv4.helloteam.falbook.falcisec;

import android.net.Uri;

import java.io.File;

public class GelenfalEvent {

    private String isim, cinsiyet, dogum, iliski;
    private byte[] kucukFoto1, kucukFoto2, kucukFoto3;
    private Uri uriKucukFoto1, uriKucukFoto2, uriKucukFoto3;

    public GelenfalEvent(String isim, String cinsiyet, String dogum, String iliski,
                         byte[] kucukFoto1, byte[] kucukFoto2, byte[] kucukFoto3) {
        this.isim = isim;
        this.cinsiyet = cinsiyet;
        this.dogum = dogum;
        this.iliski = iliski;
        this.kucukFoto1 = kucukFoto1;
        this.kucukFoto2 = kucukFoto2;
        this.kucukFoto3 = kucukFoto3;
    }

    public Uri getUriKucukFoto2() {
        return uriKucukFoto2;
    }

    public void setUriKucukFoto2(Uri uriKucukFoto2) {
        this.uriKucukFoto2 = uriKucukFoto2;
    }

    public Uri getUriKucukFoto3() {
        return uriKucukFoto3;
    }

    public void setUriKucukFoto3(Uri uriKucukFoto3) {
        this.uriKucukFoto3 = uriKucukFoto3;
    }

    public Uri getUriKucukFoto1(){
        return uriKucukFoto1;
    }

    public void setUriKucukFoto1(Uri uriKucukFoto1){
        this.uriKucukFoto1 = uriKucukFoto1;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getCinsiyet() {
        return cinsiyet;
    }

    public void setCinsiyet(String cinsiyet) {
        this.cinsiyet = cinsiyet;
    }

    public String getDogum() {
        return dogum;
    }

    public void setDogum(String dogum) {
        this.dogum = dogum;
    }

    public String getIliski() {
        return iliski;
    }

    public void setIliski(String iliski) {
        this.iliski = iliski;
    }

    public byte[] getKucukFoto1() {
        return kucukFoto1;
    }

    public void setKucukFoto1(byte[] kucukFoto1) {
        this.kucukFoto1 = kucukFoto1;
    }

    public byte[] getKucukFoto2() {
        return kucukFoto2;
    }

    public void setKucukFoto2(byte[] kucukFoto2) {
        this.kucukFoto2 = kucukFoto2;
    }

    public byte[] getKucukFoto3() {
        return kucukFoto3;
    }

    public void setKucukFoto3(byte[] kucukFoto3) {
        this.kucukFoto3 = kucukFoto3;
    }
}
