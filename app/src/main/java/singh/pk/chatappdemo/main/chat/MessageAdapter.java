package singh.pk.chatappdemo.main.chat;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import singh.pk.chatappdemo.R;
import singh.pk.chatappdemo.main.chat.pojo.Messages;

/**Shows and Manages User Chat message.
 * @author Pankaj*/
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessagesList;
    // Firebase Auth object.
    private FirebaseAuth mAuth;
    // Users Database Reference object.
    private DatabaseReference mUserDatabase;

    // constructor.
    public MessageAdapter(List<Messages> mMessagesList) {
        this.mMessagesList = mMessagesList;
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent, false);

        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        // Create Instance of FirebaseAuth.
        mAuth = FirebaseAuth.getInstance();
        // Get Current user id
        String current_user_id = mAuth.getCurrentUser().getUid();

        Messages c = mMessagesList.get(position);

        // get Id to send message user.
        String from_user = c.getFrom();

        // Create Instance of Database
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        // Get User data and set Recycler view.
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                holder.nameUser.setText(name);

                Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(holder.profileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Set Design to message TextView to both users.
        if (from_user.equals(current_user_id)){

            holder.messageText.setBackgroundResource(R.drawable.message_text_background_other);
            holder.messageText.setTextColor(Color.BLACK);
        } else {
            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.WHITE);
        }
        // Set Message in Chat RecyclerView List.
        holder.messageText.setText(c.getMessage());

    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {


        public TextView nameUser;
        public TextView messageText;
        public CircleImageView profileImage;

        public MessageViewHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.message_text_layout);
            profileImage = itemView.findViewById(R.id.message_profile_layout);
            nameUser = itemView.findViewById(R.id.name_text_layout);
        }
    }
}
