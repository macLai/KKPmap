package uievolution.viper.samplelaunchee;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.support.design.widget.TextInputLayout;
import android.view.ViewGroup;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;

import uie.multiaccess.input.UMAHIDManager;
import uie.multiaccess.app.UMAApplication;
import uie.multiaccess.input.UMAHIDInputEventListener;
import uie.multiaccess.input.UMASensorEvent;
import uie.multiaccess.input.UMAHIDConstants;
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

    private UMAApplication umaApplication = UMAApplication.INSTANCE;
    public MapController mapController = null;
    public MapViewStatus mapViewStatus = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        umaApplication.create(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        UMAHIDManager umahidManager = (UMAHIDManager)umaApplication.getUMAService(UMAApplication.UMA_HID_SERVICE);
        umahidManager.startDiscoverDevice(null);

        mapController = new MapController(this);
        mapViewStatus = new MapViewStatus(this);

        findViewById(R.id.editTextBox).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    mapViewStatus.setStatus(MapViewStatus.MAP_ACTION.ACTION_INPUTBOX_TOUCH);
                return false;
            }
        });
        findViewById(R.id.pos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapController.onMyLocationClickListener.onMyLocationClick();
            }
        });
        ((TextInputLayout)findViewById(R.id.textInputLayout)).getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId==EditorInfo.IME_ACTION_DONE )   )
                {
                    // poi serach start
                    mapController.poiSearch(v.getText().toString());
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;

            }
        });
        findViewById(R.id.position).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mapController.naviStart();
            }
        });
        findViewById(R.id.home).setOnClickListener(onRegularPoiClickListener);
        findViewById(R.id.work).setOnClickListener(onRegularPoiClickListener);
        findViewById(R.id.history1).setOnClickListener(onRegularPoiClickListener);
        findViewById(R.id.history2).setOnClickListener(onRegularPoiClickListener);
        findViewById(R.id.history3).setOnClickListener(onRegularPoiClickListener);
        findViewById(R.id.history4).setOnClickListener(onRegularPoiClickListener);
        findViewById(R.id.history5).setOnClickListener(onRegularPoiClickListener);
        findViewById(R.id.searchans0).setOnClickListener(onRegularPoiClickListener);
        findViewById(R.id.searchans1).setOnClickListener(onRegularPoiClickListener);
        findViewById(R.id.searchans2).setOnClickListener(onRegularPoiClickListener);
        findViewById(R.id.searchans3).setOnClickListener(onRegularPoiClickListener);
        findViewById(R.id.searchans4).setOnClickListener(onRegularPoiClickListener);
        findViewById(R.id.food).setOnClickListener(onHotSpotPoiClickListener);
        findViewById(R.id.hotel).setOnClickListener(onHotSpotPoiClickListener);
        findViewById(R.id.bank).setOnClickListener(onHotSpotPoiClickListener);
        findViewById(R.id.supermarket).setOnClickListener(onHotSpotPoiClickListener);
        findViewById(R.id.bus).setOnClickListener(onHotSpotPoiClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        umaApplication.start(this);
    }

    @Override
    protected void onResume() {
        mapController.baiduMapView.onResume();
        super.onResume();
        umaApplication.resume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        umaApplication.pause(this);
        mapController.baiduMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        umaApplication.stop(this);
    }

    @Override
    protected void onDestroy() {
        umaApplication.destroy(this);
        super.onDestroy();
        mapController.baiduMapView.onDestroy();
    }

    @Override
    public boolean onRotate(int distance, int direction) {
        MapViewStatus.MAPVIEWSTATUS status = mapViewStatus.getMapViewStatus();
        if (status != MapViewStatus.MAPVIEWSTATUS.STATUS_CURRENT_POS &&
                status != MapViewStatus.MAPVIEWSTATUS.STATUS_SET_POS) {
            return false;
        }
        if ( direction == UMAHIDConstants.ROTATE_CLOCKWISE) {
            mapController.zoomControl(MapController.MAP_ACTION.ACTION_ZOOM_OUT);
        }
        else
        {
            mapController.zoomControl(MapController.MAP_ACTION.ACTION_ZOOM_IN);
        }
        return true;
    }

    @Override
    public boolean onTranslate(int distanceX, int distanceY) {
        MapViewStatus.MAPVIEWSTATUS status = mapViewStatus.getMapViewStatus();
        if (status != MapViewStatus.MAPVIEWSTATUS.STATUS_CURRENT_POS &&
                status != MapViewStatus.MAPVIEWSTATUS.STATUS_SET_POS) {
            return false;
        }
        MapController.MAP_ACTION action;
        if ( distanceX > 0) {
            action = MapController.MAP_ACTION.ACTION_SCROLL_RIGHT;
        }
        else if (distanceX < 0) {
            action = MapController.MAP_ACTION.ACTION_SCROLL_LEFT;
        }
        else if (distanceY > 0) {
            action = MapController.MAP_ACTION.ACTION_SCROLL_DOWN;
        }
        else if (distanceY < 0) {
            action = MapController.MAP_ACTION.ACTION_SCROLL_UP;
        }
        else {
            return false;
        }
        mapController.scrollControl(action);

        return true;
    }

    @Override
    public boolean onPressUpButton(int button) {
        if( button == UMAHIDConstants.BUTTON_HOME) {
            mapController.onMyLocationClickListener.onMyLocationClick();
            mapViewStatus.setStatus(MapViewStatus.MAP_ACTION.ACTION_HOME_TOUCH);
            return true;
        }
        else if( button == UMAHIDConstants.BUTTON_BACK) {
            mapViewStatus.setStatus(MapViewStatus.MAP_ACTION.ACTION_BACK_TOUCH);
            return true;
        }
        else if( button == UMAHIDConstants.BUTTON_VR) {
            mapViewStatus.setStatus(MapViewStatus.MAP_ACTION.ACTION_INPUTBOX_TOUCH);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPressDownButton(int button) {
        return false;
    }

    @Override
    public void onDoubleClickButton(int button) {
        Toast.makeText(this, "onDoubleClickButton", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongPressButton(int button) {
    }

    @Override
    public void onLongPressButton(int button, int state) {
        MapViewStatus.MAPVIEWSTATUS status = mapViewStatus.getMapViewStatus();
        if (status != MapViewStatus.MAPVIEWSTATUS.STATUS_CURRENT_POS &&
                status != MapViewStatus.MAPVIEWSTATUS.STATUS_SET_POS) {
            return;
        }
        if ( state == UMAHIDConstants.GESTURE_STATE_BEGAN ) {
            if (button == UMAHIDConstants.BUTTON_LEFT) {
                mapController.longScrollControl(MapController.MAP_ACTION.ACTION_LONG_SCROLL_START, MapController.MAP_ACTION.ACTION_SCROLL_LEFT);
            }
            else if (button == UMAHIDConstants.BUTTON_RIGHT) {
                mapController.longScrollControl(MapController.MAP_ACTION.ACTION_LONG_SCROLL_START, MapController.MAP_ACTION.ACTION_SCROLL_RIGHT);
            }
            else if (button == UMAHIDConstants.BUTTON_UP) {
                mapController.longScrollControl(MapController.MAP_ACTION.ACTION_LONG_SCROLL_START, MapController.MAP_ACTION.ACTION_SCROLL_UP);
            }
            else if (button == UMAHIDConstants.BUTTON_DOWN) {
                mapController.longScrollControl(MapController.MAP_ACTION.ACTION_LONG_SCROLL_START, MapController.MAP_ACTION.ACTION_SCROLL_DOWN);
            }
        }
        else if ( state == UMAHIDConstants.GESTURE_STATE_ENDED ) {
            if (button == UMAHIDConstants.BUTTON_LEFT) {
                mapController.longScrollControl(MapController.MAP_ACTION.ACTION_LONG_SCROLL_END, MapController.MAP_ACTION.ACTION_SCROLL_LEFT);
            }
            else if (button == UMAHIDConstants.BUTTON_RIGHT) {
                mapController.longScrollControl(MapController.MAP_ACTION.ACTION_LONG_SCROLL_END, MapController.MAP_ACTION.ACTION_SCROLL_RIGHT);
            }
            else if (button == UMAHIDConstants.BUTTON_UP) {
                mapController.longScrollControl(MapController.MAP_ACTION.ACTION_LONG_SCROLL_END, MapController.MAP_ACTION.ACTION_SCROLL_UP);
            }
            else if (button == UMAHIDConstants.BUTTON_DOWN) {
                mapController.longScrollControl(MapController.MAP_ACTION.ACTION_LONG_SCROLL_END, MapController.MAP_ACTION.ACTION_SCROLL_DOWN);
            }
        }

    }

    @Override
    public void onAccelerometerUpdate(UMASensorEvent sensor) {

    }

    @Override
    public void onBackPressed() {
        if ( !mapViewStatus.setStatus(MapViewStatus.MAP_ACTION.ACTION_BACK_TOUCH))
            super.onBackPressed();

    }

    public void focusControl() {
        MapViewStatus.MAPVIEWSTATUS status = mapViewStatus.getMapViewStatus();
        UMAFocusManager focusManager = umaApplication.getFocusManager();
        if ( status == MapViewStatus.MAPVIEWSTATUS.STATUS_MENU ) {
            focusManager.setFocusRoot((ViewGroup) findViewById(R.id.menuLinearLayout));
            focusManager.requestFocusForView(findViewById(R.id.home));
            focusManager.setVisibility(0);
        }
        else if ( status == MapViewStatus.MAPVIEWSTATUS.STATUS_CURRENT_POS ||
                status == MapViewStatus.MAPVIEWSTATUS.STATUS_SET_POS) {
            focusManager.setVisibility(8);
        }
        else if (status == MapViewStatus.MAPVIEWSTATUS.STATUS_MENU_SEARCH) {
            focusManager.setFocusRoot((ViewGroup) findViewById(R.id.menuSearchLinearLayout));
            focusManager.requestFocusForView(findViewById(R.id.searchans0));
            focusManager.setVisibility(0);
        }
    }

    OnClickListener onRegularPoiClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mapController.setDirectionByPoiList(view.getResources().getResourceEntryName(view.getId()))) {
                mapViewStatus.setStatus(MapViewStatus.MAP_ACTION.ACTION_SEARCHANS_TOUCH);
            }

        }
    };

    OnClickListener onHotSpotPoiClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            mapController.poiSearch(((Button)view).getText().toString());
        }
    };



}
