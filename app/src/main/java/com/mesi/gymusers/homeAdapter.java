package com.mesi.gymusers;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class homeAdapter extends BaseAdapter {

    private Context mContext;
    private List<UserDAO> userList;

    public homeAdapter(Context context, List<UserDAO> userList){
        this.mContext = context;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int posision) {
        return userList.get(posision).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = View.inflate(mContext, R.layout.home_list_view, null);

        TextView fname = v.findViewById(R.id.fname);
        fname.setText(userList.get(i).getFname());

        TextView dayLeft = v.findViewById(R.id.ldate);
        dayLeft.setText(String.valueOf(Math.abs(calculateUserReDate(userList.get(i).getDate()))));

        ImageView flag = v.findViewById(R.id.flag);
        flag.setImageResource(R.drawable.g);

        ImageView uPro = v.findViewById(R.id.uImg);

        Picasso.get().load(new File(userList.get(i).getImg())).into(uPro);

        if (calculateUserReDate(userList.get(i).getDate())<3)
            flag.setImageResource(R.drawable.r);


        return v;
    }

    private long calculateUserReDate(String date) {

        //Current date
        Calendar currentDate = Calendar.getInstance();

        long currentLong = currentDate.getTimeInMillis();

        char d = date.charAt(0);
        char dd = date.charAt(1);

        char m = date.charAt(3);
        char mm = date.charAt(4);

        char y = date.charAt(6);
        char yy = date.charAt(7);
        char yyy = date.charAt(8);
        char yyyy = date.charAt(9);

        String fDate = ""+d+dd;

        String fMonth = ""+m+mm;

        String fYear = ""+y+yy+yyy+yyyy;

        //User end date
        Calendar lastDate = Calendar.getInstance();

        lastDate.set(Calendar.DATE, Integer.parseInt(fDate));
        lastDate.set(Calendar.MONTH,  Integer.parseInt(fMonth)-1);
        lastDate.set(Calendar.YEAR, Integer.parseInt(fYear));

        long finalLong = lastDate.getTimeInMillis();

        return (finalLong-currentLong)/86400000;
    }
}
