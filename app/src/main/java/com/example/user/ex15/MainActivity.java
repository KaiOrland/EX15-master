package com.example.user.ex15;

import android.Manifest;
import android.content.ContentUris;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

public class MainActivity extends AppCompatActivity {
    final int RESULT_LOAD_IMG = 1;
    private static final int REQUEST_GET_CONTACT_DETAILS = 2;
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

        Button getContactDetails = (Button)findViewById(R.id.getContactDetails);
        getContactDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent,REQUEST_GET_CONTACT_DETAILS);
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
            else if(requestCode==




                    && resultCode == RESULT_OK && null!= data) {
                ContactInfo ci = getContactInfo(data.getData());
                EditText firstNameOutput = (EditText) findViewById(R.id.firstNameOutput);
                EditText lastNameOutput = (EditText) findViewById(R.id.lastNameOutput);
                EditText phoneOutput = (EditText) findViewById(R.id.phoneOutput);
                EditText adressOutput = (EditText) findViewById(R.id.addressOutput);

                firstNameOutput.setText(ci.getFirstName());
                lastNameOutput.setText(ci.getLastName());
                phoneOutput.setText(ci.getCellNumber());
                adressOutput.setText(ci.getAdress());
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this,"Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    public void openContactList()
    {

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
        else if(requestCode == REQUEST_GET_CONTACT_DETAILS && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            openContactList();
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_GET_CONTACT_DETAILS);
        }
        else {
            openFilePicker();
            openContactList();
        }
    }

    private class ContactInfo{
        private String cellNum;
        private String firstName;
        private String lastName;
        private String adress;

        public String getCellNumber() {
            return cellNum;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getAdress() {
            return adress;
        }

        public void setCellNumber(String cellNum) {
            this.cellNum = cellNum;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public void setAdress(String adress) {
            this.adress = adress;
        }
    }
    private ContactInfo getContactInfo(Uri uriContact){
        long contactID = ContentUris.parseId(uriContact);
        ContactInfo ci = new ContactInfo();

        Cursor cursorDetails = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                        ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                        ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS},
                ContactsContract.Data.CONTACT_ID + " = ?",
                new String[]{Long.toString(contactID)},
                null);

        while(cursorDetails.moveToNext()){
            String rowType = cursorDetails.getString(cursorDetails.getColumnIndex(ContactsContract.Data.MIMETYPE));
            switch(rowType){
                case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                    int phoneType = cursorDetails.getInt((cursorDetails.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
                    ci.setCellNumber(cursorDetails.getString(cursorDetails.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    break;

                case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE:
                    if (ci.getAdress() == null || ci.getAdress().isEmpty())
                        ci.setAdress(cursorDetails.getString(cursorDetails.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)));
                    break;

                case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:
                    if (ci.getFirstName() == null || ci.getFirstName().isEmpty())
                        ci.setFirstName(cursorDetails.getString(cursorDetails.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)));
                    if (ci.getLastName() == null || ci.getFirstName().isEmpty())
                        ci.setLastName(cursorDetails.getString(cursorDetails.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)));
                    break;
            }
            cursorDetails.close();
        }
        return ci;
    }
}
