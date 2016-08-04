package rkr.binatestation.eqsoft.fragments;


import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.adapters.CustomerAdapter;
import rkr.binatestation.eqsoft.models.CustomerModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerSearchFragment extends DialogFragment {


    public static String tag = CustomerSearchFragment.class.getSimpleName();
    SearchView customerSearch;
    RecyclerView customersRecyclerView;

    private OnCustomerSearchFragmentInteractionListener mListener;

    public CustomerSearchFragment() {
        // Required empty public constructor
    }

    public static CustomerSearchFragment newInstance(OnCustomerSearchFragmentInteractionListener mListener) {
        CustomerSearchFragment customerSearchFragment = new CustomerSearchFragment();
        customerSearchFragment.mListener = mListener;
        return customerSearchFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_search, container, false);
        customerSearch = (SearchView) view.findViewById(R.id.FCS_customerSearch);
        customersRecyclerView = (RecyclerView) view.findViewById(R.id.FCS_customerRecyclerView);

        customerSearch.setQueryHint(getString(R.string.type_to_search));
        customersRecyclerView.setLayoutManager(new LinearLayoutManager(customersRecyclerView.getContext()));
        customerSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getCustomerList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getCustomerList(newText);
                return true;
            }
        });
        getCustomerList("");

        return view;
    }

    private void getCustomerList(final String query) {
        new AsyncTask<Void, Void, List<CustomerModel>>() {
            @Override
            protected List<CustomerModel> doInBackground(Void... voids) {
                CustomerModel customerModelDB = new CustomerModel(getContext());
                customerModelDB.open();
                List<CustomerModel> customerModelList = customerModelDB.getAllRows(query, 0);
                customerModelDB.close();
                return customerModelList;
            }

            @Override
            protected void onPostExecute(List<CustomerModel> customerModelList) {
                super.onPostExecute(customerModelList);
                customersRecyclerView.setAdapter(new CustomerAdapter(customerModelList, new CustomerAdapter.OnAdapterInteractionListener() {
                    @Override
                    public void onItemSelected(CustomerModel customerModel) {
                        if (mListener != null) {
                            mListener.onItemSelected(customerModel);
                        }
                    }
                }));
            }
        }.execute();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        return dialog;
    }

    public interface OnCustomerSearchFragmentInteractionListener {
        void onItemSelected(CustomerModel customerModel);
    }
}
