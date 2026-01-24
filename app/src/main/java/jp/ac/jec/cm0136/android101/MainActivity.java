package jp.ac.jec.cm0136.android101;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FrameLayout fragmentContainer;
    private BottomNavigationView bottomNavigation;
    private HomeFragment homeFragment;
    private ListFragment listFragment;
    private LabFragment labFragment; // Add LabFragment instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentContainer = findViewById(R.id.fragment_container);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Initialize Fragments
        homeFragment = new HomeFragment();
        listFragment = new ListFragment();
        labFragment = new LabFragment(); // Initialize LabFragment

        // Set bottom navigation listener
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(homeFragment);
                return true;
            } else if (itemId == R.id.nav_list) {
                loadFragment(listFragment);
                return true;
            } else if (itemId == R.id.nav_lab) {
                loadFragment(labFragment); // Load LabFragment
                return true;
            }
            return false;
        });

        // Set bottom navigation bar colors
        setupBottomNavigationColors();

        // Load HomeFragment by default
        if (savedInstanceState == null) {
            loadFragment(homeFragment);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        // No need to add to back stack for main navigation, to avoid weird back behavior.
        transaction.commit();
    }

    private void setupBottomNavigationColors() {
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked },
                new int[] { -android.R.attr.state_checked }
        };

        int[] colors = new int[] {
                getResources().getColor(R.color.purple),
                getResources().getColor(R.color.gray)
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);

        bottomNavigation.setItemIconTintList(colorStateList);
        bottomNavigation.setItemTextColor(colorStateList);
    }

    @Override
    public void onBackPressed() {
        if (bottomNavigation.getSelectedItemId() != R.id.nav_home) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        } else {
            super.onBackPressed();
        }
    }
    
    public void navigateToLab() {
        bottomNavigation.setSelectedItemId(R.id.nav_lab);
    }
}