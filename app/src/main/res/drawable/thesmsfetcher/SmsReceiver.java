package com.android.thesmsfetcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "otp_channel";
    private static final String CHANNEL_NAME = "OTP Notifications";
    private static final Pattern OTP_PATTERN = Pattern.compile("\\b\\d{4,8}\\b");

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) return;

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            String format = bundle.getString("format");

            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu, format);
                    String sender = sms.getDisplayOriginatingAddress();
                    String message = sms.getMessageBody();

                    if (message != null && message.contains("INDANE")) {
                        Matcher matcher = OTP_PATTERN.matcher(message);
                        if (matcher.find()) {
                            String otp = matcher.group();
                            Log.d("SmsReceiver", "OTP from " + sender + ": " + otp);

                            // Send broadcast for app to receive
                            Intent otpIntent = new Intent("com.android.SmsReceiver.OTP_RECEIVED");
                            otpIntent.putExtra("otp", otp);
                            otpIntent.putExtra("sender", sender);
                            otpIntent.putExtra("body", message);
                            context.sendBroadcast(otpIntent);
                        }
                    }
                }
            }
        }
    }
}
