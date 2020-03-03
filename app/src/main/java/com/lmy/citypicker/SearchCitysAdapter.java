package com.lmy.citypicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aaa on 2016/6/12.
 */
public class SearchCitysAdapter extends BaseAdapter
{
    Context context;
    List<CityInfo> citys;
    public SearchCitysAdapter(Context context){
        this(context,new ArrayList<CityInfo>());
    }
    public SearchCitysAdapter(Context context, List<CityInfo> citys){
        this.citys=citys;
        this.context=context;
    }

    public void setCitys(List<CityInfo> citys) {
        this.citys = citys;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return citys.size();
    }

    @Override
    public Object getItem(int position) {
        return citys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.search_city_item,null);
            holder.tv_name=(TextView)convertView.findViewById(R.id.tv_city);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        CityInfo cityInfo = citys.get(position);
        holder.tv_name.setText(cityInfo.city);

        return convertView;
    }
    class ViewHolder{
        TextView tv_name;
    }
}
