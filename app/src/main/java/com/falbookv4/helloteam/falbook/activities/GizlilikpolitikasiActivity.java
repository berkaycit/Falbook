package com.falbookv4.helloteam.falbook.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.falbookv4.helloteam.falbook.R;

public class GizlilikpolitikasiActivity extends AppCompatActivity {

    private ViewStub stub = null;
    private TextView txtBaslik, txtIcerik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gizlilikpolitikasi);

        stub = (ViewStub) findViewById(R.id.stub_gizlilik);
        View inflated = stub.inflate();

        txtBaslik = (TextView) inflated.findViewById(R.id.txtGizlilikPolitikasiBaslik);
        txtIcerik = (TextView) inflated.findViewById(R.id.txtGizlilikPolitikasi);

        txtBaslik.setGravity(Gravity.CENTER);
        txtBaslik.setPadding(24, 24, 24 ,24);
        txtIcerik.setPadding(24, 0, 24, 0);

        txtBaslik.setText(R.string.gizlilikpolitikasi_baslik);
    }

    @Override
    protected void onDestroy() {

        if(stub != null){
            stub.setVisibility(View.GONE);
        }
        super.onDestroy();
    }
}
