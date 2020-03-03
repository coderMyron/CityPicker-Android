package com.lmy.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lmy.citypicker.CityInfo;
import com.lmy.citypicker.CityPickerHelper;
import com.lmy.citypicker.R;

public class MainActivity extends AppCompatActivity {

    private TextView tvCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCity = findViewById(R.id.tv_city);
        Button btnCity = findViewById(R.id.btn_city);
        btnCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CityPickerHelper.getInstance(MainActivity.this).showCitys(new CityPickerHelper.CityCallBack() {
                    @Override
                    public void onChooseCity(CityInfo cityInfo) {
                        Log.d("city","province:" + cityInfo.province + ",city:" + cityInfo.city);
                        tvCity.setText(cityInfo.province + cityInfo.city);
                    }
                });
            }
        });
    }
}
