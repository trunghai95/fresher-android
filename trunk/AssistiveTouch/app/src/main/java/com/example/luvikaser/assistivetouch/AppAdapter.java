package com.example.luvikaser.assistivetouch;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Luvi Kaser on 7/18/2016.
 */
class AppAdapter extends BaseAdapter {
    public static boolean[] itemCheckeds;
    private PackageManager mPackageManager = null;
    private Activity mContext;
    private int nItem;
    private List<ResolveInfo> mApps;

    AppAdapter(Activity context, List<ResolveInfo> apps, PackageManager packageManager, boolean[] itemCheckeds, int nItem) {
        super();
        this.mPackageManager = packageManager;
        this.mContext = context;
        this.mApps = apps;
        this.itemCheckeds = itemCheckeds;
        this.nItem = nItem;
    }

    @Override
    public int getCount() {
        return mApps.size();
    }

    @Override
    public ResolveInfo getItem(int i) {
        return mApps.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.row, null);

            holder = new ViewHolder();
            holder.apkIcon = (ImageView) convertView.findViewById(R.id.icon);
            holder.apkName = (TextView) convertView.findViewById(R.id.label);
            holder.ck1 = (CheckBox) convertView.findViewById(R.id.checkBox);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Set data for view is located at position
        holder.apkName.setText(getItem(position).loadLabel(mPackageManager));
        holder.ck1.setChecked(itemCheckeds[position]);
        holder.apkIcon.setImageDrawable(getItem(position).loadIcon(mPackageManager));

        holder.ck1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (holder.ck1.isChecked()) {
                    if (nItem == 0) { //If the number of applications are choosed is zero then Toast a message
                        holder.ck1.setChecked(false);
                        Toast.makeText(v.getContext(), "The panel is full. Cannot select more apps.", Toast.LENGTH_SHORT).show();
                    } else {
                        --nItem;
                        itemCheckeds[position] = true;
                    }
                } else {
                    ++nItem;
                    itemCheckeds[position] = false;
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
        ImageView apkIcon;
        TextView apkName;
        CheckBox ck1;
    }

}