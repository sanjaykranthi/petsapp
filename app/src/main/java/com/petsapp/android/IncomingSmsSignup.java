package com.petsapp.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by WEB DESIGNING on 01-03-2016.
 */
public class IncomingSmsSignup extends BroadcastReceiver {

    final SmsManager sms = SmsManager.getDefault();

    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber.substring(3);
                    String message = currentMessage.getDisplayMessageBody();
                    try {
                        if (senderNum.equals("PETAPP")) {
                            SignupActivity Sms = new SignupActivity();
                            // Sms.receivedSms(message);
                            Sms.recivedSms(message);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }
    }
}
