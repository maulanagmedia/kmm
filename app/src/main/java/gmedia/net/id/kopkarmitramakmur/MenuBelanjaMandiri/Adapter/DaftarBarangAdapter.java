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
import gmedia.net.id.kopkarmitramakmur.MenuBelanjaMandiri.DetailBelanjaMandiri;
import gmedia.net.id.kopkarmitramakmur.Util.ImageUtils;
import gmedia.net.id.kopkarmitramakmur.Util.ItemValidation;
import gmedia.net.id.kopkarmitramakmur.Util.Utils;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class DaftarBarangAdapter extends RecyclerView.Adapter<DaftarBarangAdapter.MyViewHolder> {

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
    private LinearLayout llAdd;
    private LinearLayout llCancel;
    private TextView tvSubmitCart;
    private ImageUtils iu = new ImageUtils();

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivImage;
        public LinearLayout llContainer;
        public TextView tvTitle, tvHarga, tvInfo;

        public MyViewHolder(View view) {
            super(view);
            llContainer = (LinearLayout)view.findViewById(R.id.ll_container);
            ivImage = (ImageView) view.findViewById(R.id.iv_thumbnail);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvHarga = (TextView) view.findViewById(R.id.tv_harga);
            tvInfo = (TextView) view.findViewById(R.id.tv_info);
        }
    }

    public DaftarBarangAdapter(Context context, List<CustomListItem> masterList){
        this.context = context;
        this.masterList = masterList;
    }

    public void AddMoreData(List<CustomListItem> listItems){
        masterList.addAll(listItems);
        notifyDataSetChanged();
    }

    private void DialogForm(final CustomListItem item) {
        dialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.layout_item_cart, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_kopkar_logo);
        dialog.setTitle("Tambah ke Keranjang");

        tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        tvHarga = (TextView) dialogView.findViewById(R.id.tv_harga);
        ivGambar = (ImageView) dialogView.findViewById(R.id.iv_thumbnail);
        edtJumlah = (EditText) dialogView.findViewById(R.id.edt_jumlah);
        edtJumlah.setSelection(edtJumlah.getText().length());
        edtJumlah.requestFocus();
        llAdd = (LinearLayout) dialogView.findViewById(R.id.ll_add);
        tvSubmitCart = (TextView) dialogView.findViewById(R.id.tv_cart_submit);
        tvSubmitCart.setText(context.getResources().getString(R.string.add_cart_string));
        llCancel = (LinearLayout) dialogView.findViewById(R.id.ll_cancel);

        tvTitle.setText(item.getItem2());
        tvHarga.setText(iv.ChangeToRupiahFormat(item.getItem3()));
        iu.LoadImageNormal(context, item.getItem4(), ivGambar);

        llCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogViews.dismiss();
            }
        });

        llAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(iv.parseNullInteger(edtJumlah.getText().toString()) <= 0){
                    edtJumlah.setError("Jumlah tidak boleh kurang dari 1");
                    return;
                }

                boolean addedFlag = false;
                int x = 0;
                for (CustomListItem itemRow: DetailBelanjaMandiri.cartList){
                    if(itemRow.getItem1().equals(item.getItem1())){
                        addedFlag = true;
                        int lastCount = iv.parseNullInteger(itemRow.getItem6());
                        int newCount = iv.parseNullInteger(edtJumlah.getText().toString());
                        String totalJumlah = String.valueOf(lastCount + newCount);

                        DetailBelanjaMandiri.cartList.set(x, new CustomListItem(item.getItem1(), item.getItem2(), item.getItem3(), item.getItem4(),totalJumlah));
                    }
                    x++;
                }

                if(!addedFlag) DetailBelanjaMandiri.cartList.add(new CustomListItem(item.getItem1(), item.getItem2(), item.getItem3(), item.getItem4(), edtJumlah.getText().toString()));

                Utils.setBadgeCount(context, DetailBelanjaMandiri.iconCart, DetailBelanjaMandiri.cartList.size());
                dialogViews.dismiss();
            }
        });

        /*dialog.setPositiveButton("Filter", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                filterList = new ArrayList<CustomItem>();
                filterList = filterAdapter.getAllData();
                kategori = "";
                dialog.dismiss();
                for (CustomItem item: filterList){
                    if(item.isItem7()){
                        kategori = kategori + item.getItem1() + ",";
                    }
                }
                kategori = removeLastChar(kategori);
                getMerchantByFilter();
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });*/

        dialogViews = dialog.create();
        dialogViews.show();

    }

    @Override
    public DaftarBarangAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cv_list_barang, parent, false);

        return new DaftarBarangAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final CustomListItem cli = masterList.get(position);

        holder.tvTitle.setText(cli.getItem2());
        holder.tvHarga.setText(iv.ChangeToRupiahFormat(cli.getItem3()));
        holder.tvInfo.setText(cli.getItem6());
        iu.LoadImageNormal(context, cli.getItem4(), holder.ivImage);

        holder.llContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogForm(cli);
            }
        });

    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }

}