package gmedia.net.id.kopkarmitramakmur.MenuBelanjaMandiri;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.CustomView.EndlessScroll;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.MenuBelanjaMandiri.Adapter.DaftarBarangAdapter;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.Utils;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class DetailBelanjaMandiri extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private String TAG = "DetailBelanja";
    private static int cartCount = 0;
    private RecyclerView rvListBarang;
    private List<CustomListItem> masterList, filterList, addList;
    public static List<CustomListItem> cartList;
    private int startIndex = 0, count;
    private String keyword = "";
    private AlertDialog.Builder dialog;
    private AlertDialog dialogViews;
    private View dialogView;
    public static int DETAIL_BELANJA = 103;
    public static LayerDrawable iconCart;
    private static EndlessScroll lastScroll;
    private LinearLayout llTampil;
    private ItemValidation iv = new ItemValidation();
    private String kategoriBarang = "";
    private List<String> kategoriBarangList;
    private Spinner spKategori;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_belanja_mandiri);
        handleIntent(getIntent());

        initUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == DETAIL_BELANJA){
            Bundle bundle = data.getExtras();
            if(bundle != null){

                Type cartListType = new TypeToken<List<CustomListItem>>(){}.getType();
                Gson gson = new Gson();
                String cartListGson = bundle.getString("cart");
                if(cartListGson != null){

                    cartList = gson.fromJson(cartListGson, cartListType);
                    updateNotificationsBadge(cartList.size());
                }
            }
        }
    }

    private void initUI() {

        rvListBarang = (RecyclerView) findViewById(R.id.rv_list_barang);
        llTampil = (LinearLayout) findViewById(R.id.ll_tampil);
        spKategori = (Spinner) findViewById(R.id.sp_kategori);

        startIndex = 0;
        count = getResources().getInteger(R.integer.count);
        cartList = new ArrayList<>();
        updateNotificationsBadge(cartList == null ? 0:cartList.size());

        llTampil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DetailBelanjaMandiri.this, RiwayatBelanja.class);
                startActivity(intent);
            }
        });

        //getListBarang();
        getKategoriBarang();

        spKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TextView tvSpinner = (TextView) parent.getChildAt(0);
                if (tvSpinner != null) {
                    tvSpinner.setTextColor(getResources().getColor(R.color.color_white));
                    tvSpinner.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }

                kategoriBarang = parent.getItemAtPosition(position).toString().toLowerCase();
                startIndex = 0;
                getListBarang();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getKategoriBarang(){

        JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(DetailBelanjaMandiri.this, jBody, "GET", WebServiceURL.getKategoriBarang, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    kategoriBarangList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);

                            kategoriBarangList.add(item.getString("nama_kategori").toUpperCase());

                        }
                    }
                    setKategoriAdapter(kategoriBarangList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

            }
        });
    }

    private void setKategoriAdapter(List<String> listItem){

        spKategori.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            ArrayAdapter adapter = new ArrayAdapter(DetailBelanjaMandiri.this, R.layout.spinner_1, listItem);
            spKategori.setAdapter(adapter);
            spKategori.setSelection(0);
            kategoriBarang = spKategori.getItemAtPosition(0).toString().toLowerCase();
            getListBarang();
        }
    }

    private void getListBarang(){

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", keyword);
            jBody.put("start", String.valueOf(startIndex));
            jBody.put("jumlah", String.valueOf(count));
            jBody.put("kategori", kategoriBarang);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(DetailBelanjaMandiri.this, jBody, "POST", WebServiceURL.getListBarang, "", "", 0, new ApiVolley.VolleyCallback() {
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

                            masterList.add(new CustomListItem(item.getString("id") ,item.getString("nama_barang"),item.getString("harga_barang"), item.getString("gambar"), item.getString("info")));

                        }
                    }
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

    private void LoadMoreBarang(){

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", keyword);
            jBody.put("start", String.valueOf(startIndex));
            jBody.put("jumlah", String.valueOf(count));
            jBody.put("kategori", kategoriBarang);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(DetailBelanjaMandiri.this, jBody, "POST", WebServiceURL.getListBarang, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    addList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject item = jsonArray.getJSONObject(i);

                            addList.add(new CustomListItem(item.getString("id") ,item.getString("nama_barang"),item.getString("harga_barang"),item.getString("gambar"), item.getString("info")));

                        }
                        DaftarBarangAdapter adapter = (DaftarBarangAdapter) rvListBarang.getAdapter();
                        adapter.AddMoreData(addList);
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

    private void setListAdapter(List<CustomListItem> listItem){

        rvListBarang.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            DaftarBarangAdapter menuAdapter = new DaftarBarangAdapter(DetailBelanjaMandiri.this, listItem);

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(DetailBelanjaMandiri.this, 1);
            rvListBarang.setLayoutManager(mLayoutManager);
            rvListBarang.setItemAnimator(new DefaultItemAnimator());
            rvListBarang.setAdapter(menuAdapter);

            EndlessScroll scroll = new EndlessScroll((GridLayoutManager) mLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                    startIndex += count;
                    LoadMoreBarang();
                }
            };

            if(lastScroll != null) rvListBarang.removeOnScrollListener(lastScroll);
            lastScroll = scroll;
            rvListBarang.addOnScrollListener(scroll);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Searhbar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_with_cart_menu, menu);
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

                if(newText.trim().equals("")) getFilterList("");
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getFilterList("");
                return false;
            }
        });

        //Cart
        MenuItem item = menu.findItem(R.id.menu_cart);
        iconCart = (LayerDrawable) item.getIcon();

        // Update LayerDrawable's BadgeDrawable
        Utils.setBadgeCount(this, iconCart, cartList == null ? 0:cartList.size());

        return true;
    }

    private void getFilterList(String keyword){

        this.keyword = keyword;
        startIndex = 0;
        getListBarang();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_cart) {

            // Open chart intent
            Gson gson = new Gson();
            Intent intent = new Intent(DetailBelanjaMandiri.this, DetailCart.class);
            String selectedItems = gson.toJson(cartList);
            intent.putExtra("cart", selectedItems);
            startActivityForResult(intent, DETAIL_BELANJA);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "handleIntent: "+ query);
        }
    }

    public void updateNotificationsBadge(int count) {
        cartCount = count;

        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
        invalidateOptionsMenu();
    }
}