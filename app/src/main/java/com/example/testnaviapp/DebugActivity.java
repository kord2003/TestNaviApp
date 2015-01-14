package com.example.testnaviapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class DebugActivity extends Activity implements OnClickListener{
    private static final String TAG = DebugActivity.class.getName();
    private Button btnSendLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        btnSendLogs = (Button)findViewById(R.id.btnSendLogs);
        btnSendLogs.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSendLogs:
                sendLogs();
                break;
            default:
                break;
        }
    }

    private void sendLogs() {
        File logFile = FileLogger.getLogFile(this);

        Log.d(TAG, "logFile = " + logFile);
        File zippedLogFile = Zipper.zipFile(this, logFile);
        if(zippedLogFile != null) {
            Uri zippedLogFileUri = Uri.fromFile(zippedLogFile);
            //Log.d(TAG, "zippedLogFileUri = " + zippedLogFileUri);

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, zippedLogFileUri);
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "subject: logs");
            sharingIntent.putExtra(Intent.EXTRA_EMAIL, "kord2003@gmail.com");
            startActivity(Intent.createChooser(sharingIntent, "Send email"));
        } else {
            Toast.makeText(this, "Log file is empty", Toast.LENGTH_SHORT).show();
        }
    }
}
