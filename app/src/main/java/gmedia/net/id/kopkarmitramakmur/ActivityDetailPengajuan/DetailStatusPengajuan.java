package gmedia.net.id.kopkarmitramakmur.ActivityDetailPengajuan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.ActivityDetailPengajuan.Adapter.DetailStatusAdapter;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.GlobalFunction;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class DetailStatusPengajuan extends AppCompatActivity {

    private TextView tvSubtitle;
    private RecyclerView rvListStatus;
    private String noTransaksi;
    private ItemValidation iv = new ItemValidation();
    private List<CustomListItem> masterList;
    private SessionManager session;
    private String nik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_status_pengajuan);
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

        tvSubtitle = (TextView) findViewById(R.id.tv_subtitle);
        rvListStatus = (RecyclerView) findViewById(R.id.rv_detail_status);
        session = new SessionManager(DetailStatusPengajuan.this);
        nik = session.getNik();
        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            setTitle("Detail Status Pinjaman");
            noTransaksi = bundle.getString("no_transaksi");

            getDetailStatus();
        }
    }

    private void getDetailStatus() {

        JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(DetailStatusPengajuan.this, jBody, "GET", WebServiceURL.getDetailStatus + nik + "/"+ noTransaksi, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    masterList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);
                            String dateFormatDisplay = getResources().getString(R.string.format_date_display);
                            String dateFull = "yyyy-MM-dd hh:mm:ss";
                            masterList.add(new CustomListItem(GlobalFunction.getStatus(item.getString("status")), item.getString("no_transaksi"), iv.ChangeFormatDateString(item.getString("tgl_pengajuan"),dateFull,dateFormatDisplay), iv.ChangeToRupiahFormat(item.getString("nilai")), item.getString("nama_penyetuju"), item.getString("jabatan"), item.getString("keterangan"), iv.ChangeFormatDateString(item.getString("tgl_persetujuan"),dateFull,dateFormatDisplay)));
                        }
                    }

                    setAdapter(masterList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    private void setAdapter(List listItem) {

        rvListStatus.setAdapter(null);

        if(listItem != null && listItem.size()>0){

            DetailStatusAdapter adapter = new DetailStatusAdapter(DetailStatusPengajuan.this, listItem);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(DetailStatusPengajuan.this, 1);
            rvListStatus.setLayoutManager(mLayoutManager);
            rvListStatus.setItemAnimator(new DefaultItemAnimator());
            rvListStatus.setAdapter(adapter);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}
