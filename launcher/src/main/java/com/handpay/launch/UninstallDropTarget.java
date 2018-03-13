//package com.handpay.launch;
//
//import android.annotation.TargetApi;
//import android.content.ComponentName;
//import android.content.Context;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.UserManager;
//import android.util.AttributeSet;
//import android.util.Pair;
//import android.widget.Toast;
//
//import com.handpay.launch.compat.UserHandleCompat;
//import com.handpay.launch.util.Thunk;
//
//public class UninstallDropTarget extends ButtonDropTarget {
//
//    private Context mContext;
//
//    public UninstallDropTarget(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public UninstallDropTarget(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        mContext = context;
//    }
//
//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//        // Get the hover color
//        mHoverColor = getResources().getColor(R.color.uninstall_target_hover_tint);
//
//        mDrawable = getResources().getDrawable(R.drawable.settings_icon);
//        if (Utilities.ATLEAST_JB_MR1) {
//            setCompoundDrawablesRelativeWithIntrinsicBounds(mDrawable, null, null, null);
//        } else {
//            setCompoundDrawablesWithIntrinsicBounds(mDrawable, null, null, null);
//        }
//    }
//
//    @Override
//    protected boolean supportsDrop(DragSource source, Object info) {
//        return supportsDrop(getContext(), info);
//    }
//
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
//    public static boolean supportsDrop(Context context, Object info) {
//        if (Utilities.ATLEAST_JB_MR2) {
//            UserManager userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
//            Bundle restrictions = userManager.getUserRestrictions();
//            if (restrictions.getBoolean(UserManager.DISALLOW_APPS_CONTROL, false)
//                    || restrictions.getBoolean(UserManager.DISALLOW_UNINSTALL_APPS, false)) {
//                return false;
//            }
//        }
//
//        Pair<ComponentName, Integer> componentInfo = getAppInfoFlags(info);
//        return componentInfo != null && (componentInfo.second & AppInfo.DOWNLOADED_FLAG) != 0;
//    }
//
//    /**
//     * @return the component name and flags if {@param info} is an AppInfo or an app shortcut.
//     */
//    private static Pair<ComponentName, Integer> getAppInfoFlags(Object item) {
//        if (item instanceof AppInfo) {
//            AppInfo info = (AppInfo) item;
//            return Pair.create(info.componentName, info.flags);
//        } else if (item instanceof ShortcutInfo) {
//            ShortcutInfo info = (ShortcutInfo) item;
//            ComponentName component = info.getTargetComponent();
//            if (info.itemType == LauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION
//                    && component != null) {
//                return Pair.create(component, info.flags);
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public void onDrop(DragObject d) {
//        // Differ item deletion
//        if (d.dragSource instanceof UninstallSource) {
//            ((UninstallSource) d.dragSource).deferCompleteDropAfterUninstallActivity();
//        }
//        super.onDrop(d);
//    }
//
//    @Override
//    void completeDrop(final DragObject d) {
//        final Pair<ComponentName, Integer> componentInfo = getAppInfoFlags(d.dragInfo);
//        final UserHandleCompat user = ((ItemInfo) d.dragInfo).user;
//        Toast.makeText(mContext,"不能移动到此处",Toast.LENGTH_SHORT).show();
//        if (startUninstallActivity(mLauncher, d.dragInfo)) {
//
//            final Runnable checkIfUninstallWasSuccess = new Runnable() {
//                @Override
//                public void run() {
//                    String packageName = componentInfo.first.getPackageName();
//                    boolean uninstallSuccessful = !AllAppsList.packageHasActivities(
//                            getContext(), packageName, user);
//                    sendUninstallResult(d.dragSource, uninstallSuccessful);
//                }
//            };
//            mLauncher.addOnResumeCallback(checkIfUninstallWasSuccess);
//        } else {
//            sendUninstallResult(d.dragSource, false);
//        }
//    }
//
//    public static boolean startUninstallActivity(Launcher launcher, Object info) {
//        final Pair<ComponentName, Integer> componentInfo = getAppInfoFlags(info);
//        final UserHandleCompat user = ((ItemInfo) info).user;
//        return launcher.startApplicationUninstallActivity(
//                componentInfo.first, componentInfo.second, user);
//    }
//
//    @Thunk
//    void sendUninstallResult(DragSource target, boolean result) {
//        if (target instanceof UninstallSource) {
//            ((UninstallSource) target).onUninstallActivityReturned(result);
//        }
//    }
//
//    /**
//     * Interface defining an object that can provide uninstallable drag objects.
//     */
//    public static interface UninstallSource {
//
//        /**
//         * A pending uninstall operation was complete.
//         * @param result true if uninstall was successful, false otherwise.
//         */
//        void onUninstallActivityReturned(boolean result);
//
//        /**
//         * Indicates that an uninstall request are made and the actual result may come
//         * after some time.
//         */
//        void deferCompleteDropAfterUninstallActivity();
//    }
//}
