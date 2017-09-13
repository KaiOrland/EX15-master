package com.example.user.ex15;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    final int RESULT_LOAD_IMG = 1;
    private static final int REQUEST_GET_DETAILS = 2;
    private static final int REQUEST_WRITE_PERMISSION = 786;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button getImage = (Button)findViewById(R.id.newImgButton);

        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galerryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galerryIntent,RESULT_LOAD_IMG);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null!= data)
            {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn,null,null,null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = (ImageView) findViewById(R.id.contactImg);
                imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this,"Something went wronge", Toast.LENGTH_LONG).show();
        }
    }

    public void openContactList()
    {
        Button getContactDetails = (Button)findViewById(R.id.getContactDetails);
        getContactDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent,REQUEST_GET_DETAILS);
            }
        });
    }

    public void openFilePicker()
    {
        Button getContact = (Button)findViewById(R.id.newImgButton);
        getContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galerryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galerryIntent,RESULT_LOAD_IMG);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openFilePicker();
        }
        else if(requestCode == REQUEST_GET_DETAILS && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            openContactList();
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_GET_DETAILS);
        }
        else {
            openFilePicker();
            openContactList();
        }
    }
}
