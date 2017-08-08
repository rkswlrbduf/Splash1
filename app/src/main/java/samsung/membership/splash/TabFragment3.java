package samsung.membership.splash;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by yumin on 2017-07-29.
 */

public class TabFragment3 extends Fragment {

    private GridView gridView;
    private int count = 0;
    private int[] image = new int[]{};
    private GestureAdapter3 adapter3;

    final List<Bitmap> imageLists = new ArrayList<Bitmap>();

    int img[] = {};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("TAG","RECALL");

        View view = inflater.inflate(R.layout.tab_fragment_2, container, false);

        gridView = (GridView) view.findViewById(R.id.gridView2);

        imageLists.add(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.addbutton));

        adapter3 = new GestureAdapter3(getApplicationContext(), R.layout.gesture_item, imageLists, getActivity());

        gridView.setAdapter(adapter3);

        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BusProvider3.getInstance().register(this);
    }

    public void onDestroyView() {
        BusProvider3.getInstance().unregister(this);
        super.onDestroyView();

    }

    @Subscribe
    public void onActivityResult(ActivityResultEvent activityResultEvent){
        onActivityResult(activityResultEvent.getRequestCode(), activityResultEvent.getResultCode(), activityResultEvent.getData());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) if(requestCode == 3000) SendPicture(data);
    }

    private void SendPicture(Intent data) {

        Uri imgUri = data.getData();
        String imagePath = getRealPathFromURI(imgUri); // path 경로
        Log.d("TAG", imagePath);
        /*Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        bitmap = ThumbnailUtils.extractThumbnail(bitmap,displayMetrics.widthPixels/2,displayMetrics.widthPixels/2);*/
        int degree = ImageUtil.GetExifOrientation(imagePath);
        Bitmap resizeBitmap = ImageUtil.loadBackgroundBitmap(getApplicationContext(), imagePath);
        Bitmap rotateBitmap = ImageUtil.GetRotatedBitmap(resizeBitmap, degree);
        Bitmap roundBitmap = ImageUtil.getRoundedCornerBitmap(rotateBitmap);
        imageLists.add(0,roundBitmap);
        //bitmap.recycle();
        adapter3.notifyDataSetChanged();

    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }

}

class GestureAdapter3 extends BaseAdapter {

    private final int GALLERY_CODE = 3000;

    Context context;
    int layout;
    int count;
    Context activityContext;
    List<Bitmap> list;
    LayoutInflater inf;

    public GestureAdapter3(Context context, int layout, List<Bitmap> list, Context getActivity) {
        this.context = context;
        this.layout = layout;
        this.list = list;
        this.activityContext = getActivity;
        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inf.inflate(layout, parent,false);
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            convertView.setLayoutParams(new GridView.LayoutParams(((int)(displayMetrics.widthPixels/2)),((int)(displayMetrics.widthPixels/2))));
            ImageView iv = (ImageView) convertView.findViewById(R.id.gesture_image);
            iv.setImageResource(R.drawable.addbutton);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    ((AddActivity2) activityContext).startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_CODE);
                }
            });
            Log.d("TAG", "TAGGGGGG");
        } else {
            ImageView iv = (ImageView) convertView.findViewById(R.id.gesture_image);
            iv.setImageBitmap(list.get(position));
            count++;
        }
        return convertView;
    }
}
final class BusProvider3 {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider3() {
        // No instances.
    }
}
