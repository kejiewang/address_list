package com.example.asus.kojewang;

import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.database.Cursor;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.EventLog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.asus.kojewang.fragement.adressFragment;

import java.security.acl.Group;
import java.security.spec.EllipticCurve;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
     Toolbar toolbar;
    DrawerLayout drawer;
    adressFragment aF;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        toolbar = (Toolbar) findViewById(R.id.ToolBar);
        //toolbar.setNavigationIcon(R.drawable.ic_menu_manage);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //Toast.makeText(MainActivity.this,item.getTitle(),Toast.LENGTH_SHORT);
                drawer.closeDrawers();
                switch (item.getItemId()){
                    case R.id.nav_get:
                        break;
                    case  R.id.nav_set:
                        //dealWithContact();
                        aF = new adressFragment();
                        replaceFragment(aF);
                        break;
                   // case R.id
                }

                return true;
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.show_lines);
        }

    }


    //往通讯录之中添加东西，测试
    private void dealWithContact() {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int rawContactInsertIndex = 0;
        for(int i = 0; i<10; ++i){
            rawContactInsertIndex = ops.size();
            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).withValue(ContactsContract.RawContacts.ACCOUNT_TYPE,null).
            withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(ContactsContract.RawContacts.ACCOUNT_NAME,null).withYieldAllowed(true).build());

            /**开始往通讯录里面添加东西**/
            // 添加姓名
            ops.add(ContentProviderOperation
                    .newInsert(
                            android.provider.ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Contacts.Data.RAW_CONTACT_ID,
                            rawContactInsertIndex)
                    .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "name"+i)
                    .withYieldAllowed(true).build());
            // 添加号码
            ops.add(ContentProviderOperation
                    .newInsert(
                            android.provider.ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Contacts.Data.RAW_CONTACT_ID,
                            rawContactInsertIndex)
                    .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "num"+i)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, "").withYieldAllowed(true).build());
        }
        // 真正添加
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }


    private void replaceFragment(adressFragment adressFragment) {//碎片替换
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.centerLayout, adressFragment);
        transaction.commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.option_menu,menu);
        return super.onPrepareOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
           case android.R.id.home:
               drawer.openDrawer(GravityCompat.START);
               break;
           case R.id.getContacts:
               aF.addList(GetSysContacts());
               break;
       }
        return  true;
    }

    private ArrayList<ContactGroup> GetSysContacts() {//试验代码
        ArrayList<ContactGroup> temp = new ArrayList<ContactGroup>();
        Cursor cursor = getContentResolver().query(ContactsContract.Groups.CONTENT_URI, null, null, null, null);
        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE));
            long id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Groups._ID));
            ContactGroup cg = new ContactGroup(name ,id);

            //查询分组下面的所有联系人
            String[] RAW_PROJECTION = new String[] { ContactsContract.Data.RAW_CONTACT_ID, };
            String RAW_CONTACTS_WHERE = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + "=?"+ " and "+ ContactsContract.Data.MIMETYPE
                    + "=" + "'" + ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE + "'";
            // 通过分组的id 查询得到RAW_CONTACT_ID

            Cursor cursor2 = getContentResolver().query(

                    ContactsContract.Data.CONTENT_URI, RAW_PROJECTION,

                    RAW_CONTACTS_WHERE, new String[] { id + "" }, "data1 asc");

            ArrayList<ContactPerson> contactList = new ArrayList<ContactPerson>();
            while(cursor2.moveToNext()){
                int col = cursor2.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID);
                long raw_contact_id = cursor2.getLong(col);
                ContactPerson cp  = new ContactPerson();
                cp.setPersonId(raw_contact_id);
                Uri dataUri = Uri.parse("content://com.android.contacts/data");
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
                contactList.add(cp);
                dataCursor.close();
            }
            cursor2.close();
            cg.setPersonList(contactList);
            temp.add(cg);
        }
        cursor.close();
        //cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI,)
        //查询所有的联系人
        ContactGroup cg = new  ContactGroup("系统全部",-1);
        ArrayList<ContactPerson> al = new ArrayList<ContactPerson>();
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        if(cursor!= null){
            while(cursor.moveToNext()){
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                long id = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
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

        return temp;
    }

}
