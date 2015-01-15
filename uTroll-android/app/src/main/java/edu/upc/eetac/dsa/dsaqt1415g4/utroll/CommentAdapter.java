package edu.upc.eetac.dsa.dsaqt1415g4.utroll;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqt1415g4.utroll.api.Comment;

public class CommentAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private static class ViewHolder {
        TextView tvUsername;
        TextView tvContent;
        TextView tvLikeCount;
        TextView tvDislikeCount;
    }

    private final ArrayList<Comment> data;

    public CommentAdapter(Context context, ArrayList<Comment> data) {
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
        return ((Comment) getItem(position)).getCommentid();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row_comment, null);
            viewHolder = new ViewHolder();
            viewHolder.tvUsername = (TextView) convertView
                    .findViewById(R.id.tvUsername);
            viewHolder.tvContent = (TextView) convertView
                    .findViewById(R.id.tvContent);
            viewHolder.tvLikeCount = (TextView) convertView
                    .findViewById(R.id.tvLikeCount);
            viewHolder.tvDislikeCount = (TextView) convertView
                    .findViewById(R.id.tvDislikeCount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String username = data.get(position).getUsername();
        String content = data.get(position).getContent();
        int likes = data.get(position).getLikes();
        int dislikes = data.get(position).getDislikes();
        viewHolder.tvUsername.setText(username);
        viewHolder.tvContent.setText(content);
        viewHolder.tvLikeCount.setText(Integer.toString(likes));
        viewHolder.tvDislikeCount.setText(Integer.toString(dislikes));

        return convertView;
    }
}
