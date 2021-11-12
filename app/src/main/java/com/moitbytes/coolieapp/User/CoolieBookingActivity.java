package com.moitbytes.coolieapp.User;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.moitbytes.coolieapp.Coolie.Coolie_Order_Pojo;
import com.moitbytes.coolieapp.R;
import com.moitbytes.coolieapp.RetrofitApiService;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class CoolieBookingActivity extends AppCompatActivity implements PaymentResultListener
{
    String FCM_AUTH_KEY = "YOUR_CLOUD_SERVER_KEY";
    Button btPay;
    String token;
    TextView type, coolie_name, n_trolley, n_container, n_bag, n_wheelchair, order_date,
    order_time, station_name, amount, train_name;
    SharedPreferences preferences;
    String book_type, PNR;
    String b_coolie_name, coolie_phone, b_order_date, b_order_time, b_station_name, train_n;
    float book_amount = 0.0f;
    int b_n_trolley = 0, b_n_container = 0, b_n_bag = 0, b_n_wheelchair = 0;

    LinearLayout coolie_visibility, orderCompletionLayout;
    Retrofit retrofit;
    boolean pay_success = false;

    String usr_email, usr_phone, first_name, last_name;
    FirebaseDatabase rootNode;
    DatabaseReference reference1, reference2;
    long maxId = 0;
    long maxId2 = 0;
    int amount2;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coolie_booking);
        preferences = getSharedPreferences("coolie_shared", MODE_PRIVATE);
        Intent i = getIntent();
        book_type = i.getExtras().getString("type", "");
        token = i.getExtras().getString("token","");
        Log.e("chk1",token+"hello World");
        b_station_name = i.getExtras().getString("station_name", "");
        PNR = preferences.getString("pnr", "");
        b_order_date = preferences.getString("order_date", "");
        b_order_time = preferences.getString("order_time", "");
        book_amount = preferences.getFloat("amount", 0.0f);

        //User Details
        usr_email = preferences.getString("email", "");
        usr_phone = preferences.getString("phone", "");
        first_name = preferences.getString("first_name", "");
        last_name = preferences.getString("last_name", "");

        type = findViewById(R.id.order_type);
        coolie_name = findViewById(R.id.coolie_name);
        n_trolley = findViewById(R.id.n_of_trolley);
        n_container = findViewById(R.id.n_of_container);
        n_bag = findViewById(R.id.n_of_bags);
        n_wheelchair = findViewById(R.id.n_of_wheelchair);
        order_time = findViewById(R.id.order_time);
        order_date = findViewById(R.id.order_date);
        station_name = findViewById(R.id.station_name);
        amount = findViewById(R.id.book_amount);
        coolie_visibility = findViewById(R.id.coolieBaggage);
        train_name = findViewById(R.id.train_name);
        orderCompletionLayout = findViewById(R.id.order_complete_detail);

        orderCompletionLayout.setVisibility(View.GONE);


        coolie_visibility.setVisibility(View.GONE);
        n_wheelchair.setVisibility(View.GONE);



        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Fetching Order Details");
        pd.setCancelable(false);
        pd.show();
        String base_url = "https://1ldf3h.deta.dev";
        retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(ScalarsConverterFactory.create()).build();

        RetrofitApiService service = retrofit.create(RetrofitApiService.class);
        Call<String> response = service.getPNRDetails(PNR);

        response.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(Call<String> call, Response<String> response)
            {
                pd.dismiss();
                if(response.isSuccessful())
                {
                    assert response.body() != null;
                    JSONObject jsonObject;
                    try
                    {
                        jsonObject = new JSONObject(response.body());
                        JSONObject data = jsonObject.getJSONObject("valid");
                        Boolean valid = data.getBoolean("valid");
                        if(valid)
                        {
                            train_n = data.getString("train_name");
                            train_name.setText("Train Name: "+train_n);
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t)
            {

            }
        });
        type.setText("Booking Type: "+book_type);
        order_date.setText("Order Date: "+b_order_date);
        order_time.setText("Order Time: "+b_order_time);
        station_name.setText("Station Code: "+b_station_name);
        amount.setText("Amount: "+book_amount);


        if(book_type.equals("coolie"))
        {
            coolie_visibility.setVisibility(View.VISIBLE);
            n_wheelchair.setVisibility(View.GONE);
            b_coolie_name = i.getExtras().getString("coolie_name", "");
            coolie_phone = i.getExtras().getString("phone", "");
            b_n_trolley = preferences.getInt("qty_trolley", 0);
            b_n_container = preferences.getInt("qty_container", 0);
            b_n_bag = preferences.getInt("qty_bag", 0);

            coolie_name.setText("Coolie Name: "+b_coolie_name);
            n_trolley.setText("Number of Trolleys: "+b_n_trolley);
            n_container.setText("Number of Containers: "+b_n_container);
            n_bag.setText("Number of Bags: "+b_n_bag);

        }
        else
        {
            b_n_wheelchair = i.getExtras().getInt("n_wheelchair", 0);
        }

        btPay = findViewById(R.id.btnPay);
        String Samount = String.valueOf(book_amount);
        amount2 = Math.round(Float.parseFloat(Samount)*100);
        registerForContextMenu(btPay);



        btPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                btPay.performLongClick();
            }
        });
    }


    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Choose your Payment Mode");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pay_menu, menu);
    }


    @SuppressLint("NonConstantResourceId")
    public boolean onContextItemSelected(MenuItem item) {
        //find out which menu item was pressed
        switch (item.getItemId()) {
            case R.id.razorpay:

                Checkout checkout = new Checkout();
                checkout.setKeyID("rzp_test_7gmkhm9HYQAJbt");
                checkout.setImage(R.drawable.ic_razorpay);
                JSONObject jsonObject = new JSONObject();
                try
                {
                    jsonObject.put("name", first_name+" "+last_name);
                    jsonObject.put("description",
                            "Booking Coolie at"+b_station_name+" station");
                    jsonObject.put("theme.color",
                            "#0093DD");
                    jsonObject.put("currency", "INR");
                    jsonObject.put("amount", amount2);
                    jsonObject.put("prefill.contact",
                            "+91 "+usr_phone);
                    jsonObject.put("prefill.email",
                            usr_email);
                    checkout.open(CoolieBookingActivity.this,
                            jsonObject);

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                return true;

            case R.id.cash:
                onPaymentSuccess("cash");
                return true;
            default:
                return false;
        }
    }


    public void GoBackToCoolieSearch(View view)
    {
        onBackPressed();
    }

    @Override
    public void onBackPressed()
    {
        if(pay_success)
        {
            GoToUserDashboard();
        }
        else
        {
            if(book_type.equals("coolie"))
            {
                Intent i = new Intent(CoolieBookingActivity.this,
                        BookCoolieActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                overridePendingTransition(0, 0);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                finish();
            }
            else
            {
                Intent i = new Intent(CoolieBookingActivity.this,
                        BookWheelChairActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                overridePendingTransition(0, 0);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                finish();
            }
        }
    }

    @Override
    public void onPaymentSuccess(String s)
    {
        pay_success = true;
        btPay.setVisibility(View.GONE);
        orderCompletionLayout.setVisibility(View.VISIBLE);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss",
                Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        String coolie_f_name = "";
        String coolie_l_name = "";
        rootNode = FirebaseDatabase.getInstance();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = null;
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Please Wait! While we check your payment!");
        pd.setCancelable(false);
        pd.show();
        String user_fcm = preferences.getString("fcmToken", "");
        if(book_type.equals("coolie"))
        {
            //Coolie and User

            long max = 1000;
            long min = 999999;
            long range = max - min + 1;
            maxId = (long)(Math.random() * range) + min;
            coolie_f_name = b_coolie_name.substring(0, b_coolie_name.indexOf(' '));
            coolie_l_name = b_coolie_name.substring(b_coolie_name.indexOf(' ')+1);
            reference1 = rootNode.getReference("user_orders");
            reference2 = rootNode.getReference("coolie_orders");




            OrderDataPojo dataPojo = new OrderDataPojo("coolie",
                    usr_phone, coolie_f_name, coolie_l_name, coolie_phone,
                    b_order_time, b_order_date, b_station_name,
                    b_n_wheelchair, b_n_trolley, b_n_container, b_n_bag, book_amount,
                    false,false, currentDateandTime);

            Coolie_Order_Pojo coolie_order_pojo = new Coolie_Order_Pojo(coolie_phone,
                    String.valueOf(maxId+1), first_name, last_name, usr_phone,
                    b_order_time, b_order_date, b_station_name, train_n,
                    b_n_wheelchair, b_n_trolley, b_n_container, b_n_bag, book_amount,
                    false,false, currentDateandTime);


            reference1.child(currentDateandTime).setValue(dataPojo);
            reference2.child(currentDateandTime).setValue(coolie_order_pojo);

            String usr_msg = "";
            String coolie_msg= "";

            if(s.equals("cash"))
            {
                usr_msg = "Hi "+first_name+" "+last_name+".Your order has been confirmed."+
                        "Below are the coolie details: "+ ".Coolie Name: "+b_coolie_name+".Coolie Phone: "+
                        coolie_phone+ ".Payment Mode : Cash"+
                        ".For any queries use support.Thanks--Team Coolie App";

                coolie_msg = "Hi "+b_coolie_name+".You have a new order!"+
                        ".Below are the customer details: "+ ".Customer Name: "+first_name+
                        " "+last_name+".Customer Phone: "+
                        usr_phone+" Train Name: "+train_n+
                        ".Date: "+b_order_date+
                        ".Time: "+b_order_time+
                        ".Payment Mode: Cash"+
                        ".Do collect the amount from the user"+
                        ".For any queries use support.Thanks--Team Coolie App";
            }
            else
            {
                usr_msg = "Hi "+first_name+" "+last_name+".Your order has been confirmed."+
                        ".Below are the coolie details: "+ ".Coolie Name: "+b_coolie_name+".Coolie Phone: "+
                        coolie_phone+
                        ".For any queries use support.Thanks--Team Coolie App";

                coolie_msg = "Hi "+b_coolie_name+".You have a new order!"+
                        ".Below are the customer details: "+ ".Customer Name: "+first_name+
                        " "+last_name+".Customer Phone: "+
                        usr_phone+".Train Name: "+train_n+
                        ".Date: "+b_order_date+
                        ".Time: "+b_order_time+
                        ".For any queries use support.Thanks--Team Coolie App";
            }


            final String coolie_temp_msg = coolie_msg;

            body = RequestBody.create
                    (mediaType,
                            "{\"0\":" +
                                    "\"coolie\"," +
                                    "\"1\":\""+coolie_phone+"\"," +//coolie phone number
                                    "\"2\":\""+coolie_msg+"\"," +//coolie message
                                    "\"3\":\""+usr_phone+"\"," +//user phone number
                                    "\"4\":\""+usr_msg+"\"," +//user message
                                    "\"5\":\""+token+"\","+ //coolie fcm
                                    "\"6\":\""+user_fcm+"\"}"//user fcm
                    );

            String base_url = "https://9433xu.deta.dev/";
            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(ScalarsConverterFactory.create()).build();

            RetrofitApiService service = retrofit.create(RetrofitApiService.class);
            Call<String> response = service.sendUserSMS(body);
            response.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response)
                {
                    pd.dismiss();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    pd.dismiss();
                }
            });

            //FCM Send Notification
            String base_url_1 = "https://fcm.googleapis.com";

            mediaType = MediaType.parse("application/json");
            body = RequestBody.create(mediaType, "{\r\n \"to\" : " +
                    "\""+user_fcm+"\",\r\n " +
                    "\"notification\" : {\r\n     " +
                    "\"body\" : \""+usr_msg+"\",\r\n     " +
                    "\"title\": \"Pick Up Confirmed\"\r\n }\r\n}");

            String conType = "application/json";
            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url_1)
                    .addConverterFactory(ScalarsConverterFactory.create()).build();

            service = retrofit.create(RetrofitApiService.class);
            Call<String> response1 = service.sendFcmToken(conType, FCM_AUTH_KEY,
                    body);

            response1.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response)
                {

                }

                @Override
                public void onFailure(Call<String> call, Throwable t)
                {

                }
            });



            //Send to Coolie
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                    getReference("fcmTokens");

            Query checkUser = databaseReference.orderByChild("phone").equalTo(coolie_phone);
            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                    {
                        String coolie_fcm = snapshot.child(coolie_phone).child("fcmToken").
                                getValue(String.class);
                        MediaType mediaType = MediaType.parse("application/json");
                        RequestBody body = RequestBody.create(mediaType, "{\r\n \"to\" : " +
                                "\""+coolie_fcm+"\",\r\n " +
                                "\"notification\" : {\r\n     " +
                                "\"body\" : \""+coolie_temp_msg+"\",\r\n     " +
                                "\"title\": \"You got a new order!\"\r\n }\r\n}");

                        retrofit = new Retrofit.Builder()
                                .baseUrl(base_url_1)
                                .addConverterFactory(ScalarsConverterFactory.create()).build();

                        RetrofitApiService service = retrofit.create(RetrofitApiService.class);
                        Call<String> response2 = service.sendFcmToken(conType, FCM_AUTH_KEY,
                                body);

                        response2.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                pd.dismiss();
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        }
        else
        {
            //Wheelchair so only user
            reference1 = rootNode.getReference("user_orders");
            OrderDataPojo dataPojo = new OrderDataPojo("wheelchair",
                    usr_phone, coolie_f_name, coolie_l_name, coolie_phone,
                    b_order_time, b_order_date, b_station_name,
                    b_n_wheelchair, b_n_trolley, b_n_container, b_n_bag, book_amount,
                    false,false, currentDateandTime);

            reference1.child(String.valueOf(currentDateandTime)).setValue(dataPojo);

            String usr_msg = "Hi "+first_name+" "+last_name+".Your order has been confirmed."+
                    ".Below are the details: "+ ".Your "+b_n_wheelchair+" wheelchairs are ready at the station."+
                    ".Our person would reach out to you before 15 minutes of arrival"+
                    ".For any queries use support.Thanks--Team Coolie App";

            body = RequestBody.create
                    (mediaType,
                            "{\"0\":" +
                                    "\"wheelchair\"," +
                                    "\"1\":\""+"0"+"\"," +
                                    "\"2\":\""+"0"+"\"," +
                                    "\"3\":\""+usr_phone+"\"," +
                                    "\"4\":\""+usr_msg+"\"}");


            String base_url = "https://9433xu.deta.dev/";
            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(ScalarsConverterFactory.create()).build();

            RetrofitApiService service = retrofit.create(RetrofitApiService.class);
            Call<String> response = service.sendUserSMS(body);
            response.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response)
                {

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    pd.dismiss();
                }
            });


            String base_url_1 = "https://fcm.googleapis.com";

            mediaType = MediaType.parse("application/json");
            body = RequestBody.create(mediaType, "{\r\n \"to\" : " +
                    "\""+user_fcm+"\",\r\n " +
                    "\"notification\" : {\r\n     " +
                    "\"body\" : \""+usr_msg+"\",\r\n     " +
                    "\"title\": \"Wheelchairs Confirmed\"\r\n }\r\n}");

            String conType = "application/json";
            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url_1)
                    .addConverterFactory(ScalarsConverterFactory.create()).build();

            service = retrofit.create(RetrofitApiService.class);
            Call<String> response1 = service.sendFcmToken(conType, FCM_AUTH_KEY,
                    body);

            response1.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response)
                {
                    pd.dismiss();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t)
                {

                }
            });

        }
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(
                CoolieBookingActivity.this);
        dialog.setIcon(R.drawable.ic_order_accept);
        dialog.setTitle("Boo-Yah! Your Payment was Successful");
        dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onPaymentError(int i, String s)
    {
        pay_success = false;
        btPay.setVisibility(View.VISIBLE);
        orderCompletionLayout.setVisibility(View.GONE);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(
                CoolieBookingActivity.this);
        dialog.setIcon(R.drawable.ic_order_decline);
        dialog.setTitle("Oops! Payment Failed. Do you wish to try again?");
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                btPay.performClick();
            }
        });
        dialog.setCancelable(false);
        dialog.show();

    }

    public void exitFromHere(View view)
    {
        GoToUserDashboard();
    }

    private void GoToUserDashboard()
    {
        Intent i = new Intent(CoolieBookingActivity.this,
                User_Dashboard.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(0, 0);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        finish();
    }
}
