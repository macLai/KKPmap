package uievolution.viper.samplelaunchee;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRouteGuideManager.CustomizedLayerItem;
import com.baidu.navisdk.adapter.BNRouteGuideManager.OnNavigationListener;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviBaseCallbackModel;
import com.baidu.navisdk.adapter.BaiduNaviCommonModule;
import com.baidu.navisdk.adapter.NaviModuleFactory;
import com.baidu.navisdk.adapter.NaviModuleImpl;
//import com.baidu.panosdk.plugin.indoor.R;

public class BNDemoGuideActivity extends Activity {

    private BNRoutePlanNode mBNRoutePlanNode = null;
    private BaiduNaviCommonModule mBaiduNaviCommonModule = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createHandler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {}
//        View view = BNRouteGuideManager.getInstance().onCreate(this, new OnNavigationListener() {
//
//            @Override
//            public void onNaviGuideEnd() {
//                finish();
//
//            }
//
//            @Override
//            public void notifyOtherAction(int actionType, int arg1, int arg2, Object obj) {
//                if (actionType == 0) {
//                    Toast.makeText(BNDemoGuideActivity.this, "到达目的地",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        View view = BNRouteGuideManager.getInstance().onCreate(this,mOnNavigationListener);

        if ( view != null ) {
            setContentView(view);
        }

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mBNRoutePlanNode = (BNRoutePlanNode) bundle.getSerializable(MapController.ROUTE_PLAN_NODE);
            }
        }
    }

    @Override
    protected void onResume() {
        BNRouteGuideManager.getInstance().onResume();
        super.onResume();

        hd.sendEmptyMessageDelayed(MSG_SHOW, 5000);
    }

    protected void onPause() {
        super.onPause();
        BNRouteGuideManager.getInstance().onPause();
    };

    @Override
    protected void onDestroy() {
        BNRouteGuideManager.getInstance().onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        BNRouteGuideManager.getInstance().onStop();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        BNRouteGuideManager.getInstance().onBackPressed(false);
    }

    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        BNRouteGuideManager.getInstance().onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    };

    private void addCustomizedLayerItems() {
//        List<CustomizedLayerItem> items = new ArrayList<CustomizedLayerItem>();
//        CustomizedLayerItem item1 = null;
//        if (mBNRoutePlanNode != null) {
//            item1 = new CustomizedLayerItem(mBNRoutePlanNode.getLongitude(), mBNRoutePlanNode.getLatitude(),
//                    mBNRoutePlanNode.getCoordinateType(), getResources().getDrawable(R.drawable.ic_launcher), CustomizedLayerItem.ALIGN_CENTER);
//            items.add(item1);
//
//            BNRouteGuideManager.getInstance().setCustomizedLayerItems(items);
//        }
//        BNRouteGuideManager.getInstance().showCustomizedLayer(true);
    }

    private static final int MSG_SHOW = 1;
    private static final int MSG_HIDE = 2;
    private Handler hd = null;

    private OnNavigationListener mOnNavigationListener = new OnNavigationListener() {

        @Override
        public void onNaviGuideEnd() {
            //退出导航
            finish();
        }

        @Override
        public void notifyOtherAction(int actionType, int arg1, int arg2, Object obj) {
                if (actionType == 0) {
                    Toast.makeText(BNDemoGuideActivity.this, "到达目的地",
                            Toast.LENGTH_SHORT).show();
                }
        }

    };

    private void createHandler() {
        if ( hd == null ) {
            hd = new Handler(getMainLooper()) {
                public void handleMessage(android.os.Message msg) {
                    if ( msg.what == MSG_SHOW ) {
                        BNRouteGuideManager.getInstance().showCustomizedLayer(false);
//                        addCustomizedLayerItems();
//                      hd.sendEmptyMessageDelayed(MSG_HIDE, 5000);
                    } else if ( msg.what == MSG_HIDE ) {
                        BNRouteGuideManager.getInstance().showCustomizedLayer(false);
//                      hd.sendEmptyMessageDelayed(MSG_SHOW, 5000);
                    }

                };
            };
        }
    }
}