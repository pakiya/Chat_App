package singh.pk.chatappdemo.main.app_user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import singh.pk.chatappdemo.R;
import singh.pk.chatappdemo.main.app_user.pojo.Users;
import singh.pk.chatappdemo.main.profile_setting.user_profile.ProfileActivity;

/**Shows and Manages All ChatApp User.
 * @author Pankaj*/
public class UsersActivity extends AppCompatActivity {

    // Views.
    @BindView(R.id.users_appBar) Toolbar mToolbar;
    @BindView(R.id.users_list) RecyclerView mUsersList;

    // Users Database reference object.
    private DatabaseReference mUsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        // Initialize ButterKnife.
        ButterKnife.bind(this);
        // Set Toolbar Name and back btn.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Users Database Instance and create user filed.
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        // RecyclerView
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Set All Users list data in Users PoJo class Using Firebase Recycler Adapter
        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                mUsersDatabase
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users users, int position) {
                // Set users details in RecyclerView list.
                viewHolder.setDisplayName(users.getName());
                viewHolder.setUserStatus(users.getStatus());
                viewHolder.setUserImage(users.getThumb_image());

                final String user_id = getRef(position).getKey();
                // Set onClickListener in All User List.
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id", user_id);
                        startActivity(profileIntent);

                    }
                });
            }
        };

        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        @BindView(R.id.user_single_name) TextView userNameView;
        @BindView(R.id.user_single_status) TextView userStatusView;
        @BindView(R.id.user_single_image) CircleImageView userImageView;
        @BindView(R.id.user_single_online_icon) ImageView userOnline;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ButterKnife.bind(this, itemView);
        }

        public void setDisplayName(String name){
            userNameView.setText(name);
        }
        public void setUserStatus(String status){
            userStatusView.setText(status);
        }

        public void setUserImage(String thumb_image){
            Picasso.get().load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);
        }

        // TODO set user Status is Online and Not.
        public void setUserOnline(String online_status) {

            if(online_status.equals("true")){

                userOnline.setVisibility(View.VISIBLE);

            } else {

                userOnline.setVisibility(View.INVISIBLE);

            }

        }

    }
}
