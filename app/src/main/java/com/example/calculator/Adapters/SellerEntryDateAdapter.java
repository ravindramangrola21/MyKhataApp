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

import com.example.calculator.DatabaseHelper;
import com.example.calculator.Fragments.SellerDataFragment;
import com.example.calculator.R;
import com.example.calculator.SellerItems;

import java.util.ArrayList;

public class SellerEntryDateAdapter extends RecyclerView.Adapter<SellerEntryDateAdapter.SellerEntryDateViewHolder> {
    ArrayList dateList;
    ArrayList sNodateList;
    Context context;
    private ArrayList<SellerItems> sellerItemsList;


    public SellerEntryDateAdapter(ArrayList dateList, ArrayList sNodateList) {

        this.dateList = dateList;
        this.sNodateList = sNodateList;
    }

    @NonNull
    @Override
    public SellerEntryDateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_day_card, parent, false);
        context = parent.getContext();
        return new SellerEntryDateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerEntryDateViewHolder holder, int position) {
        holder.date.setText(dateList.get(position).toString());
        DatabaseHelper db = new DatabaseHelper(context);
        Cursor cursor = db.getDataFromSellerTable((Integer) sNodateList.get(position));
        sellerItemsList = new ArrayList<>();

        while (cursor.moveToNext())
        {
            double iWeight =Double.parseDouble(cursor.getString(3));
            double iRate =Double.parseDouble(cursor.getString(4));
            double sum = iRate*iWeight;
            sellerItemsList.add(new SellerItems(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2), cursor.getString(3), cursor.getString(4),sum));
        }
        SellerDataAdapter sellerDataAdapter = new SellerDataAdapter(context, sellerItemsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        holder.recyclerView.setLayoutManager(linearLayoutManager);
        holder.recyclerView.setAdapter(sellerDataAdapter);

        

    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }


    public class SellerEntryDateViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        TextView date;
        RecyclerView recyclerView;

        public SellerEntryDateViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.sellerEntryDate);
            recyclerView = itemView.findViewById(R.id.sellerDayEntry);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            Dialog dialog = new Dialog(itemView.getContext());
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
                    int position = getAdapterPosition();
                    DatabaseHelper db = new DatabaseHelper(itemView.getContext());
                    boolean isDeleted = db.deleteItemFromTable1((Integer) sNodateList.get(position), true);
                    if(isDeleted) {
                        Toast.makeText(context, "Entries Deleted", Toast.LENGTH_SHORT).show();
                        dateList.remove(position);
                        sNodateList.remove(position);
                        notifyItemRemoved(position);
                    }
                    dialog.dismiss();
                }
            });
            dialog.show();
            return true;
        }
    }
}
