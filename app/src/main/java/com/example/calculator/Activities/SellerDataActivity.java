package com.example.calculator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calculator.DatabaseHelper;
import com.example.calculator.Fragments.SellerDataFragment;
import com.example.calculator.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SellerDataActivity extends AppCompatActivity {
    ListView listView;
    FloatingActionButton floatingActionButton;
    Button save, cancel, hearderBtn;
    TextView headerDate;
    private String pickDate="dd/mm/yyyy";
    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_data);
        db = new DatabaseHelper(this);
        headerDate = findViewById(R.id.headerDate);
        hearderBtn = findViewById(R.id.headerBtn);

        Bundle bundle= getIntent().getExtras();
        if(bundle!=null) {
            pickDate = bundle.getString("date");
            Log.d("SellerDataActivity", "onCreate: " + pickDate);
            headerDate.setText(pickDate);
        }

        hearderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerDataActivity.this, MainActivity.class);
                intent.putExtra("oldDate",pickDate);
                startActivity(intent);
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, new SellerDataFragment());
        fragmentTransaction.commit();

        floatingActionButton = findViewById(R.id.addNewEntry);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dl = new Dialog(SellerDataActivity.this);
                dl.setContentView(R.layout.new_entry_dialog);

                cancel = dl.findViewById(R.id.cancelBtn);
                save =  dl.findViewById(R.id.saveBtn);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText sName = dl.findViewById(R.id.sellerName);
                        EditText iName = dl.findViewById(R.id.itemName);
                        EditText itemWeight = dl.findViewById(R.id.itemWeight);
                        EditText itemRate = dl.findViewById(R.id.itemRate);
                        int date_id=1;
                        Cursor cursor = db.getDateID(pickDate);
                        while (cursor.moveToNext())
                            date_id=cursor.getInt(0);

                        if(TextUtils.isEmpty(sName.getText()))
                            sName.setError("Seller name is required");
                        else if(TextUtils.isEmpty(iName.getText()))
                            iName.setError("Item name is required");
                        else if (TextUtils.isEmpty(itemWeight.getText()))
                            itemWeight.setError("Item weight is required");
                        else if(TextUtils.isEmpty(itemRate.getText()))
                            itemRate.setError("Item rate is required");
                        else {
                            Boolean result = db.insertIntoSellerTable(sName.getText().toString().toUpperCase(), iName.getText().toString().toUpperCase(), itemWeight.getText().toString(), itemRate.getText().toString(), date_id);

                            if (result)
                                Toast.makeText(SellerDataActivity.this, "New Entry added", Toast.LENGTH_SHORT).show();

                            dl.dismiss();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment, new SellerDataFragment());
                            fragmentTransaction.commit();
                        }

                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dl.dismiss();
                    }
                });
                dl.show();

            }
        });


    }
}