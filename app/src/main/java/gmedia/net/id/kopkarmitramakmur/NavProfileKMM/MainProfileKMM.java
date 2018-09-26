package gmedia.net.id.kopkarmitramakmur.NavProfileKMM;

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
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.NavProfileKMM.Adapter.ProfileKMMAdapter;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class MainProfileKMM extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View layout;
    private List<CustomListItem> masterList;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView rvProfileKMM;
    private ItemValidation iv = new ItemValidation();

    public MainProfileKMM() {
        // Required empty public constructor
    }

    public static MainProfileKMM newInstance(String param1, String param2) {
        MainProfileKMM fragment = new MainProfileKMM();
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
        layout = inflater.inflate(R.layout.fragment_main_profile_kmm, container, false);

        initUI();
        return layout;
    }

    private void initUI() {

        rvProfileKMM = (RecyclerView) layout.findViewById(R.id.rv_profile_kmm);

        getDetailProfileKMM();
    }

    private void getDetailProfileKMM() {

        JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(getContext(), jBody, "GET", WebServiceURL.getKMMProfile, "", "", 0, new ApiVolley.VolleyCallback() {
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
                            masterList.add(new CustomListItem(item.getString("judul") , item.getString("keterangan")));
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

    private void setAdapter(List<CustomListItem> listItem) {

        rvProfileKMM.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            ProfileKMMAdapter adapter = new ProfileKMMAdapter(getContext(), listItem);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
            rvProfileKMM.setLayoutManager(mLayoutManager);
            rvProfileKMM.setItemAnimator(new DefaultItemAnimator());
            rvProfileKMM.setAdapter(adapter);
        }
    }
}
