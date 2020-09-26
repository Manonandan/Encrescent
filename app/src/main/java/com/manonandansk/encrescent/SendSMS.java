package com.manonandansk.encrescent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SendSMS extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btnSend;
    private EditText etMessage,etPhone;
    private final static int REQUEST_CODE_PERMISSION_SEND_SMS = 123;
    private EditText secretKey;
    private String msg= "";
    public String secretKey1 = "";
    private String AES = "AES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        etMessage = (EditText) findViewById(R.id.etMessage);
        etPhone = (EditText) findViewById(R.id.etPhone);
        btnSend = (Button) findViewById(R.id.btnSend);
        secretKey = (EditText) findViewById(R.id.secretKey);

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        btnSend.setEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (checkPermission(Manifest.permission.SEND_SMS)) {
            btnSend.setEnabled(true);
        } else {
            ActivityCompat.requestPermissions(SendSMS.this, new String[]{
                    (Manifest.permission.SEND_SMS)}, REQUEST_CODE_PERMISSION_SEND_SMS);
        }


        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                try{
                    secretKey1 = RandomKey.getAlphaNumericString(12);

                    msg = encrypt(etMessage.getText().toString(),secretKey.getText().toString());
                    //msg = encrypt(etMessage.getText().toString(),secretKey1);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //msg = etMessage.getText().toString();
                String PhNo = etPhone.getText().toString().trim();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(PhNo,null,msg,null,null);
                Toast.makeText(SendSMS.this,"SMS Sent",Toast.LENGTH_LONG).show();


            }
        });
    }

    private String encrypt(String Data, String Password) throws Exception{

        SecretKeySpec key = generateKey(Password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = Base64.encodeToString(encVal,Base64.DEFAULT);
        return encryptedValue;
    }

    private SecretKeySpec generateKey(String Password) throws Exception{

        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = Password.getBytes("UTF-8");
        digest.update(bytes,0,bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,"AES");
        return secretKeySpec;
    }


    private boolean checkPermission(String permission){
        int checkPermission = ContextCompat.checkSelfPermission(this, permission);
        return checkPermission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu,menu);
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_PERMISSION_SEND_SMS:
                if (grantResults.length > 0 && grantResults [0] == PackageManager.PERMISSION_GRANTED){
                    btnSend.setEnabled(true);
                }
                break;
        }
    }

}
