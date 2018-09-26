package gmedia.net.id.kopkarmitramakmur.NavProfile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class DetailProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View layout;
    private TextView tvNik;
    private TextView tvNama;
    private TextView tvTglLahir;
    private TextView tvDept;
    private TextView tvPlafond;
    private TextView tvRekening;
    private TextView tvEmail;
    private TextView tvNoHP;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;

    public DetailProfile() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailProfile newInstance(String param1, String param2) {
        DetailProfile fragment = new DetailProfile();
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

        layout = inflater.inflate(R.layout.fragment_detail_profile, container, false);
        initUI();
        return layout;
    }

    private void initUI() {

        tvNik = (TextView) layout.findViewById(R.id.tv_nik);
        tvNama = (TextView) layout.findViewById(R.id.tv_nama);
        tvTglLahir = (TextView) layout.findViewById(R.id.tv_tgl_lahir);
        tvDept = (TextView) layout.findViewById(R.id.tv_departemen);
        tvPlafond = (TextView) layout.findViewById(R.id.tv_plafond);
        tvRekening = (TextView) layout.findViewById(R.id.tv_rekening);
        tvEmail = (TextView) layout.findViewById(R.id.tv_email);
        tvNoHP = (TextView) layout.findViewById(R.id.tv_no_hp);

        session = new SessionManager(getContext());
        getProfileData(session.getNik());
    }

    private void getProfileData(final String nik) {

        JSONObject jBody = new JSONObject();

        ApiVolley request = new ApiVolley(getContext(), jBody, "GET", WebServiceURL.getPeserta + nik, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");

                        for(int i = 0; i < 1;i++){

                            JSONObject item = jsonArray.getJSONObject(i);
                            tvNik.setText(item.getString("nik_pst"));
                            tvNama.setText(item.getString("nm_pst"));
                            String formatDate = getContext().getResources().getString(R.string.format_date);
                            String formatDateDisplay = getContext().getResources().getString(R.string.format_date_display);
                            String newDate = iv.ChangeFormatDateString(item.getString("tgl_lahir_pst"), formatDate, formatDateDisplay);
                            tvTglLahir.setText(newDate);
                            tvDept.setText(item.getString("departemen"));
                            tvPlafond.setText(iv.ChangeToRupiahFormat(item.getString("plafon")));
                            tvRekening.setText(item.getString("no_rekening"));
                            tvEmail.setText(item.getString("email"));
                            tvNoHP.setText(item.getString("no_hp"));
                        }
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
