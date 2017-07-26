package com.falbookv4.helloteam.falbook.classes;

public class Kullanicilar {

    private String isim, soyisim;
    private String mail;
    private String cinsiyet;
    private String iliski;
    private String dogum;
    private Integer telve;
    private String profilfoto;
    private String cihazID;

    public Kullanicilar(){

    }

    public Kullanicilar(String isim, String soyisim, String mail, String cinsiyet, String iliski, String dogum, Integer telve, String profilfoto, String cihazID) {
        this.isim = isim;
        this.soyisim = soyisim;
        this.mail = mail;
        this.cinsiyet = cinsiyet;
        this.iliski = iliski;
        this.dogum = dogum;
        this.telve = telve;
        this.profilfoto = profilfoto;
        this.cihazID = cihazID;
    }

    public Kullanicilar(String isim, String soyisim, String mail, String cinsiyet, String iliski, String dogum) {
        this.isim = isim;
        this.soyisim = soyisim;
        this.mail = mail;
        this.cinsiyet = cinsiyet;
        this.iliski = iliski;
        this.dogum = dogum;
    }

    public String getCihazID() {
        return cihazID;
    }

    public void setCihazID(String cihazID) {
        this.cihazID = cihazID;
    }

    public Integer getTelve() {
        return telve;
    }

    public void setTelve(Integer telve) {
        this.telve = telve;
    }

    public String getSoyisim() {
        return soyisim;
    }

    public void setSoyisim(String soyisim) {
        this.soyisim = soyisim;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getCinsiyet() {
        return cinsiyet;
    }

    public void setCinsiyet(String cinsiyet) {
        this.cinsiyet = cinsiyet;
    }

    public String getIliski() {
        return iliski;
    }

    public void setIliski(String iliski) {
        this.iliski = iliski;
    }

    public String getDogum() {
        return dogum;
    }

    public void setDogum(String dogum) {
        this.dogum = dogum;
    }

    public String getProfilfoto() {
        return profilfoto;
    }

    public void setProfilfoto(String profilfoto) {
        this.profilfoto = profilfoto;
    }
}
