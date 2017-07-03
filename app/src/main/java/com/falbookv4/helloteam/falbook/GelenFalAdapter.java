package com.falbookv4.helloteam.falbook;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

class GelenFalAdapter extends RecyclerView.Adapter<GelenFalAdapter.ViewHolder> {

    private String[] titles = {
            "Kullanıcı İsmi",
            "Kullanıcı İsmi",
            "Kullanıcı İsmi",
            "Kullanıcı İsmi",
            "Kullanıcı İsmi",
            "Kullanıcı İsmi",
            "Kullanıcı İsmi",
            "Kullanıcı İsmi"};

    private String[] details = {
            "Falınız yorumlanıyor",
            "Falınız yorumlanıyor",  "Falınız yorumlanıyor",
            "Falınız yorumlanıyor",  "Falınız yorumlanıyor",
            "Falınız yorumlanıyor",  "Falınız yorumlanıyor",
            "Falınız yorumlanıyor"};

    private int[] images = {
            R.drawable.kahvefalifoto,
            R.drawable.kahvefalifoto,
            R.drawable.kahvefalifoto,
            R.drawable.kahvefalifoto,
            R.drawable.kahvefalifoto,
            R.drawable.kahvefalifoto,
            R.drawable.kahvefalifoto,
            R.drawable.kahvefalifoto };

    class ViewHolder extends RecyclerView.ViewHolder{

        public int currentItem;
        public ImageView itemImage;
        public TextView itemTitle;
        public TextView itemDetail;

        public ViewHolder(View itemView) {
            super(itemView);
            itemImage = (ImageView)itemView.findViewById(R.id.cardKahveFotografi);
            itemTitle = (TextView)itemView.findViewById(R.id.cardKullaniciIsmi);
            itemDetail = (TextView)itemView.findViewById(R.id.cardFalYorumu);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    int position = getAdapterPosition();

                    v.getContext().startActivity(new Intent(v.getContext(),FaldetayActivity.class));

                    /*
                    Snackbar.make(v, "item " + position,
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    */

                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_gelenfal, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.itemTitle.setText(titles[i]);
        viewHolder.itemDetail.setText(details[i]);
        viewHolder.itemImage.setImageResource(images[i]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}
