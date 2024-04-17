package com.example.callredial;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextInputEditText editTextPhoneNumber;
    Spinner spinnerSimSelection;
    AppCompatButton buttonCall, buttonAutoRedial;
    boolean isAutoRedialEnabled = false;
    String phoneNumber;
    PhoneAccountHandle selectedPhoneAccountHandle;




    // contact pickar
    private static final int RESULT_PICK_CONTACT =1;
    private TextView phone;
    private Button select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        spinnerSimSelection = findViewById(R.id.spinnerSimSelection);
        buttonCall = findViewById(R.id.buttonCall);
        buttonAutoRedial = findViewById(R.id.buttonAutoRedial);










        select = findViewById (R.id.select);

        select.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent in = new Intent (Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult (in, RESULT_PICK_CONTACT);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            Toast.makeText(this, "Failed To pick contact", Toast.LENGTH_SHORT).show();
        }
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;

        try {
            String phoneNo = null;
            Uri uri = data.getData ();
            cursor = getContentResolver ().query (uri, null, null,null,null);
            cursor.moveToFirst ();
            int phoneIndex = cursor.getColumnIndex (ContactsContract.CommonDataKinds.Phone.NUMBER);

            phoneNo = cursor.getString (phoneIndex);

            editTextPhoneNumber.setText (phoneNo);


        } catch (Exception e) {
            e.printStackTrace();
        }

























        // Setup SIM selection spinner

        setupSimSelectionSpinner();

        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = editTextPhoneNumber.getText().toString();
                if (!phoneNumber.isEmpty()) {
                    callNumber(phoneNumber);



                } else {
                    Toast.makeText(MainActivity.this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonAutoRedial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isAutoRedialEnabled = !isAutoRedialEnabled;
                if (isAutoRedialEnabled) {
                    startAutoRedial();
                    buttonAutoRedial.setText("Stop Auto Redial");
                } else {
                    buttonAutoRedial.setText("Auto Redial");
                }
            }
        });
    }
                //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> START SIM  DATA >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>



//    private void setupSimSelectionSpinner() {
//        TelecomManager telecomManager = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            telecomManager = getSystemService(TelecomManager.class);
//        }
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//
//            return;
//        }
//        List<PhoneAccountHandle> phoneAccounts = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            phoneAccounts = telecomManager.getCallCapablePhoneAccounts();
//        }
//
//        ArrayAdapter<PhoneAccountHandle> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, phoneAccounts);
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinnerSimSelection.setAdapter(adapter);
//
//
//        List<PhoneAccountHandle> finalPhoneAccounts = phoneAccounts;
//        spinnerSimSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//
//                selectedPhoneAccountHandle = finalPhoneAccounts.get(position);
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                selectedPhoneAccountHandle = null;
//            }
//        });
//    }
                    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> END SIM >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>





                    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> END SIM >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    private void setupSimSelectionSpinner() {
        TelecomManager telecomManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            telecomManager = getSystemService(TelecomManager.class);
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // If permission not granted, handle it here.
            // For instance, you might want to request permission at runtime.


            return;
        }

        List<PhoneAccountHandle> phoneAccounts = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && telecomManager != null) {
            phoneAccounts = telecomManager.getCallCapablePhoneAccounts();
        }

        // Adding SIM 1 and SIM 2 options
        List<String> simOptions = new ArrayList<>();
        simOptions.add("Sim 1");
        simOptions.add("Sim 2");

//         Adding retrieved SIM card handles

        if (phoneAccounts != null) {

            simOptions.add(0,phoneAccounts.toString());

            simOptions.add(1,phoneAccounts.toString());



//            for (PhoneAccountHandle phoneAccountHandle : phoneAccounts) {
//
//                simOptions.add(phoneAccountHandle.toString());
//            }

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, simOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSimSelection.setAdapter(adapter);

        List<PhoneAccountHandle> finalPhoneAccounts = phoneAccounts;
        spinnerSimSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                // Check if it's Sim 1 or Sim 2
                if (position == 0 || position == 1) {
                    selectedPhoneAccountHandle = null; // No phone account handle selected
                } else {
                    selectedPhoneAccountHandle = finalPhoneAccounts.get(position-2);
                }
            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPhoneAccountHandle = null;
            }
        });
    }







    private void callNumber(String phoneNumber) {
        if (selectedPhoneAccountHandle != null) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                callIntent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, selectedPhoneAccountHandle);
            }
            startActivity(callIntent);
        } else {
            Toast.makeText(MainActivity.this, "Please select a SIM card", Toast.LENGTH_SHORT).show();
        }
    }

    private void startAutoRedial() {
        final Handler handler = new Handler();
        final int delay = 10000; //milliseconds

        handler.postDelayed(new Runnable() {
            public void run() {
                if (isAutoRedialEnabled) {
                    callNumber(phoneNumber);
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
        } else {
            Toast.makeText(this, "Permission denied. Can't make calls.", Toast.LENGTH_SHORT).show();
        }
    }
}
