package gmedia.net.id.kopkarmitramakmur.MenuInfoPinjaman;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.MenuInfoPinjaman.Adapter.BulanTahunAdapter;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.Converter;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class DetailInfoPinjaman extends AppCompatActivity {

    private List<CustomListItem> masterList, filterList;
    private List<CustomListItem> bulanTahunList;
    private ItemValidation iv = new ItemValidation();
    private LinkedHashMap<String, List<CustomListItem>> bulanTahunHashList;
    private RecyclerView rvListWaktu;
    private String nik;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info_pinjaman);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);

        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Searhbar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                getFilterList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getListPinjaman();
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    private void initUI() {

        setTitle("Pinjaman Tunai");
        session = new SessionManager(DetailInfoPinjaman.this);
        nik = session.getNik();

        rvListWaktu = (RecyclerView) findViewById(R.id.rv_list_waktu);

        getListPinjaman();
    }

    private void getListPinjaman() {

        JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(DetailInfoPinjaman.this, jBody, "GET", WebServiceURL.getPinjaman + nik, "", "", 0, new ApiVolley.VolleyCallback() {
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
                            masterList.add(new CustomListItem(item.getString("id") , item.getString("keterangan"), Converter.convertBulan(item.getString("bulan")), item.getString("tahun"), item.getString("pokok"), item.getString("saldo"), item.getString("angsuran"), item.getString("periode"),item.getString("diangsur")));
                        }
                    }

                    FilterDendaByPembayaran(masterList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    private void getFilterList(String keyword){

        filterList = new ArrayList<>();
        for(CustomListItem cli :masterList){

            String key = cli.getItem3() + " " + cli.getItem4();
            if(key.toLowerCase().contains(keyword.toLowerCase())){

                filterList.add(cli);
            }
        }

        FilterDendaByPembayaran(filterList);
    }

    private void FilterDendaByPembayaran(List<CustomListItem> listItem) {

        bulanTahunHashList = new LinkedHashMap<String, List<CustomListItem>>();

        // Pecah berdasarkan bulan tahun
        for (CustomListItem items: listItem) {

            String key = items.getItem3()+" "+items.getItem4();
            if(bulanTahunHashList.containsKey(key)){

                List<CustomListItem> list = bulanTahunHashList.get(key);
                list.add(items);
            }else{

                List<CustomListItem> list = new ArrayList<CustomListItem>();
                list.add(items);
                bulanTahunHashList.put(key, list);
            }
        }

        bulanTahunList = new ArrayList<>();
        for(String key: bulanTahunHashList.keySet()){
            List<CustomListItem> list = bulanTahunHashList.get(key);

            CustomListItem cli = new CustomListItem(key);
            bulanTahunList.add(cli);
        }

        setBulanTahunList(bulanTahunList);
    }

    private void setBulanTahunList(List listItem) {

        rvListWaktu.setAdapter(null);

        if(listItem != null && listItem.size()>0){

            BulanTahunAdapter adapter = new BulanTahunAdapter(DetailInfoPinjaman.this, listItem, bulanTahunHashList );
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(DetailInfoPinjaman.this, 1);
            rvListWaktu.setLayoutManager(mLayoutManager);
            rvListWaktu.setItemAnimator(new DefaultItemAnimator());
            rvListWaktu.setAdapter(adapter);
        }
    }
}
