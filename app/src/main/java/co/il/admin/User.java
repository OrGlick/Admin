package co.il.admin;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User
{
    public String key;
    public String uid;
    public String name;
    public String personId;

    public User(){
        //required
    }

    public User(String key, String uid, String name, String personId)
    {
        this.key = key;
        this.uid = uid;
        this.name = name;
        this.personId = personId;
    }
}
