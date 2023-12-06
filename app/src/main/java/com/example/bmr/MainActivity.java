package com.example.bmr;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Integer limit;
    int REQUEST_IMAGE_CAPTURE = 0;
    String title;

    String photoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner spinner =	(Spinner)	findViewById(R.id.type_spinner);
//	Create	an	ArrayAdapter using	 the	string	array	and	a	default	spinner	 layout
        ArrayAdapter<CharSequence> adapter =	ArrayAdapter.createFromResource(this,
                R.array.type_array,	android.R.layout.simple_spinner_item);
//	Specify	 the	layout	to	use	when	the	list	of	choices	 appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        returnDataPref();
//	Apply	 the	adapter	to	the	spinner
        spinner.setAdapter(adapter);
        Uri location =
                Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
// Verify it resolves
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
        boolean isIntentSafe = activities.size() > 0;

        //add listener to button

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        photoPath = sharedPreferences.getString("photoPath", "");
        Log.i("PATH", "onCreate: " + photoPath);

       //set imageView from PhotoPath
        if (!photoPath.equals("")){
            mImageView = findViewById(R.id.imageView);
            mImageView.setImageURI(Uri.parse(photoPath));
        }










// Start an activity if it's safe
        if (isIntentSafe) {
            startActivity(mapIntent);
        }
    }


    ImageView mImageView;

    public void dispatchTakePictureIntent(View view){


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            Log.i("PATH", "dispatchTakePictureIntent: " + "photoFile1");
            File photoFile = null;
            try {
                Log.i("PATH", "dispatchTakePictureIntent: " + "photoFile2");
                photoFile = createImageFile();
                Log.i("PATH", "dispatchTakePictureIntent: " + "photoFile3");

            } catch (IOException ignored) {
            }
            if (photoFile != null) {

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
            mImageView = findViewById(R.id.imageView);


    }
    String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
        String timeStamp = new
                SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".png", /* suffix */
                storageDir /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();



        Log.i("PATH", mCurrentPhotoPath);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("photoPath", photoPath);
                editor.apply();
                FileOutputStream out = new FileOutputStream(photoPath);
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                mImageView.setImageURI(Uri.parse(photoPath));
                out.flush();
                out.close();
            } catch (Exception e) {
                Log.e("ERR", "ERROR:" + e.toString());
            }
        }
    }

    String gender;
    DatabaseHandler db = new DatabaseHandler(this);

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
    if (view.getId() == R.id.radio_male && checked){
        gender = "male";
    }
    else if (view.getId() == R.id.radio_female && checked){
        gender = "female";
    }



}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    public final static String EXTRA_MESSAGE = "com.example.lab.MESSAGE";


    @Override
    protected void onResume() {
        super.onResume();
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        limit = Integer.parseInt(preferences.getString("pref_limit", "0"));
        title = preferences.getString("pref_title", "");
    }
public  void calculateBMR(View view){
    EditText ageT = (EditText) findViewById(R.id.age);
    EditText weightT = (EditText) findViewById(R.id.weight);
    EditText heightT = (EditText) findViewById(R.id.height);
    Spinner spinner = (Spinner) findViewById(R.id.type_spinner);
    int exercise =spinner.getSelectedItemPosition();
    double age = Double.parseDouble(ageT.getText().toString());
    double weight = Double.parseDouble(weightT.getText().toString());
    double height = Double.parseDouble(heightT.getText().toString());
    double A =
            gender.equals("male") ? (10*weight) + (6.25*height) + (5*age) + 5
                    : (10*weight) + (6.25*height)+ (5*age) - 161;
    double BMR = exercise == 0 ? A * 1.2
            : exercise == 1 ? A * 1.375
            : exercise == 2 ? A * 1.55
            : exercise == 3 ? A * 1.725
            : A * 1.9;

    Intent intent = new Intent(this, BMR_view.class);
    intent.putExtra(EXTRA_MESSAGE,  String.format("%.2f", BMR));
    startActivity(intent);
    Log.i("GENDER", String.valueOf(gender));
    Log.i("AGE", String.valueOf(age));
    Log.i("WEIGHT", String.valueOf(weight));
    Log.i("HEIGHT", String.valueOf(height));
    Log.i("EXERCISE", String.valueOf(exercise));

    Log.i("BMR", String.valueOf(BMR));
    saveCurrentData();
}

    public void saveCurrentData(){
        EditText ageT = (EditText) findViewById(R.id.age);
        EditText weightT = (EditText) findViewById(R.id.weight);
        EditText heightT = (EditText) findViewById(R.id.height);
        Spinner spinner = (Spinner) findViewById(R.id.type_spinner);
        int exercise =spinner.getSelectedItemPosition();
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("gender" , gender);
        editor.putString("age", ageT.getText().toString());
        editor.putString("weight", weightT.getText().toString());
        editor.putString("height", heightT.getText().toString());
        editor.putInt("exercise", exercise);
        editor.apply();
    }
    public void returnDataPref(){
        EditText ageT = (EditText) findViewById(R.id.age);
        EditText weightT = (EditText) findViewById(R.id.weight);
        EditText heightT = (EditText) findViewById(R.id.height);
        Spinner spinner = (Spinner) findViewById(R.id.type_spinner);
        RadioButton male = (RadioButton) findViewById(R.id.radio_male);
        RadioButton female = (RadioButton) findViewById(R.id.radio_female);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        gender = sharedPreferences.getString("gender", "");
        if (!gender.equals("")){
            if (gender.equals("male")) {
                male.setChecked(true);
            } else {
                female.setChecked(true);
            }
        }
        ageT.setText(sharedPreferences.getString("age", ""));
        weightT.setText(sharedPreferences.getString("weight", ""));
        heightT.setText(sharedPreferences.getString("height", ""));
        spinner.setSelection(sharedPreferences.getInt("exercise", 0));
    }

    public void onClear(MenuItem item){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        gender = sharedPreferences.getString("gender", "");
        db.deleteAll();

        finish();
        startActivity(getIntent());
    }


    public void showHistory(MenuItem item) {
        StringBuilder message = new StringBuilder();
        List<historys> data = db.getAllContacts();
        if (data.size() != 0){
            for (historys element : data) {
                //break if limit
                if (limit != 0 && data.indexOf(element) == limit){
                    break;
                }
                message.append("Time : ").append(element.getName()).append("\n").append("BMR : ").append(element.getPhoneNumber()).append("\n\n");

            }
        }
        showMessage(message.toString());
        Log.i("BMR_view", "onSave: " + data.toString());
    }

    public void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);

        // Use HTML formatting to make "Time" and "BMR" bold and add line breaks
        //limit message follow limit in setting

        String formattedMessage =  message.replace("Time :", "<b>Time :</b>")
                .replace("BMR :", "<b>BMR :</b>").replace("\n", "<br>");

        builder.setMessage(Html.fromHtml(formattedMessage, Html.FROM_HTML_MODE_LEGACY));
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss(); // Close the dialog
            }
        });
        builder.show();
    }


    public void goToSetting(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


}