package it.vonneumannapps.mylocations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    // questa immagine descrive i luoghi visitati, con indirizzo e immagine

    // lo splash
    void openSplashActivity() {

        Intent intent = new Intent(MainActivity.this, SplashActivity.class);

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openSplashActivity();
    }
}
