package co.il.admin.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import co.il.admin.Fragments.SignUp;
import co.il.admin.R;

public class AddUsersActivity extends AppCompatActivity
{
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users);

        SignUp signInFragment = new SignUp();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.add_users_linear_layout, signInFragment, "sign in explanation fragment");
        fragmentTransaction.commit();
    }
}