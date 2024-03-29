package com.pape.ricettacolomisterioso.ui.recipes;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.pape.ricettacolomisterioso.R;
import com.pape.ricettacolomisterioso.databinding.FragmentNewRecipeBinding;
import com.pape.ricettacolomisterioso.models.Recipe;
import com.pape.ricettacolomisterioso.viewmodels.NewRecipeViewModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewRecipeFragment extends Fragment {
    private static final String TAG = "NewRecipeFragment";
    private List<String> CATEGORIES;
    private FragmentNewRecipeBinding binding;

    private Recipe recipe;
    private MutableLiveData<Long> insertId;
    private NewRecipeViewModel model;

    private Date saveDate;
    private DatePickerDialog datePickerDialog;
    private Calendar c;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentNewRecipeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();


        setHasOptionsMenu(true);


        model = new ViewModelProvider(this).get(NewRecipeViewModel.class);
        final Observer<Long> observer = new Observer<Long>() {
            @Override
            public void onChanged(Long insertId) {
                if (insertId >= 0) {
                    Toast.makeText(getContext(), R.string.new_recipe_toast_succes, Toast.LENGTH_LONG).show();
                    Navigation.findNavController(getView()).popBackStack();
                }
            }
        };

        insertId = model.getInsertId();
        insertId.observe(this.getActivity(), observer);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);
        bottomNavigationView.setVisibility(View.GONE);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);
        bottomNavigationView.setVisibility(View.VISIBLE);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTextInputs();
        initDatePicker();
        initFab();
        //inits ingredients: item list card view
        initItemListCardView(binding.newRecipeIngredientsAddButton,
                view.findViewById(R.id.new_recipe_ingredientList_linearLayout),
                R.layout.new_recipe_item);

        //inits steps: item list card view
        initItemListCardView(binding.newRecipeStepsAddButton,
                view.findViewById(R.id.new_recipe_stepList_linearLayout),
                R.layout.new_recipe_item);
    }

    private void initTextInputs() {
        binding.textInputRecipeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.textInputLayoutRecipeName.setError(null);
            }
        });

        binding.textInputRecipeCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.textInputLayoutRecipeCategory.setError(null);
            }
        });
        CATEGORIES = Arrays.asList(getResources().getStringArray(R.array.recipeCategoriesString));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, CATEGORIES);
        binding.textInputRecipeCategory.setAdapter(adapter);


    }

    private void initFab() {
        binding.newRecipeSaveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecipe();
            }
        });
    }

    private void initItemListCardView(Button button, LinearLayout layoutToInflate, int itemToInflate) {
        button.setOnClickListener(v -> {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addView = layoutInflater.inflate(itemToInflate, null);
            layoutToInflate.addView(addView);
            layoutToInflate.getChildAt(layoutToInflate.getChildCount() - 1).requestFocus();
            TextInputLayout textInputLayout = (TextInputLayout) layoutToInflate.getChildAt(layoutToInflate.getChildCount() - 1);
            textInputLayout.setEndIconVisible(true);
            textInputLayout.setEndIconActivated(true);
            textInputLayout.setEndIconOnClickListener(v1 -> {
                layoutToInflate.removeView(textInputLayout);
            });
        });
    }

    private void initDatePicker() {
        saveDate = Calendar.getInstance().getTime();
        binding.textInputRecipeSaveDate.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(saveDate));

        binding.textInputRecipeSaveDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textInputLayoutRecipeSaveDate.setError(null);
                c = Calendar.getInstance();

                final int day = c.get(Calendar.DAY_OF_MONTH);
                final int month = c.get(Calendar.MONTH);
                final int year = c.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(getContext(), R.style.Theme_MyTheme_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDayOfMonth) {
                        Calendar cPicked = Calendar.getInstance();
                        cPicked.set(mYear, mMonth, mDayOfMonth);
                        saveDate = cPicked.getTime();
                        String dateString = DateFormat.getDateInstance(DateFormat.SHORT).format(saveDate);
                        binding.textInputRecipeSaveDate.setText(dateString);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Navigation.findNavController(getView()).popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addRecipe() {
        if (recipe == null) recipe = new Recipe();

        recipe.setTitle(binding.textInputRecipeName.getText().toString());
        String categoryString = binding.textInputRecipeCategory.getText().toString();

        recipe.setSaveDate(saveDate);
        recipe.setCategoryId(CATEGORIES.indexOf(categoryString));


        boolean isValid = true;

        //ProductName
        if (recipe.getTitle().equals("")) {
            binding.textInputLayoutRecipeName.setError(getResources().getString((R.string.error_empty_field)));
            binding.textInputLayoutRecipeName.requestFocus();
            isValid = false;
        }
        //Category
        if (categoryString.equals("")) {
            binding.textInputLayoutRecipeCategory.setError(getResources().getString((R.string.error_empty_field)));
            if (isValid) binding.textInputLayoutRecipeCategory.requestFocus();
            isValid = false;
        } else if (recipe.getCategoryId() < 0) {
            binding.textInputLayoutRecipeCategory.setError(getResources().getString((R.string.error_not_a_category)));
            if (isValid) binding.textInputLayoutRecipeCategory.requestFocus();
            isValid = false;
        }

        if (isValid) {

            List<String> ingredients = new ArrayList<String>();
            List<String> steps = new ArrayList<String>();

            LinearLayout ingBind = binding.newRecipeIngredientListLinearLayout;
            LinearLayout stepBind = binding.newRecipeStepListLinearLayout;

            for (int i = 0; i < ingBind.getChildCount(); i++) {
                TextInputLayout text = (TextInputLayout) ingBind.getChildAt(i);
                String prov = text.getEditText().getText().toString();
                if (prov.length() == 0) {
                } else {
                    ingredients.add(prov);
                    Log.d(TAG, "ingredient n" + (i + 1) + ">  " + prov);
                }

            }

            for (int i = 0; i < stepBind.getChildCount(); i++) {
                TextInputLayout text = (TextInputLayout) stepBind.getChildAt(i);
                String prov = text.getEditText().getText().toString();
                if (prov.length() == 0) {
                } else {
                    steps.add(prov);
                    Log.d(TAG, "step n" + (i + 1) + ">  " + prov);
                }
            }


            recipe.setIngredients(ingredients);
            recipe.setSteps(steps);

            insertId = model.addRecipe(recipe);
        }
    }


}
