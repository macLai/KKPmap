package uievolution.viper.samplelaunchee;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.StrictMath;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class  MapController {
    public BaiduMap mBaiduMap;
    public MapView baiduMapView;
    private MainActivity activity;
    private LocationClient mLocClient;
    private PoiSearch poiSearch;
    public MyLocationListenner myListener = new MyLocationListenner();
    private TimerTaskForLongScroll timerTaskForLongScroll = new TimerTaskForLongScroll();
    private Hashtable<String, LatLng> poiLoc = new Hashtable<String, LatLng>() {{
        put("home", new LatLng(31.24353, 121.48338));
        put("work", new LatLng(31.05665, 121.38497));
        put("history1", new LatLng(31.23543, 121.47838));
        put("history2", new LatLng(31.30986, 121.62557));
        put("history3", new LatLng(31.15737, 121.81500));
        put("history4", new LatLng(31.24510, 121.50638));
        put("history5", new LatLng(31.22804, 121.42516));
    }};

    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    private static final String APP_FOLDER_NAME = "KKPmap";
    private String mSDCardPath = null;
    String authinfo = null;

    public MapController(MainActivity main) {
        if(main == null) return;
        activity = main;

        baiduMapView = (MapView) activity.findViewById(R.id.bmapView);

        mBaiduMap = baiduMapView.getMap();
        if(mBaiduMap == null) return;
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setOnMapTouchListener(onMapTouchListener);
        mBaiduMap.setOnMyLocationClickListener(onMyLocationClickListener);
        mBaiduMap.setOnMapStatusChangeListener(onMapStatusChangeListener);
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(18.0f));

        mLocClient = new LocationClient(activity);
        if(mLocClient == null) return;
        mLocClient.registerLocationListener(myListener);

        poiSearch = PoiSearch.newInstance();
        if(mLocClient == null) return;
        poiSearch.setOnGetPoiSearchResultListener(poiSearchListener);

        ///LocationClientOption类用来设置定位SDK的定位方式，
        LocationClientOption option = new LocationClientOption(); //以下是给定位设置参数
        if(option == null) return;
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        if ( initDirs() ) {
            initNavi();
        }
    }

    BaiduMap.OnMapTouchListener onMapTouchListener = new BaiduMap.OnMapTouchListener() {
        public void onTouch(MotionEvent event) {
        }
    };
    BaiduMap.OnMapStatusChangeListener onMapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            if(mBaiduMap == null) return;
            if(mBaiduMap.getMapStatus() == null) return;
            if(mBaiduMap.getMapStatus().target == null) return;
            double latitude = mBaiduMap.getMapStatus().target.latitude;
            latitude = new BigDecimal(latitude).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
            double longitude = mBaiduMap.getMapStatus().target.longitude;
            longitude = new BigDecimal(longitude).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();

            if(mLocClient == null) return;
            if(mLocClient.getLastKnownLocation() == null) return;
            if(latitude != mLocClient.getLastKnownLocation().getLatitude() ||
                    longitude != mLocClient.getLastKnownLocation().getLongitude()) {
                activity.findViewById(R.id.position).setVisibility(View.VISIBLE);
            }
            else {
                activity.findViewById(R.id.position).setVisibility(View.INVISIBLE);
            }
        }
    };
    BaiduMap.OnMyLocationClickListener onMyLocationClickListener = new BaiduMap.OnMyLocationClickListener() {
        public boolean onMyLocationClick(){
            if(mLocClient == null) return true;
            if(mLocClient.getLastKnownLocation() == null) return true;
            if(mBaiduMap == null) return true;
            LatLng ll = new LatLng(mLocClient.getLastKnownLocation().getLatitude(),mLocClient.getLastKnownLocation().getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            activity.findViewById(R.id.position).setVisibility(View.INVISIBLE);
            return true;
        }
    };

    public void poiSearch(String text) {
        if(mLocClient == null) return;
        if(mLocClient.getLastKnownLocation() == null) return;
        if(poiSearch == null) return;
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
        LatLng ll = new LatLng(mLocClient.getLastKnownLocation().getLatitude(),mLocClient.getLastKnownLocation().getLongitude());
        nearbySearchOption.location(ll).keyword(text).radius(5000).pageNum(1);
        poiSearch.searchNearby(nearbySearchOption);
    }


    static public enum MAP_ACTION {
        ACTION_ZOOM_OUT, ACTION_ZOOM_IN, ACTION_SCROLL_UP, ACTION_SCROLL_DOWN, ACTION_SCROLL_LEFT, ACTION_SCROLL_RIGHT, ACTION_LONG_SCROLL_START, ACTION_LONG_SCROLL_END
    }
    public void zoomControl(MAP_ACTION direction) {
        if(mBaiduMap == null) return;
        if ( direction == MAP_ACTION.ACTION_ZOOM_OUT ) {
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomOut());
        }
        if (direction == MAP_ACTION.ACTION_ZOOM_IN) {
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomIn());
        }
    }

    public void scrollControl(MAP_ACTION direction) {
        if(mBaiduMap == null) return;
        if ( direction == MAP_ACTION.ACTION_SCROLL_UP) {
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.scrollBy(0, -100));
        }
        else if ( direction == MAP_ACTION.ACTION_SCROLL_DOWN) {
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.scrollBy(0, 100));
        }
        else if ( direction == MAP_ACTION.ACTION_SCROLL_LEFT) {
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.scrollBy(-100, 0));
        }
        else if ( direction == MAP_ACTION.ACTION_SCROLL_RIGHT) {
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.scrollBy(100, 0));
        }
    }

    class TimerTaskForLongScroll {
        private MAP_ACTION direction;
        private Handler handler = new Handler();
        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                scrollControl(direction);
                handler.postDelayed(runnable, 100);
            }
        };

        public void startTimer(MAP_ACTION direction) {
            this.direction = direction;
            stopTimer();
            runnable.run();
        }

        public void stopTimer() {
            handler.removeCallbacks(runnable);
        }
    }
    public void longScrollControl(MAP_ACTION state, MAP_ACTION direction) {
        if(state == MAP_ACTION.ACTION_LONG_SCROLL_START) {
            timerTaskForLongScroll.startTimer(direction);
        }
        else if (state == MAP_ACTION.ACTION_LONG_SCROLL_END) {
            timerTaskForLongScroll.stopTimer();
        }
    }

    OnGetPoiSearchResultListener poiSearchListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult == null
                    || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
                Toast.makeText(activity, "未找到结果", Toast.LENGTH_LONG).show();
                return;
            }
            if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                int poiSearchListMaxNum = ((LinearLayout)activity.findViewById(R.id.menuSearchLinearLayout)).getChildCount()-1;
                int poiSearchMaxNum = StrictMath.min(poiSearchListMaxNum, poiResult.getAllPoi().size());
                for (int i = 0; i < poiSearchMaxNum; i++) {
                    int id;
                    String ans = "searchans" + i;
                    try {
                        id = activity.getResources().getIdentifier(ans, "id", activity.getPackageName());
                        String name = poiResult.getAllPoi().get(i).name + "\n" + poiResult.getAllPoi().get(i).address;
//                        ((Button)activity.findViewById(id)).setText(name);
                        ((Button)activity.findViewById(id)).setText((Html.fromHtml("<font color=\"#323334\">"
                                + poiResult.getAllPoi().get(i).name + "</font>"+ "<br/>"
                                + "<small> <font color=\"#6D6D6D\">" + poiResult.getAllPoi().get(i).address
                                + "</font> </small>")), TextView.BufferType.SPANNABLE);
                        ((Button)activity.findViewById(id)).setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        Toast.makeText(activity, "发生未知错误", Toast.LENGTH_LONG).show();
                        return;
                    }
                    poiLoc.put(ans, poiResult.getAllPoi().get(i).location);
                }
                for (int i = poiSearchMaxNum; i < poiSearchListMaxNum; i++ ) {
                    int id;
                    String ans = "searchans" + i;
                    try {
                        id = activity.getResources().getIdentifier(ans, "id", activity.getPackageName());
                        ((Button)activity.findViewById(id)).setVisibility(View.GONE);
                    } catch (Exception e) {
                        Toast.makeText(activity, "发生未知错误", Toast.LENGTH_LONG).show();
                        return;
                    }

                }
                activity.mapViewStatus.setStatus(MapViewStatus.MAP_ACTION.ACTION_SEARCH_FINISHED);
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    public boolean setDirectionByPoiList(String direction) {
        if ( poiLoc.containsKey(direction)){
            setDirection( poiLoc.get(direction) );
            return true;
        }
        return false;
    }

    private void setDirection(LatLng direction) {
        if(direction == null) return;
        if(mBaiduMap == null) return;
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(direction);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    public void naviStart() {
        if ( BaiduNaviManager.isNaviInited() ) {
            routeplanToNavi();
        }
    }

    private void routeplanToNavi() {
        if(mLocClient == null) return;
        if(mLocClient.getLastKnownLocation() == null) return;
        if(mBaiduMap == null) return;
        if(mBaiduMap.getMapStatus() == null) return;
        if(mBaiduMap.getMapStatus().target == null) return;
        BNRoutePlanNode sNode = new BNRoutePlanNode(mLocClient.getLastKnownLocation().getLongitude(), mLocClient.getLastKnownLocation().getLatitude(), "1", null, BNRoutePlanNode.CoordinateType.BD09LL);
        BNRoutePlanNode eNode = new BNRoutePlanNode(mBaiduMap.getMapStatus().target.longitude, mBaiduMap.getMapStatus().target.latitude, "1", null, BNRoutePlanNode.CoordinateType.BD09LL);

        List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
        list.add(sNode);
        list.add(eNode);
        BaiduNaviManager.getInstance().launchNavigator(activity, list, 1, true, new DemoRoutePlanListener(sNode));
    }


    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
            Intent intent = new Intent(activity,
                    BNDemoGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE,
                    (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            activity.startActivity(intent);
        }

        @Override
        public void onRoutePlanFailed() {
            //  TODO Auto-generated method stub
            Toast.makeText(activity, "导航启动失败", Toast.LENGTH_LONG).show();
        }
    }
    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if ( mSDCardPath == null ) {
            return false;
        }
        File f = new File( mSDCardPath, APP_FOLDER_NAME);
        if ( !f.exists() ) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    // 导航 初始化引擎
    private void initNavi() {
//      BaiduNaviManager.getInstance().setNativeLibraryPath(
//              mSDCardPath + "/BaiduNaviSDK_SO");
        BaiduNaviManager.getInstance().init(activity, mSDCardPath, APP_FOLDER_NAME,
                new BaiduNaviManager.NaviInitListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                        if (0 == status) {
                        } else {
                            authinfo = "key校验失败, " + msg;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, authinfo, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    public void initSuccess() {
                        BNaviSettingManager
                                .setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
                        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
                        // BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
                        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
                        BNaviSettingManager.setIsAutoQuitWhenArrived(true);

                        Bundle bundle = new Bundle();
                        bundle.putString(BNCommonSettingParam.TTS_APP_ID, "9660018");
                        BNaviSettingManager.setNaviSdkParam(bundle);
                    }

                    public void initStart() {
                    }

                    public void initFailed() {
                        Toast.makeText(activity, "百度导航引擎初始化失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }, null, ttsHandler, ttsPlayStateListener);
    }

    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
//                    Toast.makeText(activity, "Handler : TTS play start", Toast.LENGTH_LONG).show();
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
//                    Toast.makeText(activity, "Handler : TTS play end", Toast.LENGTH_LONG).show();
                    break;
                }
                default:
                    break;
            }
        }
    };

    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
//            Toast.makeText(activity, "TTSPlayStateListener : TTS play end", Toast.LENGTH_LONG).show();
        }

        @Override
        public void playStart() {
//            Toast.makeText(activity, "TTSPlayStateListener : TTS play start", Toast.LENGTH_LONG).show();
        }
    };

    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || baiduMapView == null || mBaiduMap == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (activity.findViewById(R.id.position).getVisibility() != View.VISIBLE) {
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll);//.zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i)
        {

        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
}