package uievolution.viper.samplelaunchee;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.widget.LinearLayout;
import android.widget.AbsoluteLayout;
import android.view.View;

public class  MapViewStatus {
    private MainActivity activity;

    static public enum MAP_ACTION {
        ACTION_INPUTBOX_TOUCH, ACTION_BACK_TOUCH, ACTION_SEARCH_FINISHED, ACTION_SEARCHANS_TOUCH, ACTION_HOME_TOUCH
    }

    static public enum MAPVIEWSTATUS {
        STATUS_CURRENT_POS, STATUS_MENU, STATUS_MENU_SEARCH, STATUS_SET_POS, STATUS_NAVI, STATUS_EXIT, STATUS_GOTO_NAVI
    }

    public MapViewStatus(MainActivity main) {
        this.activity = main;
    }

    public boolean setStatus(MAP_ACTION action) {
        MAPVIEWSTATUS status = getMapViewStatus();
        switch (action) {
            case ACTION_INPUTBOX_TOUCH:
                if(status == MAPVIEWSTATUS.STATUS_MENU ||
                        status == MAPVIEWSTATUS.STATUS_MENU_SEARCH ||
                        status == MAPVIEWSTATUS.STATUS_NAVI ||
                        status == MAPVIEWSTATUS.STATUS_EXIT ||
                        status == MAPVIEWSTATUS.STATUS_GOTO_NAVI) break;
                activity.findViewById(R.id.menuLayout).setVisibility(View.VISIBLE);
                activity.findViewById(R.id.menuLinearLayout).setVisibility(View.VISIBLE);
                activity.findViewById(R.id.menuSearchLinearLayout).setVisibility(View.GONE);
                break;
            case ACTION_BACK_TOUCH:
                if(status == MAPVIEWSTATUS.STATUS_MENU_SEARCH) {
                    activity.findViewById(R.id.menuLinearLayout).setVisibility(View.VISIBLE);
                    activity.findViewById(R.id.menuSearchLinearLayout).setVisibility(View.GONE);
                }
                else if(status == MAPVIEWSTATUS.STATUS_MENU) {
                    activity.findViewById(R.id.menuLayout).setVisibility(View.GONE);
                    activity.findViewById(R.id.menuLinearLayout).setVisibility(View.GONE);
                }
                else if(status == MAPVIEWSTATUS.STATUS_NAVI) {
                }
                else if(status == MAPVIEWSTATUS.STATUS_SET_POS || status == MAPVIEWSTATUS.STATUS_CURRENT_POS) {
                    return false;
                }
                else if(status == MAPVIEWSTATUS.STATUS_EXIT) {
                    activity.isExit.getNegativeButton().callOnClick();
                }
                else if(status == MAPVIEWSTATUS.STATUS_GOTO_NAVI) {
                    activity.isGoToNavi.getNegativeButton().callOnClick();
                }
                else {
                    return false;
                }
                break;
            case ACTION_SEARCH_FINISHED:
                if(status == MAPVIEWSTATUS.STATUS_MENU) {
                    activity.findViewById(R.id.menuLinearLayout).setVisibility(View.GONE);
                    activity.findViewById(R.id.menuSearchLinearLayout).setVisibility(View.VISIBLE);
                }
                else if(status == MAPVIEWSTATUS.STATUS_MENU_SEARCH) {
                }
                break;
            case ACTION_SEARCHANS_TOUCH:
                activity.findViewById(R.id.menuLayout).setVisibility(View.GONE);
                break;
            case ACTION_HOME_TOUCH:
                activity.findViewById(R.id.menuLayout).setVisibility(View.GONE);
        }
        activity.focusControl();
        return true;
    }

    public MAPVIEWSTATUS getMapViewStatus() {
        // TODO isDestroyed是否可用
        if(activity.isDestroyed()) return MAPVIEWSTATUS.STATUS_NAVI;
        else if(activity.isExit.getNegativeButton() != null && activity.isExit.getNegativeButton().isShown()) return MAPVIEWSTATUS.STATUS_EXIT;
        else if(activity.isGoToNavi.getNegativeButton() != null && activity.isGoToNavi.getNegativeButton().isShown()) return MAPVIEWSTATUS.STATUS_GOTO_NAVI;
        else if(activity.findViewById(R.id.menuLinearLayout).isShown()) return MAPVIEWSTATUS.STATUS_MENU;
        else if(activity.findViewById(R.id.menuSearchLinearLayout).isShown()) return MAPVIEWSTATUS.STATUS_MENU_SEARCH;
        else if(activity.findViewById(R.id.position).isShown())  return MAPVIEWSTATUS.STATUS_SET_POS;
        else return MAPVIEWSTATUS.STATUS_CURRENT_POS;
    }
}