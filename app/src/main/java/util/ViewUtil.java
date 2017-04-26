package util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hlkhjk_ok.weathercool.MainActivity;
import com.example.hlkhjk_ok.weathercool.MyApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by hlkhjk_ok on 17/4/7.
 */


public class ViewUtil {
    private static String SPLASH_KEY = "com.example.hlkhjk_ok.weathercool.SplashKey";

    /*
    * EditView失去焦点时，隐藏键盘.
    *
    * */
    public static boolean isShouldHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top  = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();

            return !(ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom);
        }

        return false;
    }


    private static String[] date = new String[24];//X轴的标注
    private static int[] score = new int[24];//图表的数据点
    private static List<PointValue> mPointValues = new ArrayList<PointValue>();
    private static List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();

    public static void showForcastView(LineChartView chartview) {
        getAxisXLables();
        getAxisPoints();
        initLineChart(chartview);
    }

    /**
     * 设置X 轴的显示
     */
    private static void getAxisXLables() {
        for (int i=0; i<date.length; i++) {
            date[i] = Integer.toString(i);
        }

        for (int i = 0; i < date.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(date[i]));
        }
    }

    /**
     * 图表的每个点的显示
     */
    private static void getAxisPoints() {
        for (int i=0; i<24;i++) {
            score[i] = (int)(24*Math.random());
        }

        for (int i = 0; i < score.length; i++) {
            mPointValues.add(new PointValue(i, (float)score[i]));
        }
    }

    private static void initLineChart(LineChartView lineChart) {
        Line line = new Line(mPointValues).setColor(Color.parseColor("#000000"));  //折线的颜色（橙色）
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.DIAMOND);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(true);//曲线是否平滑，即是曲线还是折线
        line.setStrokeWidth(1); //设置线宽度
        line.setFilled(true);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.GRAY);  //设置字体颜色
        axisX.setName("date");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        axisX.setMaxLabelChars(24); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        axisY.setName("");//y轴标注
        axisY.setTextSize(10);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边


        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 2);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
        /**注：下面的7，10只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         */
        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        v.right = 7;
        lineChart.setCurrentViewport(v);
    }

    /*
    *  天气图片信息解析.
    *  天气对应信息
    *  file, weather
    * */

    public static Bitmap getAssertsBitmap(String path) {
        AssetManager am = MyApplication.getContext().getResources().getAssets();
        Bitmap image = null;
        try {
            InputStream in = am.open(path);
            image = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public static void setFullScreen(AppCompatActivity mContext) {
        mContext.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = mContext.getSupportActionBar();
            if (actionBar != null) actionBar.hide();
        }
    }

    /*
    *
    * 需另加判断versionCode来确定是否是覆盖安装.
    *
    * */

    public static boolean getSplashVal() {
        boolean result = false;
        Context context = MyApplication.getContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences(SPLASH_KEY, context.MODE_PRIVATE);
        result = sharedPreferences.getBoolean("hasStart", false);

        return result;
    }

    public static void setSplashVal(boolean val) {
        Context context = MyApplication.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SPLASH_KEY, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("hasStart", val);
        editor.commit();
    }

    public static boolean checkpermission(String permissionKey) {
        Context context = MyApplication.getContext();
        PackageManager pm = context.getPackageManager();
        return (PackageManager.PERMISSION_GRANTED == pm.checkPermission(permissionKey, context.getPackageName()));
    }

    //    权限审核.
    public String[] checkPermissions(final String[] permissions) {
        List<String> list = new ArrayList<String>();

        String[] lastPermissions = null;
        for (String perm:permissions) {
            if (ContextCompat.checkSelfPermission(MyApplication.getContext(), perm) != PackageManager.PERMISSION_GRANTED) {
                list.add(perm);
            }
        }

        int size = list.size();
        lastPermissions = new String[size];

        for(int i=0; i<size; i++) {
            lastPermissions[i] = list.get(i);
        }
        return lastPermissions;
    }

}
