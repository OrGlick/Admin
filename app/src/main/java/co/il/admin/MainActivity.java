package co.il.admin;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    EditText etEmail, etPassword;
    Button btnConfirm;
    String email, password;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth  = FirebaseAuth.getInstance();
        inIt();
    }

    private void inIt()
    {
        etEmail = findViewById(R.id.edit_text_email);
        etPassword = findViewById(R.id.edit_text_password);
        btnConfirm = findViewById(R.id.button_confirm_email_and_password);
        btnConfirm.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("מאמת...");
    }

    @Override
    public void onClick(View view)
    {
        progressDialog.show();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        if (view == btnConfirm && !email.equals("") && !password.equals(""))
        {
            removeSpacesFromTheEnd();
            if (isEmailValid(email) && isPasswordValid(password))
            {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful())
                                {
                                    firebaseUser = auth.getCurrentUser();

                                    if(firebaseUser.getUid().equals("uQ2vFKEhjOgaa0KoQlh7eBDLIJm1")) //admin uid
                                    {
                                        // move to control activity
                                        Intent intentToAddUsersActivity = new Intent(MainActivity.this, ControlActivity.class);
                                        startActivity(intentToAddUsersActivity);
                                    }
                                    else
                                    {
                                        progressDialog.dismiss();
                                        auth.signOut();
                                        Helper.showError("אימייל או סיסמה לא נכונים", MainActivity.this);
                                    }
                                }
                                else
                                {
                                    progressDialog.dismiss();
                                    Helper.showError("אימייל או סיסמה לא נכונים", MainActivity.this);
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

    private void showErrorAndCloseProgressDialog()
    {
        progressDialog.dismiss();
        Helper.showError("אנא הכנס/י אימייל וסיסמה תקינים", MainActivity.this);
    }

    private void removeSpacesFromTheEnd()
    {
        if (password.charAt(password.length()-1) == ' ')
            password = password.substring(0, password.length()-1);
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

    private boolean isPasswordValid(String password)
    {
        return password.length() == 9;
    }
}