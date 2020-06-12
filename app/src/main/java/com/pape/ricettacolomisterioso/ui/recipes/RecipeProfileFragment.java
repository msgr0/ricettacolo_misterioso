package com.pape.ricettacolomisterioso.ui.recipes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pape.ricettacolomisterioso.R;
import com.pape.ricettacolomisterioso.databinding.FragmentRecipeProfileBinding;
import com.pape.ricettacolomisterioso.models.Recipe;
import com.pape.ricettacolomisterioso.viewmodels.RecipeProfileViewModel;

import java.util.ArrayList;

public class RecipeProfileFragment extends Fragment {
    private RecipeProfileViewModel model;
    private FragmentRecipeProfileBinding binding;

    final static String TAG="RecipeProfileFragment";

    public RecipeProfileFragment() {

    }

    public static RecipeProfileFragment newInstance() {
        RecipeProfileFragment fragment = new RecipeProfileFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipeProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        Recipe recipe = RecipeProfileFragmentArgs.fromBundle(getArguments()).getRecipe();
        inflateRecipeInfo(recipe);
        Log.d(TAG, recipe.toString());

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);


        binding = FragmentRecipeProfileBinding.inflate(getLayoutInflater());
        model = new ViewModelProvider(this).get(RecipeProfileViewModel.class);
       /* Recipe recipe = RecipeProfileFragmentArgs.fromBundle(getArguments()).getRecipe();

        inflateRecipeInfo(recipe);*/
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);
        bottomNavigationView.setVisibility(View.GONE);


        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onDetach() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);

        super.onDetach();
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);
        bottomNavigationView.setVisibility(View.VISIBLE);

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

    }



    public void inflateRecipeInfo (Recipe recipe){
        binding.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        binding.imageView.setImageResource(recipe.getCategoryPreviewId(getContext()));

        binding.textViewRecipeProfileTitle.setText(recipe.getRecipe_name());
        binding.textViewRecipeProfileCategory.setText(recipe.getRecipe_category());
        ArrayList<String> ingredients = (ArrayList)recipe.getIngredients();
        ArrayList<String> steps = (ArrayList)recipe.getSteps();
        for (String ing:ingredients ) {
            //inflate a textview
            //assign that textview the text in ing
            TextView toadd = new TextView(getContext());
            toadd.setText(" ● " + ing);
            toadd.setTextAppearance(getContext(),R.style.TextAppearance_AppCompat_Medium);
            toadd.setPadding(0, 0 , 0 , 8);
            binding.linearLayoutRecipeProfileIngredients.addView(toadd);
        }

/*        int i = 1;
        for (String step:steps) {
            TextView toadd = new TextView(getContext());
            toadd.setText(" "+i+"● "+ step);
            toadd.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Medium);
            toadd.setPadding(0, 0 , 0 , 12);
            binding.linearLayoutRecipeProfileSteps.addView(toadd);
            i++;
        }
        */
        TextView toadd = new TextView(getContext());
        toadd.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Medium);
        toadd.setPadding(0, 0 , 0 , 12);
        toadd.setText(Html.fromHtml("<ol>"));
        int i=1;

        for (String step:steps) {
            toadd.append(Html.fromHtml(" <li>" + i + " " + step + "\n</li>"));
            i++;
        }
        toadd.append(Html.fromHtml("</ol>"));
        binding.linearLayoutRecipeProfileSteps.addView(toadd);




    }
}