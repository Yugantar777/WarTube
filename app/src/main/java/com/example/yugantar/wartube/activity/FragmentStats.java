package com.example.yugantar.wartube.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yugantar.wartube.R;
import com.example.yugantar.wartube.model.Item;
import com.example.yugantar.wartube.model.Model;
import com.example.yugantar.wartube.model.Statistics;
import com.example.yugantar.wartube.rest.ApiClient;
import com.example.yugantar.wartube.rest.ApiInterface;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentStats extends Fragment {
    private String API_KEY = "AIzaSyB6WmcpqekrUkmnhx8wjcERWrWjFr0m-fU";
    TextView textViewpewdiepie, textViewtseries, sub_diff;
    public BarChart barChart;
    public ArrayList<BarEntry> subscribers;
    public BarDataSet barDataSet;
    public BarData barData;
    static String[] s1 = new String[2];
    ApiInterface apiService;
    View view;
    Context context;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = inflater.getContext();
        view = inflater.inflate(R.layout.stats_fragment, null);
        textViewpewdiepie = view.findViewById(R.id.textViewpewdiepie);
        textViewtseries = view.findViewById(R.id.textViewtseries);
        sub_diff = view.findViewById(R.id.sub_diff);
        barChart = view.findViewById(R.id.barChart);
        apiService = ApiClient.getClient().create(ApiInterface.class);


        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

                                  @Override
                                  public void run() {


                                      Call<Model> call = apiService.getSubscribers("statistics", "PewDiePie", API_KEY);


                                      call.enqueue(new Callback<Model>() {
                                          @Override
                                          //response code
                                          public void onResponse(@NonNull Call<Model> call, @NonNull Response<Model> response) {
                                              if (!response.isSuccessful()) {
                                                  textViewpewdiepie.setText("Error");
                                              } else {
                                                  List<Item> items = response.body().getItems();
                                                  for (Item item : items) {
                                                      Statistics statistics = item.getStatistics();
                                                      textViewpewdiepie.setText(statistics.getSubscriberCount().trim());
                                                      s1[0] = statistics.getSubscriberCount().trim();

                                                  }
                                              }


                                          }

                                          @Override
                                          public void onFailure(Call<Model> call, Throwable t) {
                                              // Log error here since request failed
                                          }
                                      });

                                      Call<Model> call2 = apiService.getSubscribers("statistics", "tseries", API_KEY);

                                      call2.enqueue(new Callback<Model>() {
                                          @Override
                                          public void onResponse(Call<Model> call, Response<Model> response) {


                                              if (!response.isSuccessful()) {

                                                  textViewtseries.setText("Error");
                                              } else {
                                                  List<Item> items = response.body().getItems();
                                                  for (Item item : items) {
                                                      Statistics statistics = item.getStatistics();
                                                      textViewtseries.setText(statistics.getSubscriberCount().trim());
                                                      s1[1] = statistics.getSubscriberCount().trim();


                                                      //Difference of subscribers
                                                      Integer[] a = new Integer[2];
                                                      if (s1[0] != null) {

                                                          a[0] = Integer.parseInt(s1[0].trim());
                                                          a[1] = Integer.parseInt(s1[1].trim());

                                                          if (a[0] > a[1]) {
                                                              int b = a[0] - a[1];

                                                              String s = Integer.toString(b);
                                                              sub_diff.setText(s);
                                                          } else {
                                                              int b = a[1] - a[0];
                                                              String s = Integer.toString(b);
                                                              sub_diff.setText(s);
                                                          }
                                                      }


                                                  }

                                              }


                                          }

                                          @Override
                                          public void onFailure(Call<Model> call, Throwable t) {
                                          }
                                      });

                                      if (s1[0] != null && s1[1] != null) {
                                          subscribers = new ArrayList<>();
                                          barChart.setDescription(null);
                                          subscribers.add(new BarEntry(0, Float.parseFloat(s1[1]) + 0));
                                          subscribers.add(new BarEntry(1, Float.parseFloat(s1[0]) + 0));
                                          barDataSet = new BarDataSet(subscribers, "Subscribers");
                                          barDataSet.setColors(new int[]{R.color.colorPrimaryDark, R.color.colorPewdiepie}, context);
                                          barData = new BarData(barDataSet);
                                          try {
                                              barChart.invalidate();
                                          }
                                          catch (Exception e){

                                          }

                                          barChart.setData(barData);
                                      } else {
                                          //Default Bar Chart
                                          subscribers = new ArrayList<>();
                                          barChart.setDescription(null);
                                          subscribers.add(new BarEntry(0, 80083443));
                                          subscribers.add(new BarEntry(1, 80587809));
                                          barDataSet = new BarDataSet(subscribers, "Subscribers");
                                          barDataSet.setColors(new int[]{R.color.colorPrimaryDark, R.color.colorPewdiepie}, context);
                                          barData = new BarData(barDataSet);
                                          try {
                                              barChart.invalidate();
                                          }
                                          catch (Exception e){

                                          }
                                          barChart.setData(barData);
                                      }

                                  }

                              },
//Set how long before to start calling the TimerTask (in milliseconds)
                0,
//Set the amount of time between each execution (in milliseconds)
                1000);


        return view;


    }


}







