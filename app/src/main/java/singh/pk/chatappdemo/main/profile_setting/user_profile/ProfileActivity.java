package singh.pk.chatappdemo.main.profile_setting.user_profile;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import singh.pk.chatappdemo.R;

/**Shows and Manages User Profile.
 * @author Pankaj*/
public class ProfileActivity extends AppCompatActivity {

    // Views
    @BindView(R.id.profile_image) ImageView mProfileImage;
    @BindView(R.id.profile_displayName) TextView mProfileName;
    @BindView(R.id.profile_status) TextView mProfileStatus;
    @BindView(R.id.profile_totalFriends) TextView mProfileFriendsCount;
    @BindView(R.id.profile_send_req_btn) TextView mProfileSendReqBtn;
    @BindView(R.id.profile_decline_btn) TextView mDeclineBtn;

    // User Database Reference Object.
    private DatabaseReference mUsersDatabase;

    // Friend Request Database Reference object.
    private DatabaseReference mFriendReqDatabase;
    // Friends Database Reference object.
    private DatabaseReference mFriendDatabase;

    private FirebaseUser mCurrent_user;

    private ProgressDialog mProgressDialog;

    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        final String user_id = getIntent().getStringExtra("user_id");

        mCurrent_state = "not_friends";

        // Create database Instances.
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        // Set ProgressDialog.
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the user data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        // Get User Data from database and set views.
        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);

                // - ---------------------------FRIENDS LIST / REQUEST FEATURE -----------------------

                mFriendReqDatabase.child(mCurrent_user.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.hasChild(user_id)){

                                    String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                                    if (req_type.equals("received")){

                                        mCurrent_state = "req_received";
                                        mProfileSendReqBtn.setText("Accept Friend Request");

                                        mDeclineBtn.setVisibility(View.VISIBLE);
                                        mDeclineBtn.setEnabled(true);

                                    } else if (req_type.equals("sent")){

                                        mCurrent_state = "req_sent";
                                        mProfileSendReqBtn.setText("Cancel Friend Request");

                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);

                                    }

                                    mProgressDialog.dismiss();

                                } else {
                                    mFriendDatabase.child(mCurrent_user.getUid())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    if (dataSnapshot.hasChild(user_id)){

                                                        mCurrent_state = "friends";
                                                        mProfileSendReqBtn.setText("UnFriend this Person");

                                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                                        mDeclineBtn.setEnabled(false);

                                                    }

                                                    mProgressDialog.dismiss();
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    mProgressDialog.dismiss();
                                                }
                                            });
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                mProgressDialog.dismiss();
                            }
                        });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProfileSendReqBtn.setEnabled(false);

                // - ---------------------------NOT FRIENDS STATE-----------------------

                if (mCurrent_state.equals("not_friends")){

                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).child("request_type").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        mCurrent_state = "req_sent";
                                        mProfileSendReqBtn.setText("Cancel Friend Request");

                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);

                                    }
                                });


                            } else {
                                Toast.makeText(ProfileActivity.this,"Failed Sending Request.", Toast.LENGTH_SHORT).show();
                            }

                            mProfileSendReqBtn.setEnabled(true);

                            mCurrent_state = "req_sent";
                            mProfileSendReqBtn.setText("Cancel Friend Request");
                        }
                    });

                }

                // - ---------------------------CANCEL REQUEST STATE-----------------------

                if (mCurrent_state.equals("req_sent")){

                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mProfileSendReqBtn.setEnabled(true);
                                    mCurrent_state = "not_friends";
                                    mProfileSendReqBtn.setText("Send Friend Request");

                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                    mDeclineBtn.setEnabled(false);
                                }
                            });
                        }
                    });

                }

                // - ---------------------------REQUEST RECEIVED STATE-----------------------

                if (mCurrent_state.equals("req_received")){

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    mFriendDatabase.child(mCurrent_user.getUid()).child(user_id).child("date").setValue(currentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendDatabase.child(user_id).child(mCurrent_user.getUid()).child("date").setValue(currentDate)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {


                                                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue()
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {

                                                                                    mProfileSendReqBtn.setEnabled(true);
                                                                                    mCurrent_state = "friends";
                                                                                    mProfileSendReqBtn.setText("UnFriend this Person");

                                                                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                                                                    mDeclineBtn.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            });

                                                }
                                            });
                                }
                            });
                }


                // - ---------------------------UNFRIENDS-----------------------

                if (mCurrent_state.equals("friends")){

                }

            }
        });
    }
}
