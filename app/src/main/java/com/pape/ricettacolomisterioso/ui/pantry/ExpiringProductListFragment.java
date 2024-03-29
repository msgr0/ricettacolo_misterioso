package com.pape.ricettacolomisterioso.ui.pantry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pape.ricettacolomisterioso.adapters.ExpiringProductListAdapter;
import com.pape.ricettacolomisterioso.databinding.FragmentExpiringProductListBinding;
import com.pape.ricettacolomisterioso.models.Product;
import com.pape.ricettacolomisterioso.viewmodels.ExpiringProductListViewModel;

import java.util.List;

public class ExpiringProductListFragment extends Fragment {

    private ExpiringProductListViewModel model;
    private ExpiringProductListAdapter mAdapter;

    private FragmentExpiringProductListBinding binding;

    public ExpiringProductListFragment() {
    }

    public static ExpiringProductListFragment newInstance() {
        return new ExpiringProductListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExpiringProductListBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        model = new ViewModelProvider(this).get(ExpiringProductListViewModel.class);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.expiringProductListRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new ExpiringProductListAdapter(getActivity(), model.getExpiringProductsOrdered().getValue(), new ExpiringProductListAdapter.OnItemInteractions() {
            @Override
            public void onItemClick(Product product) {
                Bundle recipeBundle = new Bundle();
                recipeBundle.putParcelable("product", product);

                ExpiringProductListFragmentDirections.ActionExpiringProductListToProductProfileFragment action =
                        ExpiringProductListFragmentDirections.actionExpiringProductListToProductProfileFragment(product);

                Navigation.findNavController(view).navigate(action);
            }
        });
        binding.expiringProductListRecyclerView.setAdapter(mAdapter);

        model.getExpiringProductsOrdered().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                mAdapter.setData(model.getExpiringProductsOrdered().getValue());
                checkEmptyList();
            }
        });

        model.getAllProductsOrderByExpirationDate();
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Navigation.findNavController(getView()).popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkEmptyList() {
        if (model.getExpiringProductsOrdered().getValue() == null || model.getExpiringProductsOrdered().getValue().size() == 0) {
            binding.expiringProductEmptyImageView.setVisibility(View.VISIBLE);
            binding.expiringProductListEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            binding.expiringProductEmptyImageView.setVisibility(View.INVISIBLE);
            binding.expiringProductListEmptyTextView.setVisibility(View.INVISIBLE);
        }
    }
}
