package gmedia.net.id.kopkarmitramakmur.MenuInfoPotongan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.MenuInfoPotongan.Adapter.PotonganAdapter;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.Converter;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class DetailInfoPotongan extends AppCompatActivity {

    private Spinner spBulan, spTahun;
    private RecyclerView rvPotongan;
    private TextView tvTotal;
    private SessionManager session;
    private String nik;
    private ItemValidation iv = new ItemValidation();
    private List<CustomListItem> masterList, filterList;
    private String bulanNow;
    private String tahunNow;
    private TextView tvNama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_potongan);

        initUI();
    }

    private void initUI() {

        spBulan = (Spinner) findViewById(R.id.sp_bulan);
        spTahun = (Spinner) findViewById(R.id.sp_tahun);
        rvPotongan = (RecyclerView) findViewById(R.id.rv_list_potongan);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        tvNama = (TextView) findViewById(R.id.tv_nama);
        setTitle("Info Potongan");

        session = new SessionManager(DetailInfoPotongan.this);
        nik = session.getNik();
        tvNama.setText(session.getNama()+" ("+ nik+")");

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        bulanNow = Converter.convertBulan(String.valueOf(calendar.get(Calendar.MONTH)+1));
        tahunNow  = String.valueOf(calendar.get(Calendar.YEAR));
        getDetailPotongan();
    }

    private void getDetailPotongan() {

        JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(DetailInfoPotongan.this, jBody, "GET", WebServiceURL.getPotongan + nik, "", "", 0, new ApiVolley.VolleyCallback() {
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
                            masterList.add(new CustomListItem(item.getString("id"), item.getString("nik_pst"), Converter.convertBulan(item.getString("bulan")), item.getString("tahun"), item.getString("keterangan"), item.getString("nilai")));
                        }
                    }

                    parseData(masterList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    private void parseData(final List<CustomListItem> listItem){

        if(listItem != null && listItem.size() > 0){

            // Bulan
            List<String> bulanList = new ArrayList<>();
            /*for (int x = 0; x < listItem.size(); x++){

                CustomListItem item = listItem.get(x);
                boolean isNew = true;
                for(String bulan: bulanList){

                    if(bulan.equals(item.getItem3())) isNew = false;
                }
                if(isNew) bulanList.add(item.getItem3());
            }*/

            bulanList.add("Januari");
            bulanList.add("Februari");
            bulanList.add("Maret");
            bulanList.add("April");
            bulanList.add("Mei");
            bulanList.add("Juni");
            bulanList.add("Juli");
            bulanList.add("Agustus");
            bulanList.add("September");
            bulanList.add("Oktober");
            bulanList.add("November");
            bulanList.add("Desember");
            setBulanAdapter(bulanList);

            // Tahun
            List<String> tahunList = new ArrayList<>();
            for (int x = 0; x < listItem.size(); x++){

                CustomListItem item = listItem.get(x);
                boolean isNew = true;
                for(String tahun: tahunList){

                    if(tahun.equals(item.getItem4())) isNew = false;
                }
                if(isNew) tahunList.add(item.getItem4());
            }
            setTahunAdapter(tahunList);

        }

        spBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(spBulan.getAdapter() != null && spTahun.getAdapter() != null){

                    String selectedBulan = spBulan.getSelectedItem().toString();
                    String selectedTahun = spTahun.getSelectedItem().toString();

                    rvPotongan.setAdapter(null);
                    tvTotal.setText(iv.ChangeToRupiahFormat("0"));

                    if(listItem != null && listItem.size() > 0){

                        int total = 0;
                        filterList = new ArrayList<CustomListItem>();
                        for(CustomListItem item: masterList){

                            if(item.getItem3().toLowerCase().equals(selectedBulan.toLowerCase()) && item.getItem4().equals(selectedTahun)){

                                total += iv.parseNullInteger(item.getItem7());
                                filterList.add(item);
                            }
                        }

                        PotonganAdapter adapter = new PotonganAdapter(DetailInfoPotongan.this, filterList);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(DetailInfoPotongan.this, 1);
                        rvPotongan.setLayoutManager(mLayoutManager);
                        rvPotongan.setItemAnimator(new DefaultItemAnimator());
                        rvPotongan.setAdapter(adapter);
                        tvTotal.setText(iv.ChangeToRupiahFormat(String.valueOf(total)));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(spBulan.getAdapter() != null && spTahun.getAdapter() != null){

                    String selectedBulan = spBulan.getSelectedItem().toString();
                    String selectedTahun = spTahun.getSelectedItem().toString();

                    rvPotongan.setAdapter(null);
                    tvTotal.setText(iv.ChangeToRupiahFormat("0"));

                    if(listItem != null && listItem.size() > 0){

                        int total = 0;
                        filterList = new ArrayList<CustomListItem>();
                        for(CustomListItem item: masterList){

                            if(item.getItem3().toLowerCase().equals(selectedBulan.toLowerCase()) && item.getItem4().equals(selectedTahun)){

                                total += iv.parseNullInteger(item.getItem7());
                                filterList.add(item);
                            }
                        }

                        PotonganAdapter adapter = new PotonganAdapter(DetailInfoPotongan.this, filterList);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(DetailInfoPotongan.this, 1);
                        rvPotongan.setLayoutManager(mLayoutManager);
                        rvPotongan.setItemAnimator(new DefaultItemAnimator());
                        rvPotongan.setAdapter(adapter);
                        tvTotal.setText(iv.ChangeToRupiahFormat(String.valueOf(total)));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setBulanAdapter(List<String> listItem){

        spBulan.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            ArrayAdapter adapter = new ArrayAdapter(DetailInfoPotongan.this, R.layout.normal_spinner, listItem);
            spBulan.setAdapter(adapter);
            spBulan.setSelection(0);
            int x = 0;
            for(String item:listItem){
                if(item.equals(bulanNow)) spBulan.setSelection(x);
                x++;
            }
        }
    }

    private void setTahunAdapter(List<String> listItem){

        spTahun.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            ArrayAdapter adapter = new ArrayAdapter(DetailInfoPotongan.this, R.layout.normal_spinner, listItem);
            spTahun.setAdapter(adapter);
            spTahun.setSelection(0);
            int x = 0;
            for(String item:listItem){
                if(item.equals(tahunNow)) spTahun.setSelection(x);
                x++;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
