package gmedia.net.id.kopkarmitramakmur;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

import java.util.Timer;
import java.util.TimerTask;

import gmedia.net.id.kopkarmitramakmur.NotificationUtil.InitFirebaseSetting;

public class SplashScreen extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splasch_screen);
        FirebaseApp.initializeApp(this);
        InitFirebaseSetting.getFirebaseSetting(SplashScreen.this);

        //startService(new Intent(SplashScreen.this, TokenGetter.class));
        Timer timer = new Timer();
        timer.schedule(
                new TimerTask() {

                    @Override
                    public void run() {

                        intent = new Intent(SplashScreen.this, LoginScreen.class);
                        finish();
                        startActivity(intent);
                    }
                },
                2000 // 2 sec
        );
    }
}
