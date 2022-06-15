package co.il.admin.Fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import co.il.admin.R;
import co.il.admin.Helper;

public class SignInExplanation extends Fragment implements View.OnClickListener {

    View view;
    EditText etEmail, etPassword;
    TextView tvShowSelectedDate;
    Button btnConfirm, btnSelectIdDate;
    String email, id, idDate;
    DatePickerDialog datePickerDialog;

    FirebaseAuth auth;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_in_explanation, container, false);

        inIt();
        initDatePickerDialog();

        // Inflate the layout for this fragment
        return view;
    }

    private void inIt()
    {
        etEmail = view.findViewById(R.id.edit_text_email);
        etPassword = view.findViewById(R.id.edit_text_password);
        tvShowSelectedDate = view.findViewById(R.id.tv_show_selected_id_date);
        btnConfirm = view.findViewById(R.id.button_confirm_email_and_password);
        btnConfirm.setOnClickListener(this);
        btnSelectIdDate = view.findViewById(R.id.button_select_id_date);
        btnSelectIdDate.setOnClickListener(this);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("מאמת...");

        auth = FirebaseAuth.getInstance();
    }

    private void initDatePickerDialog()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month+=1;
                idDate = makeDateString(year, month, day);
                tvShowSelectedDate.setText("תאריך: " + idDate);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year1 = calendar.get(Calendar.YEAR);
        int month1 = calendar.get(Calendar.MONTH);
        int day1 = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(getActivity(), dateSetListener, year1, month1, day1);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDateString(int year, int month, int day)
    {
        String day1 = String.valueOf(day);
        String month1 = String.valueOf(month);
        String year1 = String.valueOf(year);
        return day1 + "." + month1 + "." + year1;
    }

    @Override
    public void onClick(View view)
    {
        if(view == btnConfirm)
        {
            progressDialog.show();
            email = etEmail.getText().toString();
            id = etPassword.getText().toString();
            if (view == btnConfirm && !email.equals("") && !id.equals("") && !idDate.equals(""))
            {
                removeSpacesFromTheEnd();
                if (isEmailValid(email) && isIdValid(id))
                {
                    String pass = id + idDate;
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                //move to next fragment
                                moveToTrainFacesFragment();
                            }
                        }
                    });

                }
                else
                    showErrorAndCloseProgressDialog();
            }
            else
                showErrorAndCloseProgressDialog();
        }
        else if(view == btnSelectIdDate)
            datePickerDialog.show();
    }

    private void showErrorAndCloseProgressDialog()
    {
        progressDialog.dismiss();
        Helper.showError("אנא הכנס/י אימייל ותעודת זהות תקינים", getActivity());
    }

    private void removeSpacesFromTheEnd()
    {
        if (id.charAt(id.length()-1) == ' ')
            id = id.substring(0, id.length()-1);
        if (email.charAt(email.length()-1) == ' ')
            email = email.substring(0, email.length()-1);
    }

    // פונקציה שבודקת את תקינות האימייל
    private boolean isEmailValid(String email)
    {
        char c;
        for(int i = 0; i < email.length(); i++)
        {
            c = email.charAt(i);
            if((c >= 'a' && c <= 'z') || (c >= '1' && c <= '9'))
            {
                for(int j = i+1; j < email.length(); j++)
                {
                    c = email.charAt(j);
                    if(c == '@')
                    {
                        for(int k = j+1; k < email.length(); k++)
                        {
                            c = email.charAt(k);
                            if((c >= 'a' && c <= 'z') || (c >= '1' && c <= '9'))
                            {
                                for(int l = k+1; l < email.length(); l++)
                                {
                                    c = email.charAt(l);
                                    if(c == '.')
                                    {
                                        for(int m = l+1; m < email.length(); m++)
                                        {
                                            c = email.charAt(m);
                                            if((c >= 'a' && c <= 'z') || (c >= '1' && c <= '9') || c == '-' || c =='_')
                                            {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isIdValid(String id)
    {
        return id.length() == 9;
    }

    private void moveToTrainFacesFragment()
    {
        TrainFacesExplanation trainFacesExplanation = new TrainFacesExplanation();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.add_users_linear_layout, trainFacesExplanation
                , "train faces explanation");
        fragmentTransaction.commit();
    }
}