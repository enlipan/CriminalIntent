package com.itlipan.lee.criminalintent;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by Lee on 2015/6/21.
 */
public class CriminalIntentJSONSerializer {
    private static final String TAG = "JSONSerializer";
    private Context mContext;
    private String mFileName;

    public CriminalIntentJSONSerializer(Context c, String s) {
        mContext = c;
        mFileName = s;
    }

    public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
        ArrayList<Crime> crimes = new ArrayList<Crime>();
        BufferedReader reader = null;

        try {
            InputStream in = mContext.openFileInput(mFileName);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            //Parse jsonString
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();

            //Build the array OF crimes
            for (int i = 0; i < array.length(); i++) {
                crimes.add(new Crime(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            // Ignore
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return crimes;
    }

    public void saveCrimes(ArrayList<Crime> crimes) throws
            JSONException, IOException {
        //create JSON data
        JSONArray array = new JSONArray();
        for (Crime c : crimes) {
            array.put(c);
        }

        //Write to disk
        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
            Log.d(TAG,array.toString());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

    }

}
