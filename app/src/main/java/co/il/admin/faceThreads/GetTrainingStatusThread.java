package co.il.admin.faceThreads;

import android.os.Handler;
import android.os.Message;

import com.microsoft.projectoxford.face.contract.TrainingStatus;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.IOException;

import co.il.admin.Helper;
import co.il.admin.MyFaceClient;

public class GetTrainingStatusThread extends Thread
{
    Handler handler;

    public GetTrainingStatusThread(Handler handler)
    {
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();

        boolean isDone = false;

        Message message = new Message();
        String exceptionMessage;
        while (!isDone)
        {
            try
            {
                TrainingStatus status = MyFaceClient.faceServiceClient
                        .getPersonGroupTrainingStatus(Helper.PERSON_GROUP_ID);
                message.what = Helper.SUCCESS_CODE;
                message.obj = status;
                if (status.status == TrainingStatus.Status.Succeeded)
                    isDone = true;
            }
            catch (ClientException | IOException e)
            {
                exceptionMessage = String.format("error on getting training status: %s", e.getMessage());
                message.obj = exceptionMessage;
                message.what = Helper.ERROR_CODE;
            }
            handler.sendMessage(message);
            try
            {
                Thread.sleep(3000);
            }
            catch (Exception e)
            {
                exceptionMessage = String.format("error on getting training status: %s", e.getMessage());
                message.obj = exceptionMessage;
                message.what = Helper.ERROR_CODE;
                handler.sendMessage(message);
            }

        }
    }
}
