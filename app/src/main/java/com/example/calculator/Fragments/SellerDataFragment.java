package com.example.calculator.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calculator.Activities.SellerDataActivity;
import com.example.calculator.Adapters.SellerEntryDateAdapter;
import com.example.calculator.DatabaseHelper;
import com.example.calculator.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class SellerDataFragment extends Fragment{
    private TextView sellerNameTxt;
    View view;
    RecyclerView recyclerView;
    ArrayList<String> dateList;
    ArrayList<Integer> sNoDateList;
    DatabaseHelper db;
    SellerEntryDateAdapter adapter;
    private static boolean alertDialogFlag=true;
    FloatingActionButton addnewEntryBtn;
    private String sellerNameArg="seller Name";
    private int sellerIdArg = 1;
    private TextView pickDate;
    Cursor cursor1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.seller_data_fragment, container, false);
        recyclerView =  view.findViewById(R.id.recyclerview);
        db = new DatabaseHelper(getContext());
        sellerNameTxt = view.findViewById(R.id.headeSellerName);
        pickDate = getActivity().findViewById(R.id.headerDate);
        Bundle args= getArguments();
        try {
            sellerNameArg = args.getString("sellerName");
            sellerIdArg = args.getInt("sellerID");
            sellerNameTxt.setText(args.getString("sellerName"));

        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        dateList = new ArrayList<>();
        sNoDateList = new ArrayList<>();

        adapter = new SellerEntryDateAdapter(dateList, sNoDateList, sellerIdArg, cursor1);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        showAlertDialog();
        getData(db);
        addnewEntryBtn = view.findViewById(R.id.addSellerNewEntry);
        addnewEntryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewEntryFormDialog(sellerIdArg);
            }
        });
        return view;
    }

    private void openNewEntryFormDialog(int sellerIdargs) {
        Dialog dl = new Dialog(getContext());
        dl.setContentView(R.layout.seller_new_entry_dialog);
        Button cancel, save;
        cancel = dl.findViewById(R.id.cancelBtn);
        save =  dl.findViewById(R.id.saveBtn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText iName, itemWeight, itemRate, nangCount, chungi, tax;
                iName = dl.findViewById(R.id.itemName);
                itemWeight = dl.findViewById(R.id.itemWeight);
                itemRate = dl.findViewById(R.id.itemRate);
                nangCount = dl.findViewById(R.id.nangCount);
                chungi = dl.findViewById(R.id.chungi);
                tax = dl.findViewById(R.id.tax);

                int date_id=1;
                Cursor cursor = db.getDateID(pickDate.getText().toString());
                while (cursor.moveToNext())
                    date_id=cursor.getInt(0);

                if(TextUtils.isEmpty(iName.getText()))
                    iName.setError("Item name is required");
                else if (TextUtils.isEmpty(itemWeight.getText()))
                    itemWeight.setError("Item weight is required");
                else if(TextUtils.isEmpty(itemRate.getText()))
                    itemRate.setError("Item rate is required");

                else {
                    Bundle args= getArguments();
                    Boolean result = db.insertIntoSellerTable(args.getInt("sellerID"), iName.getText().toString().toUpperCase(),
                            itemWeight.getText().toString(), itemRate.getText().toString(), date_id, TextUtils.isEmpty(nangCount.getText())?1:Integer.parseInt(nangCount.getText().toString()),
                            TextUtils.isEmpty(chungi.getText())?5:Double.parseDouble(chungi.getText().toString()), TextUtils.isEmpty(tax.getText())?0:Double.parseDouble(tax.getText().toString()));
                    if (result) {
                        Toast.makeText(getContext(), "New Entry added", Toast.LENGTH_SHORT).show();

                        dateList.clear();
                        sNoDateList.clear();
                        getData(db);
                        adapter = new SellerEntryDateAdapter(dateList, sNoDateList, sellerIdArg, cursor1);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                    dl.dismiss();
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

    private void showAlertDialog() {
        if(alertDialogFlag)
        {
            Cursor cur = null;
            if(db!=null)
                cur = db.getSellerDataCount(sellerIdArg);
            if(cur!=null && cur.getCount()!=0) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Alert").setMessage("All entries consist Weight in Kg, Rate in Rupay/Kg, Chungi in Rupay/item and Amount in Rupay.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create();
                alertDialog.show();
                alertDialogFlag = false;
            }
        }
    }

    private void getData(DatabaseHelper db) {
        Cursor cursor = db.getDateFromCustomerDateTable( "dd/mm/yyyy",false);

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
                cursor1 = db.getDataFromSellerTable(cursor.getInt(0), sellerIdArg);
                if(cursor1.getCount()==0)
                {
                    sNoDateList.remove(sNoDateList.size()-1);
                    dateList.remove(dateList.size()-1);
                    db.deleteSellerTrans(cursor.getInt(0), sellerIdArg);
                }
            }
            if(sNoDateList.size()==0)
                Toast.makeText(getContext(), "No Entries Available", Toast.LENGTH_SHORT).show();
        }
    }
}