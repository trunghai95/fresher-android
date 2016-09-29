package com.vng.fresherandroid.mykara;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.vng.fresherandroid.mykara.Fragment.FavoriteSongsFragment;
import com.vng.fresherandroid.mykara.Fragment.ForeignSongsFragment;
import com.vng.fresherandroid.mykara.Fragment.InformationFragment;
import com.vng.fresherandroid.mykara.Fragment.SingersFragment;
import com.vng.fresherandroid.mykara.Fragment.VietSongsFragment;
import com.vng.fresherandroid.mykara.Model.SongItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public static final String KEY_SONG_ITEM = "SongItem";
    public static final String KEY_SINGER = "Singer";
    private SendTextSearchToFragment mListener;
    private FragmentTabHost mTabHostWindow;
    private SearchView searchView;
    private MenuItem searchItem;

    public ArrayList<SongItem> mListVietSongs = new ArrayList<SongItem>();
    public ArrayList<SongItem> mListForeignSongs = new ArrayList<SongItem>();
    public String mVietSections = "";
    public String mForeignSections = "";
    public Integer[] mVietStartPositions = new Integer[27];
    public Integer[] mForeignStartPositions = new Integer[27];

    /**
     * Register listener
     */
    public void setListener(SendTextSearchToFragment listener) {
        mListener = listener;
        Log.e("listener", listener+"");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabHostWindow = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHostWindow.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        // Add 5 tabs
        mTabHostWindow.addTab(getTabSpec(getResources().getString(R.string.Tab1_name), getResources().getString(R.string.Tab1_title),
                R.drawable.tab1_drawable), VietSongsFragment.class, null);
        mTabHostWindow.addTab(getTabSpec(getResources().getString(R.string.Tab2_name), getResources().getString(R.string.Tab2_title),
                R.drawable.tab2_drawable), ForeignSongsFragment.class, null);
        mTabHostWindow.addTab(getTabSpec(getResources().getString(R.string.Tab3_name), getResources().getString(R.string.Tab3_title),
                R.drawable.tab3_drawable), FavoriteSongsFragment.class, null);
        mTabHostWindow.addTab(getTabSpec(getResources().getString(R.string.Tab4_name), getResources().getString(R.string.Tab4_title),
                R.drawable.tab4_drawable), SingersFragment.class, null);
        mTabHostWindow.addTab(getTabSpec(getResources().getString(R.string.Tab5_name), getResources().getString(R.string.Tab5_title),
                R.drawable.tab5_drawable), InformationFragment.class, null);

        TextView tv = (TextView) mTabHostWindow.getChildAt(0).findViewById(R.id.textView);
        tv.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.text_tab_selected));
        mTabHostWindow.setOnTabChangedListener(new FragmentTabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                searchItem.collapseActionView();
                for (int i = 0; i < mTabHostWindow.getTabWidget().getChildCount(); i++) {
                    TextView tv = (TextView) mTabHostWindow.getTabWidget().getChildAt(i).findViewById(R.id.textView);
                    tv.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.text_tab_unselected));
                }
                TextView tv = (TextView) mTabHostWindow.getCurrentTabView().findViewById(R.id.textView);
                tv.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.text_tab_selected));
            }
        });

        // Custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_home);

    }

    private FragmentTabHost.TabSpec getTabSpec(String tag, String title, int icon) {
        FragmentTabHost.TabSpec tabSpec = mTabHostWindow.newTabSpec(tag);
        tabSpec.setIndicator(getTabIndicator(mTabHostWindow.getContext(), title, icon));

        return tabSpec;
    }

    private View getTabIndicator(Context context, String title, int icon) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);

        ImageView iv = (ImageView) view.findViewById(R.id.imageView);
        iv.setImageResource(icon);

        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(title);

        return view;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(this, MainActivity.class)));
        searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // User pressed the search button
        searchView.clearFocus();
        return true;
    }

    /**
     * Send text Search to current fragment
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        mListener.sendData(newText);
        return false;
    }

    /**
     * Interface send text search from SearchView to current fragment
     */
    public interface SendTextSearchToFragment {
        void sendData(String textSearch);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                item.setChecked(true);
                FirebaseAuth .getInstance().signOut();
                Intent intent = new Intent(this, SplashScreen.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}