package uievolution.viper.samplelaunchee;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.ViewGroup;

import uie.multiaccess.app.UMAApplication;
import uie.multiaccess.input.UMAHIDInputEventListener;
import uie.multiaccess.input.UMASensorEvent;
import uie.multiaccess.view.UMAFocusManager;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.BaiduMap;

public class MainActivity extends Activity implements UMAHIDInputEventListener {

//    private UMAApplication umaApplication = UMAApplication.INSTANCE;
    private BaiduMap mBaiduMap;
    private MapView baiduMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
//        umaApplication.create(this);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//

        baiduMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = baiduMapView.getMap();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        umaApplication.start(this);
    }

    @Override
    protected void onResume() {
        baiduMapView.onResume();
        super.onResume();
//        umaApplication.resume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
//        umaApplication.pause(this);
        baiduMapView.onPause();
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
        baiduMapView.onDestroy();
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
