package com.pape.ricettacolomisterioso.repositories;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;

import com.pape.ricettacolomisterioso.models.AppDatabase;
import com.pape.ricettacolomisterioso.models.Product;
import com.pape.ricettacolomisterioso.models.ProductApiResponse;
import com.pape.ricettacolomisterioso.services.EbayService;
import com.pape.ricettacolomisterioso.services.FoodService;
import com.pape.ricettacolomisterioso.ui.MainActivity;
import com.pape.ricettacolomisterioso.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductsRepository {

    private static final String TAG = "ProductsRepository";

    private static ProductsRepository instance;
    private AppDatabase appDatabase;
    private FoodService foodService;
    private EbayService ebayService;

    private ProductsRepository() {
        appDatabase = MainActivity.db;

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.FOOD_API_BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).build();

        Retrofit retrofit2 = new Retrofit.Builder().baseUrl(Constants.EBAY_API_BASE_URL).build();

        foodService = retrofit.create(FoodService.class);
        ebayService = retrofit2.create(EbayService.class);
    }

    public static synchronized ProductsRepository getInstance() {
        if (instance == null) {
            instance = new ProductsRepository();
        }
        return instance;
    }

    public void getProducts(MutableLiveData<List<Product>> products) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "run: getProducts()");
                    products.postValue(appDatabase.productDao().getAll());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    public void getProductInfo(MutableLiveData<Product> product, String code) {
        Call<ProductApiResponse> call = foodService.getProductInfo(code, Constants.FOOD_API_USER_AGENT);
        // It shows the use of method enqueue to do the HTTP request asynchronously.
        call.enqueue(new Callback<ProductApiResponse>() {
            @Override
            public void onResponse(@NotNull Call<ProductApiResponse> call, @NotNull Response<ProductApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if(response.body().getStatus()==1){ //API returns successful even if product was not found
                        Log.d(TAG, "onResponse: " + response.body().toString());
                        product.postValue(response.body().getProduct());
                    }
                    else {
                        Log.d(TAG, "onResponse: Success. Product not found");
                        product.postValue(null);
                    }
                } else if (response.errorBody() != null) {
                    product.postValue(null);
                    try {
                        new Throwable(response.errorBody().string()).printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductApiResponse> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
            }
        });
    }

    public void getProductInfo2(MutableLiveData<Product> product, String code) {
        String requestBodyText = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            +"<FindProductsRequest xmlns=\"urn:ebay:apis:eBLBaseComponents\">"
            +"<ProductID type=\"EAN\">"+code+"</ProductID>"
            +"<MaxEntries>1</MaxEntries>"
	        +"<AvailableItemsOnly>true</AvailableItemsOnly>"
            +"</FindProductsRequest>";
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/xml"), requestBodyText);
        Call<String> call = ebayService.getProductInfo(requestBody, Constants.EBAY_API_APP_ID);
        // It shows the use of method enqueue to do the HTTP request asynchronously.
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "onResponse: " + response.body().toString());
                } else if (response.errorBody() != null) {
                    try {
                        new Throwable(response.errorBody().string()).printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
            }
        });
    }
}
