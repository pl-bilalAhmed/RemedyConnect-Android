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

import com.remedywebsolutions.YourPractice.R;

public class SecureMessageAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;

    
    public SecureMessageAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return data.size();
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

        HashMap<String, String> secureMessages = new HashMap<String, String>();
        secureMessages = data.get(position);
        
        // Setting all values in listview
         patient.setText(secureMessages.get("patient"));
     //   patient.setText(message.get("patientName"));
     //   date.setText(message.get("date"));
       // thumb_image.setImageResource(R.a);

        return vi;
    }
}