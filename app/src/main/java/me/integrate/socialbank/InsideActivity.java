package me.integrate.socialbank;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class InsideActivity extends AppCompatActivity implements FragmentChangeListener {
    private DrawerLayout mDrawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    // close drawer when item is tapped
                    mDrawerLayout.closeDrawers();

                    // Add code here to update the UI based on the item selected
                    // For example, swap UI fragments here
                    int itemId = menuItem.getItemId();
                    switch (itemId) {
                        case R.id.dashboard:
                            replaceFragment(new BoardFragment());
                            break;
                        case R.id.myProfile:
                            replaceFragment(new MyProfileFragment());
                            break;
                        case R.id.myEvents:
                            replaceFragment(new AllMyEventsFragment());
                            break;
                        case R.id.nearbyEvents:
                            replaceFragment(new NearbyEventsFragment());
                            break;
                        case R.id.logout:
                            logout();
                            break;
                        case R.id.myAccount:
                            replaceFragment(new MyAccountFragment());
                            break;
                        case R.id.newEvent:
                            replaceFragment(new CreateEventFragment());
                            break;
                        case R.id.searchUsers:
                            replaceFragment(new UserSearchFragment());
                            break;
                    }

                    return true;
                });

        View headerView = navigationView.getHeaderView(0);
        TextView userName = (TextView) headerView.findViewById(R.id.userName);
        userName.setText(SharedPreferencesManager.INSTANCE.read(this, "user_name"));
        TextView userEmail = (TextView) headerView.findViewById(R.id.userEmail);
        userEmail.setText(SharedPreferencesManager.INSTANCE.read(this, "user_email"));

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container_inside) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            BoardFragment firstFragment = new BoardFragment(); //it will be replaced by BoardFragm

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_inside, firstFragment).commit();

            String eventId = getIntent().getStringExtra("event");
            if (eventId != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("id", Integer.parseInt(eventId));
                Fragment eventFragment = EventFragment.newInstance(bundle);
                replaceFragment(eventFragment);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        SharedPreferencesManager.INSTANCE.remove(this, "token");
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_inside, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }

}