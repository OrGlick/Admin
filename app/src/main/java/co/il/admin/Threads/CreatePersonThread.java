package co.il.admin.Threads;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.IOException;

import co.il.admin.AzureCreds;
import co.il.admin.Helper;

public class CreatePersonThread extends Thread
{

    Handler handler;
    FaceServiceClient faceServiceClient;

    public CreatePersonThread(Handler handler)
    {
        this.handler = handler;
    }

    @Override
    public void run()
    {
        super.run();

        // get Azure creds from firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("AzureCreds");
        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                AzureCreds creds = snapshot.getValue(AzureCreds.class);
                faceServiceClient = new FaceServiceRestClient(creds.getEndPoint(), creds.getApiKey());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {}
        });

        //wait until we get the Azure creds from firebase and create the faceServiceClient
        //if we're still waiting for the creds, faceServiceClient we be null
        while (faceServiceClient == null)
        {
            try
            {
                //wait 10 milliseconds
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        String exceptionMessage = "";
        Message message = new Message();
        try
        {
            message.obj = faceServiceClient.createPerson
                    (Helper.PERSON_GROUP_ID, "p", null);
            message.what = Helper.SUCCESS_CODE;
        }
        catch (ClientException | IOException e)
        {
            exceptionMessage = String.format("create person failed: %s", e.getMessage());
            message.obj = exceptionMessage;
            message.what = Helper.ERROR_CODE;
        }
        handler.sendMessage(message);
    }
}
