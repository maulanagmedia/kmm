package gmedia.net.id.kopkarmitramakmur.MenuBelanjaMandiri;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.MainActivity;
import gmedia.net.id.kopkarmitramakmur.MenuBelanjaMandiri.Adapter.DetailRiwayatBelanjaAdapter;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.GlobalFunction;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class DetailRiwayatBelanja extends AppCompatActivity {

    private RecyclerView rvBarang;
    private ItemValidation iv = new ItemValidation();
    private TextView tvTotal;
    private int total = 0;
    private String noTransaksi;
    private List<CustomListItem> masterList;
    private SessionManager session;
    private String backToHome = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_riwayat_belanja);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initUI();
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

    private void initUI() {

        rvBarang = (RecyclerView) findViewById(R.id.rv_barang);
        tvTotal = (TextView) findViewById(R.id.tv_total);

        Bundle bundle = getIntent().getExtras();
        session = new SessionManager(DetailRiwayatBelanja.this);
        if(!session.saveLogin()){
            session.logoutUser();
        }

        if(bundle != null){

            noTransaksi = bundle.getString("no_transaksi");
            setTitle(noTransaksi);
            try {

                if(bundle.getString("back") != null) backToHome = bundle.getString("back");
            }catch (Exception e){

                e.printStackTrace();
            }
            getDetailBarang();
        }
    }

    private void getDetailBarang() {


        JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(DetailRiwayatBelanja.this, jBody, "GET", WebServiceURL.getDetailRiwayatBelanja + session.getNik()+ "/" + noTransaksi, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    masterList = new ArrayList<>();
                    total = 0;
                    String statusTransaksi = "";

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);
                            masterList.add(new CustomListItem(item.getString("id") , item.getString("nama_barang"),item.getString("harga_barang"),item.getString("gambar"),item.getString("jumlah")));
                            total += (iv.parseNullInteger(item.getString("jumlah")) * iv.parseNullInteger(item.getString("harga_barang")));
                            statusTransaksi = GlobalFunction.getStatusBelanja(item.getString("status"));

                        }
                    }

                    setTitle(Html.fromHtml(noTransaksi + " " + statusTransaksi));
                    tvTotal.setText(iv.ChangeToRupiahFormat(String.valueOf(total)));
                    setListAdapter(masterList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    private void setListAdapter(List<CustomListItem> listItem){

        rvBarang.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            DetailRiwayatBelanjaAdapter menuAdapter = new DetailRiwayatBelanjaAdapter(DetailRiwayatBelanja.this, listItem);

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(DetailRiwayatBelanja.this, 1);
            rvBarang.setLayoutManager(mLayoutManager);
            rvBarang.setItemAnimator(new DefaultItemAnimator());
            rvBarang.setAdapter(menuAdapter);
        }
    }


    @Override
    public void onBackPressed() {
        if( backToHome.equals("1") && !getIntent().getBooleanExtra("backto", false)){

            Intent intent = new Intent(DetailRiwayatBelanja.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            finish();
            return;
        }else{
            super.onBackPressed();
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        }
    }
}
