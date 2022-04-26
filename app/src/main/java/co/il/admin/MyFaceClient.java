package co.il.admin;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

public class MyFaceClient
{
    private final static String end_point = "https://face-recognition1.cognitiveservices.azure.com/face/v1.0";
    private final static String api_key = "34e5e823448e42819b39f786e698318c";
    public final static FaceServiceClient faceServiceClient = new FaceServiceRestClient(end_point, api_key);
}
