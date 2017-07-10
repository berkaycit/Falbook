package com.falbookv4.helloteam.falbook.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.falbookv4.helloteam.falbook.R;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

public class SSSActivity extends AppCompatActivity {

    private ExpandableRelativeLayout expandableLayout1, expandableLayout2, expandableLayout3, expandableLayout4,
            expandableLayout5,expandableLayout6,expandableLayout7,expandableLayout8,expandableLayout9, expandableLayout10;
    private Toolbar toolbarSSS;

    public void init(){

        toolbarSSS = (Toolbar) findViewById(R.id.toolbarSSS);
    }

    public void handler(){


        setSupportActionBar(toolbarSSS);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void expandableButton1(View view) {
        expandableLayout1 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout1);
        expandableLayout1.toggle();
    }

    public void expandableButton2(View view) {
        expandableLayout2 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout2);
        expandableLayout2.toggle();
    }

    public void expandableButton3(View view) {
        expandableLayout3 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout3);
        expandableLayout3.toggle();
    }

    public void expandableButton4(View view) {
        expandableLayout4 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout4);
        expandableLayout4.toggle();
    }

    public void expandableButton5(View view) {
        expandableLayout5 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout5);
        expandableLayout5.toggle();
    }

    public void expandableButton6(View view) {
        expandableLayout6 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout6);
        expandableLayout6.toggle();
    }

    public void expandableButton7(View view) {
        expandableLayout7 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout7);
        expandableLayout7.toggle();
    }

    public void expandableButton8(View view) {
        expandableLayout8 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout8);
        expandableLayout8.toggle();
    }

    public void expandableButton9(View view) {
        expandableLayout9 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout9);
        expandableLayout9.toggle();
    }

    public void expandableButton10(View view) {
        expandableLayout10 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout10);
        expandableLayout10.toggle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sss);
        init();
        handler();
    }
}
