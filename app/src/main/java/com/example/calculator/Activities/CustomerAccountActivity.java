package com.example.calculator.Activities;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.calculator.Adapters.CustomerEntriesAdapter;
import com.example.calculator.Adapters.CustomerNameAdapter;
import com.example.calculator.Adapters.CustomerSingleEntryAdapter;
import com.example.calculator.ChildItem;
import com.example.calculator.DatabaseHelper;
import com.example.calculator.ParentItem;
import com.example.calculator.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

public class CustomerAccountActivity extends AppCompatActivity {
    private static String TAG = CustomerAccountActivity.class.getSimpleName();
    private FloatingActionButton addNewEntry;
    EditText vegName, vegWeight, vegRate;
    Button cancelDialog, addEntry;
    DatabaseHelper db;
    String pickName,pickDate;
    private int C_ID;
    ArrayList<ParentItem> parentItems = new ArrayList<>();
    ArrayList<ChildItem> childItems = new ArrayList<>();
    ArrayList<Integer> dateIDList = new ArrayList();
    ArrayList<Integer> cIDList = new ArrayList();
    RecyclerView recyclerView;
    private SwitchCompat filterTogle;
    private SwipeRefreshLayout refreshLayout;

    private CustomerEntriesAdapter adapter;
    private CustomerSingleEntryAdapter adaptr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_account);
        db = new DatabaseHelper(CustomerAccountActivity.this);
        TextView txtview= findViewById(R.id.setCname);

        addNewEntry = findViewById(R.id.addEntry);
        recyclerView = findViewById(R.id.cDataEntryRecyclerView);
        filterTogle = findViewById(R.id.togleFilter);
        refreshLayout = findViewById(R.id.refreshLayout);




        adapter = new CustomerEntriesAdapter(this, parentItems, dateIDList, cIDList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(CustomerAccountActivity.this));




        Bundle bundle= getIntent().getExtras();
        if(bundle!=null) {
            pickName = bundle.getString("name");
            C_ID = Integer.parseInt(bundle.getString("ID"));
            pickDate = bundle.getString("date");
            Log.d("SellerDataActivity", "onCreate: " + pickName);
            txtview.setText(pickName);
        }
        filterTogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("localSharedPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("switch", filterTogle.isChecked());
                editor.apply();
                finish();
                startActivity(getIntent());

            }
        });

        addNewEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog Dl= new Dialog(CustomerAccountActivity.this);
                Dl.setContentView(R.layout.customer_data_entry_dialog);

                vegName = Dl.findViewById(R.id.vegName);
                vegWeight = Dl.findViewById(R.id.vegWeight);
                vegRate = Dl.findViewById(R.id.vegRate);
                cancelDialog = Dl.findViewById(R.id.cancelDialog);
                addEntry = Dl.findViewById(R.id.addNewEntry);

                addEntry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(TextUtils.isEmpty(vegName.getText()))
                            vegName.setError("Item name is required");
                        else if(TextUtils.isEmpty(vegWeight.getText()))
                            vegWeight.setError("Weight is required");
                        else if(TextUtils.isEmpty(vegRate.getText()))
                            vegRate.setError("Rate is required");
                        else {
                            Cursor cursor = db.getDateID(pickDate);
                            int dateId = 2;
                            while (cursor.moveToNext())
                                dateId = cursor.getInt(0);
                            boolean result = db.insertIntoCustomerEntriesTable(C_ID, vegName.getText().toString(), vegWeight.getText().toString(), vegRate.getText().toString(), dateId);
                            if (result) {
                                vegName.setText("");
                                vegWeight.setText("");
                                vegRate.setText("");
                                Toast.makeText(CustomerAccountActivity.this, "New Entry added", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                cancelDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dl.dismiss();
                        finish();
                        startActivity(getIntent());
                    }
                });


                Dl.show();
            }
        });
        getData(db);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });
        checkToggle();

    }

    private void getData(DatabaseHelper db) {
        SharedPreferences preferences = getSharedPreferences("localSharedPref", MODE_PRIVATE);
        Cursor cursorDate = db.getDateFromCustomerDateTable(pickDate, preferences.getBoolean("switch", false));
        while (cursorDate.moveToNext())
        {
            Log.d(TAG, "getData"+cursorDate.getString(1)+"final");
           ParentItem parentItem = new ParentItem(cursorDate.getString(1));
           parentItems.add(parentItem);
           dateIDList.add(cursorDate.getInt(0));
           cIDList.add(C_ID);

           Cursor cursorEntry = db.getDataFromCustomerEntriesTable(cursorDate.getInt(0), C_ID);

           if(cursorEntry.getCount()==0) {
               dateIDList.remove(dateIDList.size()-1);
               cIDList.remove(cIDList.size()-1);
               parentItems.remove(parentItems.size() - 1);
           }
        }
        if(parentItems.size()==0)
            Toast.makeText(CustomerAccountActivity.this, "No Entry Available", Toast.LENGTH_SHORT).show();
    }

    private void checkToggle()
    {
        SharedPreferences preferences = getSharedPreferences("localSharedPref", MODE_PRIVATE);
        filterTogle.setChecked(preferences.getBoolean("switch", false));
    }



}