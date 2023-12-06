package com.example.bmr;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.annotation.ElementType;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class BMR_view extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmr_view);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = (TextView) findViewById(R.id.text_message);
        textView.setTextSize(40);
        textView.setText(message);
        Uri location =
                Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
// Verify it resolves
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
        boolean isIntentSafe = activities.size() > 0;
// Start an activity if it's safe
        if (isIntentSafe) {
            startActivity(mapIntent);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_menu, menu);
        return true;
    }

    public void onShare(MenuItem item) {
        TextView BMR = (TextView) findViewById(R.id.BMR);
        TextView Cal = (TextView) findViewById(R.id.Calories);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        message =  BMR.getText().toString() + " " + message + " " + Cal.getText().toString();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, message);
        startActivity(shareIntent);
        Log.i("BMR_view", "onShare: " + message);

    }
    DatabaseHandler db = new DatabaseHandler(this);
    public void saveBMRandDATEIME(){
        TextView BMR = (TextView) findViewById(R.id.BMR);
        TextView Cal = (TextView) findViewById(R.id.Calories);
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        message =  BMR.getText().toString() + " " + message + " " + Cal.getText().toString();
        String message2 = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

            db.addContact(new historys(timeStamp, message2));

    }
    public void onSave(MenuItem item) {

        saveBMRandDATEIME();
        StringBuilder message = new StringBuilder();
        List<historys> data = db.getAllContacts();
        for (historys element : data) {
            message.append("Time : ").append(element.getName()).append("\n").append("BMR : ").append(element.getPhoneNumber()).append("\n\n");

        }


        showMessage("History Details", message.toString());
        Log.i("BMR_view", "onSave: " + data.toString());
    }
    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);

        // Use HTML formatting to make "Time" and "BMR" bold and add line breaks
        String formattedMessage =  message.replace("Time :", "<b>Time :</b>")
                .replace("BMR :", "<b>BMR :</b>").replace("\n", "<br>");

        builder.setMessage(Html.fromHtml(formattedMessage, Html.FROM_HTML_MODE_LEGACY));
        builder.show();
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss(); // Close the dialog
            }
        });
    }




}