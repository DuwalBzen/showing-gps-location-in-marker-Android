package com.example.merolocation;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.merolocation.Model.MyItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class MarkerClusterRenderer extends DefaultClusterRenderer<MyItem> {

    private static final int MARKER_DIMENSION = 48;  // 2
    private final IconGenerator iconGenerator;
    private final ImageView markerImageView;

    public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
        iconGenerator = new IconGenerator(context);  // 3
        markerImageView = new ImageView(context);
        markerImageView.setLayoutParams(new ViewGroup.LayoutParams(MARKER_DIMENSION, MARKER_DIMENSION));
        iconGenerator.setContentView(markerImageView);  // 4
    }

    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) { // 5// 7

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.parking_2));  // 8
        markerOptions.title((item.getTitle())).snippet(item.getSnippet());

    }


}
