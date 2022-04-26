package co.il.admin.faceThreads;

import android.os.Handler;
import android.os.Message;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.IOException;

import co.il.admin.Helper;
import co.il.admin.MyFaceClient;

public class CreatePersonThread extends Thread
{

    private final Handler handler;

    public CreatePersonThread(Handler handler)
    {
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String personName = firebaseUser.getDisplayName();
        String exceptionMessage = "";
        Message message = new Message();
        try
        {
            CreatePersonResult createPersonResult = MyFaceClient.faceServiceClient
                    .createPerson(Helper.PERSON_GROUP_ID, personName, null);
            message.obj = createPersonResult;
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
