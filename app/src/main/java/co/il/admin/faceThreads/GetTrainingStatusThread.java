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

        Message[] messages = new Message[1000];
        Message[] errorMessages = new Message[1000];
        for (int i = 0; i < messages.length; i++)
        {
            messages[i] = new Message();
            errorMessages[i] = new Message();
        }
        int count = 0;
        String exceptionMessage;
        while (!isDone)
        {
            try
            {
                TrainingStatus status = MyFaceClient.faceServiceClient
                        .getPersonGroupTrainingStatus(Helper.PERSON_GROUP_ID);
                messages[count].what = Helper.SUCCESS_CODE;
                messages[count].obj = status;
                if (status.status == TrainingStatus.Status.Succeeded)
                    isDone = true;
            }
            catch (ClientException | IOException e)
            {
                exceptionMessage = String.format("error on getting training status: %s", e.getMessage());
                messages[count].obj = exceptionMessage;
                messages[count].what = Helper.ERROR_CODE;
            }
            handler.sendMessage(messages[count]);
            try
            {
                Thread.sleep(3000);
            }
            catch (Exception e)
            {
                exceptionMessage = String.format("error on getting training status: %s", e.getMessage());
                errorMessages[count].obj = exceptionMessage;
                errorMessages[count].what = Helper.ERROR_CODE;
                handler.sendMessage(errorMessages[count]);
            }
            count++;
        }
    }
}
