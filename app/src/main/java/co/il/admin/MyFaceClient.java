package co.il.admin;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

public class MyFaceClient
{
    private final static String end_point = "https://face-recognition-elections.cognitiveservices.azure.com/face/v1.0";
    private final static String api_key = "1aead12364c046729e104ee6707a3a5f";
    public final static FaceServiceClient faceServiceClient = new FaceServiceRestClient(end_point, api_key);
}
