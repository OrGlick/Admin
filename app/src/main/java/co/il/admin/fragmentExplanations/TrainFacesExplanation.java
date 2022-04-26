package co.il.admin.fragmentExplanations;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Objects;

import co.il.admin.R;
import co.il.admin.ShowTrainingStatus;
import co.il.admin.User;
import co.il.admin.faceThreads.AddFaceThread;
import co.il.admin.faceThreads.CreatePersonThread;
import co.il.admin.Helper;

public class TrainFacesExplanation extends Fragment implements View.OnClickListener {

    View view;
    Button btnContinueToCamera;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    User user;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference userReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentConnectedUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_train_faces_explanation, container, false);


        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        currentConnectedUser = firebaseAuth.getCurrentUser();

        progressBar = view.findViewById(R.id.progress_bar);
        btnContinueToCamera = view.findViewById(R.id.button_start_to_register);
        btnContinueToCamera.setOnClickListener(this);

        handleImage();
        return view;
    }


    @Override
    public void onClick(View view) {
        createPerson();
    }

    private void createPerson() {
        Handler createPersonHandler = new Handler(new Handler.Callback()
        {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                progressBar.setVisibility(View.INVISIBLE);
                if (message.what == Helper.SUCCESS_CODE) {
                    CreatePersonResult createPersonResult = (CreatePersonResult) message.obj;
                    progressBar.setVisibility(View.VISIBLE);
                    saveUserToFireBase(createPersonResult);
                    progressBar.setVisibility(View.INVISIBLE);
                    checkAndAskForPermissionsAndAddTheFaces();
                }
                else if (message.what == Helper.ERROR_CODE) {
                    Helper.showError(String.valueOf(message.obj), getActivity());
                }
                return true;
            }
        });
        CreatePersonThread createPersonThread = new CreatePersonThread(createPersonHandler);
        progressBar.setVisibility(View.VISIBLE);
        createPersonThread.start();
    }

    public void saveUserToFireBase(CreatePersonResult createPersonResult)
    {
        user = new User("", currentConnectedUser.getUid(), currentConnectedUser.getDisplayName()
                , String.valueOf(createPersonResult.personId));
        userReference = firebaseDatabase.getReference("Users").push();
        user.key = userReference.getKey();

        userReference.setValue(user);
    }

    private void checkAndAskForPermissionsAndAddTheFaces()
    {
        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        {
            addFaces();
        }
        else
        {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    int count = 0;
    private void addFaces()
    {
        count++;
        if (count <= 3)
        {
            Intent intentToCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activityResultLauncher.launch(intentToCamera);
        }
        else
        {
            ShowTrainingStatus trainingStatus = new ShowTrainingStatus();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.add_users_linear_layout, trainingStatus
                    , "training status");
            fragmentTransaction.commit();
        }

    }

    private void handleImage() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
                , new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result)
                    {
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
                            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                            Handler handler = new Handler(new Handler.Callback()
                            {
                                @Override
                                public boolean handleMessage(@NonNull Message message)
                                {
                                    progressDialog.dismiss();

                                    if (message.what == Helper.SUCCESS_CODE)
                                    {
                                        addFaces();//continue to add faces
                                    }
                                    else if (message.what == Helper.ERROR_CODE)
                                    {
                                        Helper.showError(String.valueOf(message.obj), getActivity());
                                    }
                                    return true;
                                }
                            });
                            // TODO: 24/04/2022 create an array of addFaceThread and use it (if it crashes)
                            AddFaceThread addFaceThread = new AddFaceThread(handler, inputStream,
                                    user.personId);
                            addFaceThread.start();
                        }
                    }
                });
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->
            {
                if (isGranted) {
                    addFaces();
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

    public TrainFacesExplanation() {
        // Required empty public constructor
    }
}