package co.il.admin;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User
{
    public String databaseKey;
    public String firebaseUid;
    public String nameFromFirebase;
    public String azurePersonId;

    public User() {
        //required
    }

    public User(String databaseKey, String firebaseUid, String nameFromFirebase, String azurePersonId)
    {
        this.databaseKey = databaseKey;
        this.firebaseUid = firebaseUid;
        this.nameFromFirebase = nameFromFirebase;
        this.azurePersonId = azurePersonId;
    }
}
