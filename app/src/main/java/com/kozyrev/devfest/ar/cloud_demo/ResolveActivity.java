package com.kozyrev.devfest.ar.cloud_demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.kozyrev.devfest.ar.SolarActivity;

public class ResolveActivity extends SolarActivity {

    public static final String SHORT_CODE_KEY = "SHORT_CODE_KEY";
    private Anchor resolvedAnchor;

    public static void start(Context context, int shortCode) {
        Intent intent = new Intent(context, ResolveActivity.class);
        intent.putExtra(SHORT_CODE_KEY, shortCode);
        context.startActivity(intent);
    }

    private StorageManager storageManager;
    private final SnackbarHelper snackbarHelper = new SnackbarHelper();
    private boolean needToResolveAnchor = false;
    private boolean needToLoadAnchor = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageManager = new StorageManager(this);
    }

    private void loadCloudAnchor() {
        int shortCode = getIntent().getIntExtra(SHORT_CODE_KEY, 0);

        if (shortCode == 0) {
            Toast.makeText(this, "Cloud Anchor can not be empty", Toast.LENGTH_SHORT).show();
            finish();
        }

        storageManager.getCloudAnchorId(
                shortCode,
                (cloudAnchorId) -> {
                    if (cloudAnchorId == null) {
                        return;
                    }
                    resolvedAnchor = arSceneView.getSession().resolveCloudAnchor(cloudAnchorId);
                    needToResolveAnchor = true;
                    Toast.makeText(this, "Now resolving anchor...", Toast.LENGTH_LONG).show();
                    snackbarHelper.showMessage(this, "Now resolving anchor...");
                });

    }

    @Override
    protected void onUpdateScene() {
        if (needToLoadAnchor && arSceneView.getArFrame().getCamera().getTrackingState() == TrackingState.TRACKING) {
            needToLoadAnchor = false;
            loadCloudAnchor();
        }
        if (resolvedAnchor != null && needToResolveAnchor) {
            // If the app is waiting for a resolving action to complete.
            Anchor.CloudAnchorState cloudState = resolvedAnchor.getCloudAnchorState();
            if (cloudState.isError()) {
                Toast.makeText(this, "Error resolving anchor: " + cloudState, Toast.LENGTH_LONG).show();
                snackbarHelper.showMessageWithDismiss(this, "Error resolving anchor: " + cloudState);
                needToResolveAnchor = false;
            } else if (cloudState == Anchor.CloudAnchorState.SUCCESS) {
                setNewAnchor(resolvedAnchor);
                Toast.makeText(this, "Anchor resolved successfully!", Toast.LENGTH_LONG).show();
                snackbarHelper.showMessageWithDismiss(this, "Anchor resolved successfully!");
                needToResolveAnchor = false;
            }
        }
    }

    @Override
    protected void onSingleTap(MotionEvent tap) {
        // do nothing
    }

    private void setNewAnchor(Anchor resolvedAnchor) {
        AnchorNode anchorNode = new AnchorNode(resolvedAnchor);
        anchorNode.setParent(arSceneView.getScene());
        Node solarSystem = createSolarSystem();
        anchorNode.addChild(solarSystem);
    }
}
