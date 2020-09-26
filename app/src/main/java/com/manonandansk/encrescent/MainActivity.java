package com.manonandansk.encrescent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Serializable {

    private Toolbar toolbar;
    private ListView lvSMS;
    private final static int REQUEST_CODE_PERMISSION_READ_SMS = 456;
    ArrayList<String> smsList = new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    public static MainActivity instance;

    public static MainActivity Instance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab1);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SendSMS.class));
            }
        });

        //toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        instance = this;
        //List View for Message
        lvSMS = (ListView) findViewById(R.id.lvSMS);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, smsList);
        lvSMS.setAdapter(arrayAdapter);

        if (checkPermission(Manifest.permission.READ_SMS)) {
            refreshInbox();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    (Manifest.permission.READ_SMS)}, REQUEST_CODE_PERMISSION_READ_SMS);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_refresh:
                refreshInbox();
                break;
            case R.id.action_settings:

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkPermission(String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(this, permission);
        return checkPermission == PackageManager.PERMISSION_GRANTED;
    }

    public void refreshInbox() {
        final String addr;
        final String body;


        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"),
                null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("Body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;

        arrayAdapter.clear();
        do {
            String str = "SMS from :" + smsInboxCursor.getString(indexAddress) + "\n";
            //str += smsInboxCursor.getString(indexBody);
            arrayAdapter.add(str);

        } while (smsInboxCursor.moveToNext());

        //addr = smsInboxCursor.getString(indexAddress);
        //body = smsInboxCursor.getString(indexBody);

        lvSMS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String addr = "";
                String body="";
                ContentResolver contentResolver = getContentResolver();
                Cursor sms = contentResolver.query(Uri.parse("content://sms/inbox"),
                        null, null, null, null);
                int indexBody = sms.getColumnIndex("Body");
                int indexAddress = sms.getColumnIndex("address");
                if (sms.moveToFirst())
                {
                    int i;
                    for(i = 0;i < position;i++)
                        sms.moveToNext();
                    addr = sms.getString(indexAddress);
                    body = sms.getString(indexBody);

                }

                Intent intent = new Intent(MainActivity.this, DecryptSMS.class);
                intent.putExtra("a", addr);
                intent.putExtra("b", body);
                startActivity(intent);
                //startActivity(new Intent(MainActivity.this, decryptSMS.class));
            }
        });

    }


    public void updateList(final String smsMsg) {
        arrayAdapter.insert(smsMsg, 0);
        arrayAdapter.notifyDataSetChanged();
    }
}
