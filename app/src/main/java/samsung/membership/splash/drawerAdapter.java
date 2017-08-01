package samsung.membership.splash;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by KyuYeol on 2017-07-28.
 */

public class drawerAdapter extends ArrayAdapter<String> {

    private LayoutInflater layoutInflater;

    public drawerAdapter(Context context, ArrayList<String> titles) {
        super(context, 0, titles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
