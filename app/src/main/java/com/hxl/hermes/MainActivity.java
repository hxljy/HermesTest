package com.hxl.hermes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.hxl.hermes.core.Hermes;
import com.hxl.hermes.dao.FileManager;
import com.hxl.hermes.dao.Person;
import com.hxl.hermes.dao.UserManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Hermes.getDefault().register(UserManager.class);
        Hermes.getDefault().register(FileManager.class);
    }

    public void start(View view){
        FileManager.getInstance().setPath("/sdcard/0");
        startActivity(new Intent(this, SecondActivity.class));
//        UserManager.getInstance().setPerson(new Person("hxl", "123456"));
    }

    public void getInfo(View view){
        Person person = UserManager.getInstance().getPerson();
        if (person != null){
            Toast.makeText(this, person.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
