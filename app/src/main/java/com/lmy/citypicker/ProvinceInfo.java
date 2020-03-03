package com.lmy.citypicker;

import java.util.ArrayList;
import java.util.List;

public class ProvinceInfo {

    public static final String PROVINCE = "province";
    public static final String HOTS = "hots";
    public static final String OTHERS = "others";

    String province;
    List<CityInfo> hots = new ArrayList<>();
    List<CityInfo> others = new ArrayList<>();
}
