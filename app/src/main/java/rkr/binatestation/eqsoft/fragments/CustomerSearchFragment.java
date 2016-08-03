package rkr.binatestation.eqsoft.fragments;


import android.app.Dialog;
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

import java.util.ArrayList;
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
    List<CustomerModel> customerModelList = new ArrayList<>();

    public CustomerSearchFragment() {
        // Required empty public constructor
    }

    public static CustomerSearchFragment newInstance() {
        return new CustomerSearchFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_search, container, false);
        customerSearch = (SearchView) view.findViewById(R.id.FCS_customerSearch);
        customersRecyclerView = (RecyclerView) view.findViewById(R.id.FCS_customerRecyclerView);

        customerSearch.setQueryHint(getString(R.string.type_to_search));
        customersRecyclerView.setLayoutManager(new LinearLayoutManager(customersRecyclerView.getContext()));
        customersRecyclerView.setAdapter(new CustomerAdapter(customerModelList));
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        return dialog;
    }
}
