package com.vne.medicinealert;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vne.medicinealert.Adapter.MedicineAdapter;
import com.vne.medicinealert.Modal.Medicine;
import com.vne.medicinealert.Modal.User;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private int RC_SIGN_IN = 1;
    //firebase instances
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ChildEventListener mChildEventListener;

    private String currentUserId;
    private String currentUserMail;
    private String currentDisplayName;
    FloatingActionButton fab;

    DatabaseReference userMedicines;

    ProgressDialog pd;
    ListView lstMedicines;
    ArrayList<Medicine> medicines;
    MedicineAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lstMedicines = (ListView) findViewById(R.id.lstMedicines);
        pd = new ProgressDialog(MainActivity.this);
        pd.setTitle("Bağlanıyor...");
        pd.setCancelable(false);

        medicines = new ArrayList<>();
        mAdapter = new MedicineAdapter(MainActivity.this, medicines);
        lstMedicines.setAdapter(mAdapter);
        fab = new FloatingActionButton(MainActivity.this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //User is signed in
                    currentUserId = mFirebaseAuth.getCurrentUser().getUid();
                    userMedicines = mDatabaseReference.child("medicines").child(currentUserId);
                    if(mChildEventListener == null){
                        mChildEventListener = new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                // TODO: 20.04.2017 bunların içleri doldurulacak
                                Medicine med = dataSnapshot.getValue(Medicine.class);
                                medicines.add(med);
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                Log.d("onChildRemoved", "calistirildi");
                                Medicine med = dataSnapshot.getValue(Medicine.class);

                                for(int i = 0; i < medicines.size(); i++){
                                    if(medicines.get(i).getMedicineName() == med.getMedicineName() && medicines.get(i).getMedicineTime() == med.getMedicineTime()){
                                        medicines.remove(i);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        };
                    }
                    userMedicines.addChildEventListener(mChildEventListener);
                    pd.show();
                    getMedicines();
                } else {
                    //User is signed out
                    startActivityForResult(
                        AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                            .build(),
                        RC_SIGN_IN);
                }
            }
        };
    }

    protected void fabClick(View v){
        Intent intent = new Intent(this, AddMedicineActivity.class);
        currentUserId = mFirebaseAuth.getCurrentUser().getUid();
        intent.putExtra("userid", currentUserId);
        startActivity(intent);
    }

    protected void getMedicines(){

        currentUserId = mFirebaseAuth.getCurrentUser().getUid();
        userMedicines = mDatabaseReference.child("medicines").child(currentUserId);
        userMedicines.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                medicines.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    medicines.add(data.getValue(Medicine.class));
                }

                mAdapter.notifyDataSetChanged();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                //User signed in
                currentUserId = mFirebaseAuth.getCurrentUser().getUid();
                currentUserMail = mFirebaseAuth.getCurrentUser().getEmail();
                currentDisplayName = mFirebaseAuth.getCurrentUser().getDisplayName();
                DatabaseReference databaseUser = mDatabaseReference.child("users").child(currentUserId);

                databaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            //kullanıcı var
                        } else {
                            //User not exist
                            //Toast.makeText(MainActivity.this, "Kullanıcı yok", Toast.LENGTH_SHORT).show();
                            User u = new User();
                            u.setUserName(currentDisplayName);
                            u.setMail(currentUserMail);
                            mDatabaseReference.child("users").child(currentUserId).setValue(u);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            } else if(resultCode == RESULT_CANCELED){
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
