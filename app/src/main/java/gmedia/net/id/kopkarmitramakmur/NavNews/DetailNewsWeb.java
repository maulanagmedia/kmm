package gmedia.net.id.kopkarmitramakmur.NavNews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.MainActivity;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class DetailNewsWeb extends AppCompatActivity {

    private WebView wvNews;
    private String link, idNews;
    private Toolbar toolbar;
    private ItemValidation iv = new ItemValidation();
    private String state = "0";
    private String backToHome = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news_web);

        getSupportActionBar().hide();
        initUI();
    }

    private void initUI() {

        wvNews = (WebView) findViewById(R.id.wv_news);
        wvNews.getSettings().setJavaScriptEnabled(true);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("News");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent intent = new Intent(DetailNewsWeb.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                finish();*/
                onBackPressed();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            idNews = bundle.getString("id");

            try {

                state = bundle.getString("state");
            }catch (Exception e){

                e.printStackTrace();
            }

            try {

                if(bundle.getString("back") != null) backToHome = bundle.getString("back");
            }catch (Exception e){

                e.printStackTrace();
            }
            getNewsDetail();
        }
    }

    private void getNewsDetail(){

        JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(DetailNewsWeb.this, jBody, "GET", WebServiceURL.getNews + idNews, "", "", 0, new ApiVolley.VolleyCallback() {
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
                            link = json.getString("link");
                            if(!link.equals("")){

                                URL url = null;
                                try {
                                    url = new URL(link);
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }

                                new Thread(){

                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        super.run();
                                        try {
                                            URL url = new URL(link);
                                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                                            con.setRequestMethod("HEAD");
                                            con.connect();
                                            if(con.getResponseCode() == HttpURLConnection.HTTP_OK){

                                                loadURL(link);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            backToHeadNews();
                                        }
                                    }

                                }.start();
                            }else{
                                backToHeadNews();
                            }

                        }
                    }else{
                        backToHeadNews();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    backToHeadNews();
                }
            }

            @Override
            public void onError(String result) {
                backToHeadNews();
            }
        });
    }

    private void loadURL(final String url) {
        wvNews.post(new Runnable() {
            public void run() {
                wvNews.loadUrl(url);
            }
        });
    }

    private void backToHeadNews(){
        Intent intent = new Intent(DetailNewsWeb.this, DetailNews.class);
        intent.putExtra("id", idNews);
        intent.putExtra("state", "1");
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
        //onBackPressed();
    }

    @Override
    public void onBackPressed() {

        if( backToHome.equals("1") && !getIntent().getBooleanExtra("backto", false)){

            Intent intent = new Intent(DetailNewsWeb.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            finish();
            return;
        }else{
            super.onBackPressed();
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        }

        /*if(state.equals("2")){
            Intent intent = new Intent(DetailNewsWeb.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("state",state);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            finish();
        }else{
            Intent intent = new Intent(DetailNewsWeb.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("state", "1");
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            finish();
        }*/
    }
}
