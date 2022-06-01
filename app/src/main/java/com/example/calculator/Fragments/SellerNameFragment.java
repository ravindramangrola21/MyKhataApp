package com.example.calculator.Fragments;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.calculator.Adapters.SellerNameAdapter;
import com.example.calculator.DatabaseHelper;
import com.example.calculator.R;
import com.example.calculator.SellerNames;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class SellerNameFragment extends Fragment {
    private ArrayList<SellerNames> sellerNamesList;
    private SellerNameAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton addNewSeller;
    private DatabaseHelper db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_name, container, false);
        recyclerView = view.findViewById(R.id.sellerNameRecyclerView);
        db = new DatabaseHelper(getContext());
        sellerNamesList = new ArrayList<>();
        adapter = new SellerNameAdapter(sellerNamesList, (AppCompatActivity) getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        addNewSeller = view.findViewById(R.id.addNewSeller);

        addNewSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNameEntryDialog();
            }
        });
        getDataFromSellerNameTable();
        return view;
    }

    private void getDataFromSellerNameTable() {
        Cursor cursor = db.getDataFromTable6();
        if(cursor.getCount()==0)
            Toast.makeText(getContext(), "No Seller Available", Toast.LENGTH_SHORT).show();
        else
        {
            while (cursor.moveToNext())
            {
                sellerNamesList.add(new SellerNames(cursor.getString(1), cursor.getInt(0)));
            }
        }

    }

    public void openNameEntryDialog()
    {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.new_customer_dialog);
        EditText enterSellerName = dialog.findViewById(R.id.customerName);
        enterSellerName.setHint("Enter Seller Name");
        Button saveSellerName = dialog.findViewById(R.id.saveCustomer);
        Button cancelDialog = dialog.findViewById(R.id.cancelCustomer);

        saveSellerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isInserted = db.insertIntoSellerNameTable(enterSellerName.getText().toString().toUpperCase());
                if(isInserted){
                    Toast.makeText(getContext(), "New Seller Added", Toast.LENGTH_SHORT).show();

                    sellerNamesList.clear();
                    getDataFromSellerNameTable();
                    adapter = new SellerNameAdapter(sellerNamesList, (AppCompatActivity) getActivity());
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(linearLayoutManager);
                }
                dialog.dismiss();
            }
        });
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}