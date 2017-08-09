package samsung.membership.splash;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.squareup.picasso.Picasso;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private GoogleApiClient googleApiClient;
    private String[] drawerTitles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private LinearLayout storyLinearLayout;


    public static RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<MyData> dataSet;
    public static boolean OpenedChannelCounter = false;
    public static Menu menu;

    private String server = "172.24.1.1";
    private int port = 21;
    private String id = "kyuyeol";
    private String password = "password";
    private ImageView image;
    private GridView gv;

    FTPUtility ftpUtility;
    FTPClient ftpClient;
    FTPFile ftpFile[] = null;

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;
    int posX1 = 0, posX2 = 0, posY1 = 0, posY2 = 0;
    float oldDist = 1f;
    float newDist = 1f;

    private int count;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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




        OpenedChannelCounter = true;

        try {
            ftpUtility = new FTPUtility("172.24.1.1", 21, "kyuyeol", "password");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TAG", "ERROR");
        }

        new DownloadFilesTask(MainActivity.this).execute();

    }

    private void selectItem(int position) {
        if(position == 0) {
            LoginManager.getInstance().logOut();
            onClickLogout();
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                }
            });
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void onClickLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {

            }
        });
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        googleApiClient.connect();
        super.onStart();
    }

    public void updateRecyclerView() {
        dataSet.remove(0);
        recyclerView.removeViewAt(0);
        adapter.notifyDataSetChanged();
        dataSet.add(0, (new MyData(ftpFile[0].getName(), 0, false)));
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
        if(id == R.id.action_add && OpenedChannelCounter == false) {
            this.startActivity(new Intent(MainActivity.this, AddActivity.class));
            return true;
        }

        if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                posX1 = (int) event.getX();
                posY1 = (int) event.getY();
                mode = DRAG;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    posX2 = (int) event.getX();
                    posY2 = (int) event.getY();

                    if (Math.abs(posX2 - posX1) > 20 || Math.abs(posY2 - posY1) > 20) {
                        posX1 = posX2;
                        posY1 = posY2;
                    }
                } else if (mode == ZOOM) {
                    newDist = spacing(event);
                    if (newDist - oldDist > 100) {
                        oldDist = newDist;
                        //MainActivity.this.gv.setNumColumns(MainActivity.this.gv.getNumColumns()-1);
                    }
                    else if (oldDist - newDist > 100) {
                        oldDist = newDist;
                        //MainActivity.this.gv.setNumColumns(MainActivity.this.gv.getNumColumns()+1);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                newDist = spacing(event);
                oldDist = spacing(event);
                break;
            case MotionEvent.ACTION_CANCEL:
            default:
                break;
        }
        return false;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private class DownloadFilesTask extends AsyncTask<Void, String, Void> {

        private ProgressDialog progressDialog;
        private Context context;

        protected DownloadFilesTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Start");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ftpUtility.connect();
            ftpUtility.login();
            ftpUtility.cd("/home/pi/raspicam_example_with_opencv/capture");
            publishProgress("max", Integer.toString(ftpUtility.list().length));
            OutputStream output = null;

            ftpFile =  ftpUtility.list();
            dataSet = new ArrayList<>();

            adapter = new RecyclerViewAdapter(dataSet, count, MainActivity.this);
            for(int i = 0;i<ftpUtility.list().length;i++) {
                if(i==0) dataSet.add(new MyData(ftpFile[i].getName(), i, true));
                else dataSet.add(new MyData(ftpFile[i].getName(), i, false));
            /*dataSet.add(new MyData("#Mini", R.mipmap.ic_launcher, false));
            dataSet.add(new MyData("#ToyStroy", R.mipmap.ic_launcher, false));
            dataSet.add(new MyData("#ToyStroy2", R.mipmap.ic_launcher, false));
            dataSet.add(new MyData("#ToyStroy3", R.mipmap.ic_launcher, false));*/
            }

            //adapter = new MyAdapter(getApplicationContext(),R.layout.row,ftpUtility.list().length);
            File f;
            /*try {
                FTPFile ftpfile[] = ftpUtility.list();
                Log.d("TAG",ftpfile.length + "");
                for(int i=0;i<ftpfile.length;i++) {
                    Log.d("NAME" , ftpfile[i].getName());
                    f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(),ftpfile[i].getName());
                    output = new FileOutputStream(f);
                    Log.d("TAG_NAME", ftpfile[i].getName());
                    ftpUtility.pwd();
                    ftpUtility.ftpClient.retrieveFile(ftpfile[i].getName(),output);
                    output.close();
                    publishProgress("progress", Integer.toString(i), "작업 번호 " + Integer.toString(i) +  "번 수행중");
                }
                //File local = new File(source);
                //output = new FileOutputStream(local);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            Log.d("TAG", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
            ftpUtility.logout();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if(values[0].equals("progress")) {
                progressDialog.setProgress(Integer.parseInt(values[1]));
                progressDialog.setMessage(values[2]);
            } else if(values[0].equals("max")) {
                progressDialog.setMax(Integer.parseInt(values[1]));
            }

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //image.setImageURI(Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/2017-07-30 22: 38: 45.jpg"));
            //gv.setAdapter(adapter);
            recyclerView.setAdapter(adapter);
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }

}
