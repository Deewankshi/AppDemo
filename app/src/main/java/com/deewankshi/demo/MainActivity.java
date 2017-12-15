package com.deewankshi.demo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton fab;
    String namevalue = "";
    String descvalue = "";
    String data = "";
    DatabaseHandler db;
    ArrayList<DataModel> databrands = new ArrayList<DataModel>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        db = new DatabaseHandler(this);

        if (ApplicationClass.isNetworkAvailable(MainActivity.this))
        {
            new GetBrands().execute();
        }else
        {
            Log.d("count ",db.getAllBrands().size()+"");
            Recycler_View_Adapter adapter = new Recycler_View_Adapter(db.getAllBrands(), MainActivity.this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(1000);
            itemAnimator.setRemoveDuration(1000);
            recyclerView.setItemAnimator(itemAnimator);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup();
            }
        });

    }


    //---------------------------------------Popp
    public void showPopup(){
        final View popupView = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_item_dialog, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        popupWindow.setFocusable(true);
        popupWindow.update();
        final EditText editname = (EditText) popupView.findViewById(R.id.editTitle);
        final EditText editdesc = (EditText) popupView.findViewById(R.id.editDesc);
        Button submitbtn = (Button) popupView.findViewById(R.id.btnsubmit);
        editdesc.requestFocus();
        editname.requestFocus();

        popupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow.isShowing())
                {
                    popupWindow.dismiss();
                }
            }
        });

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                     if (ApplicationClass.isNetworkAvailable(MainActivity.this))
                     {
                         if(editname.getText().toString().trim().equals(""))
                         {
                             Toast.makeText(getBaseContext(), "Enter brand name", Toast.LENGTH_LONG).show();
                         }
                         else if(editdesc.getText().toString().trim().equals("")){
                             Toast.makeText(getBaseContext(), "Enter some description", Toast.LENGTH_LONG).show();
                         }
                         else {
                             // call AsynTask to perform network operation on separate thread
                              namevalue =  editname.getText().toString();
                              descvalue = editdesc.getText().toString();
                              if (popupWindow.isShowing())
                              {
                                  popupWindow.dismiss();
                              }
                             new HttpAsyncTask().execute("http://appsdata2.cloudapp.net/demo/androidApi/insert.php");
                         }

                     }else
                     {
                         Toast.makeText(MainActivity.this, "Unable to process, please check your internet connection!", Toast.LENGTH_LONG).show();
                     }
            }
        });
    }

    //-----------------------------------Post data on server i.e. brand details
    private class GetBrands extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection;
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Fetching data.....");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL("http://appsdata2.cloudapp.net/demo/androidApi/list.php");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            }catch( Exception e) {
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }
            return result.toString();
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject obj = new JSONObject(result);
                String code = obj.getString("error_code");
                if (code.equals("1"))
                {
                    JSONArray jary = obj.getJSONArray("brand_list");
                    for (int i=0;i<jary.length();i++)
                    {
                        JSONObject jobj  = jary.getJSONObject(i);
                        DataModel data = new DataModel();
                        data.setTitle(jobj.getString("name"));
                        data.setId(Integer.parseInt(jobj.getString("id")));
                        data.setDescription(jobj.getString("description"));
                        data.setTimeframe(jobj.getString("created_at"));
                        databrands.add(data);
                    }

                    for (int i=0;i<databrands.size();i++)
                    {
                        // Inserting Brands
                        Log.d("Insert: ", "Inserting ..");
                        if (db.checkAlreadyExist(databrands.get(i).getId()))
                        {

                        }else {
                            db.adddata(new DataModel(databrands.get(i).getId(), databrands.get(i).getTitle(), databrands.get(i).getDescription(), databrands.get(i).getTimeframe()));
                        }
                    }

                    Recycler_View_Adapter adapter = new Recycler_View_Adapter(databrands, MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
                    itemAnimator.setAddDuration(1000);
                    itemAnimator.setRemoveDuration(1000);
                    recyclerView.setItemAnimator(itemAnimator);

                }
                Log.i("DEEWA",obj.toString());
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    }


//-----------------------------------Post data on server i.e. brand details
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
    ProgressDialog progressDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("processing.....");
        progressDialog.show();
    }

    @Override
        protected String doInBackground(String... urls) {
            sendPost(urls[0]);
            return null;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        try {
            JSONObject obj = new JSONObject(data);
             if (obj.getString("error_code").equals("1")) {
                 Toast.makeText(getBaseContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                 new GetBrands().execute();
             }else
             {
                 Toast.makeText(getBaseContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
             }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
            progressDialog.dismiss();
        }
    }


    //--------------------------------------------------creating json object of brands to post data
    public void sendPost(String urlAdress) {
               HttpURLConnection httpURLConnection = null;
                 try {
                     httpURLConnection = (HttpURLConnection) new URL(urlAdress).openConnection();
                     httpURLConnection.setRequestMethod("POST");
                     httpURLConnection.setDoOutput(true);
                     JSONObject brand1 = new JSONObject();
                     try {
                         brand1.put("name", namevalue);
                         brand1.put("description", descvalue);
                     } catch (JSONException e) {
                         // TODO Auto-generated catch block
                         e.printStackTrace();
                     }

                     JSONArray jsonArray = new JSONArray();
                     jsonArray.put(brand1);

                     JSONObject jsonParam = new JSONObject();
                     jsonParam.put("brand", jsonArray);
                     String jsonStr = jsonParam.toString();
                     System.out.println("jsonString: "+jsonStr);

                     DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                     wr.writeBytes("data=" + jsonStr);
                     wr.flush();
                     wr.close();

                     InputStream in = httpURLConnection.getInputStream();
                     InputStreamReader inputStreamReader = new InputStreamReader(in);

                     int inputStreamData = inputStreamReader.read();
                     while (inputStreamData != -1) {
                         char current = (char) inputStreamData;
                         inputStreamData = inputStreamReader.read();
                         data += current;
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                 } finally {
                     if (httpURLConnection != null) {
                         httpURLConnection.disconnect();
                     }
                 }


    }
}
