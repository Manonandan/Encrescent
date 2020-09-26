package com.manonandansk.encrescent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";
    public static String msgBody;
    public static String msgAddress;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            Object[] sms = (Object[]) bundle.get(SMS_BUNDLE);
            String smsMsg = "";

            SmsMessage smsMessage;
            int i;
            for (i = 0; i < sms.length; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = bundle.getString("format");
                    smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                } else {
                    smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                }

                msgBody = smsMessage.getMessageBody().toString();
                msgAddress = smsMessage.getOriginatingAddress();

                smsMsg += "SMS from: " + msgAddress + "\n";
                smsMsg += msgBody + "\n";
            }
            MainActivity inst = MainActivity.Instance();
            //intent.putExtra(EXTRA_TEXT, msgAddress);
            //intent.putExtra(EXTRA_TEXT1, msgBody);
            inst.updateList(smsMsg);

        }
    }
}
