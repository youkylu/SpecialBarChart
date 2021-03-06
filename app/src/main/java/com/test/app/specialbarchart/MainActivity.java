package com.test.app.specialbarchart;

import android.graphics.Color;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.test.app.specialbarchart.SpecialBarChart.OnChartClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Integer> paintColor = new ArrayList<>();
    private List<HashMap<String, Integer>> datas = new ArrayList<>();
    private SpecialBarChart histogramCharView;
    private List<Integer> listHigh =  new ArrayList<>();
    private  List<Integer> listWidth = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 画笔颜色的数组
        paintColor.add(Color.rgb(39, 32, 162));
        paintColor.add(Color.rgb(58, 169, 214));
        paintColor.add(Color.rgb(242, 180, 47));


//        HashMap<String, Integer> map1 = new HashMap<>();
//        map1.put("high", 1);
//        map1.put("width", 30);
//        datas.add(map1);
//
//
//        HashMap<String, Integer> map2 = new HashMap<>();
//        map2.put("high", 2);
//        map2.put("width", 40);
//        datas.add(map2);
//
//
//        HashMap<String, Integer> map3 = new HashMap<>();
//        map3.put("high", 3);
//        map3.put("width", 10);
//        datas.add(map3);
//
//        HashMap<String, Integer> map4 = new HashMap<>();
//        map4.put("high", 2);
//        map4.put("width", 60);
//        datas.add(map4);

        listHigh.add(1);
        listHigh.add(2);
        listHigh.add(3);
        listHigh.add(2);
        listHigh.add(1);
        listWidth.add(30);
        listWidth.add(40);
        listWidth.add(10);
        listWidth.add(60);
        listWidth.add(5);

        histogramCharView = (SpecialBarChart) findViewById(R.id.specialBarChart);
        histogramCharView.setData(listHigh,listWidth , paintColor);

        histogramCharView.setOnChartClickListener(new OnChartClickListener() {
            @Override
            public void onClick(int num) {
                histogramCharView.setText("测试12", "深睡");
//                Toast.makeText(getApplication(), datas.get(num).get("high")+"-"+datas.get(num).get("width"), Toast.LENGTH_SHORT).show();
//                Log.d("测试", datas.get(num).get("high")+"");
//                Log.d("测试", datas.get(num).get("width")+"");
            }
        });
    }
}
