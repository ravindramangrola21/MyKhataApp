package com.example.calculator.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calculator.ChildItem;
import com.example.calculator.DatabaseHelper;
import com.example.calculator.Listenteners.OnCustomerChildChanged;
import com.example.calculator.R;

import java.util.ArrayList;

public class CustomerSingleEntryAdapter extends RecyclerView.Adapter<CustomerSingleEntryAdapter.CustomerSingleEntryViewHolder> {
    ArrayList<ChildItem> childItemArrayList;
    Activity activity;
    OnCustomerChildChanged onChildChanged;
    private double dueAmount=0.0;

    public CustomerSingleEntryAdapter(ArrayList<ChildItem> childItemArrayLists, Activity activity, double dueAmount, OnCustomerChildChanged onChildChanged) {
        this.childItemArrayList = childItemArrayLists;
        this.activity = activity;
        this.dueAmount = dueAmount;
        this.onChildChanged = onChildChanged;
    }

    @Override
    public CustomerSingleEntryAdapter.CustomerSingleEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_single_entry_card, parent, false);
        return new CustomerSingleEntryViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull CustomerSingleEntryAdapter.CustomerSingleEntryViewHolder holder, int position) {
        ChildItem childItem = childItemArrayList.get(position);
        holder.itemName.setText(childItem.vegName);
        holder.itemWeight.setText(childItem.vegWeight);
        holder.itemRate.setText(childItem.vegRate);
        holder.itemAmount.setText(String.valueOf(childItem.vegAmount));
    }

    @Override
    public int getItemCount() {
        return childItemArrayList.size();
    }

    public class CustomerSingleEntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView itemName, itemWeight, itemRate, itemAmount;
        LinearLayout linearLayout;
        public CustomerSingleEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item1name);
            itemWeight = itemView.findViewById(R.id.item1weight);
            itemRate = itemView.findViewById(R.id.item1rate);
            itemAmount = itemView.findViewById(R.id.item1sum);
            linearLayout = itemView.findViewById(R.id.linearLayout);

            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.long_click_dialog);
            Button cancelBtn = dialog.findViewById(R.id.cancelDelete);
            Button confirmBtn = dialog.findViewById(R.id.confirmDelete);

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseHelper db= new DatabaseHelper(activity.getApplicationContext());
                    ChildItem childItem = childItemArrayList.get(position);
                    boolean isDeleted= db.deleteItemFromTable3(childItem.entryId, childItem.vegAmount, childItem.cId, childItem.dateId);
                    if(isDeleted) {
                        Toast.makeText(activity.getApplicationContext(), "Item Entry Deleted", Toast.LENGTH_SHORT).show();
                        childItemArrayList.remove(position);
                        notifyItemRemoved(position);
                        onChildChanged.changed();
                    }
                    dialog.dismiss();
                }
            });
            dialog.show();

            return true;
        }
    }
}
