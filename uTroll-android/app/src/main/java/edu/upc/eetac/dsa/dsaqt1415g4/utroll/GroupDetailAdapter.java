package edu.upc.eetac.dsa.dsaqt1415g4.utroll;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.Group;
import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.User;

public class GroupDetailAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private static class ViewHolder {
        TextView tvGroupUsername;
        TextView tvGroupUserPoints;
    }

    private final ArrayList<User> data;

    public GroupDetailAdapter(Context context, ArrayList<User> data) {
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
        return ((User) getItem(position)).getGroupid(); //Esto no sirve
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row_group_detail, null);
            viewHolder = new ViewHolder();
            viewHolder.tvGroupUsername = (TextView) convertView
                    .findViewById(R.id.tvGroupUsername);
            viewHolder.tvGroupUserPoints = (TextView) convertView
                    .findViewById(R.id.tvGroupUserPoints);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String username = data.get(position).getUsername();
        int points = data.get(position).getPoints();
        viewHolder.tvGroupUsername.setText("Username: " + username);
        viewHolder.tvGroupUserPoints.setText("Points: " + Integer.toString(points));

        return convertView;
    }
}
