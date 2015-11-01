package com.example.android.camera2basic;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.microsoft.projectoxford.speechrecognition.ISpeechRecognitionServerEvents;
import com.microsoft.projectoxford.speechrecognition.MicrophoneRecognitionClient;
import com.microsoft.projectoxford.speechrecognition.RecognitionResult;
import com.microsoft.projectoxford.speechrecognition.SpeechRecognitionMode;
import com.microsoft.projectoxford.speechrecognition.SpeechRecognitionServiceFactory;

/**
 * Created by Jerry on 2015-10-31.
 */


public class SpeechListener extends AsyncTask<Void,String,String> implements ISpeechRecognitionServerEvents {


    public Activity myActivity;
    public SpeechListener(Activity a){
        Log.i("*********************","CONSTRUCTOR*********************");
        myActivity = a;
        initializeRecoClient();
    }

    String text="";
    boolean changed;
    boolean recording;

    @Override
    protected String doInBackground(Void[] params) {
        do{
            m_micClient.startMicAndRecognition();
            recording = true;

            while(recording) {
            if(changed);
            {
                    Log.i("*********************", "BACKGROUND*********************" + text);
                    publishProgress(text);
                    changed = false;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            m_micClient.endMicAndRecognition();
        }while(true);
    }
    TextView textView;
    @Override
    protected void onPreExecute() {
        Log.i("*********************","PREEXECUTE*********************");
        textView = (TextView) myActivity.findViewById(R.id.textView);
    }

    @Override
    protected void onProgressUpdate(String... s) {
        Log.i("*********************","PROGRESS UPDATE*********************");
        textView.setText(s[0]);
    }

    int m_waitSeconds = 20;
    MicrophoneRecognitionClient m_micClient = null;
    SpeechRecognitionMode m_recoMode = SpeechRecognitionMode.ShortPhrase;


    void initializeRecoClient()
    {
        Log.i("*********************","INITRECO*********************");
        String language = "en-us";
        String subscriptionKey ="099e24e4629e4a21a51c922e112ed3c2";

        if (null == m_micClient) {
            m_micClient = SpeechRecognitionServiceFactory.createMicrophoneClient(//myActivity,
                    m_recoMode,
                    language,
                    this,
                    subscriptionKey);
        }
    }

    @Override
    public void onPartialResponseReceived(String s) {
        Log.i("*********************","PARTRESP*********************");
        changed = true;
        text = s;
    }

    @Override
    public void onFinalResponseReceived(RecognitionResult recognitionResult) {
        Log.i("*********************","FINALRESP*********************");
        text = "";
        changed = true;
        recording = false;
    }

    @Override
    public void onIntentReceived(String s) {
        Log.i("*********************","INTENTRECI*********************");
    }

    @Override
    public void onError(int i, String s) {
        Log.i("*********************","ERROR*********************");
    }
    @Override
    public void onAudioEvent(boolean b) {
        Log.i("*********************","AUD EVENT*********************");
    }
}
