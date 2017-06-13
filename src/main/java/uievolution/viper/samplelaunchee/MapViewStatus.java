package uievolution.viper.samplelaunchee;

import android.widget.LinearLayout;
import android.widget.AbsoluteLayout;
import android.view.View;

public class  MapViewStatus {
    private MainActivity activity;

    static public enum MAP_ACTION {
        ACTION_INPUTBOX_TOUCH, ACTION_BACK_TOUCH, ACTION_MULTI_FUNCTION, ACTION_SEARCH_FINISHED, ACTION_SEARCHANS_TOUCH
    }

    static public enum MAPVIEWSTATUS {
        STATUS_CURRENT_POS, STATUS_MENU, STATUS_MENU_SEARCH, STATUS_SET_POS, STATUS_NAVI
    }

    public MapViewStatus(MainActivity main) {
        this.activity = main;
    }

    public void setStatus(MAP_ACTION action) {
        switch (action) {
            case ACTION_INPUTBOX_TOUCH:
                if(getMapViewStatus() != MAPVIEWSTATUS.STATUS_MENU ||
                    getMapViewStatus() != MAPVIEWSTATUS.STATUS_MENU_SEARCH ||
                    getMapViewStatus() != MAPVIEWSTATUS.STATUS_NAVI) break;
                activity.findViewById(R.id.menuLayout).setVisibility(View.VISIBLE);
                activity.findViewById(R.id.menuLinearLayout).setVisibility(View.VISIBLE);
                activity.findViewById(R.id.menuSearchLinearLayout).setVisibility(View.GONE);
                // TODO focus设定问题
                break;
            case ACTION_BACK_TOUCH:
                if(getMapViewStatus() == MAPVIEWSTATUS.STATUS_MENU_SEARCH) {
                    activity.findViewById(R.id.menuLinearLayout).setVisibility(View.VISIBLE);
                    // TODO 搜索画面更新问题
                    activity.findViewById(R.id.menuSearchLinearLayout).setVisibility(View.GONE);
                    // TODO focus设定问题
                }
                else if(getMapViewStatus() == MAPVIEWSTATUS.STATUS_MENU) {
                    activity.findViewById(R.id.menuLayout).setVisibility(View.GONE);
                }
                break;
            case ACTION_MULTI_FUNCTION:
                if(getMapViewStatus() != MAPVIEWSTATUS.STATUS_MENU ||
                        getMapViewStatus() != MAPVIEWSTATUS.STATUS_MENU_SEARCH ||
                        getMapViewStatus() != MAPVIEWSTATUS.STATUS_NAVI) {
                    activity.findViewById(R.id.menuLayout).setVisibility(View.VISIBLE);
                    activity.findViewById(R.id.menuLinearLayout).setVisibility(View.VISIBLE);
                    activity.findViewById(R.id.menuSearchLinearLayout).setVisibility(View.GONE);
                    // TODO focus设定问题
                }
                break;
            case ACTION_SEARCH_FINISHED:
                if(getMapViewStatus() == MAPVIEWSTATUS.STATUS_MENU) {
                    activity.findViewById(R.id.menuLinearLayout).setVisibility(View.GONE);
                    activity.findViewById(R.id.menuSearchLinearLayout).setVisibility(View.VISIBLE);
                    // TODO 搜索画面更新问题
                }
                else if(getMapViewStatus() == MAPVIEWSTATUS.STATUS_MENU_SEARCH) {
                    // TODO 搜索画面更新问题
                    // TODO focus设定问题
                }
                break;
            case ACTION_SEARCHANS_TOUCH:
                activity.findViewById(R.id.menuLayout).setVisibility(View.GONE);
                break;
        }
    }

    public MAPVIEWSTATUS getMapViewStatus() {
        // TODO isDestroyed是否可用
        if(activity.isDestroyed()) return MAPVIEWSTATUS.STATUS_NAVI;
        if(activity.findViewById(R.id.menuLinearLayout).getVisibility() == View.VISIBLE) return MAPVIEWSTATUS.STATUS_MENU;
        if(activity.findViewById(R.id.menuSearchLinearLayout).getVisibility() == View.VISIBLE) return MAPVIEWSTATUS.STATUS_MENU_SEARCH;
        // TODO 现在地和目的地设定的区别
        else return MAPVIEWSTATUS.STATUS_CURRENT_POS;
    }
}