package com.example.asus.kojewang.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.asus.kojewang.ContactPerson;
import com.example.asus.kojewang.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2018/1/15.
 */

public class PersonAdapter extends ArrayAdapter {

    ArrayList<ContactPerson> list;
    Context context;
    public PersonAdapter(Context context, ArrayList<ContactPerson> list) {
        super(context,android.R.layout.simple_list_item_1, list);
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        ViewHolder viewHolder;
        if(convertView == null){
            rowView = LayoutInflater.from(context).inflate(R.layout.personitem,null,false);
            viewHolder = new ViewHolder();
            viewHolder.iv = (ImageView) rowView.findViewById(R.id.imageView2);
            viewHolder.tvname = (TextView) rowView.findViewById(R.id.textitemname);
            viewHolder.tvnum = (TextView) rowView.findViewById(R.id.textitemnum);
            rowView.setTag(viewHolder);
        }else{
            rowView = convertView;
            viewHolder = (ViewHolder) rowView.getTag();
        }
        viewHolder.tvname.setText(list.get(position).getName());
        viewHolder.tvnum.setText(list.get(position).getNum());
        final  int pos = position;
        if(TextUtils.isEmpty(list.get(position).getNum()))
            viewHolder.iv.setImageResource(R.drawable.nocall);
        else {
            viewHolder.iv.setImageResource(R.drawable.call3);

        }
        viewHolder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(list.get(pos).getNum())) {
                    Uri uri;
                    Intent i;
                    uri = Uri.parse("tel:" + list.get(pos).getNum());
                    i = new Intent(Intent.ACTION_CALL, uri);
                    context.startActivity(i);
                }
            }
        });
        return  rowView;
    }


}

