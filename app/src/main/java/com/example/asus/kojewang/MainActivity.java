package com.example.asus.kojewang;

import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.database.Cursor;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.asus.kojewang.fragement.adressFragment;

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
              // adressFragment adressfragment =getSupportFragmentManager().findFragmentById(R.id.);
               aF.addList(GetSysContacts());
               break;
       }
        return  true;
    }

    private ArrayList<String> GetSysContacts() {//试验代码
        ArrayList<String> temp =  new ArrayList<String>();
//        Cursor cursor = null;
//        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
//        if(cursor != null){
//            while(cursor.moveToNext()) {
//                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                String group = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.DISPLAY_NAME));
//                temp.add(name + '\n' +number+ '\n'+group);
//            }
//
//        }
        Cursor cursor = null;


        try {

            cursor = getContentResolver().query(ContactsContract.Groups.CONTENT_URI,null, null, null, null);
            while (cursor.moveToNext()) {
                //GroupEntity ge = new GroupEntity();
                int groupId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Groups._ID)); // 组id
                String groupName = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE)); // 组名
                temp.add(""+groupId + "\n"+groupName);
            }
            return temp;
            //return groupList;


        } finally {

            if (cursor != null) {

                cursor.close();

            }


        //return temp;
    }
}

}
