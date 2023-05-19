package com.mesi.gymusers;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView fname;
    TextView ldate;
    ImageView flag;
    ImageView uImg;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        fname = itemView.findViewById(R.id.fname);
        ldate = itemView.findViewById(R.id.ldate);
        flag = itemView.findViewById(R.id.flag);
        uImg = itemView.findViewById(R.id.uImg);
    }
}
