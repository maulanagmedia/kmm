package gmedia.net.id.kopkarmitramakmur.MenuInfoPotongan.Adapter;

/**
 * Created by Shin on 3/21/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.kopkarmitramakmur.R;
import gmedia.net.id.kopkarmitramakmur.GeneralModel.CustomListItem;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;

public class PotonganAdapter extends RecyclerView.Adapter<PotonganAdapter.MyViewHolder> {

    private Context context;
    private List<CustomListItem> titleList;
    private ItemValidation iv = new ItemValidation();

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle, tvValue;

        public MyViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvValue = (TextView) view.findViewById(R.id.tv_value);
        }
    }

    public PotonganAdapter(Context context, List titleList){
        this.context = context;
        this.titleList = titleList;
    }

    @Override
    public PotonganAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_list_potongan, parent, false);

        return new PotonganAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final CustomListItem cli = titleList.get(position);
        holder.tvTitle.setText((position+1) +". "+ cli.getItem6());
        holder.tvValue.setText(iv.ChangeToRupiahFormat(cli.getItem7()));

    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

}