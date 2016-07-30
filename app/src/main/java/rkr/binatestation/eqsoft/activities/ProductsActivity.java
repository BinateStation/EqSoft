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
import rkr.binatestation.eqsoft.adapters.ProductAdapter;

public class ProductsActivity extends AppCompatActivity {
    SearchView productSearch;
    RecyclerView productsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        productSearch = (SearchView) findViewById(R.id.AP_productSearch);
        productsRecyclerView = (RecyclerView) findViewById(R.id.Ap_productsRecyclerView);

        productSearch.setQueryHint(getString(R.string.type_to_search));
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(productsRecyclerView.getContext()));
        productsRecyclerView.setAdapter(new ProductAdapter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);
        return true;
    }

}
