package gmedia.net.id.kopkarmitramakmur.NavChangePassword;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.Util.ApiVolley;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;
import gmedia.net.id.kopkarmitramakmur.Util.WebServiceURL;

public class DetailChangePassword extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View layout;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;

    private String mParam1;
    private String mParam2;
    private EditText edtOldPassword, edtNewPassword,edtReNewPassword;
    private LinearLayout llChangePassword;

    public DetailChangePassword() {
        // Required empty public constructor
    }

    public static DetailChangePassword newInstance(String param1, String param2) {
        DetailChangePassword fragment = new DetailChangePassword();
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
        layout = inflater.inflate(R.layout.fragment_detail_change_password, container, false);

        initUI();
        return layout;
    }

    private void initUI() {

        edtOldPassword = (EditText) layout.findViewById(R.id.edt_old_password);
        edtNewPassword = (EditText) layout.findViewById(R.id.edt_new_password);
        edtReNewPassword = (EditText) layout.findViewById(R.id.edt_re_password);
        llChangePassword = (LinearLayout) layout.findViewById(R.id.ll_change_password);

        llChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtOldPassword.getText().length() <=0 ){
                    edtOldPassword.setError("Password lama tidak boleh kosong");
                    edtOldPassword.requestFocus();
                    return;
                }

                if(edtNewPassword.getText().length() <=0 ){
                    edtNewPassword.setError("Password baru tidak boleh kosong");
                    edtNewPassword.requestFocus();
                    return;
                }

                if(edtReNewPassword.getText().length() <=0 ){
                    edtReNewPassword.setError("Re password baru tidak boleh kosong");
                    edtReNewPassword.requestFocus();
                    return;
                }

                if(!edtReNewPassword.getText().toString().trim().equals(edtNewPassword.getText().toString().trim())){
                    edtReNewPassword.setError("Re password baru tidak sama");
                    edtReNewPassword.requestFocus();
                    return;
                }

                JSONObject jBody = new JSONObject();
                session = new SessionManager(getContext());

                try {
                    jBody.put("username", session.getNik());
                    jBody.put("oldpassword", edtOldPassword.getText());
                    jBody.put("newpassword", edtNewPassword.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ApiVolley request = new ApiVolley(getContext(), jBody, "PUT", WebServiceURL.changePassword, "", "", 0, new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        JSONObject responseAPI;
                        try {
                            responseAPI = new JSONObject(result);
                            String status = responseAPI.getJSONObject("metadata").getString("status");
                            String message = responseAPI.getJSONObject("metadata").getString("message");

                            if(iv.parseNullInteger(status) == 200){
                                Toast.makeText(getContext(),"Password berhasil diubah", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getContext(),message, Toast.LENGTH_SHORT).show();
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
        });

    }
}
