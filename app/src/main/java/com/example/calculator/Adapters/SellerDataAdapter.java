package com.example.calculator.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.collection.CircularArray;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calculator.DatabaseHelper;
import com.example.calculator.R;
import com.example.calculator.SellerItems;

import java.util.ArrayList;

public class SellerDataAdapter extends RecyclerView.Adapter<SellerDataAdapter.SellerViewHolder> {
    private Context mContext;
    public ArrayList<SellerItems> sellerItemsArrayList;

    public SellerDataAdapter(Context mContext, ArrayList<SellerItems> sellerItemsArrayList) {
        this.mContext = mContext;
        this.sellerItemsArrayList = sellerItemsArrayList;

    }


    @NonNull
    @Override
    public SellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.entry_card_view, parent, false);
        return new SellerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerViewHolder holder, int position) {
        try {
            SellerItems sellerItem = sellerItemsArrayList.get(position);
            holder.name_id.setText(sellerItem.sellerName);
            holder.item_id.setText(sellerItem.itemName);
            holder.weight_id.setText(sellerItem.itemWeight);
            holder.rate_id.setText(sellerItem.itemRate);
            holder.total_id.setText(sellerItem.totalAmount.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return sellerItemsArrayList.size();
    }

    public class SellerViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        TextView name_id, item_id, weight_id, rate_id, total_id;
        private DatabaseHelper db;

        public SellerViewHolder(@NonNull View itemView) {
            super(itemView);
            name_id = itemView.findViewById(R.id.agent);
            weight_id = itemView.findViewById(R.id.weight);
            rate_id = itemView.findViewById(R.id.rate);
            item_id = itemView.findViewById(R.id.itemname);
            total_id = itemView.findViewById(R.id.totalRupees);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
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
                    DatabaseHelper db = new DatabaseHelper(itemView.getContext());
                    int position = getAdapterPosition();
                    SellerItems sellerItem = sellerItemsArrayList.get(position);
                    boolean isDeleted = db.deleteItemFromTable1(sellerItem.id, false);
                    if(isDeleted) {
                        Toast.makeText(itemView.getContext(), "Entry Deleted", Toast.LENGTH_SHORT).show();
                        sellerItemsArrayList.remove(position);
                        notifyItemRemoved(position);
                    }
                    dialog.dismiss();
                }
            });

            dialog.show();

            return true;
        }

        @Override
        public void onClick(View view) {
            Dialog d= new Dialog(itemView.getContext());
            d.setContentView(R.layout.seller_data_dialog);
            Button okBtn = d.findViewById(R.id.dialogOkBtn);
            TextView sellerName = d.findViewById(R.id.dialogSellerName);
            TextView itemName = d.findViewById(R.id.dialogItemName);
            TextView itemWeight = d.findViewById(R.id.dialogItemWeight);
            TextView itemRate = d.findViewById(R.id.dialogItemRate);
            TextView itemAmount = d.findViewById(R.id.dialogItemAmount);
            SellerItems sellerItem = sellerItemsArrayList.get(getAdapterPosition());
            sellerName.setText(sellerItem.sellerName);
            itemName.setText(sellerItem.itemName);
            itemWeight.setText(sellerItem.itemWeight);
            itemRate.setText(sellerItem.itemRate);
            itemAmount.setText(sellerItem.totalAmount.toString());
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    d.dismiss();
                }
            });
            d.show();
        }
    }
}
