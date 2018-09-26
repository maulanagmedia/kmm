package gmedia.net.id.kopkarmitramakmur.NavNews;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.CustomView.JustifiedTextView;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.ImageUtils;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class DetailNews extends AppCompatActivity {

    private TextView tvHead;
    private ImageView ivThumbnail;
    private JustifiedTextView jtvDetail;
    private ItemValidation iv = new ItemValidation();
    private String idNews;
    private SessionManager session;
    private String nik = "", state = "0";
    private ImageUtils iu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);

        initUI();
    }

    private void initUI() {

        tvHead = (TextView) findViewById(R.id.tv_head);
        ivThumbnail = (ImageView) findViewById(R.id.iv_thumbnail);
        jtvDetail = (JustifiedTextView) findViewById(R.id.jtv_detail);
        session = new SessionManager(DetailNews.this);
        nik = session.getNik();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            idNews = bundle.getString("id");
            try {
                state = bundle.getString("state");
            }catch (Exception e){
                e.printStackTrace();
            }

            if(iv.parseNullInteger(state) != 1){
                if(iv.parseNullInteger(state) == 2){
                    Intent intent = new Intent(DetailNews.this, DetailNewsWeb.class);
                    intent.putExtra("id", idNews);
                    intent.putExtra("state", state);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }else{
                    Intent intent = new Intent(DetailNews.this, DetailNewsWeb.class);
                    intent.putExtra("id", idNews);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }

            getDetailData();
        }
    }

    private void getDetailData() {

        iu = new ImageUtils();
        jtvDetail.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.default_body_font_size_2));
        jtvDetail.setTextColor(getResources().getColor(R.color.color_grey));
        jtvDetail.setAlignment(Paint.Align.LEFT);

        JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(DetailNews.this, jBody, "GET", WebServiceURL.getNews + idNews, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < 1;i++){

                            JSONObject json = jsonArray.getJSONObject(i);
                            iu.LoadImageNormal(DetailNews.this, json.getString("gambar"), ivThumbnail);
                            tvHead.setText(json.getString("judul"));
                            jtvDetail.setText(Html.fromHtml(json.getString("keterangan")).toString());

                            /*if(iv.parseNullInteger(state) != 1 && !json.getString("link").equals("")){
                                Intent intent = new Intent(DetailNews.this, DetailNewsWeb.class);
                                intent.putExtra("link", json.getString("link"));
                                intent.putExtra("id",idNews);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            }*/
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
