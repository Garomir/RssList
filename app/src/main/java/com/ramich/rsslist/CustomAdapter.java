package com.ramich.rsslist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

class CustomAdapter extends BaseAdapter {

    private List<Topic> allTopics = null;
    private Context ctx1;

    public CustomAdapter(Context ctx, List<Topic> topics) {
        super();
        ctx1 = ctx;
        allTopics = topics;
    }

    @Override
    public int getCount() {
        return allTopics.size();
    }

    @Override
    public Object getItem(int i) {
        return allTopics.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v = view;
        if (v == null) v = View.inflate(viewGroup.getContext(), R.layout.list_feed_item, null);
        TextView tvTitle = v.findViewById(R.id.tvTitle);
        TextView tvDesc = v.findViewById(R.id.tvDesc);
        tvTitle.setText(allTopics.get(i).getTitle());
        tvDesc.setText(allTopics.get(i).getDescription());
        return v;
    }
}
