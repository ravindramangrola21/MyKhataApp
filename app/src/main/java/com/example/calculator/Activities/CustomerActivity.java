package com.example.calculator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calculator.Adapters.CustomerNameAdapter;
import com.example.calculator.DatabaseHelper;
import com.example.calculator.R;

import java.util.ArrayList;

public class CustomerActivity extends AppCompatActivity {
    TextView date;
    String Date="1/1/2022";
    Button dateBtn;
    Button addCustomer, cancel, save;
    EditText customerName;
    DatabaseHelper db;
    RecyclerView recyclerView;
    ArrayList nameList,sNoList;
    CustomerNameAdapter adapter;
    String eDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        setContentView(R.layout.activity_customer_panel);
        date = findViewById(R.id.cheaderDate);
        dateBtn = findViewById(R.id.cheaderBtn);
        if(bundle != null)
            Date=bundle.getString("date");
        date.setText(Date);

        db = new DatabaseHelper(CustomerActivity.this);

        recyclerView = findViewById(R.id.customerRecyclerView);
        nameList = new ArrayList();
        sNoList = new ArrayList();
        addCustomer = findViewById(R.id.addNewC);

        adapter = new CustomerNameAdapter(CustomerActivity.this, nameList, sNoList, Date);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(CustomerActivity.this));

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerActivity.this, MainActivity.class);
                intent.putExtra("oldDate", date.getText());
                startActivity(intent);
            }
        });
        addCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(CustomerActivity.this);
                dialog.setContentView(R.layout.new_customer_dialog);
                cancel=dialog.findViewById(R.id.cancelCustomer);
                save=dialog.findViewById(R.id.saveCustomer);

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customerName = dialog.findViewById(R.id.customerName);
                        if(TextUtils.isEmpty(customerName.getText()))
                        {
                            customerName.setError("Customer name is required");
                        }
                        else {
                            boolean result = db.insertIntoCustomerNameTable(customerName.getText().toString().toUpperCase());
                            if (result)
                                Toast.makeText(CustomerActivity.this, "New Customer Added", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                            startActivity(getIntent());
                        }
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        getData(db);

    }
    private void getData(DatabaseHelper db) {
        Cursor cursor = db.getDataFromCustomerNameTable();

        if (cursor.getCount() == 0) {
            Toast.makeText(CustomerActivity.this, "No Entries available", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                sNoList.add(cursor.getInt(0));
                nameList.add(" "+cursor.getString(1));
            }
        }
    }
}