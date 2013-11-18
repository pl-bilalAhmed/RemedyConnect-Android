package com.newpush.mypractice.MedSecureAPI;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;

public class MedSecureConnection  {
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static final String base = "https://MedSecureAPI.com/";

    static final String auth = "Basic UmFscGg6Q29yaWdsaWFubw==";
    static final String api_key = "SSB3aWxsIG1ha2UgbXkgQVBJIHNlY3VyZQ%3d%3d";
    static final String token = "j2w%2bjHHs%2bF8fkvr7Vj5DlPuYg8VqXvOhbtaG4WaOqxA%3d";
    static final String api_token_post = "apikey=&" + api_key + "&token=" + token;

    public void startAsyncGetPractice(Integer practiceID) {
        AsyncAPITask task = new AsyncAPITask();
        task.execute(new String[] { practiceID.toString() });
    }

    public class AsyncAPITask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return getPractice(Integer.parseInt(params[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(context.getApplicationContext(), s, Toast.LENGTH_LONG).show();
        }
    }

    public String getPractice(int practiceID) {
        try {
            URL url = new URL(base + "api/Practice/GetPractice?practiceID=" + practiceID + "&" + api_token_post);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", auth);
            connection.setRequestProperty("Content-Type", "application/json");
            InputStream stream = null;
            connection.connect();

            StringBuffer output = new StringBuffer("");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = connection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    output.append(s);
                }
            }
            return output.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return "";
    }
}
