package info.androidhive.firebase;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.solver.SolverVariable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.button;
import static info.androidhive.firebase.R.id.antall;
import static info.androidhive.firebase.R.id.name;
import static info.androidhive.firebase.R.id.seekBar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView txtDetails;
    private TextView txtNumLambo;
    private TextView statusRente;
    private Switch inputRente;
    private EditText inputName, inputAntall;
    private Button btnSave;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private String userId;
    private SeekBar valueRente;






    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Displaying toolbar icon
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        valueRente =(SeekBar) findViewById(R.id.seekBar); // initiate the Seekbar
        txtDetails = (TextView) findViewById(R.id.txt_user);
        txtNumLambo = (TextView) findViewById(R.id.txt_lambo);
        statusRente = (TextView) findViewById(R.id.txt_rente);
        inputRente = (Switch) findViewById(R.id.sw_rentefritak);
        inputName = (EditText) findViewById(name);
        inputAntall = (EditText) findViewById(R.id.antall);
        btnSave = (Button) findViewById(R.id.btn_save);

        // Custom font:
        Typeface snapFont = Typeface.createFromAsset(getAssets(), "fonts/snap.ttf");
        Typeface pertillFont = Typeface.createFromAsset(getAssets(), "fonts/pertill.ttf");
        Typeface black = Typeface.createFromAsset(getAssets(), "fonts/black.otf");

        txtDetails.setTypeface(pertillFont); // OK
        txtNumLambo.setTypeface(pertillFont); //OK
        statusRente.setTypeface(pertillFont);

        inputAntall.setTypeface(black);
        inputName.setTypeface(black);
        btnSave.setTypeface(snapFont); // OK




        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        // store app title to 'app_title' node
        mFirebaseInstance.getReference("app_title").setValue("LamboBanken Inc.");

        // app_title change listener
        mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");

                String appTitle = dataSnapshot.getValue(String.class);

                // update toolbar title
               // getSupportActionBar().setTitle(appTitle);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });

        // Disables "Rente" seekBox if no-rente
        inputRente.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) {
                    valueRente.setEnabled(false);
                    valueRente.setProgress(0);
                }
                else {
                    valueRente.setEnabled(true); }
            }
        });






        // perform seek bar change listener event used for getting the progress value
        valueRente.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this, "Eff.rente: " + progressChangedValue + "% ",
                        Toast.LENGTH_SHORT).show();


            }
        });




        // Save / update the user
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = inputName.getText().toString();
                String antall = inputAntall.getText().toString();

                boolean rente = inputRente.isChecked();


                // Check for already existed userId
                if (TextUtils.isEmpty(userId)) {

                    // Check if both fields have been filled. Else Toast/Return
                    if ((TextUtils.isEmpty(name) || TextUtils.isEmpty(antall))) {
                        Toast.makeText(getApplicationContext(), "AVSKY! \n Fyll ut begge felt!!", Toast.LENGTH_LONG).show();
                        return; // or break, continue, throw
                    }

                    else {
                        createUser(name, antall, rente);
                    }
                } else {
                    updateUser(name, antall, rente);
                }
            }
        });

        toggleButton();
        listUsers();

    }



    private void listUsers() {
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count ", "" + snapshot.getChildrenCount());

                // need to reset if onDataChange happens.
                txtNumLambo.setText("");
                statusRente.setText("");
                txtDetails.setText("");

                txtDetails.append("Kunde:\n\n");
                statusRente.append("Renter:\n\n");
                txtNumLambo.append("Saldo:\n\n");

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User post = postSnapshot.getValue( User.class);

                    // Only for debugging:
                    String format1 = "%-20s %s %s %n";
                    System.out.printf(format1, post.name, post.antall, post.rentefritak);

                    // Printing all bank-depositors
                    txtDetails.append(post.name + "\n");
                    txtNumLambo.append(post.antall + "\n");
                    if (post.rentefritak==false) {
                        statusRente.append("Ja\n"); //Antagelig error -> (må bli string)
                    }
                    else {
                        statusRente.append("Nei\n");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("The read failed: ", firebaseError.getMessage());
            }
        });
    }


    public class MyDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Rentefradrag er kun for");
            builder.setMessage("Nybakte foreldre som \n Studenter med særskilt god årsak ikke kan drikke inn sin Lambo i nærmsete fremtid \n\n Fylles kriteriene?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // You don't have to do anything here if you just want it dismissed when clicked
                }
            });

            builder.setNegativeButton("Nope..", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // You don't have to do anything here if you just want it dismissed when clicked
                }
            });

            // Create the AlertDialog object and return it
            return builder.create();
        }
    }




    // Changing button text
    private void toggleButton() {
        if (TextUtils.isEmpty(userId)) {
            btnSave.setText("Bank it!");
        } else {
            btnSave.setText("Oppdater!");
        }
    }

    /**
     * Creating new user node under 'users'
     */
    private void createUser(String name, String antall, boolean rente) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
        }


        //mFirebaseDatabase.child(userId).child("rentefritak").setValue(rente);

        User user = new User(name, antall, rente);

        mFirebaseDatabase.child(userId).setValue(user);

        addUserChangeListener();
    }

    /**
     * User data change listener
     */
    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.name + ", " + user.antall + ", " + user.rentefritak);

                // Display newly updated name and antall
                //txtDetails.setText(user.name + ", " + user.antall );


                // clear edit text
                inputAntall.setText("");
                inputName.setText("");


                toggleButton();
                listUsers();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }


    private void updateUser(String name, String antall, boolean rente) {
        // updating the user via child nodes
        if (!TextUtils.isEmpty(name)) {
            mFirebaseDatabase.child(userId).child("name").setValue(name);

        }

        if (!TextUtils.isEmpty(antall)) {
            mFirebaseDatabase.child(userId).child("antall").setValue(antall);

        }

        mFirebaseDatabase.child(userId).child("rentefritak").setValue(rente);




    }



}