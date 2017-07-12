package com.falbookv4.helloteam.falbook.classes;

public class Fal {

    private String ID;
    private String kullanici;
    private String foto1;
    private String foto2;
    private String foto3;
    private String gonderilme_tarihi;
    private Boolean bakilmaDurumu;
    private String falAciklamasi;
    private String falBakiciID;
    private String falBakilmaTarihi;
    private String isim;
    private String fal_yorumu;

    public Fal(){

    }

    public Fal(String ID, String kullanici, String foto1, String foto2, String foto3, String gonderilme_tarihi,
               Boolean bakilmaDurumu, String falAciklamasi, String falBakiciID, String falBakilmaTarihi, String isim,
               String fal_yorumu) {
        this.ID = ID;
        this.kullanici = kullanici;
        this.foto1 = foto1;
        this.foto2 = foto2;
        this.foto3 = foto3;
        this.gonderilme_tarihi = gonderilme_tarihi;
        this.bakilmaDurumu = bakilmaDurumu;
        this.falAciklamasi = falAciklamasi;
        this.falBakiciID = falBakiciID;
        this.falBakilmaTarihi = falBakilmaTarihi;
        this.isim = isim;
        this.fal_yorumu = fal_yorumu;
    }

    public String getFal_yorumu() {
        return fal_yorumu;
    }

    public void setFal_yorumu(String fal_yorumu) {
        this.fal_yorumu = fal_yorumu;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getGonderilme_tarihi() {
        return gonderilme_tarihi;
    }

    public void setGonderilme_tarihi(String gonderilme_tarihi) {
        this.gonderilme_tarihi = gonderilme_tarihi;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getKullanici() {
        return kullanici;
    }

    public void setKullanici(String kullanici) {
        this.kullanici = kullanici;
    }

    public String getFoto1() {
        return foto1;
    }

    public void setFoto1(String foto1) {
        this.foto1 = foto1;
    }

    public String getFoto2() {
        return foto2;
    }

    public void setFoto2(String foto2) {
        this.foto2 = foto2;
    }

    public String getFoto3() {
        return foto3;
    }

    public void setFoto3(String foto3) {
        this.foto3 = foto3;
    }

    public Boolean getBakilmaDurumu() {
        return bakilmaDurumu;
    }

    public void setBakilmaDurumu(Boolean bakilmaDurumu) {
        this.bakilmaDurumu = bakilmaDurumu;
    }

    public String getFalAciklamasi() {
        return falAciklamasi;
    }

    public void setFalAciklamasi(String falAciklamasi) {
        this.falAciklamasi = falAciklamasi;
    }

    public String getFalBakiciID() {
        return falBakiciID;
    }

    public void setFalBakiciID(String falBakiciID) {
        this.falBakiciID = falBakiciID;
    }

    public String getFalBakilmaTarihi() {
        return falBakilmaTarihi;
    }

    public void setFalBakilmaTarihi(String falBakilmaTarihi) {
        this.falBakilmaTarihi = falBakilmaTarihi;
    }
}
