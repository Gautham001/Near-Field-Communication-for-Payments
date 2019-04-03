package com.example.paytmtest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText etOrderId;
    private EditText etCustomerId;
    private EditText etAmount;
    private Button btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etOrderId = (EditText)findViewById(R.id.et_orderId);
        etCustomerId = (EditText)findViewById(R.id.et_customerId);
        etAmount = (EditText)findViewById(R.id.et_Amount);
        btnPay = (Button)findViewById(R.id.btn_pay);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }


        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String orderId = etOrderId.getText().toString().trim();
                String customerId = etCustomerId.getText().toString().trim();
                String amount = etAmount.getText().toString().trim();

                Intent i = new Intent(MainActivity.this, Checksum.class);
                i.putExtra("orderid", orderId);
                i.putExtra("custid", customerId);
                i.putExtra("amount", amount);
                startActivity(i);

            }
        });

    }
}
