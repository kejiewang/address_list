package com.example.asus.kojewang;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.LinearGradient;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuAdapter;
import android.support.v7.widget.Toolbar;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.kojewang.DataBase.ContextDataBase;
import com.example.asus.kojewang.fragement.adressFragment;
import com.example.asus.kojewang.fragement.groupFragment;

import java.io.File;
import java.security.acl.Group;
import java.security.spec.EllipticCurve;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.Inflater;

import jxl.Sheet;
import jxl.Workbook;

public class MainActivity extends AppCompatActivity {
     Toolbar toolbar;
    DrawerLayout drawer;
    public adressFragment aF;
    public groupFragment gf;
    ContextDataBase db;
    int status = 0;
    ArrayList<ContactGroup> arrayListGroup;
    NavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        toolbar = (Toolbar) findViewById(R.id.ToolBar);
        //toolbar.setNavigationIcon(R.drawable.ic_menu_manage);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        db = new ContextDataBase(this);
        navView = (NavigationView) findViewById(R.id.nav_view);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //Toast.makeText(MainActivity.this,item.getTitle(),Toast.LENGTH_SHORT);
                drawer.closeDrawers();
                switch (item.getItemId()){
                    case R.id.nav_get: //进入到文件浏览器
                        Intent intent = new Intent(MainActivity.this,fileActivity.class);
                        intent.putExtra("file",-1);//存放设置文件的目的，0为获取文件，1为获取文件夹的目录
                        startActivityForResult(intent,1);
                        break;
                    case  R.id.nav_set:
                        //MainActivity.this.openOptionsMenu();
                        status = 0;
                       replaceFragment(aF);
                        break;
                    case  R.id.nav_group:
                        //MainActivity.this.closeOptionsMenu();
                       // gf.UpdateGroup(aF.getContactGroupArrayList());
                        status = 1;
                        replaceFragment(gf);
                       // gf.UpdateGroup(aF.getContactGroupArrayList());
                        break;
                }
                return true;
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.show_lines);
        }
        aF = new adressFragment();
        gf = new groupFragment();
        replaceFragment(aF);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<ContactGroup> grouplist = ReadFromDataBase();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        aF.addList(grouplist);
                        gf.UpdateGroup(grouplist);
                    }
                });
            }
        }).start();

    }

    private ArrayList<ContactGroup> ReadFromDataBase()
    {
        SQLiteDatabase db2 = db.getReadableDatabase();
        Cursor cursor = db2.rawQuery("select * from " + ContextDataBase.TABLE_NAME,null);
        HashMap<String,Integer> this_map = new HashMap<String, Integer>();
        ArrayList<ContactGroup> GroupList = new ArrayList<ContactGroup>();
        int Pos = 0;
        while(cursor.moveToNext())
        {
                String name  = cursor.getString(cursor.getColumnIndex(ContextDataBase.NAME));//获取姓名元素
                String num = cursor.getString(cursor.getColumnIndex(ContextDataBase.NUM));//获取号码
                String groupname = cursor.getString(cursor.getColumnIndex(ContextDataBase.GROUP)); //获取分组
                if(groupname.equals("")){
                    groupname = "系统全部";
                }
                int pp;
                if(this_map.containsKey(groupname)){ //hashmap中存在对应的值
                    pp = this_map.get(groupname).intValue();
                    ContactPerson cp = new ContactPerson();
                    cp.setGroup(groupname);
                    cp.setName(name);
                    cp.setNum(num);
                    GroupList.get(pp).getPersonList().add(cp);
                }else{
                    this_map.put(groupname,Pos);
                    Pos ++ ;
                    ContactGroup cg = new ContactGroup();
                    cg.setGroupName(groupname);
                    ContactPerson cp = new ContactPerson();
                    cp.setGroup(groupname);
                    cp.setName(name);
                    cp.setNum(num);
                    cg.getPersonList().add(cp);
                    GroupList.add(cg);
                }
        }
        return  GroupList;
    }

    public void WriteToDataBase()
    {
        SQLiteDatabase db2 = db.getWritableDatabase();
        db2.delete(ContextDataBase.TABLE_NAME,null,null);
        ArrayList<ContactGroup> groupList = aF.getContactGroupArrayList();
        for(ContactGroup cg : groupList){
            for(ContactPerson cp : cg.getPersonList()){
                ContentValues cv = new ContentValues();
                cv.put(ContextDataBase.NAME, cp.getName());
                cv.put(ContextDataBase.NUM, cp.getNum());
                cv.put(ContextDataBase.GROUP,cg.getGroupName());
                db2.insert(ContextDataBase.TABLE_NAME,null,cv);
            }
        }
    }

    @Override
    protected void onDestroy() {
        //将数据更新到数据

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setMessage("是否退出？").setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WriteToDataBase();
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.this.finish();
                            }
                        });
                    }
                }).start();
                //MainActivity.this.finish();
            }
        }).setNegativeButton("cancel",null).show();

       // super.onBackPressed();
    }

    private void replaceFragment(Fragment adressFragment) {//碎片替换
        navView.setCheckedItem(R.id.nav_set); //设置点击的默认选项
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.centerLayout, adressFragment);
        transaction.commit();
        transaction.show(adressFragment);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.option_menu,menu);
        return super.onPrepareOptionsMenu(menu);
    }

    private void resetData()
    {
        SQLiteDatabase db2 = db.getWritableDatabase();
        db2.delete(ContextDataBase.TABLE_NAME,null,null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       switch (item.getItemId()){
           case android.R.id.home:
               drawer.openDrawer(GravityCompat.START);
               break;
           case R.id.getContacts:
               if (status ==  1) break;
             new Thread(new Runnable() {
                   @Override
                   public void run() {
                       final ArrayList<ContactGroup> list = GetSysContacts();
                       MainActivity.this.runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               aF.addList(list);
                               gf.UpdateGroup(list);
                           }
                       });
                   }
               }).start();
               //aF.addList(GetSysContacts());
               break;
           case  R.id.setContacts:
               if(status == 1) break;
               aF.WritetoSys();
               break;
           case  R.id.getFilePath:
               if (status == 1 ) break;
               Intent intent = new Intent(MainActivity.this,fileActivity.class);
               intent.putExtra("file",1);
               startActivityForResult(intent,2);
               break;
           case R.id.pushToFile:
               if(status == 1) break;
               Intent intent2 = new Intent(MainActivity.this,fileActivity.class);
               intent2.putExtra("file",0);
               startActivityForResult(intent2,1);
               break;
           case R.id.reset:
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       resetData();

                       final ArrayList<ContactGroup> grouplist = ReadFromDataBase();
                       MainActivity.this.runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               aF.addList(grouplist);
                               gf.UpdateGroup(grouplist);
                           }
                       });
                   }
               }).start();
               break;
           case R.id.getFromSms:  //获取短信
               View view = LayoutInflater.from(this).inflate(R.layout.smslayout,null,false);
               final DatePicker dp = (DatePicker) view.findViewById(R.id.datePicker3);
               final EditText ed = (EditText) view.findViewById(R.id.editTextsmsgroupname);
               new AlertDialog.Builder(this).setTitle("设置监听短信的起始时间").setView(view).setNegativeButton("cancel",null).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                       final java.sql.Date date2 = new java.sql.Date(dp.getYear()-1900,dp.getMonth(),dp.getDayOfMonth());
                       new Thread(new Runnable() {
                           @Override
                           public void run() {
                               final ArrayList<ContactGroup> cg = ReadFromSms(date2.getTime(),ed.getText().toString());
                               MainActivity.this.runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       aF.addList(cg);
                                       gf.UpdateGroup(cg);
                                   }
                               });
                           }
                       }).start();

                   }
               }).show();

               break;
       }
        return  true;
    }
    private  ArrayList<ContactGroup>ReadFromSms(long time,String name)
    {
        ArrayList<ContactGroup> cglist =new ArrayList<ContactGroup>();
        ContactGroup cg = new ContactGroup();
        cg.setGroupName(name);
        Cursor cursor = MainActivity.this.getContentResolver().query(Uri.parse("content://sms/inbox"), new String[]{"address", "date", "body", "type"},"date >?" ,new String[]{"" +time},null);
        long data ;
        String adress = new String();
        String message = new String();
        HashSet<String> hs = new HashSet<>();
        while (cursor.moveToNext()){
            adress = cursor.getString(0);
            message = cursor.getString(2);

            if(!message.startsWith("#")){
                continue;
            }
            if(hs.contains(adress))
                continue;
            hs.add(adress);
            ContactPerson cp = new ContactPerson();
            cp.setNum(adress);
            cp.setName(message.replace("#",""));
            cg.getPersonList().add(cp);
        }
        cglist.add(cg);
        return  cglist;
    }
    private ArrayList<ContactGroup> GetSysContacts() {//试验代码
        ArrayList<ContactGroup> temp = new ArrayList<ContactGroup>();
        //查询所有的联系人
        ContactGroup cg = new  ContactGroup("系统全部",-1);
        ArrayList<ContactPerson> al = new ArrayList<ContactPerson>();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        if(cursor!= null){
            while(cursor.moveToNext()){
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                long id = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
                //测试他的分组信息
                //getContentResolver().query(ContactsContract.Data.CONTENT_URI,null,"raw_contact_id = ?"+ "and" +"",null);


                String num = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String group = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.IN_VISIBLE_GROUP));
                ContactPerson cp = new ContactPerson();
                cp.setName(name);
                cp.setNum(num);
                cp.setPersonId(id);
                cp.setGroup(group);
                al.add(cp);
            }
        }
        cg.setPersonList(al);
        temp.add(cg);
        cursor.close();

        cursor = getContentResolver().query(ContactsContract.Groups.CONTENT_URI, null, null, null, null);
        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE));
            int isdeleted = cursor.getInt(cursor.getColumnIndex(ContactsContract.Groups.DELETED));
            if(isdeleted == 1){
                continue;
            }
            long id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Groups._ID));
            cg = new ContactGroup(name ,id);

            //查询分组下面的所有联系人
            String[] RAW_PROJECTION = new String[] { ContactsContract.Data.RAW_CONTACT_ID, };
            String RAW_CONTACTS_WHERE = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + "=?"+ " and "+ ContactsContract.Data.MIMETYPE
                    + "=" + "'" + ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE + "'";
            // 通过分组的id 查询得到RAW_CONTACT_ID

            Cursor cursor2 = getContentResolver().query(

                    ContactsContract.Data.CONTENT_URI, RAW_PROJECTION,

                    RAW_CONTACTS_WHERE, new String[] { id + "" }, "data1 asc");

            ArrayList<ContactPerson> contactList = new ArrayList<ContactPerson>();
            HashMap<Long,Integer> map = new HashMap<Long, Integer>();
            while(cursor2.moveToNext()){
                int col = cursor2.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID);
                long raw_contact_id = cursor2.getLong(col);
                if(map.containsKey(raw_contact_id)){
                    continue;
                }else{
                    map.put(raw_contact_id, new Integer("1"));
                }
                ContactPerson cp  = new ContactPerson();
                cp.setPersonId(raw_contact_id);
                Uri dataUri = Uri.parse("content://com.android.contacts/data");
                Uri uu = ContactsContract.Data.CONTENT_URI;

                Cursor dataCursor = getContentResolver().query(dataUri,null, "raw_contact_id=?",new String[] { raw_contact_id + "" }, null);
                while (dataCursor.moveToNext()) {   //设置name
                    String data1 = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                    String mime = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                    if ("vnd.android.cursor.item/phone_v2".equals(mime)) {
                        cp.setNum(data1);
                    } else if ("vnd.android.cursor.item/name".equals(mime)) {
                        cp.setName(data1);
                    }else if("vnd.android.cursor.item/group_membership".equals(mime)){
                        cp.setGroup(data1);
                    }
                }
                //Cursor otherData = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,null,"contact_id=?",new  String[]{raw_contact_id + ""},null);
                //int a = otherData.getCount();
               // int b = a+1;
                //String data = new String();
//                while(otherData.moveToNext()){
//                    data = otherData.getString(otherData.getColumnIndex(ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY));
//                }

                contactList.add(cp);
                dataCursor.close();
            }
            cursor2.close();
            cg.setPersonList(contactList);
            temp.add(cg);
        }
        cursor.close();
        return temp;
    }

    //读取Execl中的内容
    ArrayList<ContactGroup> readFromExcel(String path)
    {
        ArrayList<ContactGroup> GroupList = new ArrayList<ContactGroup>();
        try{
            Workbook book = Workbook.getWorkbook(new File(path));
            Sheet sheet = book.getSheet(0);
            HashMap<String,Integer> this_map = new HashMap<String, Integer>();
            int Rows = sheet.getRows();
            int Cols = sheet.getColumns();
            int Pos = 0;
            for(int i = 1; i <= Rows; ++i){
                String name  = sheet.getCell(0,i).getContents();//获取姓名元素
                String num = sheet.getCell(1,i).getContents(); //获取号码
                String groupname = sheet.getCell(2,i).getContents(); //获取分组
                if(groupname.equals("")){
                    groupname = "系统全部";
                }
                int pp;
                if(this_map.containsKey(groupname)){ //hashmap中不存在对应的值
                    pp = this_map.get(groupname).intValue();
                    ContactPerson cp = new ContactPerson();
                    cp.setGroup(groupname);
                    cp.setName(name);
                    cp.setNum(num);
                    GroupList.get(pp).getPersonList().add(cp);
                }else{
                    this_map.put(groupname,Pos);
                    Pos ++ ;
                    ContactGroup cg = new ContactGroup();
                    cg.setGroupName(groupname);
                    ContactPerson cp = new ContactPerson();
                    cp.setGroup(groupname);
                    cp.setName(name);
                    cp.setNum(num);
                    cg.getPersonList().add(cp);
                    GroupList.add(cg);
                }
            }
        }catch (Exception e){
            Log.e("readExcel erro", e.toString() );
        }
        return  GroupList;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        navView.setCheckedItem(R.id.nav_set);
        //navView.setCheckedItem(1);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
               final String p = data.getStringExtra("filepath");
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       final ArrayList<ContactGroup> cg = readFromExcel(p);

                       MainActivity.this.runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               aF.addList(cg);
                               gf.UpdateGroup(cg);
                           }
                       });
                   }
               }).start();
            }


        }
        if(requestCode == 2) {
            if(resultCode == RESULT_OK)
            {
                String path = data.getStringExtra("filepath");
                aF.outputFile(path);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
