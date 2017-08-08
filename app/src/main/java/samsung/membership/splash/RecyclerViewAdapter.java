package samsung.membership.splash;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yumin on 2017-08-05.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ViewGroup viewGroup;
    Context context;
    Menu menu;

    public RecyclerViewAdapter(ArrayList<MyData> myDataset, int count, Context context) {
        dataSet = myDataset;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(dataSet.get(position).state == true) {
            final OpendViewHolder opendViewHolder = (OpendViewHolder)holder;
            opendViewHolder.title.setText(dataSet.get(position).title);
//            viewHolder.imageView.setImageResource(dataSet.get(position).img);
            Picasso.with(context).load("http://172.24.1.1/" + Integer.toString(dataSet.get(position).img + 1) + ".jpg").transform(PicassoTransformations.resizeTransformation).into(opendViewHolder.imageView);
            opendViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                    alertDialog.setMessage("Are you sure you want to quit?");
                    alertDialog.setButton(Dialog.BUTTON_POSITIVE ,"Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            opendViewHolder.onAir.setText("Closed");
                            opendViewHolder.button.setVisibility(View.INVISIBLE);
                            MainActivity.OpenedChannelCounter = false;
                            if(context instanceof MainActivity) {
                                ((MainActivity)context).onPrepareOptionsMenu(MainActivity.menu);
                                ((MainActivity)context).updateRecyclerView();
                            }

                        }
                    });
                    alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Cancle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            viewGroup.invalidate();
                        }
                    });
                    alertDialog.show();
                }
            });
        }
        else {
            ClosedViewHolder closedViewHolder = (ClosedViewHolder)holder;
            closedViewHolder.title.setText(dataSet.get(position).title);
            Picasso.with(context).load("http://172.24.1.1/" + Integer.toString(dataSet.get(position).img + 1) + ".jpg").transform(PicassoTransformations.resizeTransformation).into(closedViewHolder.imageView);
            //closedViewHolder.imageView.setImageResource(dataSet.get(position).img);
        }
    }

    private ArrayList<MyData> dataSet;

    public static class OpendViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView onAir;
        public TextView title;
        public TextView date;
        public Button button;

        OpendViewHolder(View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.opened_image);
            onAir = (TextView)view.findViewById(R.id.on_air);
            title = (TextView)view.findViewById(R.id.opened_title);
            date = (TextView)view.findViewById(R.id.opened_date);
            button = (Button)view.findViewById(R.id.close_channel);
        }
    }
    public static class ClosedViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView title;
        public TextView date;

        public ClosedViewHolder(View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.closed_image);
            title = (TextView)view.findViewById(R.id.closed_title);
            date = (TextView)view.findViewById(R.id.closed_date);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {

        if(dataSet.get(viewType).state == true) {
            viewGroup = parent;
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.opened_channel, parent, false);
            OpendViewHolder vh = new OpendViewHolder(v);
            return vh;
        }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.closed_channel,parent,false);
            ClosedViewHolder vh = new ClosedViewHolder(v);
            return vh;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}

class MyData{
    public int img;
    public String title;
    public String date;
    public boolean state;
    public MyData(String title, int img, boolean state){
        this.title = title;
        this.img = img;
        this.state = state;
    }
}