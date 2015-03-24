package com.remedywebsolutions.YourPractice.ImageListView;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SecureCallMessage;
import com.remedywebsolutions.YourPractice.R;

public class SecureMessageAdapter extends BaseAdapter {
    
    private Activity activity;
    private   ArrayList<SecureCallMessage>  data;
    private static LayoutInflater inflater=null;


    public SecureMessageAdapter(Activity a,  ArrayList<SecureCallMessage> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return data.size();
    }

    public void updateData(ArrayList<SecureCallMessage> d) {
       data = d;
       notifyDataSetChanged();
    }
    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null) {
            vi = inflater.inflate(R.layout.list_row, null);
        }

        TextView patient = (TextView)vi.findViewById(R.id.patient); // title (patient)
        TextView dob = (TextView)vi.findViewById(R.id.dob); // dob name
        TextView date = (TextView)vi.findViewById(R.id.messageDate); // message date
        TextView message = (TextView)vi.findViewById(R.id.message); // message
        TextView phone = (TextView)vi.findViewById(R.id.Phone); // phone
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image

        SecureCallMessage secureMessage  = data.get(position);

        
        // Setting all values in listview
        patient.setText(secureMessage.patientFirstName + " " + secureMessage.patientLastName);
        dob.setText(secureMessage.patientDob);
        date.setText(secureMessage.messageDate);
        message.setText(secureMessage.message);
        phone.setText(secureMessage.phone);
       // thumb_image.setImageResource(R.a);

        return vi;
    }
}