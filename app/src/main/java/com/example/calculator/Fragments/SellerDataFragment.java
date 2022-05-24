package com.example.calculator.Fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.calculator.Activities.SellerDataActivity;
import com.example.calculator.Adapters.SellerDataAdapter;
import com.example.calculator.Adapters.SellerEntryDateAdapter;
import com.example.calculator.DatabaseHelper;
import com.example.calculator.R;

import java.util.ArrayList;


public class SellerDataFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    ArrayList<String> dateList;
    ArrayList<Integer> sNoDateList;
    DatabaseHelper db;
    SellerEntryDateAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_2, container, false);
        recyclerView =  view.findViewById(R.id.recyclerview);
        db = new DatabaseHelper(getContext());


        dateList = new ArrayList<>();
        sNoDateList = new ArrayList<>();

        adapter = new SellerEntryDateAdapter(dateList, sNoDateList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getData(db);
        return view;
    }

    private void getData(DatabaseHelper db) {
        Cursor cursor = db.getDateFromCustomerDateTable("dd/mm/yyyy",false);
        if(cursor.getCount()==0)
        {
            Toast.makeText(getActivity(), "No Entries available", Toast.LENGTH_SHORT).show();
        }
        else
        {
            while(cursor.moveToNext())
            {
                sNoDateList.add(cursor.getInt(0));
                dateList.add(cursor.getString(1));
                Cursor cursor1 = db.getDataFromSellerTable(cursor.getInt(0));
                if(cursor1.getCount()==0)
                {
                    sNoDateList.remove(sNoDateList.size()-1);
                    dateList.remove(dateList.size()-1);
                }


            }
        }
    }
}