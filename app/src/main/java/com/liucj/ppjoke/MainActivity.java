package com.liucj.ppjoke;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.liucj.libnetwork.ApiResponse;
import com.liucj.libnetwork.GetRequest;
import com.liucj.libnetwork.JsonCallBack;
import com.liucj.ppjoke.ui.view.FixFragmentNavigator;
import com.liucj.ppjoke.utils.NavGraphBuilder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = NavHostFragment.findNavController(fragment);
        NavGraphBuilder.build(navController, this, fragment.getId());
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                navController.navigate(item.getItemId());
                return !TextUtils.isEmpty(item.getTitle());
            }
        });
        GetRequest<JSONArray> request = new GetRequest<JSONArray>("www.mooc.com");
        request.execute();
        request.execute(new JsonCallBack<JSONArray>() {
            @Override
            public void onSuccess(ApiResponse<JSONArray> response) {
                super.onSuccess(response);
            }
        });
    }

}