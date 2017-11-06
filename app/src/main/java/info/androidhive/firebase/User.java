package info.androidhive.firebase;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Ravi Tamada on 07/10/16.
 * www.androidhive.info
 */

@IgnoreExtraProperties
public class User {

    public String name;
    public String antall;
    public boolean rentefritak;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)

    public User() {
    }

    public User(String name, String antall, boolean rentefritak) {
        this.name = name;
        this.antall = antall;
        this.rentefritak = rentefritak;
    }

}
