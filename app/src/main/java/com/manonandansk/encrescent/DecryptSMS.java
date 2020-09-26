package com.manonandansk.encrescent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DecryptSMS extends AppCompatActivity {

    TextView etPhone,encText,decText;
    EditText secretKey1;
    Button btnDecrypt;
    public static String AES = "AES",outputString;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt_sms);

        etPhone = (TextView) findViewById(R.id.etPhone);
        encText = (TextView) findViewById(R.id.encText);
        secretKey1 = (EditText) findViewById(R.id.secretKey);
        btnDecrypt = (Button) findViewById(R.id.btnDecrypt);
        decText = (TextView) findViewById(R.id.decText);

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        //MainActivity a = (MainActivity) intent.getSerializableExtra("a");
        final String a = intent.getStringExtra("a");
        final String b = intent.getStringExtra("b");
        etPhone.setText(a);
        encText.setText(b);
        //Toast.makeText(decryptSMS.this,a,Toast.LENGTH_SHORT).show();

        btnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String outputString1 = "";
                try {
/*
                    Map<String, Object> keys = AES256.getRSAKeys();

                    PrivateKey privateKey = (PrivateKey) keys.get("private");
                    PublicKey publicKey = (PublicKey) keys.get("public");
*/
                    outputString1 = decrypt(b,secretKey1.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(decryptSMS.this,"A",Toast.LENGTH_SHORT).show();
                }
                //String msg = "Hi Hello!";
                //String c = outputString1.getBytes().toString();
                decText.setText(outputString1);
                //Toast.makeText(decryptSMS.this,outputString1,Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static String decrypt(String outputString, String Password) throws Exception{

        SecretKeySpec key = generateKey(Password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE,key);
        byte[] decodeValue = Base64.decode(outputString,Base64.DEFAULT);
        byte[] decVal = c.doFinal(decodeValue);
        String decryptedValue = new String(decVal);
        return  decryptedValue;
    }

    private static SecretKeySpec generateKey(String Password) throws Exception{

        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = Password.getBytes("UTF-8");
        digest.update(bytes,0,bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,"AES");
        return secretKeySpec;
    }

}
