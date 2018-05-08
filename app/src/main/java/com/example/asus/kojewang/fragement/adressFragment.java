package com.example.asus.kojewang.fragement;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.LinearGradient;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.example.asus.kojewang.Adapter.PersonAdapter;
import com.example.asus.kojewang.ContactGroup;
import com.example.asus.kojewang.ContactPerson;
import com.example.asus.kojewang.MainActivity;
import com.example.asus.kojewang.R;

import java.io.File;
import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


/**
 * Created by asus on 2018/1/3.
 */

public class adressFragment extends Fragment {
    private ListView lv;
    private ArrayList<ContactGroup> contactGroupArrayList = new ArrayList<ContactGroup>();
    private ArrayList<ContactPerson> contactPersons = new ArrayList<>();
    private Spinner sp;
    private List<String> ContactsList = new ArrayList<>();
    private List<String> ContactsListspinner = new ArrayList<>();
    private ArrayAdapter<String> spinnerArrayAdapter;
   // private ArrayAdapter<String> adapter;
    private PersonAdapter adapter;
    int Position;
    MainActivity activity;

    public ArrayList<ContactGroup> getContactGroupArrayList() {
        return contactGroupArrayList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.adresslayout,container,false);
        lv = (ListView) view.findViewById(R.id.fragementListView);
        activity = (MainActivity)getActivity();
        sp = (Spinner) view.findViewById(R.id.spinner);
        //adapter = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1, ContactsList);
        adapter = new PersonAdapter(activity,contactPersons);
        lv.setAdapter(adapter);

        //注册上下文菜单
        registerForContextMenu(lv);

        spinnerArrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, ContactsListspinner);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(spinnerArrayAdapter);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ContactGroup gp = contactGroupArrayList.get(position);
                //ContactsList.clear();
                contactPersons.clear();
                for(ContactPerson item : gp.getPersonList()){
//                    if(TextUtils.isEmpty(item.getName())||TextUtils.isEmpty(item.getNum()))
//                        continue;
                    //ContactsList.add(item.getName() +"\n" +  item.getNum());
                    contactPersons.add(item);
                }
                reflash();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if(!contactGroupArrayList.isEmpty()){
                    ContactGroup gp = contactGroupArrayList.get(0);
                    //ContactsList.clear();
                    contactPersons.clear();
                    for(ContactPerson item : gp.getPersonList()){
//                        if(TextUtils.isEmpty(item.getName())||TextUtils.isEmpty(item.getNum()))
//                            continue;
                        //ContactsList.add(item.getName() +"\n" +  item.getNum());
                        contactPersons.add(item);
                    }
                    reflash();
                }
            }
        });

        return  view;
    }

    //创建上下文菜单
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater menuInflater = new MenuInflater(activity);
        menuInflater.inflate(R.menu.contextmenu,menu);
        Position = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int d = sp.getSelectedItemPosition();
       final ContactGroup cg = contactGroupArrayList.get(d);
        final ContactPerson cp = cg.getPersonList().get(Position);
        switch (item.getItemId()){
            case R.id.edit:// 编辑
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("编辑对话框");
                LinearLayout layout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.diagloglayout,null,false);
                builder.setView(layout);
                final EditText ed1 = (EditText) layout.findViewById(R.id.editTextName);
                final EditText ed2 = (EditText) layout.findViewById(R.id.editTextPhone);
                ed1.setText(cp.getName());
                ed2.setText(cp.getNum());
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                     public void onClick(DialogInterface dialog, int which) {
                        cp.setName(ed1.getText().toString());
                        cp.setNum(ed2.getText().toString());
                    }
                }).setNegativeButton("cancel",null).show();
                activity.gf.UpdateGroup(contactGroupArrayList);

                //ContactsList.set(Position,cp.getName()+"\n" + cp.getNum());
                contactPersons.set(Position,cp);
                adapter.notifyDataSetChanged();
                break;
            case R.id.delete:
                cg.getPersonList().remove(Position);
                activity.gf.UpdateGroup(contactGroupArrayList);
                //ContactsList.remove(Position);
                contactPersons.remove(Position);
                adapter.notifyDataSetChanged();
                break;
            case R.id.phone:
                Uri uri;
                Intent i;
                uri = Uri.parse("tel:"+cp.getNum());
                i = new Intent(Intent.ACTION_DIAL,uri);
                startActivity(i);

                break;
            case R.id.sms:
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+cp.getNum()));
                intent.putExtra("sms_body",cp.getName()+"你好");
                startActivity(intent);
                break;
        }

        return super.onContextItemSelected(item);
    }

    private boolean hasGroup(ContactGroup cg)
    {
        boolean flag = true;
        Cursor cursor = activity.getContentResolver().query(ContactsContract.Groups.CONTENT_URI,null, ContactsContract.Groups.TITLE +" = ? and "+ ContactsContract.Groups.DELETED+" = ?",new String[]{cg.getGroupName(),"0"},null);
        while (cursor.moveToNext())
            cg.setGroupId(cursor.getLong(cursor.getColumnIndex(ContactsContract.Groups._ID)));
        flag = cursor.getCount() >= 1;
        cursor.close();
        return  flag;
    }
    private boolean hasPerson(ContactPerson cp)
    {
        String name = cp.getName();
        String num = cp.getNum();
        if(num == null)
            num = new String("");
        Cursor cursor1 = activity.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null,  ContactsContract.Data.MIMETYPE + " =? and " + ContactsContract.CommonDataKinds.Phone.NUMBER+"=?",new String[]{ ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,num},null);
        boolean flag = cursor1.getCount() >= 1;
       // cursor1.moveToFirst();
        while(cursor1.moveToNext()){
            cp.setName(cursor1.getString(cursor1.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)));
            cp.setPersonId(cursor1.getLong(cursor1.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID)));
        }

        cursor1.close();
        return flag;
    }

    private void  addGroup(ContactGroup cg){

        ContentValues valuess = new ContentValues();
        valuess.put(ContactsContract.Groups.TITLE, cg.getGroupName());
        Uri uri = activity.getContentResolver().insert(ContactsContract.Groups.CONTENT_URI, valuess);
        cg.setGroupId(ContentUris.parseId(uri));
    }
    //
    private  void  addPersonToGroup(ContactPerson cp, ContactGroup cg)
    {
        long personid = cp.getPersonId();// 联系人ID
        long groupid =  cg.getGroupId();// 分组ID

        Cursor cur = activity.getContentResolver().query(ContactsContract.Data.CONTENT_URI,null, ContactsContract.Data.RAW_CONTACT_ID+" = ? and "+ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID+" = ? and "+ ContactsContract.Data.MIMETYPE+" = ?",new  String[]{""+cp.getPersonId(),""+cg.getGroupId(),ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE},null);
        if(cur.getCount() >=1)
                return;
        ContentValues values = new ContentValues();
        values.put(ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID, personid);
        values.put(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, groupid);
        values.put(ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE,
                ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE);
        activity.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
    }
    private void addPerson(ContactPerson cp)
    {
        ContentValues cv = new ContentValues();
        Uri raw_content_uri = activity.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI,cv);
        long raw_contact_id = ContentUris.parseId(raw_content_uri);
        cp.setPersonId(raw_contact_id);
        cv.clear();
        cv.put(ContactsContract.Data.RAW_CONTACT_ID, raw_contact_id);
        cv.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        cv.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, cp.getName());
        activity.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, cv);
        cv.clear();
        cv.put(ContactsContract.Data.RAW_CONTACT_ID, raw_contact_id);
        cv.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        cv.put(ContactsContract.CommonDataKinds.Phone.NUMBER, cp.getNum());
        cv.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        activity.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, cv);
        cv.clear();
    }

    private int writeToSys(ArrayList<ContactGroup> arrayList){
        int num = 0;
        int nothas = 0;
        for(ContactGroup cg : arrayList){
            if(cg.getGroupName().equals("系统全部")){
                for(ContactPerson cp :cg.getPersonList()){
                    if(hasPerson(cp)){
                        num++;
                    }else{
                        nothas++;
                        addPerson(cp);
                    }
                }
            }else{
                if(!hasGroup(cg)){
                    addGroup(cg);
                }

                for (ContactPerson cp : cg.getPersonList()){
                    if(hasPerson(cp)){
                        num++;
                    }else{
                        nothas++;
                        addPerson(cp);
                    }
                    addPersonToGroup(cp,cg);
                }
            }
        }
        return nothas;
    }
    public void WritetoSys()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final  int num = writeToSys(contactGroupArrayList);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity,"总共更新"+num+"的数据",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
    public void outputFile(String path)
    {
        if(!contactGroupArrayList.isEmpty()){
            //写入到相应的文件里去
            //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final String fileName = new String(path +"/" + String.valueOf(System.currentTimeMillis())+".xls");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        WritableWorkbook wwb = Workbook.createWorkbook(new File(fileName));
                        WritableSheet ws = wwb.createSheet("sheet1",0);
                        ws.addCell(new Label(0,0,"姓名"));
                        ws.addCell(new Label(1,0,"号码"));
                        ws.addCell(new Label(2,0, "群组"));
                        int cur = 1;
                        for(ContactGroup item: contactGroupArrayList){
                            for(ContactPerson itemit : item.getPersonList()){
                                Label label = new Label(0,cur,itemit.getName());
                                ws.addCell(label);
                                Label label2 = new Label(1,cur,itemit.getNum());
                                ws.addCell(label2);
                                Label label3 = new Label(2,cur,item.getGroupName());
                                ws.addCell(label3);
                                cur++;
                            }
                        }
                        wwb.write();
                        wwb.close();
                    }catch (Exception e){
                        Log.d("write erro", e.toString());
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity,"导出完成",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }).start();

        }
    }
    public void addList(ArrayList<ContactGroup> contactGroups){
         //联系人信息的获取
         ContactsListspinner.clear();
         contactPersons.clear();
         contactGroupArrayList.clear();
         contactGroupArrayList.addAll(contactGroups);

         dowithGroupsList();
         reflash();
    }

    private void dowithGroupsList() {
        for(ContactGroup item : contactGroupArrayList){
            ContactsListspinner.add(item.getGroupName());
        }
        spinnerArrayAdapter.notifyDataSetChanged();
        sp.setSelection(0);
        if(!contactGroupArrayList.isEmpty()){
            ContactGroup gp = contactGroupArrayList.get(0);
            contactPersons.clear();
            for(ContactPerson item : gp.getPersonList()){
                contactPersons.add(item);
            }
           // reflash();
        }
        reflash();
    }

    public  void reflash(){
        adapter.notifyDataSetChanged();
        new Thread(new Runnable() {
            @Override
            public void run() {
                activity.WriteToDataBase();
            }
        }).start();

    }
}
