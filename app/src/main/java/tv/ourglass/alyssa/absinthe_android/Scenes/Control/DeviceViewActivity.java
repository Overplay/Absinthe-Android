package tv.ourglass.alyssa.absinthe_android.Scenes.Control;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import tv.ourglass.alyssa.absinthe_android.Models.OGConstants;
import tv.ourglass.alyssa.absinthe_android.R;


public class DeviceViewActivity extends AppCompatActivity {

    private TextView mDeviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Show progress bar as web view loads URL
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_device_view);

        // Set name
        mDeviceName = (TextView)findViewById(R.id.deviceName);
        Typeface font = Typeface.createFromAsset(getAssets(), OGConstants.mediumFont);
        mDeviceName.setTypeface(font);
        mDeviceName.setText(getIntent().getStringExtra(OGConstants.deviceNameExtra));

        final WebView webview = (WebView)findViewById(R.id.webview);

        // Configure web view
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(false);

        final Activity activity = this;

        // show progress
        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                activity.setProgress(progress * 1000);
            }
        });

        // show alert on error loading page
        webview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webview.setVisibility(View.INVISIBLE);
                showAlert("Uh oh!", description);
            }
        });

        webview.loadUrl(getIntent().getStringExtra(OGConstants.deviceUrlExtra));
    }

    public void showAlert(String title, String message) {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        DeviceViewActivity.super.onBackPressed();
                    }
                });
        alert.show();
    }
}
