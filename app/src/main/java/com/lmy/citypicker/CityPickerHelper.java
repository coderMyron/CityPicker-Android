package com.lmy.citypicker;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CityPickerHelper implements AMapLocationListener
{
    static PopupWindow mWindow;
    private static String TAG = "city";
    private List<ProvinceInfo> provinceList = new ArrayList<>();
    //声明mLocationOption对象
    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient;
    //热门城市
    private MyGridView gvHotCitys;
    private List<CityInfo> hotCitys = new ArrayList<>();
    private CitysAdapter hotCitysAdapter;

    //其它城市
    private MyGridView gvOtherCitys;
    private List<CityInfo> otherCitys = new ArrayList<>();
    private CitysAdapter otherCitysAdapter;
    private static Context mContext;
    private CityInfo currentCity;
    private TextView tvCurrentCity;
    private TextView tvOtherTitle;
    private EditText etCity;
    private LinearLayout llCitys;//json布局的城市
    private List<CityInfo> searchCitys = new ArrayList<>();
    private ListView lvSearch;
    private SearchCitysAdapter searchCitysAdapter;


    private static CityPickerHelper instance = null;
    private CityPickerHelper(){}
    public static synchronized CityPickerHelper getInstance(Context context){
        mContext = context;
        if (instance == null) {
            instance = new CityPickerHelper();
        }
        return instance;
    }

    public void showCitys(final CityCallBack cityCallBack)
    {
        View outerView = LayoutInflater.from(mContext).inflate(R.layout.layout_citys, null);
        mlocationClient = new AMapLocationClient(mContext);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置返回地址信息，默认为true
        mLocationOption.setNeedAddress(true);
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();

        String json = getJson("city.json",mContext);
        if (json != null && !json.equals("")) {
            try {
                provinceList.clear();
                JSONArray array = new JSONArray(json);
                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        if (object != null) {
                            ProvinceInfo province = new ProvinceInfo();
                            JSONObject provineceObject = object.optJSONObject(ProvinceInfo.PROVINCE);
                            if (provineceObject != null) {
                                province.name = provineceObject.optString(ProvinceInfo.SIMPLE);
                                province.fullName = provineceObject.optString(ProvinceInfo.FULL);
                            }
                            JSONArray hotsArray = object.optJSONArray(ProvinceInfo.HOTS);
                            if (hotsArray != null) {
                                List<CityInfo> hotsList = new ArrayList<>();
                                for (int j = 0; j < hotsArray.length(); j++) {
                                    JSONObject cityObject = hotsArray.optJSONObject(j);
                                    if (cityObject != null) {
                                        String hot = cityObject.optString(ProvinceInfo.SIMPLE);
                                        String hotFull = cityObject.optString(ProvinceInfo.FULL);
                                        if (hotFull != null) {
                                            CityInfo cityInfo = new CityInfo();
                                            if (i == 0) {
                                                String[] name = hotFull.split("-");
                                                if (name.length == 2) {
                                                    cityInfo.cityFullName = name[1];
                                                    cityInfo.provinceFullName = name[0];
                                                }else {
                                                    cityInfo.cityFullName = hotFull;
                                                    cityInfo.provinceFullName = "";
                                                }
                                                cityInfo.city = hot;
                                            }else {
                                                cityInfo.city = hot;
                                                cityInfo.cityFullName = hotFull;
                                                if (!province.name.equals("香港") &&
                                                        !province.name.equals("澳门")
                                                        && !province.name.equals("台湾")){
                                                    cityInfo.provinceFullName = province.fullName;
                                                }

                                            }
                                            hotsList.add(cityInfo);
                                        }
                                    }
                                }
                                province.hots = hotsList;
                            }

                            JSONArray othersArray = object.optJSONArray(ProvinceInfo.OTHERS);
                            if (othersArray != null) {
                                List<CityInfo> othersList = new ArrayList<>();
                                for (int j = 0; j < othersArray.length(); j++) {
                                    JSONObject otherObject = othersArray.optJSONObject(j);
                                    if (otherObject != null) {
                                        String other = otherObject.optString(ProvinceInfo.SIMPLE);
                                        String fullName = otherObject.optString(ProvinceInfo.FULL);
                                        CityInfo cityInfo = new CityInfo();
                                        if (i != 0){
                                            cityInfo.provinceFullName = province.fullName;
                                        }
                                        cityInfo.city = other;
                                        cityInfo.cityFullName = fullName;
                                        othersList.add(cityInfo);
                                    }
                                }
                                province.others = othersList;
                            }
                            provinceList.add(province);

                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG,json);
        Log.d(TAG,"size "+provinceList.size());



        RecyclerView recyclerView = outerView.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        ProvinceAdapter adapter = new ProvinceAdapter(provinceList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ProvinceAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Log.d(TAG, "recycle " + position);
                chooseProvince(position);

            }
        });

        if (provinceList.size() > 0) {
            hotCitys = provinceList.get(0).hots;
        }
        gvHotCitys = outerView.findViewById(R.id.gv_hot_citys);
        hotCitysAdapter = new CitysAdapter(mContext,hotCitys);
        gvHotCitys.setAdapter(hotCitysAdapter);
        gvHotCitys.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "hot city " + position + "-" + hotCitys.get(position));
                if (cityCallBack != null) {
                    cityCallBack.onChooseCity(hotCitys.get(position));
                    hideWindow();
                }
            }
        });

        if (provinceList.size() > 0) {
            otherCitys = provinceList.get(0).others;
        }
        gvOtherCitys = outerView.findViewById(R.id.gv_other_citys);
        otherCitysAdapter = new CitysAdapter(mContext,otherCitys);
        gvOtherCitys.setAdapter(otherCitysAdapter);
        gvOtherCitys.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "other city " + position + "-" + otherCitys.get(position));
                if (cityCallBack != null) {
                    cityCallBack.onChooseCity(otherCitys.get(position));
                    hideWindow();
                }
            }
        });

        tvCurrentCity = outerView.findViewById(R.id.tv_current_city);
        if (currentCity != null && currentCity.city != null) {
            tvCurrentCity.setVisibility(View.VISIBLE);
            tvCurrentCity.setText(currentCity.city);
        }else {
            tvCurrentCity.setVisibility(View.GONE);
        }
        tvCurrentCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cityCallBack != null && currentCity != null) {
                    cityCallBack.onChooseCity(currentCity);
                    hideWindow();
                }
            }
        });
        tvOtherTitle = outerView.findViewById(R.id.tv_other_title);
        tvOtherTitle.setText("直辖市");

        llCitys = outerView.findViewById(R.id.ll_citys);
        lvSearch = outerView.findViewById(R.id.lv_citys);
        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (cityCallBack != null) {
                    currentCity = searchCitys.get(i);
                    cityCallBack.onChooseCity(currentCity);
                    hideWindow();
                }
            }
        });
        searchCitysAdapter = new SearchCitysAdapter(mContext,searchCitys);
        lvSearch.setAdapter(searchCitysAdapter);
        etCity = outerView.findViewById(R.id.et_city);
        Button btnSearch = outerView.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etCity.getText().toString().equals("")){
                    llCitys.setVisibility(View.VISIBLE);
                    lvSearch.setVisibility(View.GONE);
                }else {
                    searchCitys.clear();
                    for (int i = 0; i < provinceList.size(); i++) {
                        ProvinceInfo provinceInfo = provinceList.get(i);
                        for (int j = 0; j < provinceInfo.hots.size(); j++) {
                            CityInfo cityInfo = provinceInfo.hots.get(j);
                            if (cityInfo.city.contains(etCity.getText().toString())){
                                searchCitys.add(cityInfo);
                            }
                        }
                        for (int j = 0; j < provinceInfo.others.size(); j++) {
                            CityInfo cityInfo = provinceInfo.others.get(j);
                            if (cityInfo.city.contains(etCity.getText().toString())){
                                searchCitys.add(cityInfo);
                            }
                        }
                    }
                    searchCitysAdapter.setCitys(searchCitys);
                    llCitys.setVisibility(View.GONE);
                    lvSearch.setVisibility(View.VISIBLE);
                }

            }
        });


        mWindow = new PopupWindow(
                outerView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mWindow.setAnimationStyle(R.style.WindowAnimation);
        mWindow.setFocusable(true);

        if (mWindow.isShowing()) return;
        mWindow.setOutsideTouchable(false);
        Window window = ((Activity) mContext).getWindow();
        if (window == null) return;
        final View decorView = window.getDecorView();
        if (decorView == null) return;
        if (!isActivityRunning(mContext)) return;
        decorView.post(new Runnable() {
            @Override
            public void run() {
                mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                mWindow.showAtLocation(decorView, Gravity.BOTTOM, 0, 0);
            }
        });


        TextView btnCanel = outerView.findViewById(R.id.tv_cancel);
        btnCanel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                hideWindow();
            }
        });

    }

    private void chooseProvince(int position){
        hotCitys = provinceList.get(position).hots;
        hotCitysAdapter.setCitys(hotCitys);
        otherCitys = provinceList.get(position).others;
        otherCitysAdapter.setCitys(otherCitys);
        if (position == 0) {
            tvOtherTitle.setText("直辖市");
        }else {
            tvOtherTitle.setText("其他城市");
        }
    }

    public static String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static void hideWindow()
    {
        if (mWindow != null && mWindow.isShowing()) {
            mWindow.dismiss();
            mWindow = null;
        }
    }

    private static boolean isActivityRunning(Context context)
    {
        if (context == null) return false;
        if (context instanceof Activity) {
            return !((Activity) context).isFinishing();
        } else {
            return false;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //aMapLocation.getCountry();//国家信息
                String province = aMapLocation.getProvince();//省信息
                String city = aMapLocation.getCity();//城市信息

                if (city != null && !city.equals("")) {
                    mlocationClient.stopLocation();
                    currentCity = new CityInfo();
                    currentCity.provinceFullName = province;
                    currentCity.cityFullName = city;
                    String simple  = city.replace("市","");
                    simple  = simple.replace("自治州","");
                    currentCity.city = simple;
                    tvCurrentCity.setVisibility(View.VISIBLE);
                    tvCurrentCity.setText(currentCity.city);
                }else {
                    tvCurrentCity.setVisibility(View.GONE);
                }
                Log.d(TAG, "location " + aMapLocation.toString());
                Log.d(TAG, "location " + city);
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e(TAG,"location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    public interface CityCallBack {
        void onChooseCity(CityInfo cityInfo);
    }
}
