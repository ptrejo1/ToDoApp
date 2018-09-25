package com.ptrejo.todo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<TodoItem> {

    private int layoutResourceId;
    private LayoutInflater inflater;
    private List<TodoItem> items;

    static class ItemHolder {
        TextView itemName;
        TextView itemDescription;
        TextView itemDueDate;
    }

    public ItemAdapter(Context context, int layoutResourceId, List<TodoItem> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.items = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ItemHolder holder = null;

        if (convertView == null) {
            convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ItemHolder();
            holder.itemName = convertView.findViewById(R.id.row_name_tv);
            holder.itemDescription = convertView.findViewById(R.id.row_description_tv);
            holder.itemDueDate = convertView.findViewById(R.id.row_date_tv);

            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        try {
            TodoItem item = items.get(position);
            holder.itemName.setText(item.getName());
            holder.itemDescription.setText(item.getDescription());
            holder.itemDueDate.setText(item.getDate());
        } catch (Exception e) { e.printStackTrace(); }

        return convertView;
    }
}
