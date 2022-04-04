package com.example.karveliasae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.renderscript.RenderScript;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import static android.Manifest.permission.CAMERA;
import android.hardware.Camera;
import android.util.Log;



public class Qr_scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    SQLiteDatabase db;
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;
    SharedPreferences preferences;
    String uid="";
    SharedPreferences kwdikos_paketou;
    CustomerActivity ca= new CustomerActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        db = openOrCreateDatabase("sales", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS sale(barcode TEXT,name TEXT,price DOUBLE,pososthta INT)");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        kwdikos_paketou = PreferenceManager.getDefaultSharedPreferences(this);
        uid = preferences.getString("str1", "0");

        //int currentApiVersion = Build.VERSION.SDK_INT;

        //  if(currentApiVersion >=  Build.VERSION_CODES.M)
        //  {
        if(checkPermission())
        {
            //Toast.makeText(getApplicationContext(), "Permission already granted!", Toast.LENGTH_LONG).show();
        }
        else
        {
            requestPermission();
        }
        //  }
    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
        if (checkPermission()) {
            if(scannerView == null) {
                scannerView = new ZXingScannerView(this);
                setContentView(scannerView);
            }
            scannerView.setResultHandler(this);
            scannerView.startCamera();
        } else {
            requestPermission();
        }
        //  }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        //Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
               /* AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
        builder.setMessage(message);
        Toast.makeText(Qr_scanner.this, message, Toast.LENGTH_SHORT).show();
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Cancel", null);
        builder.create();
        builder.show();*/
    }

    @Override
    public void handleResult(Result result) {
        //Toast.makeText(Qr_scanner.this, "handresult", Toast.LENGTH_SHORT).show();
        final String myResult = result.getText();
        final Intent intent = new Intent(this, CustomerActivity.class);
        Log.d("QRCodeScanner", result.getText());
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        builder.setMessage("fill in quantity");
        builder.setTitle("Barcode");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scannerView.resumeCameraPreview(Qr_scanner.this);
                GetSaleItem(myResult);
            }
        });
        builder.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(myResult));
                // startActivity(browserIntent);
                /*String barcode;
                barcode = myResult;
                SharedPreferences.Editor editor = kwdikos_paketou.edit();
                editor.putString("str1",barcode);
                editor.commit();
                String uid;
                uid = preferences.getString("str1", "0");
                SharedPreferences.Editor editor1 = preferences.edit();
                editor1.putString("str1",uid);
                editor1.commit();*/
                finish();
                startActivity(intent);




            }
        });
        builder.setMessage(result.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    public void GetSaleItem(final String barcode)
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    String name = snapshot.child("Cigarettes").child(barcode).child("name").getValue().toString();
                    double price = Double.parseDouble(snapshot.child("Cigarettes").child(barcode).child("retail price").getValue().toString());
                    db.execSQL("INSERT INTO sale values" + "('" + barcode + "','" + name +  "','" + price +"','1')");
                    Toast.makeText(Qr_scanner.this, "mphke", Toast.LENGTH_SHORT).show();

                } catch (Exception oo) {
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}


