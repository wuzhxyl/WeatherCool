package uimodel;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hlkhjk_ok.weathercool.R;

import java.util.List;

import model.WeatherInfo;
import util.HttpUtil;

/**
 * Created by hlkhjk_ok on 17/4/14.
 */

public class WeatherAdapater extends ArrayAdapter<WeatherInfo> {
    private int resourceId;

    private NotifyChangeListener chglistener;

    public WeatherAdapater(Context context, int resourceId, List<WeatherInfo> objects, NotifyChangeListener listener) {
        super(context, resourceId, objects);
        this.resourceId = resourceId;
        this.chglistener = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        WeatherInfo weather = getItem(position);

        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);

            holder = new ViewHolder();
            holder.bgfile = (ImageView) view.findViewById(R.id.imgview);
            holder.temp = (TextView) view.findViewById(R.id.temp);
            holder.humi = (TextView) view.findViewById(R.id.humi);
            holder.cityname = (TextView) view.findViewById(R.id.loc);
            holder.PM25 = (TextView) view.findViewById(R.id.pm);
            holder.delBtn = (ImageView) view.findViewById(R.id.delBtn);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.humi.setText((int)((double)weather.getHumi()*100) + " %");
        holder.temp.setText(Double.toString((double) weather.getTemp()));
        holder.PM25.setText((int)weather.getPm() + " ug/m³");
        holder.cityname.setText(weather.getCityname());
        holder.bgfile.setImageResource(WeatherInfo.getWeatherIcon(weather.getImage()));
        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chglistener.change(position);
            }
        });
        return view;
    }

    class ViewHolder {
        ImageView bgfile;
        TextView humi;
        TextView temp;
        TextView cityname;
        TextView PM25;
        ImageView delBtn;
    }
}
