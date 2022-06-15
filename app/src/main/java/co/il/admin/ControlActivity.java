package co.il.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ControlActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnAddUsers, btnSetElectionsDate;
    TextView tvElectionDate;
    DatePickerDialog datePickerDialog;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        init();
        initDatePickerDialog();
        showCurrentElectionsDate();
        showSuccessUserAdding();
    }

    private void init()
    {
        btnAddUsers = findViewById(R.id.btn_add_users);
        btnAddUsers.setOnClickListener(this);
        btnSetElectionsDate = findViewById(R.id.btn_set_elections_date);
        btnSetElectionsDate.setOnClickListener(this);
        tvElectionDate = findViewById(R.id.tv_election_date);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Date For Elections");
    }

    private void initDatePickerDialog()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month+=1;
                String date = makeDateString(year, month, day);
                tvElectionDate.setText(date);

                databaseReference.child("day").setValue(day);
                databaseReference.child("month").setValue(month);
                databaseReference.child("year").setValue(year);

            }
        };

        Calendar calendar = Calendar.getInstance();
        int year1 = calendar.get(Calendar.YEAR);
        int month1 = calendar.get(Calendar.MONTH);
        int day1 = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, dateSetListener, year1, month1, day1);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
    }

    private String makeDateString(int year, int month, int day)
    {
        return day + "." + month + "." + year;
    }

    private void showCurrentElectionsDate()
    {
        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                int day = snapshot.child("day").getValue(Integer.class);
                int month = snapshot.child("month").getValue(Integer.class);
                int year = snapshot.child("year").getValue(Integer.class);

                String currentDate = makeDateString(year, month, day);
                tvElectionDate.setText(currentDate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //show a dialog after a success user adding
    private void showSuccessUserAdding()
    {
        Intent intent = getIntent();
        String voted = intent.getStringExtra("add user state");
        if (voted != null)
        {
            if (voted.equals("success"))
            {
                new AlertDialog.Builder(this)
                        .setMessage("המשתמש הוסף בהצלחה!")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.dismiss();
                            }
                        })
                        .create().show();
            }
        }
    }

    @Override
    public void onClick(View view)
    {
        if(view == btnAddUsers)
        {
            //move to add users activity
            Intent intent = new Intent(this, AddUsers.class);
            startActivity(intent);
        }
        else if(view == btnSetElectionsDate)
        {
            datePickerDialog.show();
        }
    }
}