package com.prisrio.prisrio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Hansel on 26/6/2017.
 */

public class profileGridViewAdapter extends BaseAdapter{

    private ArrayList<String> imageArr;
    private int intimageArr [];
    private Context context;
    private LayoutInflater inflater;





    public profileGridViewAdapter(Context context, ArrayList<String> imageArr, int[] intimageArr){
        this.context = context;
        //this.imageArr = imageArr;
        this.intimageArr = intimageArr;
    }

    @Override
    public int getCount() {
        return intimageArr.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        View gridView = convertView;

        if(convertView == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.profileimagegallery, null);
            //imageView = new ImageView(context);
        }

         ImageView icon = (ImageView) gridView.findViewById(R.id.img_profileimagegallery_image);

        icon.setImageResource(intimageArr[position]);
        /*
        URL url = null;
        try {
            url = new URL(imageArr.get(position));
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            icon.setImageBitmap(bmp);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        //imageView.setImageResource(mThumbIds[position]);
        return gridView;
    }

}
