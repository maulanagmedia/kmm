package gmedia.net.id.kopkarmitramakmur.MenuBelanjaMandiri.Adapter;

/**
 * Created by Shin on 3/21/2017.
 */

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.Util.ImageUtils;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;

public class DetailRiwayatBelanjaAdapter extends RecyclerView.Adapter<DetailRiwayatBelanjaAdapter.MyViewHolder> {

    private Context context;
    private List<CustomListItem> masterList;
    private AlertDialog.Builder dialog;
    private AlertDialog dialogViews;
    private View dialogView;
    private ItemValidation iv = new ItemValidation();
    private TextView tvTitle;
    private TextView tvHarga;
    private ImageView ivGambar;
    private EditText edtJumlah;
    private LinearLayout llEdit;
    private LinearLayout llCancel;
    private ImageUtils iu = new ImageUtils();
    private TextView tvSubmitCart;
    private TextView tvTitle1;
    private TextView tvHarga1;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivImage;
        public TextView tvTitle, tvJumlah, tvHarga, tvHargaTotal;

        public MyViewHolder(View view) {
            super(view);
            ivImage = (ImageView) view.findViewById(R.id.iv_thumbnail);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvJumlah = (TextView) view.findViewById(R.id.tv_jumlah);
            tvHarga = (TextView) view.findViewById(R.id.tv_harga);
            tvHargaTotal = (TextView) view.findViewById(R.id.tv_total_per_item);
        }
    }

    public DetailRiwayatBelanjaAdapter(Context context, List<CustomListItem> masterList){
        this.context = context;
        this.masterList = masterList;
    }

    @Override
    public DetailRiwayatBelanjaAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_detail_riwayat_belanja, parent, false);

        return new DetailRiwayatBelanjaAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CustomListItem cli = masterList.get(position);

        holder.tvTitle.setText(cli.getItem2());
        holder.tvJumlah.setText(cli.getItem6());
        holder.tvHarga.setText(iv.ChangeToRupiahFormat(cli.getItem3()));
        String totalHarga = String.valueOf(iv.parseNullInteger(cli.getItem6()) * iv.parseNullInteger(cli.getItem3()));
        holder.tvHargaTotal.setText(iv.ChangeToRupiahFormat(totalHarga));
        iu.LoadImageNormal(context, cli.getItem4(), holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }

}