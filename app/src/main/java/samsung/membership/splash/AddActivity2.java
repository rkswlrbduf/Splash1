package samsung.membership.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.otto.Subscribe;

/**
 * Created by KyuYeol on 2017-07-28.
 */

public class AddActivity2 extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private RelativeLayout relativeLayout;
    private RelativeLayout add2Layout;
    private Button button;
    private ImageView imageView;
    private int position;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Subscribe
    public void onActivityResult(ActivityResultEvent activityResultEvent){
        onActivityResult(activityResultEvent.getRequestCode(), activityResultEvent.getResultCode(), activityResultEvent.getData());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BusProvider2.getInstance().post(new ActivityResultEvent(requestCode, resultCode, data));
        BusProvider3.getInstance().post(new ActivityResultEvent(requestCode, resultCode, data));
    }

    private boolean[] check = {true,true,true};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add2);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();

        check[0] = pref.getBoolean("Voice", true);
        check[1] = pref.getBoolean("Gesture", true);
        check[2] = pref.getBoolean("Face", true);

        relativeLayout = (RelativeLayout)findViewById(R.id.guide_layout);
        add2Layout = (RelativeLayout)findViewById(R.id.add2_layout);
        imageView = (ImageView)findViewById(R.id.guide_image);
        button = (Button)findViewById(R.id.confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position) {
                    case 0:
                        add2Layout.setFocusable(true);
                        relativeLayout.setVisibility(View.INVISIBLE);
                        check[position] = false;
                        editor.putBoolean("Voice", false);
                        editor.apply();
                        break;
                    case 1:
                        add2Layout.setFocusable(true);
                        relativeLayout.setVisibility(View.INVISIBLE);
                        check[position] = false;
                        editor.putBoolean("Gesture", false);
                        editor.apply();
                        break;
                    case 2:
                        add2Layout.setFocusable(true);
                        relativeLayout.setVisibility(View.INVISIBLE);
                        check[position] = false;
                        editor.putBoolean("Face", false);
                        editor.apply();
                        break;
                }
            }
        });

        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Voice"));
        tabLayout.addTab(tabLayout.newTab().setText("Gesture"));
        tabLayout.addTab(tabLayout.newTab().setText("Face"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        if(check[0] == false) {
            relativeLayout.setVisibility(View.INVISIBLE);
            relativeLayout.setFocusable(false);
            add2Layout.setFocusable(true);
        }

        viewPager =(ViewPager)findViewById(R.id.pager);
        TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        //tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(check[tab.getPosition()]) {
                    add2Layout.setFocusable(false);
                    relativeLayout.setFocusable(true);
                    relativeLayout.setVisibility(View.VISIBLE);
                    //imageView.setImageDrawable(); // 이미지 받으면 설정
                }
                else {
                    add2Layout.setFocusable(true);
                    relativeLayout.setVisibility(View.INVISIBLE);
                    relativeLayout.setFocusable(false);
                }
                position = tab.getPosition();
                tabLayout.getTabAt(tab.getPosition()).select();
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
}
