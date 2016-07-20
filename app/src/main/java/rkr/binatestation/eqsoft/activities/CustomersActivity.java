package rkr.binatestation.eqsoft.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.adapters.CustomerAdapter;

public class CustomersActivity extends AppCompatActivity {

    SearchView customerSearch;
    RecyclerView customersRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        customerSearch = (SearchView) findViewById(R.id.AC_customerSearch);
        customersRecyclerView = (RecyclerView) findViewById(R.id.AC_customerRecyclerView);

        customerSearch.setQueryHint(getString(R.string.type_to_search));
        customersRecyclerView.setLayoutManager(new LinearLayoutManager(customersRecyclerView.getContext()));
        customersRecyclerView.setAdapter(new CustomerAdapter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);
        return true;
    }

}
