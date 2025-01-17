package com.nandi.cqdisaster.examine.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.nandi.cqdisaster.MyAPP;
import com.nandi.cqdisaster.R;
import com.nandi.cqdisaster.examine.adapter.FillInfoPagerAdapter;
import com.nandi.cqdisaster.examine.bean.TaskInfoBean;
import com.nandi.cqdisaster.examine.constant.ConnectUrl;
import com.nandi.cqdisaster.examine.database.BaseInfoBean;
import com.nandi.cqdisaster.examine.database.BaseInfoBeanDao;
import com.nandi.cqdisaster.examine.database.CollectInfoBean;
import com.nandi.cqdisaster.examine.database.CollectInfoBeanDao;
import com.nandi.cqdisaster.examine.fragment.BaseInfoFragment;
import com.nandi.cqdisaster.examine.fragment.CollectInfoFragment;
import com.nandi.cqdisaster.examine.fragment.MediaInfoFragment;
import com.nandi.cqdisaster.examine.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class FillInfoActivity extends AppCompatActivity {

    @BindView(R.id.fillinfo_tablayout)
    TabLayout fillinfoTablayout;
    @BindView(R.id.fillinfo_viewpager)
    ViewPager fillinfoViewpager;
    public ProgressDialog progressDialog, uploadProgress;
    @BindView(R.id.btn_upload)
    Button btnUpload;
    private static final String TAG = "FillInfoActivity";
    @BindView(R.id.iv_back)
    ImageView ivBack;
    private Intent dataIntent;
    public TaskInfoBean taskInfoBean;
    private BaseInfoBeanDao baseInfoBeanDao;
    private CollectInfoBeanDao collectInfoBeanDao;
    private SharedPreferences sp;
    private String sessionId;
    private BaseInfoBean baseInfoBean;
    private CollectInfoBean collectInfoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_info);
        ButterKnife.bind(this);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        sessionId = sp.getString("sessionId", "");
        baseInfoBeanDao = MyAPP.getDaoSession().getBaseInfoBeanDao();
        collectInfoBeanDao = MyAPP.getDaoSession().getCollectInfoBeanDao();
        dataIntent = getIntent();
        taskInfoBean = (TaskInfoBean) dataIntent.getSerializableExtra("taskBean");
        Log.d("FillInfoActivity", "上级页面传递过来的taskBean" + taskInfoBean.toString());
        initView();
        initListener();
    }

    private void initView() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new BaseInfoFragment());
        fragments.add(new CollectInfoFragment());
        fragments.add(new MediaInfoFragment());
        fillinfoViewpager.setAdapter(new FillInfoPagerAdapter(getSupportFragmentManager(), fragments));
        fillinfoTablayout.setupWithViewPager(fillinfoViewpager);
        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("正在加载...");
        progressDialog.show();
    }

    private void initListener() {

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseInfoBean = baseInfoBeanDao.queryBuilder().where(BaseInfoBeanDao.Properties.TaskId.eq(taskInfoBean.getmTaskId())).unique();
                collectInfoBean = collectInfoBeanDao.queryBuilder().where(CollectInfoBeanDao.Properties.TaskId.eq(taskInfoBean.getmTaskId())).unique();
                if (collectInfoBean != null && baseInfoBean != null) {
                    boolean a2 = collectInfoBean.getCollectInfoDead().isEmpty();
                    boolean a3 = collectInfoBean.getCollectInfoMiss().isEmpty();
                    boolean a4 = collectInfoBean.getCollectInfoHeavyInjured().isEmpty();
                    boolean a5 = collectInfoBean.getCollectInfoSoftInjured().isEmpty();
                    boolean a6 = collectInfoBean.getCollectInfoEconomicLoss().isEmpty();
                    boolean a7 = collectInfoBean.getCollectInfoHouseCollapseNum().isEmpty();
                    boolean a8 = collectInfoBean.getCollectInfoHouseCollapseArea().isEmpty();
                    boolean a9 = collectInfoBean.getCollectInfoHouseDamageNum().isEmpty();
                    boolean a10 = collectInfoBean.getCollectInfoHouseDamageArea().isEmpty();
                    boolean a11 = collectInfoBean.getCollectInfoAnotherDamage().isEmpty();
                    boolean a12 = collectInfoBean.getCollectInfoFamily().isEmpty();
                    boolean a13 = collectInfoBean.getCollectInfoPeople().isEmpty();
                    boolean a14 = collectInfoBean.getCollectInfoAtHomeFamily().isEmpty();
                    boolean a15 = collectInfoBean.getCollectInfoAtHomePeople().isEmpty();
                    boolean a16 = collectInfoBean.getCollectInfoHouse().isEmpty();
                    boolean a17 = collectInfoBean.getCollectInfoHouseArea().isEmpty();
                    boolean a18 = collectInfoBean.getCollectInfoAnotherDisaster().isEmpty();
                    boolean a19 = collectInfoBean.getCollectInfoLandslideLength().isEmpty();
                    boolean a20 = collectInfoBean.getCollectInfoLandslideWidth().isEmpty();
                    boolean a21 = collectInfoBean.getCollectInfoLandslideArea().isEmpty();
                    boolean a22 = collectInfoBean.getCollectInfoLandslideVolume().isEmpty();
                    boolean a23 = collectInfoBean.getCollectInfoDistortionLength().isEmpty();
                    boolean a24 = collectInfoBean.getCollectInfoDistortionWidth().isEmpty();
                    boolean a25 = collectInfoBean.getCollectInfoDistortionArea().isEmpty();
                    boolean a26 = collectInfoBean.getCollectInfoDistortionVolume().isEmpty();
                    boolean a27 = collectInfoBean.getCollectInfoSlideDistance().isEmpty();
                    boolean a28 = collectInfoBean.getCollectInfoCrackNumber().isEmpty();
                    boolean a29 = collectInfoBean.getCollectInfoCrackMaxLength().isEmpty();
                    boolean a30 = collectInfoBean.getCollectInfoCrackMaxWidth().isEmpty();
                    boolean a31 = collectInfoBean.getCollectInfoCrackMinLength().isEmpty();
                    boolean a32 = collectInfoBean.getCollectInfoCrackMinWidth().isEmpty();
                    boolean a33 = collectInfoBean.getCollectInfoMaxDislocation().isEmpty();
                    boolean a34 = collectInfoBean.getCollectInfoRockLength().isEmpty();
                    boolean a35 = collectInfoBean.getCollectInfoRockWidth().isEmpty();
                    boolean a36 = collectInfoBean.getCollectInfoRockVolume().isEmpty();
                    boolean a37 = collectInfoBean.getCollectInfoCollapseVolume().isEmpty();
                    boolean a38 = collectInfoBean.getCollectInfoResidualVolume().isEmpty();
                    boolean a39 = collectInfoBean.getCollectInfoAnotherThings().isEmpty();
                    boolean a40 = collectInfoBean.getCollectInfoMeasure().equals("点击选择");
                    boolean a41 = collectInfoBean.getCollectInfoDisposition().equals("点击选择");
                    boolean a42 = collectInfoBean.getCollectInfoGoTime().equals("点击选择");
                    boolean a43 = collectInfoBean.getEmergencyHuNo().isEmpty();
                    boolean a44 = collectInfoBean.getEmergencyPersonNo().isEmpty();
                    boolean a45 = collectInfoBean.getCollectInfoLongitude().isEmpty();
                    boolean a46 = collectInfoBean.getCollectInfoLatitude().isEmpty();
                    if (a2 || a3 || a4 || a5 || a6 || a7 || a8 || a9 || a10 || a11 || a12 || a13 || a14 || a14 || a15 || a16 || a17 || a18 || a19 || a20 || a21 || a22 || a23 || a24 || a25 || a26 || a27 || a28 || a29 || a30 || a31 || a32 || a33 || a34 || a35 || a36 || a37 || a38 || a39 || a40 || a41 || a42 || a43 || a44 || a45 || a46) {
                        ToastUtils.showShortToast("信息未填报完整！");
                    } else {
                        OkHttpUtils.get().url(new ConnectUrl().getTaskStatusUrl())
                                .addParams("taskId", taskInfoBean.getmTaskId())
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        ToastUtils.showShortToast("连接服务器失败！");
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        String status;
                                        try {
                                            JSONObject object = new JSONObject(response);
                                            status = object.getString("status");
                                            if ("300".equals(status)) {
                                                uploadData();
                                            } else if ("200".equals(status)) {
                                                ToastUtils.showShortToast("该任务已完成无需上传信息！");
                                                finish();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    }
                } else {
                    ToastUtils.showShortToast("请先填写信息并保存后再上传！");
                }
            }
        });
    }

    private void uploadData() {
        uploadProgress = new ProgressDialog(FillInfoActivity.this, ProgressDialog.STYLE_SPINNER);
        uploadProgress.setCanceledOnTouchOutside(false);
        uploadProgress.setCancelable(false);
        uploadProgress.setMessage("正在上传...");
        uploadProgress.show();
        String name = baseInfoBean.getBaseInfoName();
        String address = baseInfoBean.getBaseInfoAddress();
        String lookPerson = baseInfoBean.getBaseInfoLookPerson();
        String lookTime = baseInfoBean.getBaseInfoLookTime();
        String happenTime = baseInfoBean.getBaseInfoHappenTime();
        String contact = baseInfoBean.getBaseInfoContact();
        String mobile = baseInfoBean.getBaseInfoMobile();
        String id = taskInfoBean.getmRowNumber();
        String taskId = taskInfoBean.getmTaskId();
        Log.d(TAG, "灾害点ID:" + id);
        OkHttpUtils.post().url(new ConnectUrl().getBaseInfoUrl())
                .addParams("sessionId", sessionId)
                .addParams("dis_name", name)
                .addParams("dis_location", address)
                .addParams("survey_name", lookPerson)
                .addParams("dis_person", contact)
                .addParams("dis_person_phone", mobile)
                .addParams("survey_time", lookTime)
                .addParams("happen_time", happenTime)
                .addParams("id", id)
                .addParams("task_id", taskId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShortToast("连接服务器失败！");
                        uploadProgress.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String status, msg;
                        try {
                            JSONObject object = new JSONObject(response);
                            status = object.getString("status");
                            msg = object.getString("message");
                            if ("200".equals(status)) {
                                upLoadConnectInfo();
                            } else if ("400".equals(status)) {
                                ToastUtils.showShortToast(msg);
                                uploadProgress.dismiss();
                                sp.edit().putBoolean("isLogin", false).apply();
                                startActivity(new Intent(FillInfoActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                ToastUtils.showShortToast(msg);
                                uploadProgress.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void upLoadConnectInfo() {
        CollectInfoBean collectInfo = new CollectInfoBean();
        collectInfo.setTaskId(collectInfoBean.getTaskId());
        collectInfo.setCollectInfoDisId(taskInfoBean.getmRowNumber());
        collectInfo.setCollectInfoLongitude(collectInfoBean.getCollectInfoLongitude());
        collectInfo.setCollectInfoLatitude(collectInfoBean.getCollectInfoLatitude());
        collectInfo.setCollectInfoType(collectInfoBean.getCollectInfoType());
        collectInfo.setCollectInfoDead(collectInfoBean.getCollectInfoDead());
        collectInfo.setCollectInfoMiss(collectInfoBean.getCollectInfoMiss());
        collectInfo.setCollectInfoHeavyInjured(collectInfoBean.getCollectInfoHeavyInjured());
        collectInfo.setCollectInfoSoftInjured(collectInfoBean.getCollectInfoSoftInjured());
        collectInfo.setCollectInfoEconomicLoss(collectInfoBean.getCollectInfoEconomicLoss());
        collectInfo.setCollectInfoHouseCollapseNum(collectInfoBean.getCollectInfoHouseCollapseNum());
        collectInfo.setCollectInfoHouseCollapseArea(collectInfoBean.getCollectInfoHouseCollapseArea());
        collectInfo.setCollectInfoHouseDamageNum(collectInfoBean.getCollectInfoHouseDamageNum());
        collectInfo.setCollectInfoHouseDamageArea(collectInfoBean.getCollectInfoHouseDamageArea());
        collectInfo.setCollectInfoAnotherDamage(collectInfoBean.getCollectInfoAnotherDamage());
        collectInfo.setCollectInfoFamily(collectInfoBean.getCollectInfoFamily());
        collectInfo.setCollectInfoPeople(collectInfoBean.getCollectInfoPeople());
        collectInfo.setCollectInfoAtHomeFamily(collectInfoBean.getCollectInfoAtHomeFamily());
        collectInfo.setCollectInfoAtHomePeople(collectInfoBean.getCollectInfoAtHomePeople());
        collectInfo.setEmergencyHuNo(collectInfoBean.getEmergencyHuNo());
        collectInfo.setEmergencyPersonNo(collectInfoBean.getEmergencyPersonNo());
        collectInfo.setCollectInfoHouse(collectInfoBean.getCollectInfoHouse());
        collectInfo.setCollectInfoHouseArea(collectInfoBean.getCollectInfoHouseArea());
        collectInfo.setCollectInfoAnotherDisaster(collectInfoBean.getCollectInfoAnotherDisaster());
        collectInfo.setCollectInfoLandslideWidth(collectInfoBean.getCollectInfoLandslideWidth());
        collectInfo.setCollectInfoLandslideLength(collectInfoBean.getCollectInfoLandslideLength());
        collectInfo.setCollectInfoLandslideArea(collectInfoBean.getCollectInfoLandslideArea());
        collectInfo.setCollectInfoLandslideVolume(collectInfoBean.getCollectInfoLandslideVolume());
        collectInfo.setCollectInfoDistortionWidth(collectInfoBean.getCollectInfoDistortionWidth());
        collectInfo.setCollectInfoDistortionLength(collectInfoBean.getCollectInfoDistortionLength());
        collectInfo.setCollectInfoDistortionArea(collectInfoBean.getCollectInfoDistortionArea());
        collectInfo.setCollectInfoDistortionVolume(collectInfoBean.getCollectInfoDistortionVolume());
        collectInfo.setCollectInfoSlideDistance(collectInfoBean.getCollectInfoSlideDistance());
        collectInfo.setCollectInfoCrackNumber(collectInfoBean.getCollectInfoCrackNumber());
        collectInfo.setCollectInfoCrackMinWidth(collectInfoBean.getCollectInfoCrackMinWidth());
        collectInfo.setCollectInfoCrackMaxWidth(collectInfoBean.getCollectInfoCrackMaxWidth());
        collectInfo.setCollectInfoCrackMinLength(collectInfoBean.getCollectInfoCrackMinLength());
        collectInfo.setCollectInfoCrackMaxLength(collectInfoBean.getCollectInfoCrackMaxLength());
        collectInfo.setCollectInfoMaxDislocation(collectInfoBean.getCollectInfoMaxDislocation());
        collectInfo.setCollectInfoRockWidth(collectInfoBean.getCollectInfoRockWidth());
        collectInfo.setCollectInfoRockLength(collectInfoBean.getCollectInfoRockLength());
        collectInfo.setCollectInfoRockVolume(collectInfoBean.getCollectInfoRockVolume());
        collectInfo.setCollectInfoCollapseVolume(collectInfoBean.getCollectInfoCollapseVolume());
        collectInfo.setCollectInfoResidualVolume(collectInfoBean.getCollectInfoResidualVolume());
        collectInfo.setCollectInfoAnotherThings(collectInfoBean.getCollectInfoAnotherThings());
        collectInfo.setCollectInfoMeasure(collectInfoBean.getCollectInfoMeasure());
        collectInfo.setCollectInfoMeasureRemark(collectInfoBean.getCollectInfoMeasureRemark());
        collectInfo.setCollectInfoDisposition(collectInfoBean.getCollectInfoDisposition());
        collectInfo.setCollectInfoDispositionRemark(collectInfoBean.getCollectInfoDispositionRemark());
        collectInfo.setCollectInfoGoTime(collectInfoBean.getCollectInfoGoTime());
        String reason = getReason(collectInfoBean.getCollectInfoDisasterReason());
        String level = getLevel(collectInfoBean.getCollectInfoDisasterLevel());
        int type = Integer.valueOf(collectInfoBean.getCollectInfoType()) + 1;
        int disOrDan = Integer.valueOf(collectInfoBean.getCollectInfoDisOrDan()) + 1;
        int isResearch = Integer.valueOf(collectInfoBean.getCollectInfoIsResearch()) + 1;
        int scale = Integer.valueOf(collectInfoBean.getCollectInfoScale()) + 1;
        int isDis = Integer.valueOf(collectInfoBean.getCollectInfoIsDisaster()) + 1;
        int newOrOld = Integer.valueOf(collectInfoBean.getCollectInfoNewOrOld()) + 1;
        collectInfo.setId(Long.valueOf(taskInfoBean.getTaskExtendsId()));
        collectInfo.setCollectInfoDisasterReason(reason);
        collectInfo.setCollectInfoDisasterLevel(level);
        collectInfo.setCollectInfoType(type + "");
        collectInfo.setCollectInfoDisOrDan(disOrDan + "");
        collectInfo.setCollectInfoIsResearch(isResearch + "");
        collectInfo.setCollectInfoScale(scale + "");
        collectInfo.setCollectInfoIsDisaster(isDis + "");
        collectInfo.setCollectInfoNewOrOld(newOrOld + "");
        if (collectInfoBean.getCollectInfoMeasure().equals("点击选择")) {
            collectInfo.setCollectInfoMeasure("");
        }
        if (collectInfoBean.getCollectInfoDisposition().equals("点击选择")) {
            collectInfo.setCollectInfoDisposition("");
        }
        if (collectInfoBean.getCollectInfoGoTime().equals("点击选择")) {
            collectInfo.setCollectInfoGoTime("");
        }
        Gson gson = new Gson();
        String info = gson.toJson(collectInfo);
        Log.d(TAG, "采集信息json字符串：" + info);
        OkHttpUtils.post().url(new ConnectUrl().getConnectInfoUrl())
                .addParams("info", info)
                .addParams("sessionId", sessionId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShortToast("连接服务器失败！");
                        uploadProgress.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String status, msg;
                        try {
                            JSONObject object = new JSONObject(response);
                            status = object.getString("status");
                            msg = object.getString("message");
                            if ("200".equals(status)) {
                                ToastUtils.showShortToast(msg);
                                uploadProgress.dismiss();
                                finish();
                            } else if ("400".equals(status)) {
                                ToastUtils.showShortToast(msg);
                                sp.edit().putBoolean("isLogin", false).apply();
                                startActivity(new Intent(FillInfoActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                ToastUtils.showShortToast(msg);
                                uploadProgress.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }


    private String getLevel(String collectInfoDisasterLevel) {
        String result = "";
        if ("0".equals(collectInfoDisasterLevel)) {
            result = "5";
        } else if ("1".equals(collectInfoDisasterLevel)) {
            result = "27";
        } else if ("2".equals(collectInfoDisasterLevel)) {
            result = "33";
        } else {
            result = "35";
        }
        return result;
    }

    private String getReason(String collectInfoDisasterReason) {
        String result = "";
        if ("0".equals(collectInfoDisasterReason)) {
            result = "46";
        } else if ("1".equals(collectInfoDisasterReason)) {
            result = "45";
        } else if ("2".equals(collectInfoDisasterReason)) {
            result = "44";
        } else if ("3".equals(collectInfoDisasterReason)) {
            result = "43";
        } else if ("4".equals(collectInfoDisasterReason)) {
            result = "39";
        } else {
            result = "26";
        }
        return result;
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}

