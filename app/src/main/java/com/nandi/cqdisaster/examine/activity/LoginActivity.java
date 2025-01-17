package com.nandi.cqdisaster.examine.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nandi.cqdisaster.R;
import com.nandi.cqdisaster.examine.constant.ConnectUrl;
import com.nandi.cqdisaster.examine.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 登录界面
 * Created by baohongyan on 2017/2/23.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.settings)
    TextView settings;
    private String name, pwd;
    private String sessiongId;
    private SharedPreferences sp;
    private ProgressDialog progressDialog;
    private boolean isLogin = false;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_login_examine);
        ButterKnife.bind(this);
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        context = this;
        initView();
        setListener();
    }


//    /**
//     * 绑定推送
//     */
//    private void initPush(){
//        CloudPushService pushService = PushServiceFactory.getCloudPushService();
//        pushService.bindAccount(sp.getString("userName", ""), new CommonCallback() {
//            @Override
//            public void onSuccess(String s) {
//                Log.d(TAG,"userName:"+s);
//            }
//
//            @Override
//            public void onFailed(String s, String s1) {
//                Log.d(TAG,"userName:"+"--s:"+s+"--s1:"+s1);
//            }
//        });
////        BasicCustomPushNotification notification = new BasicCustomPushNotification();
////        notification.setRemindType(BasicCustomPushNotification.REMIND_TYPE_SOUND);
////        notification.setStatusBarDrawable(R.mipmap.di);
////        notification.setRemindType(BasicCustomPushNotification.REMIND_TYPE_VIBRATE_AND_SOUND);
////        boolean res = CustomNotificationBuilder.getInstance().setCustomNotification(1, notification);
//    }


    private void initView() {
        etName.setText(sp.getString("userName", ""));
        etPwd.setText(sp.getString("passWord", ""));
        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("正在登录...");
    }

    private void setListener() {
        sp.edit().putString("IP", "183.230.252.112").apply();
        sp.edit().putString("PORT", "8085").apply();
        progressDialog.show();
        login();
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (validate()) {
//                    btnLogin.setEnabled(false);
//                    progressDialog.show();
//                    login();
//                }
//            }
//        });
//        settings.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                View view = LayoutInflater.from(context).inflate(R.layout.dialog_setting, null);
//                final EditText etIp = (EditText) view.findViewById(R.id.setting_ip);
//                final EditText etPort = (EditText) view.findViewById(R.id.setting_port);
//                etIp.setText(sp.getString("IP", ""));
//                etPort.setText(sp.getString("PORT", ""));
//                new AlertDialog.Builder(context)
//                        .setView(view)
//                        .setIcon(R.drawable.settings)
//                        .setTitle("设置")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                String ip = etIp.getText().toString().trim();
//                                String port = etPort.getText().toString().trim();
//                                sp.edit().putString("IP", ip).apply();
//                                sp.edit().putString("PORT", port).apply();
//                                ToastUtils.showShortToast("设置成功");
//                                dialog.dismiss();
//                            }
//                        }).show();
//            }
//        });
    }


    private void login() {
        sessiongId = UUID.randomUUID().toString();
        name="chenyan";
        pwd="123456";
        Log.d(TAG, "账号：" + name);
        Log.d(TAG, "密码：" + pwd);
        Log.d(TAG, "sessionId：" + sessiongId);
        Log.d(TAG, "偏好设置中的sessionId：" + sp.getString("sessionId", ""));

        try {
            OkHttpUtils.get().url(new ConnectUrl().getLoginUrl())
                    .addParams("userName", name)
                    .addParams("passWord", pwd)
                    .addParams("sessionId", sessiongId)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ToastUtils.showShortToast("网络故障，请检查网络！");
                            btnLogin.setEnabled(true);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            btnLogin.setEnabled(true);
                            progressDialog.dismiss();
                            String msg, status;
                            Log.d(TAG, "登录返回的数据：" + response);
                            try {
                                JSONObject object = new JSONObject(response);
                                status = object.getString("status");
                                msg = object.getString("message");
                                if ("200".equals(status)) {
                                    isLogin = true;
                                    sp.edit().putString("userId", object.getString("userId")).apply();
                                    sp.edit().putString("userName", name).apply();
                                    sp.edit().putString("passWord", pwd).apply();
                                    sp.edit().putString("sessionId", sessiongId).apply();
                                    sp.edit().putBoolean("isLogin", isLogin).apply();
//                                    initPush();
                                    ToastUtils.showShortToast(msg);
                                    Intent intent = new Intent(context, MainActivityExamine.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    ToastUtils.showShortToast(msg);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showShortToast("登录失败，请检查IP是否设置！");
            progressDialog.dismiss();
            btnLogin.setEnabled(true);
        }
    }


    //检查输入是否为空
    private boolean validate() {
        boolean valid = true;

        name = etName.getText().toString();
        pwd = etPwd.getText().toString();
        if (TextUtils.isEmpty(name)) {
            etName.setError("账号不能为空");
            valid = false;
        } else {
            etName.setError(null);
        }
        if (TextUtils.isEmpty(pwd)) {
            etPwd.setError("密码不能为空");
            valid = false;
        } else {
            etPwd.setError(null);
        }

        return valid;
    }

}
