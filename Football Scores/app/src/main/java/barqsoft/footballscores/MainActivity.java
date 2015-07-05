package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

    private final String save_tag = "Save Test";
    private static  final String BUNDLE_PAGER_CURRENT ="bundle_pager_current";
    private static final String BUNDLE_SELECTED_MATCH ="bundle_selected_match";
    private static final String BUNDLE_MAIN_FRAG ="bundle_main_frag";

    public static int selected_match_id;
    public static int current_fragment = 2;
    private PagerFragment my_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            my_main = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, my_main)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent start_about = new Intent(this, AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.v(save_tag, "will save");
        Log.v(save_tag, "fragment: " + String.valueOf(my_main.mPagerHandler.getCurrentItem()));
        Log.v(save_tag, "selected id: " + selected_match_id);

        outState.putInt(BUNDLE_PAGER_CURRENT, my_main.mPagerHandler.getCurrentItem());
        outState.putInt(BUNDLE_SELECTED_MATCH, selected_match_id);

        getSupportFragmentManager().putFragment(outState, BUNDLE_MAIN_FRAG, my_main);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.v(save_tag, "will retrive");
        Log.v(save_tag, "fragment: " + String.valueOf(savedInstanceState.getInt(BUNDLE_PAGER_CURRENT)));
        Log.v(save_tag, "selected id: " + savedInstanceState.getInt(BUNDLE_SELECTED_MATCH));

        current_fragment = savedInstanceState.getInt(BUNDLE_PAGER_CURRENT);
        selected_match_id = savedInstanceState.getInt(BUNDLE_SELECTED_MATCH);
        my_main = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState, BUNDLE_MAIN_FRAG);

        super.onRestoreInstanceState(savedInstanceState);
    }
}
