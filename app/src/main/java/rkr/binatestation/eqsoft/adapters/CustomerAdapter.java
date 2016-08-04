package rkr.binatestation.eqsoft.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.models.CustomerModel;

/**
 * Created by RKR on 20/7/2016.
 * CustomerAdapter.
 */
public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ItemView> {
    List<CustomerModel> customerModelList;
    OnAdapterInteractionListener listener;

    public CustomerAdapter(List<CustomerModel> customerModelList, OnAdapterInteractionListener listener) {
        this.customerModelList = customerModelList;
        this.listener = listener;
    }

    @Override
    public ItemView onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemView(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_customer, parent, false)
        );
    }

    private CustomerModel getItem(Integer position) {
        return customerModelList.get(position);
    }

    @Override
    public void onBindViewHolder(final ItemView holder, int position) {
        holder.ledgerName.setText(getItem(position).getLedgerName());
        holder.phone.setText(getItem(position).getMobile());
        holder.balance.setText(getItem(position).getBalance());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemSelected(getItem(holder.getAdapterPosition()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return customerModelList.size();
    }

    public interface OnAdapterInteractionListener {
        void onItemSelected(CustomerModel customerModel);
    }

    class ItemView extends RecyclerView.ViewHolder {
        TextView ledgerName, phone, balance;
        View itemView;

        public ItemView(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ledgerName = (TextView) itemView.findViewById(R.id.AC_ledgerName);
            phone = (TextView) itemView.findViewById(R.id.AC_phone);
            balance = (TextView) itemView.findViewById(R.id.AC_balance);
        }
    }
}
