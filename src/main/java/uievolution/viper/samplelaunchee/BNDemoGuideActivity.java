package uievolution.viper.samplelaunchee;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRouteGuideManager.CustomizedLayerItem;
import com.baidu.navisdk.adapter.BNRouteGuideManager.OnNavigationListener;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviBaseCallbackModel;
import com.baidu.navisdk.adapter.BaiduNaviCommonModule;
import com.baidu.navisdk.adapter.NaviModuleFactory;
import com.baidu.navisdk.adapter.NaviModuleImpl;

import me.drakeet.materialdialog.MaterialDialog;
import uie.multiaccess.app.UMAApplication;
import uie.multiaccess.input.UMAHIDConstants;
import uie.multiaccess.input.UMAHIDInputEventListener;
import uie.multiaccess.input.UMAHIDManager;
import uie.multiaccess.input.UMASensorEvent;
import uie.multiaccess.view.UMAFocusManager;
//import com.baidu.panosdk.plugin.indoor.R;

public class BNDemoGuideActivity extends Activity  implements UMAHIDInputEventListener {
    private UMAApplication umaApplication = UMAApplication.INSTANCE;
//    public AlertDialog isExit = null;
    public MaterialDialog isExit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createHandler();
        View view = BNRouteGuideManager.getInstance().onCreate(this,mOnNavigationListener);

        if ( view != null ) {
            setContentView(view);
        }

        umaApplication.create(this);
        UMAHIDManager umahidManager = (UMAHIDManager)umaApplication.getUMAService(UMAApplication.UMA_HID_SERVICE);
        umahidManager.startDiscoverDevice(null);

        // 创建退出对话框
        isExit = new MaterialDialog(this)
                .setTitle("系统提示")
                .setMessage("确定要退出吗？")
                .setPositiveButton("确定", dialogListener)
                .setNegativeButton("取消", dialogListener)
                .setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onStart() {
        BNRouteGuideManager.getInstance().onStart();
        super.onStart();
        umaApplication.start(this);

        if(hd != null)
            hd.sendEmptyMessageDelayed(MSG_SHOW, 5000);
    }

    @Override
    protected void onResume() {
        BNRouteGuideManager.getInstance().onResume();
        super.onResume();
        umaApplication.resume(this);

        if(hd != null)
            hd.sendEmptyMessageDelayed(MSG_SHOW, 5000);
    }

    protected void onPause() {
        super.onPause();
        BNRouteGuideManager.getInstance().onPause();
        umaApplication.pause(this);
    };

    @Override
    protected void onDestroy() {
        umaApplication.destroy(this);
        BNRouteGuideManager.getInstance().onDestroy();
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        BNRouteGuideManager.getInstance().onStop();
        super.onStop();
        umaApplication.stop(this);
    }

    @Override
    public void onBackPressed() {
        isExit.show();
    }

    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        BNRouteGuideManager.getInstance().onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    };

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

    @Override
    public boolean onRotate(int var1, int var2) {
        return false;
    }
    @Override
    public boolean onTranslate(int var1, int var2) {
        return false;
    }
    @Override
    public boolean onPressUpButton(int button) {
        if( button == UMAHIDConstants.BUTTON_BACK) {
            if( isExit.getNegativeButton() != null && isExit.getNegativeButton().isShown() ) {
                isExit.dismiss();
            }
            else {
                isExit.show();
                UMAFocusManager focusManager = umaApplication.getFocusManager();
                focusManager.setFocusRoot((ViewGroup)isExit.getNegativeButton().getParent());
                focusManager.requestFocusForView(isExit.getNegativeButton());
                focusManager.setVisibility(0);
            }
            return true;
        }
        return false;
    }
    @Override
    public boolean onPressDownButton(int var1) {
        return false;
    }
    @Override
    public void onDoubleClickButton(int var1) {

    }
    @Override
    public void onLongPressButton(int var1) {

    }
    @Override
    public void onLongPressButton(int var1, int var2) {

    }
    @Override
    public void onAccelerometerUpdate(UMASensorEvent var1) {

    }

    View.OnClickListener dialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if(v == isExit.getPositiveButton()) {
                BNRouteGuideManager.getInstance().onBackPressed(false);
            }
            isExit.dismiss();
        }
    };
}