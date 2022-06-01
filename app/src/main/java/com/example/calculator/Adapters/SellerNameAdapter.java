package com.example.calculator.Adapters;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calculator.DatabaseHelper;
import com.example.calculator.Fragments.SellerDataFragment;
import com.example.calculator.R;
import com.example.calculator.SellerNames;

import java.security.spec.ECField;
import java.util.ArrayList;

public class SellerNameAdapter extends RecyclerView.Adapter<SellerNameAdapter.SellerNameViewHolder> {
    private ArrayList<SellerNames> sellerNameList ;
    private DatabaseHelper db;
    private AppCompatActivity activity;

    public SellerNameAdapter(ArrayList<SellerNames> sellerNameList, AppCompatActivity activity) {
        this.sellerNameList = sellerNameList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public SellerNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.cutomer_name_card, parent, false);
        return new SellerNameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerNameViewHolder holder, int position) {
        SellerNames sellerName = sellerNameList.get(position);
        holder.sellerName.setText(sellerName.sellerName);
        holder.sellerId.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return sellerNameList.size();
    }

    public class SellerNameViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener
    {
        TextView sellerName, sellerId;
        public SellerNameViewHolder(@NonNull View itemView) {
            super(itemView);
            sellerName = itemView.findViewById(R.id.cListName);
            sellerId = itemView.findViewById(R.id.sNo);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);

        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            showDialog(position);
            return true;
        }

        private void showDialog(int position) {

            Dialog dl = new Dialog(itemView.getContext());
            dl.setContentView(R.layout.long_click_dialog);
            Button updateName= dl.findViewById(R.id.cancelDelete);
            updateName.setText("UPDATE");
            Button deleteName= dl.findViewById(R.id.confirmDelete);
            ImageView imageView = dl.findViewById(R.id.cancelOpr);
            imageView.setVisibility(View.VISIBLE);
            TextView text = dl.findViewById(R.id.questionText);
            text.setText("Do you want to make change in Seller Name?");
            db=new DatabaseHelper(activity);
            SellerNames sellerName = sellerNameList.get(position);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dl.dismiss();
                }
            });
            deleteName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isDeleted = db.deleteSellerName(sellerName.sellerId);

                    if(isDeleted) {
                        Toast.makeText(activity, "Deleted", Toast.LENGTH_SHORT).show();
                        sellerNameList.remove(position);
                        notifyItemRemoved(position);
                    }
                    dl.dismiss();
                }
            });
            updateName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dl.dismiss();
                    Dialog updateDialog = new Dialog(activity);
                    updateDialog.setContentView(R.layout.new_customer_dialog);
                    EditText  newSellerName = updateDialog.findViewById(R.id.customerName);
                    newSellerName.setText(sellerName.sellerName);
                    Button cancelUpdate = updateDialog.findViewById(R.id.cancelCustomer);
                    Button confirmUpdate = updateDialog.findViewById(R.id.saveCustomer);
                    confirmUpdate.setText("UPDATE");
                    cancelUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateDialog.dismiss();
                        }
                    });
                    confirmUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            boolean isUpdated = db.updateSellerName(sellerName.sellerId, newSellerName.getText().toString().toUpperCase());
                            if(isUpdated) {
                                Toast.makeText(activity, "Updated", Toast.LENGTH_SHORT).show();
                                SellerNames sellerNames = new SellerNames(newSellerName.getText().toString().toUpperCase(),sellerName.sellerId);
                                sellerNameList.remove(position);
                                sellerNameList.add(position, sellerNames);
                                notifyItemChanged(position);
                            }
                            updateDialog.dismiss();
                        }
                    });
                    updateDialog.show();
                }
            });
            dl.show();
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            SellerNames sellerName = sellerNameList.get(position);
            SellerDataFragment sellerDataFragment = new SellerDataFragment();
            try {
                Bundle addArgs = new Bundle();
                addArgs.putInt("sellerID",sellerName.sellerId);
                addArgs.putString("sellerName", sellerName.sellerName);
                sellerDataFragment.setArguments(addArgs);
                activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_to_left_transition,
                        R.anim.exit_right_to_left_transition, R.anim.left_to_right_transition, R.anim.exit_left_to_right_transition).
                        replace(R.id.fragment, sellerDataFragment).addToBackStack(null).commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }



        }
    }
}
