package com.siliconorchard.notification.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.siliconorchard.notification.R;
import com.siliconorchard.notification.model.HostInfo;
import com.siliconorchard.notification.widget.CircularImageView;

import java.util.List;

/**
 * Created by adminsiriconorchard on 6/14/16.
 */
public class AdapterContactList extends AdapterHostListBase {

    private Drawable drawableArrow;
    private List<Bitmap> mListBitmap;

    public AdapterContactList(Activity activity, List<HostInfo> hostInfoList, List<Bitmap> bitmaps) {
        super(activity, hostInfoList);
        mInflater = LayoutInflater.from(activity);
        mListHostInfo = hostInfoList;
        this.mListBitmap  = bitmaps;
        drawableArrow = activity.getResources().getDrawable(R.drawable.arrow_right);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_contact_list, null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_recipient);
            viewHolder.tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
            viewHolder.ivArrow = (ImageView) convertView.findViewById(R.id.iv_right_arrow);
            viewHolder.ivPP = (CircularImageView) convertView.findViewById(R.id.iv_pp);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        HostInfo hostInfo = mListHostInfo.get(position);
        viewHolder.tvName.setText(hostInfo.getDeviceName());
        if(hostInfo.getStatus() == null || hostInfo.getStatus().length()<1) {
            viewHolder.tvStatus.setText("No status");
        } else {
            viewHolder.tvStatus.setText(hostInfo.getStatus());
        }
        viewHolder.ivArrow.setImageDrawable(drawableArrow);
        Bitmap bitmap = mListBitmap.get(position);
        if(bitmap == null) {
            viewHolder.ivPP.setImageResource(R.drawable.ic_pp);
        } else {
            viewHolder.ivPP.setImageBitmap(bitmap);
        }
        return convertView;
    }
}
