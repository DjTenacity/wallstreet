package com.yeepbank.android.activity.fragment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.adapter.ProjectListAdapter;
//import com.yeepbank.android.adapter.TransProjectListAdapter;
import com.yeepbank.android.base.AbstractAdapter;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.base.BaseFragment;
import com.yeepbank.android.base.BaseModel;
import com.yeepbank.android.http.StringListener;

import com.yeepbank.android.request.business.ProjectListRequest;
import com.yeepbank.android.response.business.ProjectListResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.widget.AnimationProgressBar;
import com.yeepbank.android.widget.PullToRefresh;
import com.yeepbank.android.widget.RefreshScrollerView;
import com.yeepbank.android.widget.SwitchItemLayout;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by WW on 2015/10/13.
 */
public class ProjectListFragment extends BaseFragment implements View.OnClickListener,Cst.OnRefresh<PullToRefresh> {
    private View navigationBar;

    private SwitchItemLayout switchItemLayout;
    private View backLayout;
    private PullToRefresh projectListView;
    private ProjectListAdapter projectListAdapter;
    private ArrayList<BaseModel> projectArray;//从服务端获取的数据集合
    private LoadDialog loadDialog;
    private Handler msgHandler;
    private static final int REFRESH_LIST = 0;
    private Context mContext;
    private int page = 0;//当前页数
    private static final String IRP = "IRP";//还款中
    private static final String NCL = "NCL";//已还款
    private String status = IRP;
    private HashMap<String,ArrayList<BaseModel[]>> statusList;
    private boolean isLoading = false;
    private boolean isFirst = true;
    private String currentCount;
    private boolean isCompleteLoad = false;


    @Override
    public void initView(View view) {
        navigationBar = view.findViewById(R.id.navigation_bar);
        backLayout = view.findViewById(R.id.back_layout);
        backLayout.setOnClickListener(this);

        navigationBar.setVisibility(View.VISIBLE);
        navigationBar.findViewById(R.id.share).setVisibility(View.INVISIBLE);

        switchItemLayout = (SwitchItemLayout) navigationBar.findViewById(R.id.switch_project_bar);
        switchItemLayout.setVisibility(View.VISIBLE);
        switchItemLayout.setOnSlideCompleteListener(new Cst.OnSlideCompleteListener() {
            @Override
            public void onComplete(View view) {
                if (status.equals(IRP)) {
                    status = NCL;
                } else {
                    status = IRP;
                }
                if (statusList.get(status).size() > 0) {
                    projectListAdapter.getData().clear();
                    projectListAdapter.getData().addAll(statusList.get(status));
                    projectListAdapter.notifyDataSetChanged();
                } else {
                    projectListAdapter.getData().clear();
                    projectListAdapter.notifyDataSetChanged();
                    page = 0;
                    loadData();
                }

            }
        });
        switchItemLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(isFirst){
                    isFirst = !isFirst;
                    switchItemLayout.setButtonText("还款中("+HomeActivity.totalCount+")", "已还款");

                }else if(isCompleteLoad){
                    isCompleteLoad = false;
                    if(status.equals(IRP)){
                        switchItemLayout.setLeftButtonText("还款中("+currentCount+")");
                    }else {
                        switchItemLayout.setRightButtonText("已还款("+currentCount+")");
                    }
                }
            }
        });
        projectListView = (PullToRefresh) view.findViewById(R.id.project_list);
        projectListView.setOnRefresh(this);
        loadDialog = ((HomeActivity)getActivity()).loadding;

        msgHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case REFRESH_LIST:
                        break;
                }
            }
        };

        statusList = new HashMap<String,ArrayList<BaseModel[]>>();
        statusList.put(IRP, new ArrayList<BaseModel[]>());
        statusList.put(NCL, new ArrayList<BaseModel[]>());
    }
    private void setProjectListData(){
        if(projectListAdapter == null){
            return;
        }
        if(page == 0){
            statusList.get(status).clear();
        }



        BaseModel[] projects = null;
        if(statusList.get(status) != null && statusList.get(status).size() > 0){
            if(statusList.get(status).get(statusList.get(status).size() - 1)[1] == null){
                statusList.get(status).get(statusList.get(status).size() - 1)[1] =  projectArray.get(0);
                projectArray.remove(0);
            }
        }
        for (int i = 0; i < projectArray.size(); i++) {
            if(i % 2 == 0){
                projects = new BaseModel[2];
                statusList.get(status).add(projects);
            }

            projects[i % 2] = projectArray.get(i);
        }
        projectListAdapter.getData().clear();
        projectListAdapter.getData().addAll(statusList.get(status));
        projectListAdapter.notifyDataSetChanged();
        loadDialog.dismiss();
        page++;
    }


    @Override
    public int getLayoutResource() {
        return R.layout.project_list;
    }

    @Override
    public void fillData() {
        projectListAdapter = new ProjectListAdapter(new ArrayList<BaseModel[]>(), getActivity());
        projectListView.setBaseAdapter(projectListAdapter);

    }

    @Override
    public void onShow(Context context) {
        if(projectListAdapter!=null && projectListAdapter.getData() != null){
            projectListAdapter.getData().clear();
            projectListAdapter.notifyDataSetChanged();
        }
        page = 0;
        loadData();
    }

    private void loadData(){
        if(isLoading || ((BaseActivity)getActivity()).isNetErrorNow){
            return;
        }
        isLoading = true;
        loadDialog.showAs();
        ProjectListRequest projectListRequest = new ProjectListRequest(getActivity(), new StringListener() {
            @Override
            public void ResponseListener(String result) {

                ProjectListResponse projectListResponse = new ProjectListResponse();
                if(projectListResponse.getStatus(result) == 200){

                    if(projectArray!=null && projectArray.size() > 0){
                        projectArray.clear();
                    }

                    projectArray = projectListResponse.getObject(result);
                    currentCount = projectListResponse.getNorProjectCount(result);
                    isCompleteLoad = true;
                    if(projectArray != null && projectArray.size() > 0){
                        setProjectListData();
                    }else {
                        loadDialog.dismiss();
                    }

                }else{
                    loadDialog.dismiss();
                    projectListView.getHandler().obtainMessage(Cst.COMMON.NO_MORE,false).sendToTarget();
                    if(isAdded()){
                        ((BaseActivity)getActivity()).toast(projectListResponse.getMessage(result));
                    }
                }
                isLoading = false;
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                //((BaseActivity)getActivity()).showErrorMsg(mContext.getString(R.string.net_error),navigationBar);
                loadDialog.dismiss();
                isLoading = false;
            }
        },page,Cst.PARAMS.PAGE_SIZE,status);
        projectListRequest.stringRequest();

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_layout:
                ((HomeActivity)getActivity()).showFragment(R.id.invest);
                break;
        }
    }

    @Override
    public void refresh(PullToRefresh target) {
    }

    @Override
    public void loadMore() {
        loadData();
    }

}
