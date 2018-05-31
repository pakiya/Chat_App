package singh.pk.chatappdemo.onboarding.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import singh.pk.chatappdemo.MainActivity;
import singh.pk.chatappdemo.R;

/**Login Activity for already register User.
 * @author Pankaj*/
public class LoginActivity extends AppCompatActivity {

    // Views
    @BindView(R.id.login_email) TextInputLayout mLoginEmail;
    @BindView(R.id.login_password) TextInputLayout mLoginPassword;
    @BindView(R.id.login_btn) Button mLogin_btn;
    // ProgressDialog object.
    private ProgressDialog mLoginProgress;
    // Firebase Auth Object.
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Initialize ButterKnife.
        ButterKnife.bind(this);
        // Create Firebase Auth Instance.
        mAuth = FirebaseAuth.getInstance();
        // Initialize ProgressDialog.
        mLoginProgress = new ProgressDialog(this);

        // Get Username and Password from Views
        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mLoginEmail.getEditText().getText().toString();
                String password = mLoginPassword.getEditText().getText().toString();
                // Validation
                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please wait while we check your credentials.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    // pass data and check from the server UserName and password is correct and not
                    loginUser(email, password);
                }
            }
        });
    }

    // Check UserName and Password from the Server database.
    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    mLoginProgress.dismiss();

                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                } else {
                    mLoginProgress.hide();
                    Toast.makeText(LoginActivity.this, "Cannot Sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
