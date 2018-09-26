package gmedia.net.id.kopkarmitramakmur.MenuInfoSimpanan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.MenuInfoSimpanan.Adapter.DetailInfoAnggotaAdapter;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class SubDetailInfoAnggota extends AppCompatActivity {

    private String title;
    private TextView tvTitle;
    private RecyclerView rvListInfo;
    private List<CustomListItem> masterList;
    private TextView tvSubtitle;
    private String subtitle;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private String nik;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_detail_info_anggota);

        initUI();
    }

    private void initUI() {

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvSubtitle = (TextView) findViewById(R.id.tv_subtitle);
        rvListInfo = (RecyclerView) findViewById(R.id.rv_detail_info);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        setTitle("Informasi Anggota");

        session = new SessionManager(SubDetailInfoAnggota.this);
        nik = session.getNik();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            title = bundle.getString("title");
            subtitle = bundle.getString("subtitle");
            tvTitle.setText(title);
            tvSubtitle.setText(subtitle);
            getDataList();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataList();
    }

    private void getDataList() {

        JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(SubDetailInfoAnggota.this, jBody, "GET", WebServiceURL.getTabungan + nik, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    masterList = new ArrayList<>();
                    int total = 0;

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);
                            masterList.add(new CustomListItem(item.getString("id_tab") , item.getString("kd_tab"), item.getString("no_tab"), item.getString("keterangan"), item.getString("tot_tab")));
                            total += iv.parseNullInteger(item.getString("tot_tab"));
                        }
                    }

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

        rvListInfo.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            DetailInfoAnggotaAdapter menuAdapter = new DetailInfoAnggotaAdapter(SubDetailInfoAnggota.this, listItem);

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(SubDetailInfoAnggota.this, 1);
            rvListInfo.setLayoutManager(mLayoutManager);
            rvListInfo.setItemAnimator(new DefaultItemAnimator());
            rvListInfo.setAdapter(menuAdapter);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}
