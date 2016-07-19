package com.ianlin.camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private Button picBtn;
    private final int PIC_REQUEST_CODE = 1;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        picBtn = (Button) findViewById(R.id.takePic);
        sharedPreferences = getSharedPreferences("Users", Context.MODE_PRIVATE);

        //clicker for take pic
        picBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get runtime permission
                if (shouldAskPermission()) {
                    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != getPackageManager().PERMISSION_GRANTED) {
                        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        int permsRequestCode = 200;
                        requestPermissions(perms, permsRequestCode);
                    }
                }
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getPackageManager()) != null) {
                    // create a file where the photo should go
                    File image = null;
                    try {
                        image = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (image != null) {
                        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                        MainActivity.this.startActivityForResult(i, PIC_REQUEST_CODE);
                    }

                }
            }
        });
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String imageFileName = "Picture_" + date + ".jpg";
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Data());
//        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath =  image.getAbsolutePath();
        Log.d("path", mCurrentPhotoPath);
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        Log.d("uri", contentUri.toString());
        this.sendBroadcast(mediaScanIntent);
    }

    private boolean shouldAskPermission() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == getPackageManager().PERMISSION_GRANTED) {
            Log.d("result", "Permission: "+permissions[0]+ "was "+grantResults[0]);
        }
    }

    public void showPic() {


    }
}
