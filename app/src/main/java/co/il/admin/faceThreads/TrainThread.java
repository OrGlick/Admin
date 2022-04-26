package co.il.admin.faceThreads;

import android.os.Handler;
import android.os.Message;

import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import co.il.admin.Helper;
import co.il.admin.MyFaceClient;

public class TrainThread extends Thread
{
    Handler handler;

    public TrainThread(Handler handler)
    {
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();

        String exceptionMessage ;
        Message message = new Message();

        try
        {
            MyFaceClient.faceServiceClient.trainPersonGroup(Helper.PERSON_GROUP_ID);
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
