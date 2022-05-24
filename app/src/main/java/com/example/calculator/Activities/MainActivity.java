package com.example.calculator.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.calculator.DatabaseHelper;
import com.example.calculator.R;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();
    Button next;
    ImageView imgBtn;
    Button cBtn;
    EditText dateBox;
    private int mDay, mMonth, mYear;
    DatabaseHelper db;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db=new DatabaseHelper(MainActivity.this);
        db.checkTable1AndTable3();
        imgBtn = findViewById(R.id.calImg);
        dateBox=findViewById(R.id.dateValue);
        next = findViewById(R.id.nextBtn);
        cBtn = findViewById(R.id.customerBtn);


        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay=calendar.get(Calendar.DAY_OF_MONTH);

        String date=mDay+"/"+String.valueOf(mMonth+1)+"/"+mYear;
        dateBox.setText(date);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            try {
                String oldDate = bundle.getString("oldDate");
                dateBox.setText(oldDate.toString());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, R.style.Base_Widget_AppCompat_ActionBar_Solid, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        dateBox.setText(day+"/"+String.valueOf(month+1)+"/"+year);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result =db.insertIntoCustomerDateTable(dateBox.getText().toString());
                if(TextUtils.isEmpty(dateBox.getText())){
                    dateBox.setError("Date is Required");
                }
                else{
                    Intent intent = new Intent(MainActivity.this, SellerDataActivity.class);
                    intent.putExtra("date",dateBox.getText().toString());
                    startActivity(intent);
                }
            }
        });
        cBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result =db.insertIntoCustomerDateTable(dateBox.getText().toString());

                if(TextUtils.isEmpty(dateBox.getText())){
                    dateBox.setError("Date is Required");
                }
                else{
                    Intent intent = new Intent(MainActivity.this, CustomerActivity.class);
                    intent.putExtra("date",dateBox.getText().toString());
                    startActivity(intent);
                }

            }
        });
    }
}