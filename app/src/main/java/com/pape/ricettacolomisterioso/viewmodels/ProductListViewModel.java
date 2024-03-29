package com.pape.ricettacolomisterioso.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pape.ricettacolomisterioso.models.Item;
import com.pape.ricettacolomisterioso.models.Product;
import com.pape.ricettacolomisterioso.repositories.ProductsRepository;
import com.pape.ricettacolomisterioso.repositories.ShoppingListRepository;

import java.util.List;

public class ProductListViewModel extends ViewModel {
    private static final String TAG = "ProductListViewModel";

    private MutableLiveData<List<Product>> products;
    private MutableLiveData<Integer> deleteId;
    private MutableLiveData<Long> insertId;

    public MutableLiveData<List<Product>> getProducts() {
        if (products == null) {
            products = new MutableLiveData<>();
        }
        return products;
    }

    public MutableLiveData<Integer> getDeleteId() {
        if (deleteId == null) {
            deleteId = new MutableLiveData<>();
        }
        return deleteId;
    }

    public MutableLiveData<Long> getInsertId() {
        if (insertId == null) {
            insertId = new MutableLiveData<>();
        }
        return insertId;
    }

    public MutableLiveData<List<Product>> getProductsByCategory(int category) {
        if (products == null) {
            products = new MutableLiveData<>();
        }

        Log.d(TAG, "getProductsByCategory: getProductsUpdate");
        ProductsRepository.getInstance().getProductsByCategory(products, category);
        return products;
    }

    public MutableLiveData<List<Product>> getAllProducts() {
        if (products == null) {
            products = new MutableLiveData<>();
        }

        Log.d(TAG, "getAllProducts: getProductsUpdate");
        ProductsRepository.getInstance().getProducts(products);
        return products;
    }

    public MutableLiveData<List<Product>> getProductsSearched(String product_name) {
        if (products == null) {
            products = new MutableLiveData<>();
        }

        Log.d(TAG, "getAllProducts: getProductsUpdate");
        ProductsRepository.getInstance().getProductSearched(products, product_name);
        return products;
    }

    public void delete(Product product) {
        ProductsRepository.getInstance().delete(product, getDeleteId());
    }

    public void addProduct(Product product) {
        ProductsRepository.getInstance().addItem(product, getInsertId());
    }

    public void addProductToShoppingList(Item item) {
        ShoppingListRepository.getInstance().addItem(item, getInsertId());
    }

    public void addProductToShoppingList(String itemName, int quantity) {
        addProductToShoppingList(new Item(itemName, quantity, false));
    }

    public void addProductToShoppingList(String itemName) {
        addProductToShoppingList(itemName, 1);
    }
}
