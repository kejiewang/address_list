package com.example.asus.kojewang;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.kojewang.Adapter.fileAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2018/1/5.
 */

public class fileActivity extends AppCompatActivity {
    ListView lv1;
    private List<String> items = null;
    private List<String> paths = null;
    private Button bt;
    //private String rootPath = "/";
    private String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private TextView mPath;
    private int data;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        lv1 = (ListView) findViewById(R.id.FileListView1);//
        bt= (Button)findViewById(R.id.button2);
        Intent intent = getIntent();
        data = intent.getIntExtra("file",1);
        if(data != 1){
            bt.setVisibility(View.GONE);
        }
        mPath = (TextView) findViewById(R.id.FiletextView);
        String main= Environment.getExternalStorageDirectory().getPath()+File.separator+"Kojewang";
        File destDir = new File(main);
        if (!destDir.exists()) {
            destDir.mkdirs();//在根创建了文件夹hello
        }
        destDir.mkdirs();
        showFileDir(main);
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dealPathPosition(position);
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("filepath", mPath.getText());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


    boolean isHasSdcard(){
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    private void dealPathPosition(int position) {
        File file = new File(paths.get(position));
        if(file.canRead()){
            if(file.isDirectory()){
                //如果是文件夹
                showFileDir(paths.get(position));
            }else{
                if(data == 0){
                    final  int pos = position;
                    //添加对话框
                    new AlertDialog.Builder(fileActivity.this).setMessage("确认文件").setNegativeButton("cancel",null).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.putExtra("filepath", paths.get(pos));
                            fileActivity.this.setResult(RESULT_OK, intent);
                            fileActivity.this.finish();
                        }
                    }).show();
                    return;
                }
               // else
                openFile(file);
            }
        }else{
            new AlertDialog.Builder(this).setTitle("警告").setMessage("权限不足").setPositiveButton("OK",null).show();
        }
    }

    private void openFile(File f) {
        Intent intent  = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(f),"*/*");
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            Uri uri = data.getData();
            Toast.makeText(fileActivity.this,uri.toString(),Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showFileDir(String filePath) {
        //设定当前路径
        mPath.setText(filePath);
        items = new ArrayList<String>();
        paths = new ArrayList<String>();
        File f = new File(filePath);
        if(f==null){
            f = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        }
        File[] files = f.listFiles(); //通过listFiles获取当前File(目录)的所有文件及子目录

        if (!filePath.equals(rootPath)) { //如果不是根目录
            //b1设定为根目录
            items.add("b1");
            paths.add(rootPath);
            //b2设定为返回上一层
            items.add("b2");
            paths.add(f.getParent());
        }
        for(int i = 0; i < files.length; i++){
            File file = files[i];
            if(file.isFile()){
                String temp = file.getName();
                if(!temp.endsWith("xls")){
                    continue;
                }
            }
            items.add(file.getName());
            paths.add(file.getPath());
        }
        lv1.setAdapter(new fileAdapter(this, items, paths));
    }

}
