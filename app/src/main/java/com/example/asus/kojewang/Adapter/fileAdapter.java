package com.example.asus.kojewang.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.kojewang.R;

import java.io.File;
import java.util.List;

/**
 * Created by Kojewang on 2017/12/27
 * Adater used to inflate to the file.
 */

/**
    文件适配器
 */
public class fileAdapter extends ArrayAdapter {

    private LayoutInflater mInflater; //用来保存上下文菜单
    private int[] mIcons;
    private  List<String> items;
    private  List<String> paths;
    public fileAdapter(Context context, List<String> itemList, List<String> pathList) {
        super(context, android.R.layout.simple_list_item_1, itemList);
        mInflater = LayoutInflater.from(context);
        items = itemList;
        paths = pathList;
        mIcons = new int[]{ R.drawable.back01, R.drawable.back02, R.drawable.folder, R.drawable.doc};
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView text;
        ImageView icon;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.file_row,null,false);
        }
        text = (TextView) convertView.findViewById(R.id.textView2);
        icon = (ImageView) convertView.findViewById(R.id.imageView);
        File f = new File(paths.get(position).toString());
        /** 根据目录中的items来设置相应的图标*/
        if(items.get(position).toString().equals("b1")){ //设置返回根目录
            text.setText("Back to /");
            icon.setImageResource(mIcons[0]);
        }else if(items.get(position).toString().equals("b2")){//设置返回上一层目录
            text.setText("Back to ..");
            icon.setImageResource(mIcons[1]);
        }else{//文件夹的目录
            text.setText(f.getName());
            if(f.isDirectory()){
                icon.setImageResource(mIcons[2]);
            }else{
                icon.setImageResource(mIcons[3]);
            }
        }
        return convertView;
    }
}
