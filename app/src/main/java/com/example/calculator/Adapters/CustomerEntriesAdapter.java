package com.example.calculator.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calculator.ChildItem;
import com.example.calculator.DatabaseHelper;
import com.example.calculator.ParentItem;
import com.example.calculator.R;

import java.util.ArrayList;

public class CustomerEntriesAdapter extends RecyclerView.Adapter<CustomerEntriesAdapter.CustomerEntriesViewHolder> {

    private Activity activity;
    ArrayList<ParentItem> parentItemList = new ArrayList<>();
    ArrayList<ChildItem> childItemList;
    ArrayList<Integer> dateIDList = new ArrayList<>();
    ArrayList<Integer> cIDList = new ArrayList<>();
    private static DatabaseHelper db;
    private double totalAmount=0;

    public CustomerEntriesAdapter(Activity activity, ArrayList<ParentItem> parentItemList, ArrayList<Integer> dateIDList, ArrayList<Integer> cIDList) {
        this.activity = activity;
        this.parentItemList = parentItemList;
        this.dateIDList = dateIDList;
        this.cIDList = cIDList;
    }


    @NonNull
    @Override
    public CustomerEntriesAdapter.CustomerEntriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        db= new DatabaseHelper(activity);
        view = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.customer_entries_card, parent, false);
        return new CustomerEntriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerEntriesAdapter.CustomerEntriesViewHolder holder, int position) {
        ParentItem parentItem = parentItemList.get(position);
        holder.dateEntry.setText(parentItem.date);

        Cursor cursorEntry = db.getDataFromCustomerEntriesTable(dateIDList.get(position), cIDList.get(position));
        childItemList = new ArrayList<>();
        totalAmount = 0;
        while (cursorEntry.moveToNext())
        {
            Double amount = Double.parseDouble(cursorEntry.getString(3)) * Double.parseDouble(cursorEntry.getString(4));
            totalAmount = totalAmount + amount;
            ChildItem childItem = new ChildItem(cursorEntry.getInt(0),cursorEntry.getString(2), cursorEntry.getString(3), cursorEntry.getString(4), amount);
            childItemList.add(childItem);
        }
        CustomerSingleEntryAdapter cseAdapater = new CustomerSingleEntryAdapter(childItemList, activity);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        holder.rvChild.setLayoutManager(linearLayoutManager);
        holder.rvChild.setAdapter(cseAdapater);
        holder.totalAmount.setText(String.valueOf(totalAmount));


    }

    @Override
    public int getItemCount() {
        try {
            return parentItemList.size();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }


    public class CustomerEntriesViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        RecyclerView rvChild;
        TextView dateEntry, totalAmount;
        public CustomerEntriesViewHolder(@NonNull View itemView) {
            super(itemView);

            rvChild = itemView.findViewById(R.id.cEntriesRecyclerView);
            dateEntry = itemView.findViewById(R.id.entrydate);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public boolean onLongClick(View view) {
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.long_click_dialog);
            Button cancelDelete, confirmDelete;
            cancelDelete = dialog.findViewById(R.id.cancelDelete);
            confirmDelete = dialog.findViewById(R.id.confirmDelete);

            cancelDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            confirmDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseHelper db = new DatabaseHelper(activity);
                    int position = getAdapterPosition();
                    boolean isDeleted = db.deleteItemFromTable3ForDate(dateIDList.get(position),cIDList.get(position));
                    if(isDeleted)
                    {
                        Toast.makeText(activity, "Entries Deleted", Toast.LENGTH_SHORT).show();
                    }
                    parentItemList.remove(position);
                    notifyItemRemoved(position);
                    dialog.dismiss();
                }
            });
            dialog.show();
            return true;
        }
    }
}
