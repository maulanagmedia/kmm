package gmedia.net.id.kopkarmitramakmur.MenuInfoPinjaman.Adapter;

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

public class DetailPinjamanAdapter extends RecyclerView.Adapter<DetailPinjamanAdapter.MyViewHolder> {

    private Context context;
    private List<CustomListItem> masterList;
    private ItemValidation iv = new ItemValidation();

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CardView cvContainer;
        public TextView tvTitle, tvPokokPinjaman, tvSaldoPinjaman, tvAngsuranPBulan, tvPeriodePinjaman, tvSudahDiangsur;

        public MyViewHolder(View view) {
            super(view);
            cvContainer = (CardView) view.findViewById(R.id.cv_container);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvPokokPinjaman = (TextView) view.findViewById(R.id.tv_pokok_pinjaman);
            tvSaldoPinjaman = (TextView) view.findViewById(R.id.tv_saldo_pinjaman);
            tvAngsuranPBulan = (TextView) view.findViewById(R.id.tv_angsuran_per_bulan);
            tvPeriodePinjaman = (TextView) view.findViewById(R.id.tv_periode_pinjaman);
            tvSudahDiangsur = (TextView) view.findViewById(R.id.tv_sudah_diangsur);

        }
    }

    public DetailPinjamanAdapter(Context context, List masterlist){
        this.context = context;
        this.masterList = masterlist;
    }

    @Override
    public DetailPinjamanAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_list_detail_pinjaman, parent, false);

        return new DetailPinjamanAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final CustomListItem cli = masterList.get(position);
        holder.tvTitle.setText(cli.getItem2());
        holder.tvPokokPinjaman.setText(iv.ChangeToRupiahFormat(cli.getItem6()));
        holder.tvSaldoPinjaman.setText(iv.ChangeToRupiahFormat(cli.getItem7()));
        holder.tvAngsuranPBulan.setText(iv.ChangeToRupiahFormat(cli.getItem8()));
        holder.tvPeriodePinjaman.setText(cli.getItem9());
        holder.tvSudahDiangsur.setText(cli.getItem10());
    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }

}