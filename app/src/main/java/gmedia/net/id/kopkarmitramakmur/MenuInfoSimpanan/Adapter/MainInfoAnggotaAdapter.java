package gmedia.net.id.kopkarmitramakmur.MenuInfoSimpanan.Adapter;

/**
 * Created by Shin on 3/21/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.MenuInfoSimpanan.SubDetailInfoAnggota;
import gmedia.net.id.kopkarmitramakmur.Util.ImageUtils;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;

public class MainInfoAnggotaAdapter extends RecyclerView.Adapter<MainInfoAnggotaAdapter.MyViewHolder> {

    private Context context;
    private List<CustomListItem> masterList;
    private ItemValidation iv = new ItemValidation();
    private String keterangan;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView cvContainer;
        public ImageView ivThumbnail;
        public TextView tvTitle;

        public MyViewHolder(View view) {
            super(view);
            cvContainer = (CardView) view.findViewById(R.id.cv_container);
            ivThumbnail = (ImageView) view.findViewById(R.id.iv_thumbnail);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
        }
    }

    public MainInfoAnggotaAdapter(Context context, List<CustomListItem> masterList, String keterangan){
        this.context = context;
        this.masterList = masterList;
        this.keterangan = keterangan;
    }

    @Override
    public MainInfoAnggotaAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_list_one, parent, false);

        return new MainInfoAnggotaAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final CustomListItem cli = masterList.get(position);

        holder.tvTitle.setText(cli.getItem2());
        // loading image using Picasso library
        ImageUtils iu = new ImageUtils();
        iu.LoadIcon(context, cli.getItem5(), holder.ivThumbnail);

        // Onclick item menu
        holder.cvContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Intent intent = new Intent(context, SubDetailInfoAnggota.class);
            intent.putExtra("title", cli.getItem2());
            intent.putExtra("subtitle", keterangan);
            context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }

}
