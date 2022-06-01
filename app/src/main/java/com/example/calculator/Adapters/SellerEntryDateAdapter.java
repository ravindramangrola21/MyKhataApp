package com.example.calculator.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calculator.DatabaseHelper;
import com.example.calculator.Listenteners.OnSellerChildChanged;
import com.example.calculator.R;
import com.example.calculator.SellerItems;

import java.util.ArrayList;

public class SellerEntryDateAdapter extends RecyclerView.Adapter<SellerEntryDateAdapter.SellerEntryDateViewHolder> implements OnSellerChildChanged {
    private ArrayList dateList, sNodateList;
    private int sellerId;
    private Context context;
    private ArrayList<SellerItems> sellerItemsList;
    Cursor cursor1;
    DatabaseHelper db;
    Double NetDueAmount=0.0;
    Double dues = 0.0;


    public SellerEntryDateAdapter(ArrayList dateList, ArrayList sNodateList, int sellerId, Cursor cursor1) {
        this.dateList = dateList;
        this.sNodateList = sNodateList;
        this.sellerId = sellerId;
        this.cursor1 = cursor1;
    }

    @NonNull
    @Override
    public SellerEntryDateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_day_card, parent, false);
        context = parent.getContext();
        return new SellerEntryDateViewHolder(view);
    }

    @SuppressLint("Range")
    @Override
    public void onBindViewHolder(@NonNull SellerEntryDateViewHolder holder, int position) {
        holder.date.setText(dateList.get(position).toString());
        Double depositedAmount=0.0;
        NetDueAmount=0.0;
        db = new DatabaseHelper(context);
        Cursor cursor = db.getDataFromSellerTable((Integer) sNodateList.get(position), sellerId);
        Cursor cursor2 = db.sellerDataFromTable7((Integer) sNodateList.get(position), sellerId);
        while (cursor2.moveToNext())
            depositedAmount = cursor2.getDouble(1);
        sellerItemsList = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                Double iWeight, iRate, tax, chungi, sum;
                iWeight = Double.parseDouble(cursor.getString(3));
                iRate = Double.parseDouble(cursor.getString(4));
                int nangCount = cursor.getInt(6);
                chungi = cursor.getDouble(7);
                tax = cursor.getDouble(8);
                sum = iRate * iWeight + iRate * iWeight * (tax / 100) + nangCount * chungi;
                sellerItemsList.add(new SellerItems(cursor.getInt(0), cursor.getInt(1), (Integer)sNodateList.get(position),
                        cursor.getString(2), cursor.getString(3), cursor.getString(4), nangCount, chungi, tax, sum));
                NetDueAmount = NetDueAmount + sum;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        SellerSingleDataAdapter sellerSingleDataAdapter = new SellerSingleDataAdapter(context, sellerItemsList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        holder.recyclerView.setLayoutManager(linearLayoutManager);
        holder.recyclerView.setAdapter(sellerSingleDataAdapter);
        holder.netAmount.setText(NetDueAmount.toString());

        dues= NetDueAmount-depositedAmount;
        holder.dueAmount.setText(String.valueOf(dues));
        if(dues==0.0)
            holder.sellerLayout.setBackgroundColor(Color.parseColor("#288654"));
        else
            holder.sellerLayout.setBackgroundColor(Color.parseColor("#FFC0CB"));

    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    @Override
    public void sellerChildChanged() {
        this.notifyDataSetChanged();
    }


    public class SellerEntryDateViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView date, netAmount, dueAmount;
        RecyclerView recyclerView;
        Button depositBtn;
        LinearLayout sellerLayout;

        public SellerEntryDateViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.sellerEntryDate);
            recyclerView = itemView.findViewById(R.id.sellerDayEntry);
            netAmount = itemView.findViewById(R.id.dayNetAmount);
            dueAmount = itemView.findViewById(R.id.dayDueAmount);
            depositBtn = itemView.findViewById(R.id.sellerDepositBtn);
            sellerLayout = itemView.findViewById(R.id.sellerDayLayout);

            depositBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    depositSellerAmount();
                }
            });
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
                    boolean isDeleted = db.deleteItemFromTable1((Integer) sNodateList.get(position), true, sellerId, 0, 0.0);
                    if (isDeleted) {
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

        private void depositSellerAmount() {
            int datePos = getAdapterPosition();
            Dialog depositDialog = new Dialog(itemView.getContext());
            depositDialog.setContentView(R.layout.deposit_entries_dialog);
            depositDialog.show();
            ImageView cancelBtn = depositDialog.findViewById(R.id.cancelImageBtn);
            Button depositedBtn = depositDialog.findViewById(R.id.deposited);
            TextView netDueAmount = depositDialog.findViewById(R.id.netAmount);
            EditText currentDeposit = depositDialog.findViewById(R.id.crntDeposit);
            netDueAmount.setText(dueAmount.getText());
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    depositDialog.dismiss();
                }
            });
            depositedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    boolean isDeposited = db.insertIntoSellerTransTable((Integer) sNodateList.get(datePos),
                            sellerId, Double.parseDouble(TextUtils.isEmpty(currentDeposit.getText().toString()) ? dueAmount.getText().toString() : currentDeposit.getText().toString()));
                    if (isDeposited) {
                        Toast.makeText(itemView.getContext(), "Deposited", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                    depositDialog.dismiss();
                }
            });

        }
    }

}
