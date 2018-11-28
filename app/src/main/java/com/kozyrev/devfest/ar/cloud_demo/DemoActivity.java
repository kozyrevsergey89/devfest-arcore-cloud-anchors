package com.kozyrev.devfest.ar.cloud_demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.ar.sceneform.samples.solarsystem.R;
import com.kozyrev.devfest.ar.SolarActivity;


public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        findViewById(R.id.button_go_solar).setOnClickListener(v -> SolarActivity.start(DemoActivity.this));
        findViewById(R.id.button_go_host).setOnClickListener(v -> HostActivity.start(DemoActivity.this));
        findViewById(R.id.button_go_resolve).setOnClickListener(
                (unusedView) -> {
                    ResolveDialogFragment dialog = new ResolveDialogFragment();
                    dialog.setOkListener(this::onResolveOkPressed);
                    dialog.show(getSupportFragmentManager(), "Resolve");
                });

    }

    private void onResolveOkPressed(String dialogValue) {
        if (!TextUtils.isEmpty(dialogValue)) {
            int shortCode = Integer.parseInt(dialogValue);
            Toast.makeText(this, "Resolving CLoud Anchor...", Toast.LENGTH_LONG).show();
            ResolveActivity.start(DemoActivity.this, shortCode);
        } else {
            Toast.makeText(this, "Cloud Anchor can not be empty", Toast.LENGTH_SHORT).show();
        }
    }
}
