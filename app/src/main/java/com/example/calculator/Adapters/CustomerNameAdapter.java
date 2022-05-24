package com.example.calculator.Adapters;

import android.app.Dialog;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calculator.Activities.CustomerAccountActivity;
import com.example.calculator.DatabaseHelper;
import com.example.calculator.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.function.UnaryOperator;

public class CustomerNameAdapter extends RecyclerView.Adapter<CustomerNameAdapter.CustomerNameViewHolder> {
    Context context;
    ArrayList nameList , sNoList;
    String date;

    public CustomerNameAdapter(Context context, ArrayList nameList, ArrayList sNolist, String date) {
        this.context = context;
        this.nameList = nameList;
        this.sNoList = sNolist;
        this.date = date;
    }

    @NonNull
    @Override
    public CustomerNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cutomer_name_card, parent, false);
        return new CustomerNameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerNameAdapter.CustomerNameViewHolder holder, int position) {
        try {
            holder.namelist.setText(String.valueOf(nameList.get(position)));
            holder.sNolist.setText(String.valueOf(sNoList.get(position)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public class CustomerNameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView namelist, sNolist;
        public CustomerNameViewHolder(@NonNull View itemView) {
            super(itemView);
            namelist = itemView.findViewById(R.id.cListName);
            sNolist = itemView.findViewById(R.id.sNo);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            try{
                Intent intent = new Intent(context, CustomerAccountActivity.class);
                intent.putExtra("name", namelist.getText());
                intent.putExtra("ID", sNolist.getText());
                intent.putExtra("date",date);
                context.startActivity(intent);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            DatabaseHelper db = new DatabaseHelper(context);
            Dialog dialog = new Dialog(itemView.getContext());
            dialog.setContentView(R.layout.long_click_dialog);
            Button updateBtn = dialog.findViewById(R.id.cancelDelete);
            Button confirmBtn = dialog.findViewById(R.id.confirmDelete);
            updateBtn.setText("UPDATE");
            ImageView imageView = dialog.findViewById(R.id.cancelOpr);
            TextView question = dialog.findViewById(R.id.questionText);
            question.setText("Do you want to change this entry ?");
            imageView.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Dialog newdialog = new Dialog(itemView.getContext());
                    newdialog.setContentView(R.layout.new_customer_dialog);
                    EditText name = newdialog.findViewById(R.id.customerName);
                    Button UpdateName = newdialog.findViewById(R.id.saveCustomer);
                    UpdateName.setText("UPDATE");
                    Button CancelDialog = newdialog.findViewById(R.id.cancelCustomer);
                    UpdateName.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(View view) {
                            boolean isUpdated = db.UpdateNameInTable2(Integer.parseInt((String) sNolist.getText()), name.getText().toString().toUpperCase());
                            if(isUpdated)
                                Toast.makeText(imageView.getContext(), "Name Updated", Toast.LENGTH_SHORT).show();
                            newdialog.dismiss();
                            notifyDataSetChanged();
                        }
                    });
                    CancelDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            newdialog.dismiss();
                        }
                    });
                    name.setText(namelist.getText());
                    newdialog.show();
                }
            });
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isDeleted = db.deleteItemFromTable2and3(Integer.parseInt(String.valueOf(sNolist.getText())));
                    if(isDeleted) {
                        Toast.makeText(context, " Deleted", Toast.LENGTH_SHORT).show();
                    }
                    sNoList.remove(position);
                    nameList.remove(position);
                    notifyItemRemoved(position);
                    dialog.dismiss();
                }
            });

            dialog.show();
            return true;
        }
    }

}
