package gmedia.net.id.kopkarmitramakmur.NavProfileKMM.Adapter;

/**
 * Created by Shin on 3/21/2017.
 */

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.CustomView.JustifiedTextView;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;

public class ProfileKMMAdapter extends RecyclerView.Adapter<ProfileKMMAdapter.MyViewHolder> {

    private Context context;
    private List<CustomListItem> masterList;
    private ItemValidation iv = new ItemValidation();

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CardView cvContainer;
        public TextView tvTitle;
        public JustifiedTextView tvKeterangan;

        public MyViewHolder(View view) {
            super(view);
            cvContainer = (CardView) view.findViewById(R.id.cv_container);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvKeterangan = (JustifiedTextView) view.findViewById(R.id.tv_keterangan);
        }
    }

    public ProfileKMMAdapter(Context context, List masterList){
        this.context = context;
        this.masterList = masterList;
    }

    @Override
    public ProfileKMMAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_profile_kmm, parent, false);

        return new ProfileKMMAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final CustomListItem cli = masterList.get(position);
        holder.tvTitle.setText(cli.getItem1());
        holder.tvKeterangan.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.default_body_font_size));
        holder.tvKeterangan.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        holder.tvKeterangan.setAlignment(Paint.Align.LEFT);
        holder.tvKeterangan.setText(Html.fromHtml(cli.getItem2()).toString());

    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }

}