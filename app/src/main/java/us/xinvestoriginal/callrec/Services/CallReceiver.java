package us.xinvestoriginal.callrec.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import us.xinvestoriginal.callrec.Helpers.RecordHelper;
import us.xinvestoriginal.callrec.Models.RecordEntity;

/**
 * Created by x-inv on 20.08.2017.
 */

public class CallReceiver extends BroadcastReceiver {


    private static Boolean isIncoming = null;
    private static String  callNumber = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        RecordService.InitFromReceiver(context);


        String eState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        if (incomingNumber == null) {
            incomingNumber = callNumber;
        }else if (callNumber == null) {
            callNumber = incomingNumber;
        }

        boolean isBeginCall;
        String message = "";

        if (eState == null){
            callNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            return;
        }else if (eState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            isIncoming = true;
            message = "Call from:" +incomingNumber;
            isBeginCall = false;
        } else if (eState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            isIncoming = null;
            callNumber = null;
            isBeginCall = false;
            message = "Detected call idle event";
        }else if (eState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            isIncoming = isIncoming == null ? false : true;
            isBeginCall = true;
            message = "Detected call offhook event";
        }else{
            isBeginCall = false;
        }

        //int callingSIM = intent.getIntExtra("simId", -1);
        //if (callingSIM < 0) callingSIM = intent.getIntExtra("slot", -1);

        //message += " isIncomingCall: " + String.valueOf(isIncoming) + " number: " + String.valueOf(incomingNumber) +
        //        " isBeginCall: " + String.valueOf(isBeginCall);

        //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        //Log.e(">>>","CallReceiver call message: " + message);

        //TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        //if (tm == null) {
            // whether you want to handle this is up to you really
            //throw new NullPointerException("tm == null");
        //}
        //tm.answerRingingCall();

        if (isBeginCall) {
            if (incomingNumber != null && isIncoming != null &&
                RecordService.getNumbEnable(incomingNumber)) {
                RecordHelper.start(incomingNumber, isIncoming);
            }
        } else {
            RecordEntity track = RecordHelper.stop();
            if (track != null) RecordService.PutTrack(track,true);
        }

    }
}
