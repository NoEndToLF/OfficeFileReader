package com.wxystatic.officefilereader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wxystatic.officefilereader.permissionhelp.GetPermissionListener;
import com.wxystatic.officefilereader.permissionhelp.PermissionHelp;
import com.wxystatic.officefilereader.permissionhelp.PermissionType;
import com.wxystatic.officefilereaderlibrary.ExcelModel;
import com.wxystatic.officefilereaderlibrary.ExcelReaderHelp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_file)
    Button btnFile;
    @BindView(R.id.tv_all)
    TextView tvAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    private void showFile(String path) {
        File file = new File(path);
        try {
            InputStream inputStream = new FileInputStream(file);
            List<ExcelModel> list = ExcelReaderHelp.getInstance().readExcel(this, inputStream);
            Log.v("size=", list.size() + "");
            for (int i=0;i<list.size();i++ ) {
                if (i==0){
                    tvAll.setText(tvAll.getText()+list.get(i).getContent());
                }else{
                    if (list.get(i).getRow()==list.get(i-1).getRow()){
                        tvAll.setText(tvAll.getText()+"   "+list.get(i).getContent());
                    }else{
                        tvAll.setText(tvAll.getText()+"\n"+list.get(i).getContent());

                    }
                }

            }
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                String path = uri.getPath();
                Toast.makeText(this, path + "11111", Toast.LENGTH_SHORT).show();
                showFile(path);
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                String path = PathHelp.getPath(this, uri);
                showFile(path);
                Toast.makeText(this, path.toString(), Toast.LENGTH_SHORT).show();
            } else {//4.4一下系统调用方法
                String path = PathHelp.getRealPathFromURI(this, uri);
                showFile(path);
                Toast.makeText(MainActivity.this, path + "222222", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.btn_file)
    public void onViewClicked() {
        PermissionHelp.getPermission(this, PermissionType.STORAGE, 100, new GetPermissionListener() {
            @Override
            public void onSuccess() {
                showFileChooser();
            }
        });
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

}