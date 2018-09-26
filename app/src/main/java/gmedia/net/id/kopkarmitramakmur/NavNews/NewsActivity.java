package gmedia.net.id.kopkarmitramakmur.NavNews;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import gmedia.net.id.kopkarmitramakmur.R;

public class NewsActivity extends AppCompatActivity {

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        initUI();
    }

    private void initUI() {

        FrameLayout flContainer = (FrameLayout) findViewById(R.id.fl_main_container);
        flContainer.removeAllViews();

        fragment = new MainNews();
        callFragment(fragment);
    }

    private void callFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_main_container, fragment, fragment.getClass().getSimpleName()).setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
