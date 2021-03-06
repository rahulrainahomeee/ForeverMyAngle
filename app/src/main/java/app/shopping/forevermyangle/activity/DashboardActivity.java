package app.shopping.forevermyangle.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookSdk;

import app.shopping.forevermyangle.R;
import app.shopping.forevermyangle.fragment.base.BaseFragment;
import app.shopping.forevermyangle.fragment.fragments.CartDashboardFragment;
import app.shopping.forevermyangle.fragment.fragments.CategoryDashboardFragment;
import app.shopping.forevermyangle.fragment.fragments.ConnectionFailFragment;
import app.shopping.forevermyangle.fragment.fragments.HomeDashboardFragment;
import app.shopping.forevermyangle.fragment.fragments.UserProfileFragment;
import app.shopping.forevermyangle.receiver.callback.ConnectionReceiverCallback;
import app.shopping.forevermyangle.utils.Constants;
import app.shopping.forevermyangle.utils.GlobalData;
import app.shopping.forevermyangle.view.FMAActivity;

/**
 * @class DashboardActivity
 * @desc {@link AppCompatActivity} to handle dashboard Activity.
 */
public class DashboardActivity extends FMAActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ConnectionReceiverCallback {

    /**
     * Private data member objects.
     */
    private BaseFragment mCurrentFragment = null;  // Fragment Custom Class.
    private int mFlagFragment = 1;      // Fragment Number current.
    private FragmentManager mFragmentManager = null;
    private FragmentTransaction mFragmentTransaction = null;

    /**
     * {@link AppCompatActivity} override method(s).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);

        // Resolution dependency processing.
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Constants.RES_WIDTH = size.x;
        Constants.RES_HEIGHT = size.y;

        // Set Custom Title.
        View view = getLayoutInflater().inflate(R.layout.app_title, null);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(view);
        setContentView(R.layout.activity_dashboard);

        // Add bottom navigation.
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        mFragmentManager = getFragmentManager();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCurrentFragment == null) {
            loadFragment();
        }
        GlobalData.connectionCallback = this;

        // Reload Profile fragment everytime in onResume callback.
        if (mFlagFragment == 4) {
            loadFragment();
        }
    }

    @Override
    protected void onPause() {
        GlobalData.connectionCallback = null;
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.option_menu_search:

                startActivity(new Intent(this, SearchProductActivity.class));
                break;
            case R.id.option_menu_wishlist:

                startActivity(new Intent(this, WishlistActivity.class));
                break;
        }
        return true;
    }

    /**
     * {@link BottomNavigationView.OnNavigationItemSelectedListener} implemented method(s).
     */

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:              // Navigation home selected.

                mFlagFragment = 1;
                break;

            case R.id.navigation_category:         // Navigation category selected.

                mFlagFragment = 2;
                break;

            case R.id.navigation_cart:     // Navigation item cart selected.

                mFlagFragment = 3;
                break;

            case R.id.navigation_profile:    // Navigation item profile selected.

                mFlagFragment = 4;
                break;
        }
        loadFragment();         // Load the corresponding Fragment on dashboard.
        return true;
    }

    /**
     * @return {@link BaseFragment}
     * @method getCurrentFragment
     * @desc Method to get instance of current fragment appeared on UI.
     */
    private BaseFragment getCurrentFragment() {

        switch (mFlagFragment) {
            case 1:     // Fragment 1 to load.
                return new HomeDashboardFragment();

            case 2:     // Fragment 2 to load.
                return new CategoryDashboardFragment();

            case 3:     // Fragment 3 to load.
                return new CartDashboardFragment();

            case 4:     // Fragment 4 to load.
                return new UserProfileFragment();

            default:    // Unexpected value for fragment load: Warning.
                Toast.makeText(this, "Warning: Unexpected value for mFlagFragment=" + mFlagFragment, Toast.LENGTH_SHORT).show();
                return null;
        }
    }

    /**
     * @method loadFragment
     * @desc Method to load the current Fragment in dashboard UI.
     */
    private void loadFragment() {

        mFragmentTransaction = mFragmentManager.beginTransaction();         // Begin with fragment transaction.
        if (!mFragmentTransaction.isEmpty()) {                              // Remove older fragment if any.
            mFragmentTransaction.remove(mCurrentFragment);
        }
        mCurrentFragment = getCurrentFragment();                            // Get a new Fragment for dashboard.
        mFragmentTransaction.replace(R.id.fragment, mCurrentFragment);      // Replace with new fragment in the container.
        mFragmentTransaction.commit();                                      // Commit fragment transition finally.
    }

    /**
     * {@link FMAActivity} Override method.
     */
    @Override
    public void signalMessage(int signal) {

        switch (signal) {
            case 1:         // Load Connection-fail Page fragment.

                mFragmentTransaction = mFragmentManager.beginTransaction();         // Begin with fragment transaction.
                if (!mFragmentTransaction.isEmpty()) {                              // Remove older fragment if any.
                    mFragmentTransaction.remove(mCurrentFragment);
                }
                mCurrentFragment = new ConnectionFailFragment();                    // Get a new Fragment for dashboard.
                mFragmentTransaction.replace(R.id.fragment, mCurrentFragment);      // Replace with new fragment in the container.
                mFragmentTransaction.commit();
                break;

            case 2:         // Load Current Fragment.

                loadFragment();
            default:

                break;
        }
    }

    /**
     * {@link ConnectionReceiverCallback} interface callback method.
     */
    @Override
    public void networkConnectionStateChange() {
        signalMessage(2);
    }
}
