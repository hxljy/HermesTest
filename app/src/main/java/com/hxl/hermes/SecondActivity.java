package com.hxl.hermes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.hxl.hermes.core.Hermes;
import com.hxl.hermes.core.service.HermesService;
import com.hxl.hermes.dao.IFileManager;
import com.hxl.hermes.dao.IUserManager;
import com.hxl.hermes.dao.Person;

/**
 * --
 * <p>
 * Created by hxl on 2018/8/28 0028.
 */
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Hermes.getDefault().connect(this, HermesService.class);
    }

    public void start(View view){
        IFileManager fileManager = Hermes.getDefault().getInstance(IFileManager.class);
        String path = fileManager.getPath();
        Toast.makeText(this, path, Toast.LENGTH_SHORT).show();

    }

    public void setInfo(View view){
        IUserManager instance = Hermes.getDefault().getInstance(IUserManager.class);
        instance.setPerson(new Person("hxl", "123456"));

//        IUserManager iUserManager = Hermes.getDefault().getInstance(IUserManager.class);
//        Person person = iUserManager.getPerson();
//        Toast.makeText(this, person.toString(), Toast.LENGTH_SHORT).show();
    }
}
