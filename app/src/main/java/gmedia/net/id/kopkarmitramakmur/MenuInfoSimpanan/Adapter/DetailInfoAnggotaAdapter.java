package gmedia.net.id.kopkarmitramakmur.MenuInfoSimpanan.Adapter;

/**
 * Created by Shin on 3/21/2017.
 */

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.SessionManager;

public class DetailInfoAnggotaAdapter extends RecyclerView.Adapter<DetailInfoAnggotaAdapter.MyViewHolder> {

    private Context context;
    private List<CustomListItem> masterList;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CardView cvContainer;
        public TextView tvNoTab, tvJenisTab, tvKeterangan, tvTotal;

        public MyViewHolder(View view) {
            super(view);
            cvContainer = (CardView) view.findViewById(R.id.cv_container);
            tvNoTab = (TextView) view.findViewById(R.id.tv_no_tab);
            tvJenisTab = (TextView) view.findViewById(R.id.tv_jenis_tab);
            tvKeterangan = (TextView) view.findViewById(R.id.tv_ia_keterangan);
            tvTotal = (TextView) view.findViewById(R.id.tv_total);

        }
    }

    public DetailInfoAnggotaAdapter(Context context, List<CustomListItem> masterList){
        this.context = context;
        this.masterList = masterList;
        session = new SessionManager(context);
    }

    @Override
    public DetailInfoAnggotaAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_detail_info_anggota, parent, false);

        return new DetailInfoAnggotaAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final CustomListItem cli = masterList.get(position);

        holder.tvJenisTab.setText(cli.getItem2());
        holder.tvNoTab.setText(cli.getItem3());
        holder.tvKeterangan.setText(cli.getItem4());
        holder.tvTotal.setText(iv.ChangeToRupiahFormat(cli.getItem6()));

        /*holder.llDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String noTabungan = cli.getItem3();
                Boolean potongGaji = false;
                String keterangan = session.getNama() + " (" + session.getNik() + ")";

                Intent intent = new Intent(context, SubDetailInfoTabungan.class);
                intent.putExtra("no_tabungan", noTabungan);
                intent.putExtra("kode_tabungan", cli.getItem2());
                intent.putExtra("potong_gaji", potongGaji);
                intent.putExtra("keterangan", keterangan);
                context.startActivity(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }

}