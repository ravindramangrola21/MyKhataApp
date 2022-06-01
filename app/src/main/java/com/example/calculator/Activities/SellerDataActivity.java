package com.example.calculator.Activities;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.calculator.DatabaseHelper;
import com.example.calculator.Fragments.SellerNameFragment;
import com.example.calculator.R;

public class SellerDataActivity extends AppCompatActivity {

    Button hearderBtn;
    TextView headerDate;
    private String pickDate="dd/mm/yyyy";
    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_data);
        db = new DatabaseHelper(this);
        headerDate = findViewById(R.id.headerDate);
        hearderBtn = findViewById(R.id.headerBtn);

        Bundle bundle= getIntent().getExtras();
        if(bundle!=null) {
            pickDate = bundle.getString("date");
            Log.d("SellerDataActivity", "onCreate: " + pickDate);
            headerDate.setText(pickDate);
        }

        hearderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerDataActivity.this, MainActivity.class);
                intent.putExtra("oldDate",pickDate);
                startActivity(intent);
            }
        });

        SellerNameFragment sfragment1 = new SellerNameFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment, sfragment1, "myFragment");
        fragmentTransaction.commit();
    }
}