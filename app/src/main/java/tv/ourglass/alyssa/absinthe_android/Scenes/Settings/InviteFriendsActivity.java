package tv.ourglass.alyssa.absinthe_android.Scenes.Settings;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import tv.ourglass.alyssa.absinthe_android.Models.OGConstants;
import tv.ourglass.alyssa.absinthe_android.Models.SharedPrefsManager;
import tv.ourglass.alyssa.absinthe_android.Networking.Applejack;
import tv.ourglass.alyssa.absinthe_android.R;
import tv.ourglass.alyssa.absinthe_android.Scenes.Registration.WelcomeActivity;

import static tv.ourglass.alyssa.absinthe_android.Scenes.Registration.RegistrationBaseActivity.isValidEmail;

public class InviteFriendsActivity extends AppCompatActivity {

    private EditText mEmail;

    private ImageView mEmailCheck;

    private TextView mInvite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);

        mEmail = (EditText)findViewById(R.id.email);

        mEmailCheck = (ImageView)findViewById(R.id.emailCheck);

        mInvite = (TextView)findViewById(R.id.inviteFriend);

        // Set fonts
        Typeface font = Typeface.createFromAsset(getAssets(), OGConstants.regularFont);
        TextView text = (TextView)findViewById(R.id.emailLabel);
        if (text != null) {
            text.setTypeface(font);
        }
        mInvite.setTypeface(font);
        mEmail.setTypeface(font);

        // Add text change listeners
        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isValidEmail(mEmail.getText().toString())) {
                    mEmailCheck.animate().alpha(1f).setDuration(OGConstants.fadeInTime).start();
                    mInvite.animate().alpha(1f).setDuration(OGConstants.fadeInTime).start();

                } else {
                    mEmailCheck.animate().alpha(0f).setDuration(OGConstants.fadeOutTime).start();
                    mInvite.animate().alpha(0f).setDuration(OGConstants.fadeOutTime).start();

                }
            }
        });
    }

    public void invite(View view) {

        // TODO: include progress popup?

        Applejack.getInstance().inviteUser(this, this.mEmail.getText().toString(), new Applejack.HttpCallback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                InviteFriendsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(InviteFriendsActivity.this, "Unable to send invite", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onSuccess(final Response response) {
                InviteFriendsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(InviteFriendsActivity.this, "Invite sent!", Toast.LENGTH_SHORT).show();
                        InviteFriendsActivity.super.onBackPressed();
                    }
                });
            }
        });
    }
}
