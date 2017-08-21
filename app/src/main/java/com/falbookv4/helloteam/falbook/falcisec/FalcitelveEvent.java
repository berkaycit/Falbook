package com.falbookv4.helloteam.falbook.falcisec;

public class FalcitelveEvent {

    private int falcitelveBedeli;
    private boolean falciDilekAktif;

    public FalcitelveEvent(int falcitelveBedeli, boolean falciDilekAktif) {
        this.falcitelveBedeli = falcitelveBedeli;
        this.falciDilekAktif = falciDilekAktif;
    }

    public boolean isFalciDilekAktif() {
        return falciDilekAktif;
    }

    public void setFalciDilekAktif(boolean falciDilekAktif) {
        this.falciDilekAktif = falciDilekAktif;
    }

    public int getFalcitelveBedeli() {
        return falcitelveBedeli;
    }

    public void setFalcitelveBedeli(int falcitelveBedeli) {
        this.falcitelveBedeli = falcitelveBedeli;
    }
}
