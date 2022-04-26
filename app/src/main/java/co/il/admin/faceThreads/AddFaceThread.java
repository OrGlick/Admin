package co.il.admin.faceThreads;

import android.os.Handler;
import android.os.Message;

import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import co.il.admin.Helper;
import co.il.admin.MyFaceClient;

public class AddFaceThread extends Thread
{
    Handler handler;
    ByteArrayInputStream inputStream;
    String personId;

    public AddFaceThread(Handler handler, ByteArrayInputStream inputStream, String personId)
    {
        this.handler = handler;
        this.inputStream = inputStream;
        this.personId = personId;
    }

    @Override
    public void run() {
        super.run();

        String exceptionMessage;
        Message message = new Message();

        try
        {
            MyFaceClient.faceServiceClient.addPersonFace(Helper.PERSON_GROUP_ID,
                    UUID.fromString(personId), inputStream, "", null);
            message.what = Helper.SUCCESS_CODE;
        }
        catch (ClientException | IOException e)
        {
            exceptionMessage = String.format("add face failed: %s", e.getMessage());
            message.obj = exceptionMessage;
            message.what = Helper.ERROR_CODE;
        }

        handler.sendMessage(message);

    }
}
