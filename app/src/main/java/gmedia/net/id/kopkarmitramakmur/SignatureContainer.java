package gmedia.net.id.kopkarmitramakmur;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;
import java.util.Date;

import gmedia.net.id.kopkarmitramakmur.Util.SignatureBuilder;

public class SignatureContainer extends AppCompatActivity {

    private LinearLayout llSignature;
    private Button btnSave;
    private Button btnClear;
    private String uniqueId;
    public String current = null;
    File mypath;
    private SignatureBuilder mSignature;
    private String tempDir;
    private View viewContainer;
    private String external_dir = "GetSignature";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature_container);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initUI();
    }

    private void initUI() {

        llSignature = (LinearLayout) findViewById(R.id.ll_canvas_sign);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnClear = (Button) findViewById(R.id.btn_clear);
        setTitle("Tanda Tangan Digital");

        File imgDir = new File(android.os.Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_PICTURES) + File.separator + "KMM");
        imgDir.mkdirs();

        tempDir = imgDir.getAbsolutePath();
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir(external_dir, Context.MODE_PRIVATE);
        Date date = new Date();
        uniqueId = "GmediaTV_" + date.getYear()+date.getMonth()+date.getDate()+date.getHours()+date.getMinutes()+date.getSeconds();;
        current = uniqueId + ".png";
        mypath= new File(directory, current);

        mSignature = new SignatureBuilder(this, null, llSignature, mypath);
        mSignature.setBackgroundColor(Color.WHITE);
        llSignature.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        viewContainer = llSignature;

        btnClear.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                Log.d("log_tag", "Panel Cleared");
                mSignature.clear();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.v("log_tag", "Panel Saved");
                viewContainer.setDrawingCacheEnabled(true);
                mSignature.SaveBitmap(viewContainer);
                Bundle b = new Bundle();
                b.putString("status", "done");
                b.putString("image", mSignature.path_image);
                Intent intent = new Intent();
                intent.putExtras(b);
                intent.setData(Uri.parse(mSignature.path_image));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}
