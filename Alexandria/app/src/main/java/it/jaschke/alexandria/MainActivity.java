package it.jaschke.alexandria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import it.jaschke.alexandria.api.Callback;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, Callback {

    private static final String FRG_BOOK_DETAIL_TAG = "frg_book_detail_tag";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment navigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence title;
    /**
     * ERROR CASE:
     * IS_TABLET the value of this variable is being modified on runtime and used in different class's
     * should be used a sharedPreference to do it.
     */
    public static boolean IS_TABLET = false;
    private BroadcastReceiver messageReciever;

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        IS_TABLET = isTablet();
        if (IS_TABLET) {
            setContentView(R.layout.activity_main_tablet);
        } else {
            setContentView(R.layout.activity_main);
        }
        /**
         * ERROR CASE:
         * There is no reason to manually handling the selection of the layout,
         * since the same the result can be achieved using android app Resources with the qualifier larger
         *
         * NEW                                         OLD
         * layout-large/activity_main.xml        <---  layout/activity_main_tablet.xml
         * layout-large-land/activity_main.xml   <---  layout-land/activity_main_tablet.xml
         *
         * Now analysing the content of the files,  layout-large/activity_main.xml  should be deleted,
         * since its content is the same of layout/activity_main.xml.
         * Without layout-large/activity_main.xml file the android resource manager would fallback to use layout/activity_main.xml
         *
         * But imagining, for some reason, it was necessary to have both resources with same content, and to avoid file duplication,
         * it should be added an alias to the file on res/values-large/refs.xml  like this:
         * <item type="layout" name="activity_main">@layout/activity_main_tablet</item>
         *
         */


        messageReciever = new MessageReciever();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReciever, filter);

        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        title = getTitle();

        // Set up the drawer.
        navigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
                if (fragment != null && fragment instanceof AddBook) {
                    ((AddBook) fragment).searchCode(result.getContents());
                }
            }
        } else {
            // otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment nextFragment;

        /**
         * ERROR CASE:
         * Bug: On tablet layout, if the right_container is not empty, ie presents the details of the selected book from the list of books,
         * Fix: since we added to the backStack now we remove it
         */
        if (IS_TABLET) {
            Fragment fragment = fragmentManager.findFragmentByTag(FRG_BOOK_DETAIL_TAG);
            if (fragment != null) {
                getSupportFragmentManager().popBackStack();
            }
        }


        /**
         * ERROR CASE:
         * Bug: if the same item is selected it is created a new fragment and added to the back stack.
         * Fix: check if the the new item position is the same as the current one, if it is do nothing ie end method.
         *
         */

        switch (position) {
            default:
            case 0:
                nextFragment = new ListOfBooks();
                break;
            case 1:
                nextFragment = new AddBook();
                break;
            case 2:
                nextFragment = new About();
                break;

        }

        /**
         * ERROR CASE:
         *   Since this is a horizontal navigation it should not add transactions to the back stack
         *   Fix: remove addToBackStack() call
         */
        fragmentManager.beginTransaction()
                .replace(R.id.container, nextFragment)
                .commit();


    }

    public void setTitle(int titleId) {
        title = getString(titleId);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReciever);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean) {
        Bundle args = new Bundle();
        args.putString(BookDetail.EAN_KEY, ean);

        BookDetail fragment = new BookDetail();
        fragment.setArguments(args);

        int id = R.id.container;
        if (findViewById(R.id.right_container) != null) {
            id = R.id.right_container;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        // if it is already there pop it out
        if (IS_TABLET && fragmentManager.findFragmentByTag(FRG_BOOK_DETAIL_TAG) != null) {
            getSupportFragmentManager().popBackStack();
        }
        // then add
        fragmentManager.beginTransaction()
                .replace(id, fragment, FRG_BOOK_DETAIL_TAG)
                .addToBackStack(FRG_BOOK_DETAIL_TAG)
                .commit();

    }

    private class MessageReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra(MESSAGE_KEY) != null) {
                Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void goBack(View view) {
        getSupportFragmentManager().popBackStack();
    }

    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    @Override
    public void onBackPressed() {
        /**
         * ERROR CASE:
         * The following code is not need after fixing the horizontal navigation  problem onNavigationDrawerItemSelected()
         */
//        if (getSupportFragmentManager().getBackStackEntryCount() < 2) {
//            finish();
//        }
//

        /**
         * ERROR CASE:
         * Bug: if the navigation drawer was visible the back button ended the activity instead of hiding the drawer, for a proper navigation.
         */
        if (navigationDrawerFragment.isDrawerOpen()) {
            navigationDrawerFragment.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }


}