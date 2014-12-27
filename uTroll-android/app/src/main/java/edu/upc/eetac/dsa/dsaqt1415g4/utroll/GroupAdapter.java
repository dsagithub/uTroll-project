package edu.upc.eetac.dsa.dsaqt1415g4.utroll;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.Group;

public class GroupAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private static class ViewHolder {
        TextView tvGroupName;
        TextView tvGroupState;
    }

    private final ArrayList<Group> data;

    public GroupAdapter(Context context, ArrayList<Group> data) {
        super();
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ((Group) getItem(position)).getGroupid();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row_group, null);
            viewHolder = new ViewHolder();
            viewHolder.tvGroupName = (TextView) convertView
                    .findViewById(R.id.tvGroupName);
            viewHolder.tvGroupState = (TextView) convertView
                    .findViewById(R.id.tvGroupState);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String groupname = data.get(position).getGroupname();
        String state = data.get(position).getState();
        viewHolder.tvGroupName.setText(groupname);
        viewHolder.tvGroupState.setText(state);

        return convertView;
    }
}
