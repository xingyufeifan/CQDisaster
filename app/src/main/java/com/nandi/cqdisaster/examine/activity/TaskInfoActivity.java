package com.nandi.cqdisaster.examine.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nandi.cqdisaster.R;
import com.nandi.cqdisaster.examine.bean.TaskInfoBean;
import com.nandi.cqdisaster.examine.constant.ConnectUrl;
import com.nandi.cqdisaster.examine.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * 填报反馈信息
 */
public class TaskInfoActivity extends AppCompatActivity {
    @BindView(R.id.tv_task_id)
    TextView tvTaskId;
    @BindView(R.id.tv_workers_time)
    TextView tvWorkersTime;
    @BindView(R.id.tv_task_disaster)
    TextView tvTaskDisaster;
    @BindView(R.id.tv_task_kind)
    TextView tvTaskKind;
    @BindView(R.id.tv_task_address)
    TextView tvTaskAddress;
    @BindView(R.id.tv_task_survey_time)
    TextView tvTaskSurveyTime;
    @BindView(R.id.tv_task_township)
    TextView tvTaskTownship;
    @BindView(R.id.tv_task_reporter)
    TextView tvTaskReporter;
    @BindView(R.id.tv_task_state)
    TextView tvTaskState;
    @BindView(R.id.btn_task_com)
    Button btnTaskCom;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    private String TAG = "TaskActivity";


    private Intent intent;
    private String mId;
    private SharedPreferences sp;
    private String sessionId;
    private ProgressDialog progressDialog;
    private TaskInfoBean taskInfoBean;
    private String[] mStates = new String[]{"未发送", "已发送", "已反馈", "已完成", "废弃"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);
        ButterKnife.bind(this);
        intent = getIntent();
        mId = intent.getStringExtra("TaskId");
        tvTaskId.setText(mId);
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        sessionId = sp.getString("sessionId", "");

        initData();
        setListener();
    }

    private void setListener() {
        btnTaskCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskInfoActivity.this, CompleteInfoActivity.class);
                intent.putExtra("TaskBean", (Serializable) taskInfoBean);
                Log.e(TAG, "未提交任务页面任务ID:" + taskInfoBean.getmTaskId());
                startActivity(intent);
            }
        });
    }

    private void initData() {
        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("正在加载...");
        progressDialog.show();
        setOkHttp();

    }


    private void setOkHttp() {
        Log.d("WorkbenchFragment", "任务ID：" + mId);
        try {
            OkHttpUtils.get().url(new ConnectUrl().getOneTaskUrl())
                    .addParams("id", mId)
                    .addParams("sessionId", sessionId)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            progressDialog.dismiss();
                            ToastUtils.showShortToast("网络故障，请检查网络！");
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            progressDialog.dismiss();
                            String msg, status;
                            Log.d(TAG, "登录返回的数据：" + response);
                            try {
                                JSONObject object = new JSONObject(response);
                                status = object.getString("status");
                                msg = object.getString("message");
                                if ("200".equals(status)) {
                                    //mListData.clear();
                                    JSONArray message = object.getJSONArray("message");
                                    JSONObject oj = message.getJSONObject(0);
                                    taskInfoBean = new TaskInfoBean();
                                    taskInfoBean.setmTaskId(mId);
                                    taskInfoBean.setmDisasterLevel(oj.getString("dis_level"));
                                    taskInfoBean.setmIsDisaster(oj.getString("dis_sf"));
                                    taskInfoBean.setmRowNumber(oj.getString("dis_id"));
                                    taskInfoBean.setmDisasterName(oj.getString("dis_name"));
                                    taskInfoBean.setmDisasterLng(oj.getString("dis_lon"));
                                    taskInfoBean.setmDisasterLat(oj.getString("dis_lat"));
                                    taskInfoBean.setmDisasterLocation(oj.getString("dis_location"));
                                    taskInfoBean.setmDisasterContact(oj.getString("dis_person"));
                                    taskInfoBean.setmDisasterMobile(oj.getString("dis_person_phone"));
                                    taskInfoBean.setmDisasterType(oj.getString("xinorjiu"));
                                    taskInfoBean.setmHappenTime(oj.getString("happen_time"));
                                    taskInfoBean.setmTaskName(oj.getString("task_name"));
                                    taskInfoBean.setmSendName(oj.getString("send_name"));
                                    taskInfoBean.setmStartTime(oj.getString("start_time"));
                                    taskInfoBean.setmDisaster(oj.getString("dis_name"));
                                    taskInfoBean.setmAddress(oj.getString("survey_site"));
                                    taskInfoBean.setmSurveyTime(oj.getString("survey_time"));
                                    taskInfoBean.setmAreaName(oj.getString("area_name"));
                                    taskInfoBean.setmName(oj.getString("survey_name"));
                                    taskInfoBean.setmTaskState(oj.getString("task_state"));
                                    Log.d(TAG, "message：id-" + taskInfoBean.getmAddress());
                                    tvTaskId.setText("调查任务-" + taskInfoBean.getmDisaster());
                                    tvWorkersTime.setText("发起人：" + taskInfoBean.getmSendName() + " 时间：" + taskInfoBean.getmStartTime());
                                    tvTaskDisaster.setText("灾  害  点：" + taskInfoBean.getmDisaster());
                                    tvTaskAddress.setText("调查地点：" + taskInfoBean.getmAddress());
                                    tvTaskSurveyTime.setText("调查时间：" + taskInfoBean.getmSurveyTime());
                                    tvTaskTownship.setText("所属乡镇：" + taskInfoBean.getmAreaName());
                                    tvTaskKind.setText("发生时间：" + taskInfoBean.getmHappenTime());
                                    tvTaskReporter.setText("调  查  人：" + taskInfoBean.getmName());
                                    tvTaskState.setText("任务状态：" + mStates[Integer.parseInt(taskInfoBean.getmTaskState()) - 1]);
                                } else if ("400".equals(status)) {
                                    ToastUtils.showShortToast(msg);
                                    sp.edit().putBoolean("isLogin", false).apply();
                                    Intent intent = new Intent(TaskInfoActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();
            ToastUtils.showShortToast("加载失败！");
        }
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
