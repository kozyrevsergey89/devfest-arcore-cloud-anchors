package com.kozyrev.devfest.ar.cloud_demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.kozyrev.devfest.ar.SolarActivity;

public class HostActivity extends SolarActivity {

    private static final String TAG = HostActivity.class.getSimpleName();
    private Anchor cloudAnchor;
    private boolean needToStoreAnchor = false;

    public static void start(Context context) {
        context.startActivity(new Intent(context, HostActivity.class));
    }


    private StorageManager storageManager;
    private final SnackbarHelper snackbarHelper = new SnackbarHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageManager = new StorageManager(this);
    }

    @Override
    protected void onNewAnchorCreated(Anchor anchor) {
        Log.i(TAG, "onNewAnchorCreated");
        cloudAnchor = arSceneView.getSession().hostCloudAnchor(anchor);
        needToStoreAnchor = true;
        Log.i(TAG, "hostCloudAnchor called");
        snackbarHelper.showMessage(this, "Now hosting anchor...");
        Toast.makeText(this, "Now hosting anchor...", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onUpdateScene() {
        if (cloudAnchor != null && needToStoreAnchor) {
            Log.i(TAG, "onUpdateScene anchor is not null");
            Anchor.CloudAnchorState cloudState = cloudAnchor.getCloudAnchorState();
            if (cloudState.isError()) {
                snackbarHelper.hide(this);
                Toast.makeText(this, "Error hosting anchor: " + cloudState, Toast.LENGTH_SHORT).show();
                needToStoreAnchor = false;
                finish();
            } else if (cloudState == Anchor.CloudAnchorState.SUCCESS) {
                storageManager.nextShortCode(
                        (shortCode) -> {
                            if (shortCode == null) {
                                snackbarHelper.showMessageWithDismiss(this, "Could not obtain a short code.");
                                Toast.makeText(this, "Could not obtain a short code.", Toast.LENGTH_LONG).show();
                                needToStoreAnchor = false;
                                return;
                            }
                            storageManager.storeUsingShortCode(shortCode, cloudAnchor.getCloudAnchorId());
                            snackbarHelper.showMessageWithDismiss(
                                    this, "Anchor hosted successfully! Cloud Short Code: " + shortCode);
                            Toast.makeText(this, "Anchor hosted successfully! Cloud Short Code: " + shortCode, Toast.LENGTH_LONG).show();
                            needToStoreAnchor = false;
                        });
            }
        }

    }
}
