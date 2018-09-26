package gmedia.net.id.kopkarmitramakmur.MenuBelanjaMandiri;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
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
import gmedia.net.id.kopkarmitramakmur.MenuBelanjaMandiri.Adapter.RiwayatBelanjaAdapter;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.Converter;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class RiwayatBelanja extends AppCompatActivity {

    private static RecyclerView rvBelanja;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private String nik = "";
    public static List<CustomListItem> masterList;
    private static List<CustomListItem> filterList;
    private static Spinner spBulan;
    private static Spinner spTahun;
    private static String bulanNow;
    private static String tahunNow;
    private TextView tvNama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_belanja);
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

        setTitle("Riwayat Belanja");
        rvBelanja = (RecyclerView) findViewById(R.id.rv_list_belanja);
        session = new SessionManager(RiwayatBelanja.this);
        spBulan = (Spinner) findViewById(R.id.sp_bulan);
        spTahun = (Spinner) findViewById(R.id.sp_tahun);
        tvNama = (TextView) findViewById(R.id.tv_nama);

        nik = session.getNik();
        tvNama.setText(session.getNama()+" ("+ nik+")");

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        bulanNow = Converter.convertBulan(String.valueOf(calendar.get(Calendar.MONTH)+1));
        tahunNow  = String.valueOf(calendar.get(Calendar.YEAR));
        getDataList();
    }

    private void getDataList() {

        JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(RiwayatBelanja.this, jBody, "GET", WebServiceURL.getRiwayatBelanja + nik, "", "", 0, new ApiVolley.VolleyCallback() {
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
                            masterList.add(new CustomListItem(item.getString("no_transaksi") ,item.getString("tanggal"),item.getString("status"),item.getString("jml_barang"),item.getString("total_harga"), item.getString("jenis_pembayaran")));
                        }
                    }
                    //setListAdapter(masterList);
                    parseData(RiwayatBelanja.this, masterList, "", "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    public static void parseData(final Context context, final List<CustomListItem> listItem, final String posBulan, final String posTahun){

        if(listItem != null && listItem.size() > 0){

            // Bulan
            List<String> bulanList = new ArrayList<>();

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
            setBulanAdapter(context, bulanList, posBulan);

            // Tahun
            List<String> tahunList = new ArrayList<>();
            for (int x = 0; x < listItem.size(); x++){

                CustomListItem item = listItem.get(x);
                String tanggal = item.getItem2();
                String[] arrayTgl = tanggal.split("-");

                boolean isNew = true;
                for(String tahun: tahunList){

                    if(tahun.equals(arrayTgl[0].trim())) isNew = false;
                }
                if(isNew) tahunList.add(arrayTgl[0].trim());
            }
            setTahunAdapter(context, tahunList, posTahun);

        }

        spBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spBulan.getAdapter() != null && spTahun.getAdapter() != null){

                    String selectedBulan = spBulan.getSelectedItem().toString();
                    String selectedTahun = spTahun.getSelectedItem().toString();


                    if(listItem != null && listItem.size() > 0){

                        filterList = new ArrayList<CustomListItem>();
                        for(CustomListItem item: listItem){

                            String tanggal = item.getItem2();
                            String[] arrayTgl = tanggal.split("-");
                            String tahun = arrayTgl[0];
                            String bulan = arrayTgl[1];

                            if(selectedTahun.equals(tahun) && Converter.convertBulan(bulan).equals(selectedBulan)){

                                filterList.add(item);
                            }
                        }

                        setListAdapter(context, filterList, selectedBulan, selectedTahun);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spTahun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spBulan.getAdapter() != null && spTahun.getAdapter() != null){

                    String selectedBulan = spBulan.getSelectedItem().toString();
                    String selectedTahun = spTahun.getSelectedItem().toString();


                    if(listItem != null && listItem.size() > 0){

                        filterList = new ArrayList<CustomListItem>();
                        for(CustomListItem item: listItem){

                            String tanggal = item.getItem2();
                            String[] arrayTgl = tanggal.split("-");
                            String tahun = arrayTgl[0];
                            String bulan = arrayTgl[1];

                            if(selectedTahun.equals(tahun) && Converter.convertBulan(bulan).equals(selectedBulan)){

                                filterList.add(item);
                            }
                        }

                        setListAdapter(context, filterList, selectedBulan, selectedTahun);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private static void setBulanAdapter(Context context, List<String> listItem, String selectedBulan){

        spBulan.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            ArrayAdapter adapter = new ArrayAdapter(context, R.layout.normal_spinner, listItem);
            spBulan.setAdapter(adapter);
            spBulan.setSelection(0);
            int x = 0;
            for (String item:listItem){
                if(item.equals(bulanNow) && selectedBulan.length()<=0) spBulan.setSelection(x);
                if(item.equals(selectedBulan)) spBulan.setSelection(x);
                x++;
            }
        }
    }

    private static void setTahunAdapter(Context context, List<String> listItem, String selectedTahun){

        spTahun.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            ArrayAdapter adapter = new ArrayAdapter(context, R.layout.normal_spinner, listItem);
            spTahun.setAdapter(adapter);
            spTahun.setSelection(0);
            int x = 0;
            for (String item:listItem){
                if(item.equals(tahunNow) && selectedTahun.length()<=0) spTahun.setSelection(x);
                if(item.equals(selectedTahun)){
                    spTahun.setSelection(x);
                }
                x++;
            }
        }
    }

    private static void setListAdapter(Context context, List<CustomListItem> listItem, String selectedBulan, String selectedTahun){

        rvBelanja.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            RiwayatBelanjaAdapter menuAdapter = new RiwayatBelanjaAdapter(context, listItem, selectedBulan, selectedTahun);

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 1);
            rvBelanja.setLayoutManager(mLayoutManager);
            rvBelanja.setItemAnimator(new DefaultItemAnimator());
            rvBelanja.setAdapter(menuAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}
