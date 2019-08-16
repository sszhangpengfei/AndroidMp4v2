package com.zpf.media.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zpf.media.R;
import com.zpf.media.widget.MDLoadingView;

/**
 * author:zpf
 * date:2019-08-16
 */
public class ViewUtils {
    private static int appBarHeight = 0;

    private static int WIDTH = 0;
    private static int HEIGHT = 0;
    public static void showToast(Context context, @NonNull String str){
        Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示一个圆形进度对话框
     *
     * @param message        进度条提示信息,如果不传，默认显示：正在登录...
     * @param context        运行所在的容器
     * @param cancelListener 如果对话框可以被用户手动取消，则传递取消按钮的监听器； 如果对话框不可以被用户手动取消，则传null
     * @return 进度对话框实例
     */
    public static Dialog showProgressMessage(String message,
                                             final Context context,
                                             DialogInterface.OnCancelListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View container = inflater
                .inflate(R.layout.common_progress_dialog, null);
        final MDLoadingView loadingView = (MDLoadingView) container
                .findViewById(R.id.loadingView);
        // 提示文字控件
        TextView msgTV = (TextView) container
                .findViewById(R.id.common_progress_msg);
        msgTV.setText(message);
        dialog.setContentView(container);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                loadingView.clearAnimation();
            }
        });
        if (cancelListener != null) {
            dialog.setCancelable(true);
            dialog.setOnCancelListener(cancelListener);
        } else {
            dialog.setCancelable(false);
        }
        return dialog;
    }
//
//    /**
//     * 显示一个圆形进度对话框 (IOS)
//     *
//     * @param message        进度条提示信息,如果不传，默认显示：正在登录...
//     * @param context        运行所在的容器
//     * @param cancelListener 如果对话框可以被用户手动取消，则传递取消按钮的监听器； 如果对话框不可以被用户手动取消，则传null
//     * @return 进度对话框实例
//     */
//    public static Dialog showAppleProgressMessage(String message,
//                                                  final Context context,
//                                                  DialogInterface.OnCancelListener cancelListener) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        AlertDialog dialog = builder.create();
//        dialog.show();
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View container = inflater
//                .inflate(R.layout.common_apple_progress_dialog, null);
//        final MDLoadingView loadingView = (MDLoadingView) container
//                .findViewById(R.id.loadingView);
//        // 提示文字控件
//        TextView msgTV = (TextView) container
//                .findViewById(R.id.common_progress_msg);
//        msgTV.setText(message);
//        dialog.setContentView(container);
//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                loadingView.clearAnimation();
//            }
//        });
//        if (cancelListener != null) {
//            dialog.setCancelable(true);
//            dialog.setOnCancelListener(cancelListener);
//        } else {
//            dialog.setCancelable(false);
//        }
//        return dialog;
//    }
//
//    /**
//     * 显示一个圆形进度对话框
//     *
//     * @param imgId        图片
//     * @param context        运行所在的容器
//     * @param cancelListener 如果对话框可以被用户手动取消，则传递取消按钮的监听器； 如果对话框不可以被用户手动取消，则传null
//     * @return 进度对话框实例
//     */
//    public static Dialog showAppleProgressImage(String mesg,int imgId,
//                                                final Context context,
//                                                DialogInterface.OnCancelListener cancelListener) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        AlertDialog dialog = builder.create();
//        dialog.show();
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View container = inflater
//                .inflate(R.layout.common_apple_dialog, null);
//        final ImageView imgView = (ImageView) container
//                .findViewById(R.id.icon_img);
//        imgView.setImageResource(imgId);
//        // 提示文字控件
//        TextView msgTV = (TextView) container
//                .findViewById(R.id.common_progress_msg);
//        msgTV.setText(mesg);
//        dialog.setContentView(container);
//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//            }
//        });
//        if (cancelListener != null) {
//            dialog.setCancelable(true);
//            dialog.setOnCancelListener(cancelListener);
//        } else {
//            dialog.setCancelable(false);
//        }
//        return dialog;
//    }
//
//    /**
//     * @breif 弹出Material风格的对话框
//     * @param message
//     * @param context
//     * @param cancelListener
//     * @return
//     */
//    public static MaterialDialog showMaterialDialog(final Context context, String title, String message, String confirmBtnText,
//                                                    String negativeText,
//                                                    final View.OnClickListener posBtnListener,
//                                                    final View.OnClickListener cancelListener) {
//        final MaterialDialog mMaterialDialog = new MaterialDialog(context)
//                .setTitle(title)
//                .setMessage(message)
//                .setPositiveButton(confirmBtnText, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (posBtnListener != null) {
//                            posBtnListener.onClick(v);
//                        }
//                    }
//                })
//                .setNegativeButton(negativeText, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (cancelListener != null) {
//                            cancelListener.onClick(v);
//                        }
//                    }
//                });
//
//        ((Activity)context).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mMaterialDialog.show();
//            }
//        });
//        return mMaterialDialog;
//    }
//
//    /**
//     * @breif 弹出退出登录对话框
//     * @param message
//     * @param context
//     * @param isForceUpgrade 是否强制更新
//     * @return
//     */
//    public static Dialog showUpgradeDialog(boolean isForceUpgrade, String message,
//                                           final Context context, final View.OnClickListener posListener, final View.OnClickListener negtiveListener)
//    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialogViewStyle);
//        AlertDialog dialog = builder.create();
//        dialog.show();
//        // dimAmount在0.0f和1.0f之间，0.0f完全不暗，即背景是可见的 ，1.0f时候，背景全部变黑暗。
//        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//        lp.dimAmount = 0.4f;
//        dialog.getWindow().setAttributes(lp);
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        View container = LayoutInflater.from(context).inflate(R.layout.layout_upgrade_dialog, null);
//        TextView messageTV = (TextView) container
//                .findViewById(R.id.message);
//        Button confirmBtn = (Button) container
//                .findViewById(R.id.btn_confirm);
//        LinearLayout cancelBtn = (LinearLayout) container
//                .findViewById(R.id.btn_remind_later);
//        messageTV.setText(message);
//
//        if(isForceUpgrade){
//            cancelBtn.setVisibility(View.GONE);
//        }
//
//        confirmBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                posListener.onClick(v);
//            }
//        });
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                negtiveListener.onClick(v);
//            }
//        });
//        dialog.setContentView(container);
//        dialog.setCancelable(false);
//        return dialog;
//    }
//
//
//    /**
//     * @breif 关于弹出框
//     * @param context
//     * @return
//     */
//    public static Dialog showAboutDialog(Context context,String version) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialogViewStyle);
//        final AlertDialog dialog = builder.create();
//        dialog.show();
//        // dimAmount在0.0f和1.0f之间，0.0f完全不暗，即背景是可见的 ，1.0f时候，背景全部变黑暗。
//        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//        lp.dimAmount = 0.4f;
//        dialog.getWindow().setAttributes(lp);
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        View container = LayoutInflater.from(context).inflate(R.layout.dialog_about, null);
//        TextView versionTv = (TextView) container.findViewById(R.id.app_version_tv);
//        LinearLayout closeBtn = (LinearLayout) container.findViewById(R.id.close_about_btn);
//        closeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        versionTv.setText(version);
//        dialog.setContentView(container);
//        dialog.setCancelable(true);
//        return dialog;
//    }
//
//
//    /**
//     * @breif 登陆弹出框
//     * @param context
//     * @return
//     */
//    public static Dialog showLoginDialog(Context context, View.OnClickListener clickListener) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialogViewStyle);
//        final AlertDialog dialog = builder.create();
//        dialog.show();
//        // dimAmount在0.0f和1.0f之间，0.0f完全不暗，即背景是可见的 ，1.0f时候，背景全部变黑暗。
//        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//        lp.dimAmount = 0.4f;
//        dialog.getWindow().setAttributes(lp);
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        View container = LayoutInflater.from(context).inflate(R.layout.dialog_login, null);
//
//        LinearLayout closeBtn = (LinearLayout) container.findViewById(R.id.close_about_btn);
//        closeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        LinearLayout goLogin = (LinearLayout) container.findViewById(R.id.go_login_btn);
//        goLogin.setOnClickListener(clickListener);
//
//        dialog.setContentView(container);
//        dialog.setCancelable(true);
//        return dialog;
//    }
//
//
//    /**
//     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
//     */
//    public static int dip2px( float dpValue) {
//        final float scale = MyApplication.getAppContext().getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);
//    }
//
//    /**
//     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
//     */
//    public static int px2dip(Context context, float pxValue) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (pxValue / scale + 0.5f);
//    }
//
//    /**
//     * 获取屏幕宽高
//     * @return
//     */
//    public static int[] getWindowDisplay(){
//        try {
//            Activity activity = MyActivityManager.getInstance().getCurrentActivity();
//            if(activity == null) return new int[]{1,1};
//            if(WIDTH == 0|| HEIGHT == 0){
//                WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
//                WIDTH = wm.getDefaultDisplay().getWidth();//屏幕宽度
//                HEIGHT = wm.getDefaultDisplay().getHeight();//屏幕高度
//            }
//            return new int[]{WIDTH,HEIGHT};
//
//        }catch (Exception e){
//            return new int[]{1,1};
//        }
//
//    }

    /**
     * @param context
     * @return int pixel
     * @brief 获取屏幕宽度
     */
    public static int getScreenWidth(Activity context) {
        if (context == null) {
            return 0;
        }
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * @param context
     * @return int pixel
     * @brief 获取屏幕高度
     */
    public static int getScreenHeight(Activity context) {
        if (context == null) {
            return 0;
        }
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        if (context != null) {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    public static void setAppBarHeight(int height) {
        appBarHeight = height;
    }

    public static int getAppBarHeight() {
        return appBarHeight;
    }


    /**
     * @param activity
     * @return > 0 success; <= 0 fail
     * @brief 获取状态栏高度
     */
    /*public static int getStatusBarHeight(Activity activity) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }
    */

}
