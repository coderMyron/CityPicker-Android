package com.lmy.citypicker;

import java.util.ArrayList;
import java.util.List;

public class ProvinceInfo {

    public static final String PROVINCE = "province";
    public static final String HOTS = "hots";
    public static final String OTHERS = "others";
    public static final String FULL = "full";
    public static final String SIMPLE = "simple";

    String name;
    String fullName;
    List<CityInfo> hots = new ArrayList<>();
    List<CityInfo> others = new ArrayList<>();
}
