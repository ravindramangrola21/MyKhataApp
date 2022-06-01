package com.example.calculator.Adapters;

import android.app.Activity;
import android.app.Dialog;
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

import com.example.calculator.ChildItem;
import com.example.calculator.DatabaseHelper;
import com.example.calculator.Listenteners.OnCustomerChildChanged;
import com.example.calculator.ParentItem;
import com.example.calculator.R;

import java.util.ArrayList;

public class CustomerEntriesAdapter extends RecyclerView.Adapter<CustomerEntriesAdapter.CustomerEntriesViewHolder> implements OnCustomerChildChanged {

    private Activity activity;
    ArrayList<ParentItem> parentItemList = new ArrayList<>();
    ArrayList<ChildItem> childItemList;
    ArrayList<Integer> dateIDList = new ArrayList<>();
    Integer c_ID;
    private static DatabaseHelper db ;
    private double totalAmount=0, dues=0.0;

    public CustomerEntriesAdapter(Activity activity, ArrayList<ParentItem> parentItemList, ArrayList<Integer> dateIDList, Integer c_ID) {
        this.activity = activity;
        this.parentItemList = parentItemList;
        this.dateIDList = dateIDList;
        this.c_ID = c_ID;
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

        Cursor cursorEntry = db.getDataFromCustomerEntriesTable(dateIDList.get(position), c_ID);
        Cursor depositCursor = db.getDataFromCustomerTrans(dateIDList.get(position), c_ID);
        Double depositFirst =0.0;
        if(depositCursor.getCount()!=0) {
            while (depositCursor.moveToNext())
                depositFirst = depositCursor.getDouble(1);
        }

        childItemList = new ArrayList<>();
        totalAmount = 0;
        while (cursorEntry.moveToNext())
        {
            Double amount = Double.parseDouble(cursorEntry.getString(3)) * Double.parseDouble(cursorEntry.getString(4));
            totalAmount = totalAmount + amount;
            ChildItem childItem = new ChildItem(cursorEntry.getInt(0),cursorEntry.getString(2), cursorEntry.getString(3),
                    cursorEntry.getString(4), amount, dateIDList.get(position), c_ID);
            childItemList.add(childItem);
        }
        dues = totalAmount-depositFirst;
        CustomerSingleEntryAdapter cseAdapater = new CustomerSingleEntryAdapter(childItemList, activity, dues, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        holder.rvChild.setLayoutManager(linearLayoutManager);
        holder.rvChild.setAdapter(cseAdapater);
        holder.totalAmount.setText(String.valueOf(totalAmount));
        holder.depositedAmount.setText(depositFirst.toString());
        holder.dueAmount.setText(String.valueOf(dues));

        if(dues==0.0)
        {
            holder.linearLayout.setBackgroundColor(Color.parseColor("#288654"));
        }
        if(depositFirst==0.0)
            holder.linearLayout.setBackgroundColor(Color.parseColor("#FFC0CB"));


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

    @Override
    public void changed() {
        this.notifyDataSetChanged();
    }


    public class CustomerEntriesViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        RecyclerView rvChild;
        TextView dateEntry, totalAmount, dueAmount, depositedAmount;
        LinearLayout linearLayout;
        Button deposit;
        public CustomerEntriesViewHolder(@NonNull View itemView) {
            super(itemView);

            rvChild = itemView.findViewById(R.id.cEntriesRecyclerView);
            dateEntry = itemView.findViewById(R.id.entrydate);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            depositedAmount = itemView.findViewById(R.id.depositAmount);
            dueAmount = itemView.findViewById(R.id.dueAmount);
            linearLayout = itemView.findViewById(R.id.outerLayout);
            Button deposit= itemView.findViewById(R.id.clickDeposit);
            deposit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customerDeposit();
                }
            });
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
                    boolean isDeleted = db.deleteItemFromTable3ForDate(dateIDList.get(position),c_ID);
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
        public void customerDeposit()
        {
            int adapterPosition = getAdapterPosition();
            int date_id;
            date_id = dateIDList.get(adapterPosition);
            Dialog depositDialog = new Dialog(activity);
            depositDialog.setContentView(R.layout.deposit_entries_dialog);
            ImageView cancelDeposit = depositDialog.findViewById(R.id.cancelImageBtn);
            Button DepositedBtn = depositDialog.findViewById(R.id.deposited);
            EditText deAmount = depositDialog.findViewById(R.id.crntDeposit);
            TextView netAmount = depositDialog.findViewById(R.id.netAmount);
            netAmount.setText(dueAmount.getText());


            cancelDeposit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    depositDialog.dismiss();
                }
            });
            DepositedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean result=db.insertIntoCustomerTransTable(date_id, c_ID,
                            Double.parseDouble(TextUtils.isEmpty(deAmount.getText().toString())?dueAmount.getText().toString():deAmount.getText().toString()));
                    if(result)
                        Toast.makeText(view.getContext(), "Amount Deposited", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                    depositDialog.dismiss();
                }
            });
            depositDialog.show();
        }
    }
}
