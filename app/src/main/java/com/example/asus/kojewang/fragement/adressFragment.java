package com.example.asus.kojewang.fragement;

import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.asus.kojewang.MainActivity;
import com.example.asus.kojewang.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2018/1/3.
 */

public class adressFragment extends Fragment {
    private ListView lv;
    private Spinner sp;
    private List<String> ContactsList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.adresslayout,container,false);
        lv = (ListView) view.findViewById(R.id.fragementListView);
        MainActivity activity = (MainActivity)getActivity();
//        for(int i = 0; i < 10; ++i){
//            ContactsList.add(""+i);
//        }
        //sp = view.findViewById(R.id.)
        adapter = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1, ContactsList);
        lv.setAdapter(adapter);
        return  view;
    }
    public void addList(List<String> contactsList){
        ContactsList.clear();
        ContactsList.addAll(contactsList);
        reflash();
    }
    public  void reflash(){
        adapter.notifyDataSetChanged();
    }
}
