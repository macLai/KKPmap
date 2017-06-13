package uievolution.viper.samplelaunchee;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.ViewGroup;
import android.os.Environment;

import uie.multiaccess.app.UMAApplication;
import uie.multiaccess.input.UMAHIDInputEventListener;
import uie.multiaccess.input.UMASensorEvent;
import uie.multiaccess.view.UMAFocusManager;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.BaiduMap;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import java.io.File;
import com.baidu.mapapi.map.MyLocationData;

public class MainActivity extends Activity implements UMAHIDInputEventListener {

//    private UMAApplication umaApplication = UMAApplication.INSTANCE;
    private MapController mapController = null;
    private MapViewStatus mapViewStatus = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
//        umaApplication.create(this);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mapController = new MapController(this);
        mapViewStatus = new MapViewStatus(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
//        umaApplication.start(this);
    }

    @Override
    protected void onResume() {
        mapController.baiduMapView.onResume();
        super.onResume();
//        umaApplication.resume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
//        umaApplication.pause(this);
        mapController.baiduMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        umaApplication.stop(this);
    }

    @Override
    protected void onDestroy() {
//        umaApplication.destroy(this);
        super.onDestroy();
        mapController.baiduMapView.onDestroy();
    }

    @Override
    public boolean onRotate(int distance, int direction) {
        return false;
    }

    @Override
    public boolean onTranslate(int distanceX, int distanceY) {
        return false;
    }

    @Override
    public boolean onPressUpButton(int button) {
        return false;
    }

    @Override
    public boolean onPressDownButton(int button) {
        return false;
    }

    @Override
    public void onDoubleClickButton(int button) {

    }

    @Override
    public void onLongPressButton(int button) {

    }

    @Override
    public void onLongPressButton(int button, int state) {

    }

    @Override
    public void onAccelerometerUpdate(UMASensorEvent sensor) {

    }



}
