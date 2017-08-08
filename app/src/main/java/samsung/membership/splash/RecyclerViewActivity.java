package samsung.membership.splash;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.apache.commons.net.ftp.FTPClient;

import java.util.ArrayList;

/**
 * Created by yumin on 2017-08-05.
 */

public class RecyclerViewActivity extends AppCompatActivity {

    public static RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<MyData> dataSet;

    private String[] drawerTitles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    public static boolean OpenedChannelCounter = false;
    public static Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        drawerTitles = getResources().getStringArray(R.array.titles);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Channel");

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                //getActionBar().setTitle(drawerTitles);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        drawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerList = (ListView) findViewById(R.id.drawer_setting);
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,drawerTitles));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.back);

        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(drawable);

        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        dataSet = new ArrayList<>();
        //adapter = new RecyclerViewAdapter(dataSet, recyclerView, this);

        recyclerView.setAdapter(adapter);

        dataSet.add(new MyData("#InsideOut", R.mipmap.ic_launcher, true));
        dataSet.add(new MyData("#Mini", R.mipmap.ic_launcher, false));
        dataSet.add(new MyData("#ToyStroy", R.mipmap.ic_launcher, false));
        dataSet.add(new MyData("#ToyStroy2", R.mipmap.ic_launcher, false));
        dataSet.add(new MyData("#ToyStroy3", R.mipmap.ic_launcher, false));
        OpenedChannelCounter = true;

    }

    private void selectItem(int position) {
    }

    public void updateRecyclerView() {
        dataSet.remove(0);
        recyclerView.removeViewAt(0);
        adapter.notifyDataSetChanged();
        dataSet.add(0, new MyData("#InsideOut", R.mipmap.ic_launcher,false));
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_add);
        if(OpenedChannelCounter) {
            menuItem.setIcon(R.drawable.story);
        }
        this.menu = menu;
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_add);
        if(OpenedChannelCounter) {
            menuItem.setIcon(R.drawable.story);
        } else {
            menuItem.setIcon(R.mipmap.ic_launcher);
        }
        this.menu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.action_add) {
            //this.startActivity(new Intent(MainActivity.this, AddActivity.class));
            return true;
        }

        if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
