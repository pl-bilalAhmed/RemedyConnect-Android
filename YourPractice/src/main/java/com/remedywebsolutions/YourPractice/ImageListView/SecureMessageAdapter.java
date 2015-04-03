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

import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.CallTypes;
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
   //     TextView dob = (TextView)vi.findViewById(R.id.dob); // dob name
        TextView date = (TextView)vi.findViewById(R.id.messageDate); // message date
        TextView message = (TextView)vi.findViewById(R.id.message); // message
    //    TextView phone = (TextView)vi.findViewById(R.id.Phone); // phone
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image


        SecureCallMessage secureMessage  = data.get(position);
        ImageView dot_image=(ImageView)vi.findViewById(R.id.dot_image);
        if (secureMessage.wasOpened ) {

            dot_image.setVisibility(View.INVISIBLE);
        //    vi.setBackgroundResource(R.color.list_row_selected);
        }
        else
        {
            dot_image.setVisibility(View.VISIBLE);
         //   vi.setBackgroundResource(R.color.light_gray);
        }

        
        // Setting all values in listview
        String name = secureMessage.callerFirstName + " " + secureMessage.callerLastName;
        if(secureMessage.callerTitle != null)
        {
            name = secureMessage.callerTitle + " " + name;
        }
        patient.setText(name + " - " + Integer.toString(secureMessage.callID));
      //  dob.setText(Integer.toString(secureMessage.callID));
        date.setText(secureMessage.messageDate);
        message.setText(secureMessage.message);



        java.text.MessageFormat phoneMsgFmt=new java.text.MessageFormat("({0})-{1}-{2}");
        //suposing a grouping of 3-3-4
        String[] phoneNumArr={secureMessage.phone.substring(0, 3),
                secureMessage.phone.substring(3,6),
                secureMessage.phone.substring(6)};

     //   phone.setText(phoneMsgFmt.format(phoneNumArr));
        switch (secureMessage.callTypeId)
        {
            case CallTypes.patientToDoctor:
                ((ImageView)thumb_image).setImageResource(R.drawable.p2d);
                break;
            case CallTypes.AnsSvcToDoctor:
                ((ImageView)thumb_image).setImageResource(R.drawable.a2d);
                break;
            case CallTypes.appointment:
                ((ImageView)thumb_image).setImageResource(R.drawable.apt);
                break;
            case CallTypes.doctorToDoctor:
                ((ImageView)thumb_image).setImageResource(R.drawable.d2d);
                break;
            case CallTypes.newbornToDoctor:
                ((ImageView)thumb_image).setImageResource(R.drawable.n2d);
                break;
            case CallTypes.RoundingDoctor:
                ((ImageView)thumb_image).setImageResource(R.drawable.rnd);
                break;
            case CallTypes.hospitalAdm:
                ((ImageView)thumb_image).setImageResource(R.drawable.adm);
                break;
            case CallTypes.rxRefill:
                ((ImageView)thumb_image).setImageResource(R.drawable.rxd);
                break;
            case CallTypes.TriageToDoctor:
                ((ImageView)thumb_image).setImageResource(R.drawable.triage);
                break;
            case CallTypes.pageMyDoctor:
                ((ImageView)thumb_image).setImageResource(R.drawable.p2d);
                break;
        }
        if(secureMessage.urgent) {
            ((ImageView)thumb_image).setImageResource(R.drawable.urgent);
        }

        return vi;
    }
}