package co.il.admin;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User
{
    public String databaseKey;
    public String firebaseUid;
    public String nameFromFirebase;
    public String azurePersonId;
    public boolean isVoted;
    public boolean isBlocked;

    public User() {
        //required
    }

    public User(String databaseKey, String firebaseUid, String nameFromFirebase, String azurePersonId)
    {
        this.databaseKey = databaseKey;
        this.firebaseUid = firebaseUid;
        this.nameFromFirebase = nameFromFirebase;
        this.azurePersonId = azurePersonId;
        this.isVoted = false;
        this.isBlocked = false;
    }

    public String getDatabaseKey() {
        return databaseKey;
    }

    public void setDatabaseKey(String databaseKey) {
        this.databaseKey = databaseKey;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public String getNameFromFirebase() {
        return nameFromFirebase;
    }

    public void setNameFromFirebase(String nameFromFirebase) {
        this.nameFromFirebase = nameFromFirebase;
    }

    public String getAzurePersonId() {
        return azurePersonId;
    }

    public void setAzurePersonId(String azurePersonId) {
        this.azurePersonId = azurePersonId;
    }

    public boolean getIsVoted() {
        return this.isVoted;
    }

    public void setIsVoted(boolean isVoted) {
        this.isVoted = isVoted;
    }

    public boolean getIsBlocked() {
        return this.isBlocked;
    }

    public void setIsBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }
}
