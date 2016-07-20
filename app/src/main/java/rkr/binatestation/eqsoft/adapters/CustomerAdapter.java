package rkr.binatestation.eqsoft.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rkr.binatestation.eqsoft.R;

/**
 * Created by RKR on 20/7/2016.
 * CustomerAdapter.
 */
public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ItemView> {
    @Override
    public ItemView onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemView(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_customer, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ItemView holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    class ItemView extends RecyclerView.ViewHolder {
        public ItemView(View itemView) {
            super(itemView);
        }
    }
}
