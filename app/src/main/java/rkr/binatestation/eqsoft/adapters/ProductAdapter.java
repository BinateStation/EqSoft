package rkr.binatestation.eqsoft.adapters;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import rkr.binatestation.eqsoft.R;

/**
 * Created by RKR on 20/7/2016.
 * CustomerAdapter.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ItemView> {
    @Override
    public ItemView onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemView(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_product, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ItemView holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupEnterQuantity(view.getContext());
            }
        });
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    private void popupEnterQuantity(Context context) {
        AppCompatDialog appCompatDialog = new AppCompatDialog(context);
        appCompatDialog.setContentView(R.layout.popup_enter_product_quantity);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = appCompatDialog.getWindow();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
        window.getAttributes().windowAnimations = R.style.dialog_animation;

        appCompatDialog.show();
    }

    class ItemView extends RecyclerView.ViewHolder {
        View itemView;

        public ItemView(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
