package singh.pk.chatappdemo;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import butterknife.BindView;
import butterknife.ButterKnife;
import singh.pk.chatappdemo.main.app_user.UsersActivity;
import singh.pk.chatappdemo.main.profile_setting.SettingsActivity;
import singh.pk.chatappdemo.onboarding.login.StartActivity;

/**Dashboard Activity to ChatApp.
 * @author Pankaj*/
public class MainActivity extends AppCompatActivity {

    // Views
    @BindView(R.id.main_page_toolbar) Toolbar mToolbar;
    @BindView(R.id.main_tabPager) ViewPager mViewPager;
    @BindView(R.id.main_tabs) TabLayout mTabLayout;

    // Set Fragment ViewPager Adapter
    private SectionsPagerAdapter mSectionsPagerAdapter;
    // Users Database Reference object.
    private DatabaseReference mUserRef;
    // Firebase Auth object.
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize ButterKnife.
        ButterKnife.bind(this);
        // Create Instance of FirebaseAuth.
        mAuth = FirebaseAuth.getInstance();
        // Set Toolbar Name.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chat App");

        // For check user stats is online and not.
        if (mAuth.getCurrentUser() != null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }

        // Set Fragment of ViewPager
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // Set Fragment Name in TabLayout Toolbar.
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Get current login user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // User Not LogIn
        if (currentUser == null) sendToStart();
        else mUserRef.child("online").setValue("true");
    }
    // Open Start Activity
    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.main_logout_btn: {
                FirebaseAuth.getInstance().signOut();
                sendToStart();
            }
            break;

            case R.id.main_settings_btn: {
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
            }
            break;

            case R.id.main_all_btn: {
                Intent settingsIntent = new Intent(MainActivity.this, UsersActivity.class);
                startActivity(settingsIntent);
            }
            break;
        }
        return true;
    }
}
