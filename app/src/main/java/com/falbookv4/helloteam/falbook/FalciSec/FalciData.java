package com.falbookv4.helloteam.falbook.FalciSec;


import com.falbookv4.helloteam.falbook.R;

import java.util.ArrayList;
import java.util.List;

public class FalciData {

    private String ID;
    private Integer fotogID;
    private String ad;
    private String aciklama;
    private String falMaliyet;
    private Integer puan;

    public FalciData(){
        //boş cons. olması gerekiyor
    }

    //falcinin ID sini ve puan ı burada parametre olarak belirtmedim
    public FalciData(Integer fotogID, String ad, String aciklama, String falMaliyet){

        this.fotogID = fotogID;
        this.ad = ad;
        this.aciklama = aciklama;
        this.falMaliyet = falMaliyet;
    }

    //bütün parametreler var
    public FalciData(String ID, Integer fotogID, String ad, String aciklama, String falMaliyet, Integer puan) {
        this.ID = ID;
        this.fotogID = fotogID;
        this.ad = ad;
        this.aciklama = aciklama;
        this.falMaliyet = falMaliyet;
        this.puan = puan;
    }

    public Integer getFotogID() {
        return fotogID;
    }

    public void setFotogID(Integer fotogID) {
        this.fotogID = fotogID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getFalMaliyet() {
        return falMaliyet;
    }

    public void setFalMaliyet(String falMaliyet) {
        this.falMaliyet = falMaliyet;
    }

    public int getPuan() {
        return puan;
    }

    public void setPuan(int puan) {
        this.puan = puan;
    }

    public static List<FalciData> getDataList(){

        List<FalciData> itemList = new ArrayList<>();

        //falcı resimlerini statik olarak giriyorum
        int[] imageIDs = new int[] {
                R.drawable.falcifotogbir, R.drawable.falcifotogbir, R.drawable.falcifotogbir
        };

        //falcıların isimlerini statik olarka giriyorum
        String[] falciIsimleri = new String[] {
          "Melihat abla" , "Dilruba abla", "Kadriye abla"
        };

        String[] falMaliyetleri = new String[]{
          "200 Telve", "300 Telve", "500 Telve"
        };

        String[] falciAciklamalari = new String[] {
          "Cok guzel bakarım", "Mükemmel bakarım", "En iyi ben bakarım"
        };

        for(int i = 0; i<imageIDs.length; i++){
            itemList.add(new FalciData(imageIDs[i], falciIsimleri[i], falMaliyetleri[i], falciAciklamalari[i]));
        }

        return itemList;

    }

}
