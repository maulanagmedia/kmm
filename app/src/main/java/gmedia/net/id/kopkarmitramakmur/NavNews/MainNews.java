package gmedia.net.id.kopkarmitramakmur.NavNews;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.CustomView.EndlessScroll;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.NavNews.Adapter.NewsAdapter;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class MainNews extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private View layout;
    private RecyclerView rvNews;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private List<CustomListItem> masterList,addList;
    private int startIndex = 0, count;
    private static EndlessScroll lastScroll;

    public MainNews() {
        // Required empty public constructor
    }

    public static MainNews newInstance(String param1, String param2) {
        MainNews fragment = new MainNews();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_main_news, container, false);
        initUI();
        return layout;
    }

    private void initUI() {

        rvNews = (RecyclerView) layout.findViewById(R.id.rv_list_news);
        startIndex = 0;
        count = getResources().getInteger(R.integer.count);
        getNewsData();
    }

    private void getNewsData() {

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("start", String.valueOf(startIndex));
            jBody.put("jumlah", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(getContext(), jBody, "POST", WebServiceURL.getNews, "", "", 0, new ApiVolley.VolleyCallback() {
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
                            masterList.add(new CustomListItem(item.getString("id") ,item.getString("judul"),item.getString("keterangan"),item.getString("gambar")));

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

    private void setAdapter(List<CustomListItem> listItem){

        rvNews.setAdapter(null);

        if(listItem != null && listItem.size()>0){

            NewsAdapter adapter = new NewsAdapter(getContext(), listItem);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
            rvNews.setLayoutManager(mLayoutManager);
            rvNews.setItemAnimator(new DefaultItemAnimator());
            rvNews.setAdapter(adapter);

            EndlessScroll scroll = new EndlessScroll((GridLayoutManager) mLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                    startIndex += count;
                    LoadMoreNews();
                }
            };

            if(lastScroll != null) rvNews.removeOnScrollListener(lastScroll);
            lastScroll = scroll;
            rvNews.addOnScrollListener(scroll);
        }
    }

    private void LoadMoreNews(){

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("start", String.valueOf(startIndex));
            jBody.put("jumlah", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(getContext(), jBody, "POST", WebServiceURL.getNews, "", "", 0, new ApiVolley.VolleyCallback() {
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

                            addList.add(new CustomListItem(item.getString("id") ,item.getString("judul"),item.getString("keterangan"),item.getString("gambar")));

                        }
                        NewsAdapter adapter = (NewsAdapter) rvNews.getAdapter();
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
}
