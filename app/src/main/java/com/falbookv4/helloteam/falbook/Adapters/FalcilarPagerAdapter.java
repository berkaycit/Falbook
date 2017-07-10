package com.falbookv4.helloteam.falbook.Adapters;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.falbookv4.helloteam.falbook.FalciSec.FalciData;
import com.falbookv4.helloteam.falbook.R;

import java.util.List;

public class FalcilarPagerAdapter extends PagerAdapter {

    private List<FalciData> itemList;
    private Context context; //inflater ın inflate işlemini yapabilmek için bu nesneye ihtiyaç duyuyoruz.
    private LayoutInflater inflater;


    public FalcilarPagerAdapter(Context context, List<FalciData> itemList){
        this.context = context;
        this.itemList = itemList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount(){
        return itemList.size();
    }

    //bu metot view == object mi kontrol yapıyor -> düzgün çalışması için object ile view arasındaki ilişkiyi sürekli kontrol etmeli
    @Override
    public boolean isViewFromObject(View view, Object object){
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container , int position){
        View view = inflater.inflate(R.layout.pager_falcilar, container, false);

        //imajları ve text leri her position değerine göre değiştirip instantiate edeceğiz.
        ImageView imgFalci = (ImageView) view.findViewById(R.id.imgFalci);
        TextView txtFalciIsim = (TextView) view.findViewById(R.id.txtFalciIsim);
        TextView txtFalciAciklama = (TextView) view.findViewById(R.id.txtFalciTanitim);

        //böylece sürekli değişmiş olacak kaydırdıkça.
        FalciData temp = itemList.get(position);
        imgFalci.setImageResource(temp.getFotogID());
        txtFalciIsim.setText(temp.getAd());
        txtFalciAciklama.setText(temp.getAciklama());

        //container(viewpager) ‘ a pager_falcilar.xml ı ekliyoruz
        container.addView(view);

        return view;
    }

    //oluşturulmazsa hata alırız çünkü görüntü ekrandan çıktıkdan sonra destroy edilmeli
    //Dikkat -> object in yanında (Layout u) belirtmeliyiz
    @Override
    public void destroyItem(ViewGroup container, int position, Object object){

        container.removeView((FrameLayout) object);
    }

}
