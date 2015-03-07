package com.example.bomber;

import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EmergenciaAdapter extends BaseAdapter {
	private Context context;
    private List<DatosEmergencia> items;
 
    public EmergenciaAdapter(Context context, List<DatosEmergencia> items) {
        this.context = context;
        this.items = items;
    }
 
    @Override
    public int getCount() {
        return this.items.size();
    }
 
    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
 
        View rowView = convertView;
 
        if (convertView == null) {
            // Create a new view into the list.
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.image_list_item, parent, false);
        }
 
        // Set data into the view.
        ImageView ivItem = (ImageView) rowView.findViewById(R.id.category);
        TextView tvTitle = (TextView) rowView.findViewById(R.id.text1);
        TextView subtitle = (TextView) rowView.findViewById(R.id.text2);
 
        DatosEmergencia item = this.items.get(position);
        tvTitle.setText(item.getLugar());
        subtitle.setText(item.getTelefono());
        ivItem.setImageResource(item.getImagen());
 
        return rowView;
    }
}
