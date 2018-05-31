package singh.pk.chatappdemo.main.profile_setting.status_change;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import singh.pk.chatappdemo.R;

/**Shows and Change User current status.
 * @author Pankaj*/
public class StatusActivity extends AppCompatActivity {

    // Views
    @BindView(R.id.status_appBar) Toolbar mToolbar;
    @BindView(R.id.status_input) TextInputLayout mStatus;
    @BindView(R.id.status_save_btn) Button mSavebtn;

    // Database Reference object.
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    //Progress
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        ButterKnife.bind(this);

        // Create Instance.
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Get current userId.
        String current_uid = mCurrentUser.getUid();

        // Create Users Database Instance.
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        // Set Name and back Btn in Toolbar.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get Status from Setting Activity.
        String status_value = getIntent().getStringExtra("status_value");

        // Set Current status in editText View.
        mStatus.getEditText().setText(status_value);

        // Status is change and update also in database.
        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Progress
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we save the changes");
                mProgress.show();

                String status = mStatus.getEditText().getText().toString();

                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            mProgress.dismiss();

                        } else {

                            Toast.makeText(getApplicationContext(), "There was some error in saving Changes.", Toast.LENGTH_LONG).show();

                        }

                    }
                });

            }
        });
    }
}
