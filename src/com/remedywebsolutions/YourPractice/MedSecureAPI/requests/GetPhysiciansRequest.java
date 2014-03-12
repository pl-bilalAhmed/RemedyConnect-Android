package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.*;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GetPhysiciansRequest extends SpiceRequest<PhysiciansResponse> {
    private Context context;

    public GetPhysiciansRequest(Context context) {
        super(PhysiciansResponse.class);
        this.context = context;
    }

    @Override
    public PhysiciansResponse loadDataFromNetwork() throws Exception {
        LoggedInDataStorage storage = new LoggedInDataStorage(context);
        HashMap<String, String> userData = storage.RetrieveData();
        String practiceID = userData.get("practiceID");

        MedSecureConnection msc = new MedSecureConnection(context);
        msc.buildBaseURI("Physician", "GetPhysicians", "GET");
        msc.addParameter("PracticeID", practiceID);

        HttpURLConnection connection = msc.initConnection(true);
        String response = MedSecureConnection.getStringResult(connection);
        connection.disconnect();

        ObjectMapper mapper = new ObjectMapper();
        PhysiciansResponse result = new PhysiciansResponse();
        Physician[] physicians = mapper.readValue(response, Physician[].class);
        result.physicians = new ArrayList<Physician>(Arrays.asList(physicians));

        return result;
    }
}
