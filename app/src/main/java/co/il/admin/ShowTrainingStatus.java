package co.il.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.microsoft.projectoxford.face.contract.TrainingStatus;

import java.util.Objects;

import co.il.admin.faceThreads.GetTrainingStatusThread;
import co.il.admin.faceThreads.TrainThread;

public class ShowTrainingStatus extends Fragment
{
    View view;
    TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_training_status, container, false);
        textView = view.findViewById(R.id.text_view_training_status);

        train();

        return view;
    }

    private void train()
    {
        Handler handler = new Handler(new Handler.Callback()
        {
            @Override
            public boolean handleMessage(@NonNull Message message)
            {
                if (message.what == Helper.SUCCESS_CODE)
                    showTrainingStatus();
                else
                    Helper.showError(String.valueOf(message.obj), getActivity());
                return true;
            }
        });
        TrainThread trainThread = new TrainThread(handler);
        trainThread.start();
    }

    private void showTrainingStatus()
    {
        Handler handler = new Handler(new Handler.Callback()
        {
            @Override
            public boolean handleMessage(@NonNull Message message)
            {
                TrainingStatus trainingStatus = (TrainingStatus) message.obj;
                if (message.what == Helper.SUCCESS_CODE)
                {
                    textView.setText(String.valueOf(trainingStatus.status));
                    if (trainingStatus.status == TrainingStatus.Status.Succeeded)
                    {
                        Intent intent = new Intent(requireActivity(), ControlActivity.class);
                        intent.putExtra("add user state", "success");
                        startActivity(intent);
                    }
                }
                else
                {
                    Helper.showError(trainingStatus.message, requireActivity());
                }
                return true;
            }
        });
        GetTrainingStatusThread getTrainingStatusThread = new GetTrainingStatusThread(handler);
        getTrainingStatusThread.start();
    }

}