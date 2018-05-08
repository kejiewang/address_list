package com.example.asus.kojewang.fragement;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.asus.kojewang.ContactGroup;
import com.example.asus.kojewang.MainActivity;
import com.example.asus.kojewang.R;

import java.util.ArrayList;

/**
 * Created by asus on 2018/1/14.
 */

public class groupFragment extends Fragment {
    ListView lv;
    MainActivity activity;
    ArrayList<ContactGroup> gp = new ArrayList<ContactGroup>();
    ArrayList<String> GroupList = new ArrayList<String>();
    ArrayAdapter<String> adapter ;
    int Position;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grouplayout,container,false);
        lv = (ListView) view.findViewById(R.id.groupListView);
        activity = (MainActivity)getActivity();
        //GroupList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1, GroupList);
        lv.setAdapter(adapter);
        //gp = new ArrayList<ContactGroup>();
        registerForContextMenu(lv);
        return  view;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.editgroup:
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("编辑组别");
                LinearLayout layout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.groupcontextlayout,null,false);
                builder.setView(layout);
                final EditText ed1 = (EditText) layout.findViewById(R.id.editTextGroupName);

                ed1.setText(gp.get(Position).getGroupName());

                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //cp.setName(ed1.getText().toString());
                        gp.get(Position).setGroupName(ed1.getText().toString());
                        GroupList.set(Position,gp.get(Position).getGroupName());
                    }
                }).setNegativeButton("cancel",null).show();
                activity.aF.addList(gp);

                reflash();
                //adapter.notifyDataSetChanged();
                break;
            case R.id.deletegroup:
                gp.remove(Position);
                activity.aF.addList(gp);
                GroupList.remove(Position);
                //adapter.notifyDataSetChanged();
                reflash();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater menuInflater = new MenuInflater(activity);
        menuInflater.inflate(R.menu.groupmenu,menu);
        Position = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    public void UpdateGroup(ArrayList<ContactGroup> thegp)
    {
        gp.clear();
        gp.addAll(thegp);
        GroupList.clear();
        for(ContactGroup cg : gp){
            GroupList.add(cg.getGroupName());
        }
        //reflash();
        //adapter.notifyDataSetChanged();
    }
    public  void  reflash()
    {
        adapter.notifyDataSetChanged();
        new Thread(new Runnable() {
            @Override
            public void run() {
                activity.WriteToDataBase();
            }
        });

    }
}
