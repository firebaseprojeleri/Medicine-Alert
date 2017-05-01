package com.vne.medicinealert.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vne.medicinealert.R;
import com.vne.medicinealert.Modal.Medicine;

import java.util.ArrayList;

/**
 * Created by Volkan Şahin on 19.04.2017.
 */

public class MedicineAdapter extends BaseAdapter{
    private ArrayList<Medicine> medicines;
    private LayoutInflater inflater;

    public MedicineAdapter(Activity activity, ArrayList<Medicine> medicines) {
        this.medicines = medicines;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {return medicines.size();}

    @Override
    public Object getItem(int position) {return medicines.get(position);}

    @Override
    public long getItemId(int position) {return 0;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.listview_medicine_row, null);

        TextView medicineName = (TextView) row.findViewById(R.id.txtMedicineName);
        TextView time = (TextView) row.findViewById(R.id.txtMedicineTime);
        ImageView imgClock = (ImageView) row.findViewById(R.id.imgClock);

        Medicine medicine = medicines.get(position);
        medicineName.setText(medicine.getMedicineName());
        time.setText(medicine.getMedicineTime());
        imgClock.setImageResource(R.drawable.clock);

        return row;
    }
}
