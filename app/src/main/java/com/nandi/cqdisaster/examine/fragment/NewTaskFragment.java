package com.nandi.cqdisaster.examine.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nandi.cqdisaster.MyAPP;
import com.nandi.cqdisaster.R;
import com.nandi.cqdisaster.examine.activity.CreateTaskActivity;
import com.nandi.cqdisaster.examine.activity.QueryDisasterActivity;
import com.nandi.cqdisaster.examine.bean.CItem;
import com.nandi.cqdisaster.examine.constant.ConnectUrl;
import com.nandi.cqdisaster.examine.database.TaskBean;
import com.nandi.cqdisaster.examine.utils.DateTimePickUtil;
import com.nandi.cqdisaster.examine.utils.MyUtils;
import com.nandi.cqdisaster.examine.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

import static android.app.Activity.RESULT_OK;

/**
 * Created by lemon on 2017/2/22.
 *
 * 创建新任务
 */

public class NewTaskFragment extends Fragment {
    private String TAG="NewTaskFragment";
    private View view;
    private List<String> mDisasterList;
    private ArrayAdapter<String> mDisasterAdapter;

    private List<String> mTownshipList;
    List<CItem> CITownship = new ArrayList<CItem>();
    private ArrayAdapter<String> mTownshipAdapter;

    private List<String> mDepartmentList;
    private ArrayAdapter<String> mDepartmentAdapter;

    private List<String> mWorkersList;
    List<CItem> CIWorkers = new ArrayList<CItem>();
    private ArrayAdapter<String> mWorkersAdapter;

    /*灾害发生时间*/
    private TextView tvHanppenTime;
    /*调查时间*/
    private TextView etNewtaskTime;
    /*任务过期时间*/
    private TextView tvNewtaskOverTime;
    /* 任务发起人*/
    private TextView tvName;
    /*灾害点*/
    private TextView tvDisaster;
    /*调查地点*/
    private EditText etNewtaskDisaster;
    /*乡镇*/
    private Spinner spTownship;
    /*部门*/
    //private Spinner spDepartment;
    /*人员*/
    private Spinner spWorkers;


    private Button btnSave;
    private Button btnSubmit;
    private Button btnCancle;
    private ProgressDialog progressDialog;
    private SharedPreferences sp;
    private String sessionId;
    private String userName;
    private TaskBean taskBean;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_newtask, container, false);
        sp = getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        sessionId=sp.getString("sessionId", "");
        userName=sp.getString("userName","");
        initViews();
        initData();
        setLinsteners();
        progressDialog = new ProgressDialog(getContext(), ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("正在加载...");
        progressDialog.show();
        setOkHttp();
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                Bundle b=data.getExtras(); //data为B中回传的Intent
                String str1=b.getString("str1","");//disasterID
                String str2=b.getString("str2","");//disaster
                tvDisaster.setText(str2);
                taskBean.setMDisasterID(str1);
                taskBean.setMDisaster(str2);
                break;
            default:
                break;
        }
    }

    private void setLinsteners() {

        tvDisaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), QueryDisasterActivity.class);
                startActivityForResult(intent,0);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTaskBean();
                Log.d(TAG,"---"+taskBean.toString());
                MyAPP.getDaoSession().getTaskBeanDao().insert(taskBean);
                Toast.makeText(getActivity(),"保存成功",Toast.LENGTH_SHORT).show();
                cleanAll();
                ((CreateTaskActivity)getActivity()).setTab(1);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,taskBean.toString());
                if (isNotDataEmpty()) {
                    getTaskBean();
                    setOkHttpCommit();
                }else{
                    ToastUtils.showShortToast("请填写完整信息！");
                }
            }
        });

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanAll();
            }

        });
        etNewtaskTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickUtil dateTimePicKDialog = new DateTimePickUtil(
                        getActivity(),new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date()));
                dateTimePicKDialog.dateTimePicKDialog(etNewtaskTime);

            }
        });

        tvHanppenTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickUtil dateTimePicKDialog = new DateTimePickUtil(
                        getActivity(),new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date()));
                dateTimePicKDialog.dateTimePicKDialog(tvHanppenTime);

            }
        });

        tvNewtaskOverTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickUtil dateTimePicKDialog = new DateTimePickUtil(
                        getActivity(),new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date()));
                dateTimePicKDialog.dateTimePicKDialog(tvNewtaskOverTime);
            }
        });

        tvNewtaskOverTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String date1 = MyUtils.getSystemTime();
                String date2 = s.toString().trim();
                int bl=MyUtils.TimeCompare(1, date1, date2);
                Log.d(TAG, "----bl" + bl);
                if (!("".equals(date2))){
                    Log.d(TAG, "----date1" + date1);
                    Log.d(TAG, "----date2" + date2);
                    if (bl==1) {
                        //taskBean.setMOverTime(s.toString());
                    } else {
                        tvNewtaskOverTime.setText("");
                        ToastUtils.showShortToast("请选择今天以后的时间！");
                    }
                }
            }
        });
    }



    private String initStartDateTime = "2013年9月3日 14:44:00"; // 初始化开始时间

    private void getTaskBean(){
        if("".equals(etNewtaskTime.getText().toString().trim())){
        }else{
            taskBean.setMTime(etNewtaskTime.getText().toString().trim());
        }
        if("".equals(tvHanppenTime.getText().toString().trim())){
        }else{
            taskBean.setMHappenTime(tvHanppenTime.getText().toString().trim());
        }

        if("".equals(etNewtaskDisaster.getText().toString().trim())){
        }else{
            taskBean.setMAddress(etNewtaskDisaster.getText().toString().trim());
        }
        if("".equals(tvNewtaskOverTime.getText().toString().trim())){
        }else{
            taskBean.setMOverTime(tvNewtaskOverTime.getText().toString().trim());
        }

        Log.d(TAG,spTownship.getSelectedItemPosition()+"-"+CITownship.get(spTownship.getSelectedItemPosition()).GetID());

        if("-1".equals(CITownship.get(spTownship.getSelectedItemPosition()).GetID())){
        }else{
            taskBean.setMTownshipID(CITownship.get(spTownship.getSelectedItemPosition()).GetID());
            taskBean.setMTownship(CITownship.get(spTownship.getSelectedItemPosition()).GetValue());
            Log.d(TAG,taskBean.getMTownshipID()+taskBean.getMTownship());
        }

        if("-1".equals(CIWorkers.get(spWorkers.getSelectedItemPosition()).GetID())){
        }else{
            taskBean.setMWorkersID(CIWorkers.get(spWorkers.getSelectedItemPosition()).GetID());
            taskBean.setMWorkers(CIWorkers.get(spWorkers.getSelectedItemPosition()).GetValue());
        }

    }

    private boolean isNotDataEmpty() {
        if ("".equals(tvHanppenTime.getText().toString().trim())){return false;}
        if ("".equals(etNewtaskTime.getText().toString().trim())){return false;}
        if ("".equals(tvNewtaskOverTime.getText().toString().trim())){return false;}
        if ("请选择灾害点".equals(tvDisaster.getText().toString().trim())){return false;}
        if ("".equals(etNewtaskDisaster.getText().toString().trim())){return false;}
        if ("-1".equals(CITownship.get(spTownship.getSelectedItemPosition()).GetID())){return false;}
        if ("-1".equals(CIWorkers.get(spWorkers.getSelectedItemPosition()).GetID())){return false;}
        return true;
    }

    private void cleanAll() {
        taskBean=new TaskBean();
        tvDisaster.setText("请选择灾害点");
        tvHanppenTime.setText("");
        etNewtaskTime.setText("");
        tvNewtaskOverTime.setText("");
        etNewtaskDisaster.setText("");
        spTownship.setSelection(0,true);
        //spDepartment.setSelection(0,true);
        spWorkers.setSelection(0,true);
    }

    private void initViews() {
        tvHanppenTime= (TextView) view.findViewById(R.id.tv_newtask_hanppen_time);
        tvName= (TextView) view.findViewById(R.id.tv_newtask_name);
        etNewtaskTime = (TextView) view.findViewById(R.id.et_newtask_time);
        tvNewtaskOverTime= (TextView) view.findViewById(R.id.et_newtask_overtime);
        tvDisaster = (TextView) view.findViewById(R.id.tv_disaster);
        spTownship = (Spinner) view.findViewById(R.id.sp_township);
        //spDepartment = (Spinner) view.findViewById(R.id.sp_department);
        spWorkers = (Spinner) view.findViewById(R.id.sp_workers);
        etNewtaskDisaster = (EditText) view.findViewById(R.id.et_newtask_disaster);
        btnSave = (Button) view.findViewById(R.id.btn_newtask_save);
        btnSubmit = (Button) view.findViewById(R.id.btn_newtask_submit);
        btnCancle = (Button) view.findViewById(R.id.btn_newtask_cancle);
    }

    private void initData() {
        taskBean=new TaskBean();
        tvHanppenTime.setText("");
        etNewtaskTime.setText("");
        tvName.setText(userName);
        //灾害点
        tvDisaster.setText("请选择灾害点");

        //乡镇
        mTownshipList = new ArrayList<String>();
        CITownship=new ArrayList<CItem>();
        mTownshipList.add("请选择乡镇");
        mTownshipAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mTownshipList);
        mTownshipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTownship.setAdapter(mTownshipAdapter);


        //人员
        mWorkersList = new ArrayList<String>();
        CITownship=new ArrayList<CItem>();
        mWorkersList.add("请选择人员");
        mWorkersAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mWorkersList);
        mWorkersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spWorkers.setAdapter(mWorkersAdapter);

    }


    private void setOkHttpCommit() {
        progressDialog.show();
        try {
            OkHttpUtils.get().url(new ConnectUrl().saveSuveyTaskUrl())
                    .addParams("sessionId", sessionId)
                    .addParams("happen_time", taskBean.getMHappenTime())
                    .addParams("survey_time", taskBean.getMTime())
                    .addParams("overTime",taskBean.getMOverTime())
                    .addParams("dis_id", taskBean.getMDisasterID())
                    .addParams("dis_name", taskBean.getMDisaster())
                    .addParams("survey_site", taskBean.getMAddress())
                    .addParams("area_id", taskBean.getMTownshipID())
                    .addParams("survey_id", taskBean.getMWorkersID())
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            progressDialog.dismiss();
                            //getActivity().finish();
                            ToastUtils.showShortToast("网络故障，请检查网络！");
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            progressDialog.dismiss();
                            String msg, status;
                            Log.d(TAG, "返回的数据：" + response);
                            try {
                                JSONObject object = new JSONObject(response);
                                status = object.getString("status");
                                msg = object.getString("message");
                                if ("200".equals(status)) {
                                    getActivity().finish();
                                    ToastUtils.showShortToast(msg);
                                } else if ("400".equals(status)) {
                                    ToastUtils.showShortToast(msg);
                                    sp.edit().putBoolean("isLogin",false).apply();
//                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
//                                    getActivity().startActivity(intent);
//                                    getActivity().finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            ToastUtils.showShortToast("加载失败！");
        }

    }

    private void setOkHttp() {

        try {
            OkHttpUtils.get().url(new ConnectUrl().getAreaListUrl())
                    .addParams("sessionId",sessionId)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            progressDialog.dismiss();
                            getActivity().finish();
                            ToastUtils.showShortToast("网络故障，请检查网络！");
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            progressDialog.dismiss();
                            String msg, status;
                            Log.d(TAG, "返回的数据：" + response);
                            try {
                                JSONObject object = new JSONObject(response);
                                status = object.getString("status");
                                msg = object.getString("message");
                                if ("200".equals(status)) {
                                    mTownshipList.clear();
                                    CITownship.clear();
                                    mTownshipList.add("请选择乡镇");
                                    CITownship.add(new CItem("-1","请选择人员"));
                                    JSONArray message=object.getJSONArray("message");
                                    for(int i = 0; i < message.length(); i++){//遍历JSONArray
                                        JSONObject oj = message.getJSONObject(i);
                                        mTownshipList.add(oj.getString("area_name"));
                                        CITownship.add(new CItem(oj.getString("id"),oj.getString("area_name")));
                                    }
                                     mTownshipAdapter.notifyDataSetChanged();

                                } else if ("400".equals(status)) {
                                    ToastUtils.showShortToast(msg);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            ToastUtils.showShortToast("加载失败！");
        }

        try {
            OkHttpUtils.get().url(new ConnectUrl().getPersonListUrl())
                    .addParams("sessionId",sessionId)
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
                            Log.d(TAG, "返回的数据：" + response);
                            try {
                                JSONObject object = new JSONObject(response);
                                status = object.getString("status");
                                msg = object.getString("message");
                                if ("200".equals(status)) {
                                    mWorkersList.clear();
                                    CIWorkers.clear();
                                    mWorkersList.add("请选择人员");
                                    CIWorkers.add(new CItem("-1","请选择人员"));
                                    JSONArray message=object.getJSONArray("message");
                                    for(int i = 0; i < message.length(); i++){//遍历JSONArray
                                        JSONObject oj = message.getJSONObject(i);
                                        mWorkersList.add(oj.getString("name"));
                                        CIWorkers.add(new CItem(oj.getString("id"),oj.getString("name")));
                                    }
                                    mWorkersAdapter.notifyDataSetChanged();

                                } else if ("400".equals(status)) {
                                    ToastUtils.showShortToast(msg);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            ToastUtils.showShortToast("加载失败！");
        }

    }
}