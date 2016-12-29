package com.yeepbank.android.activity.fragment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.android.volley.VolleyError;
import com.yeepbank.android.Cst;
import com.yeepbank.android.R;
import com.yeepbank.android.activity.business.HomeActivity;
import com.yeepbank.android.adapter.ProjectListAdapter;
import com.yeepbank.android.adapter.TransProjectListAdapter;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.base.BaseFragment;
import com.yeepbank.android.http.StringListener;
import com.yeepbank.android.model.business.Project;
import com.yeepbank.android.model.business.TranProject;
import com.yeepbank.android.request.business.ProjectListRequest;
import com.yeepbank.android.request.business.TransformListRequest;
import com.yeepbank.android.response.business.ProjectListResponse;
import com.yeepbank.android.response.business.TransformResponse;
import com.yeepbank.android.utils.LoadDialog;
import com.yeepbank.android.widget.PullToRefresh;
import com.yeepbank.android.widget.SwitchItemLayout;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by WW on 2015/11/10.
 */
public class TransformListFragment extends BaseFragment implements
        View.OnClickListener,Cst.OnRefresh<PullToRefresh>,View.OnTouchListener{

    private View navigationBar;
    private SwitchItemLayout switchItemLayout;
    private View backLayout;
    private PullToRefresh projectListView;
    private TransProjectListAdapter transProjectListAdapter;
    private ArrayList<TranProject> tranProjectArray;//从服务端获取的数据集合
    private LoadDialog loadDialog;
    private Handler msgHandler;
    private static final int REFRESH_LIST = 0;
    private Context mContext;

    private  int TransferingPage = 0;//转让中，当前页数；
    private  int TransferedPage = 0;//已转让，当前页数
    public static final int pageSize = 10;//每页条目数
    private static final String IPB = "IPB";//转让中
    private static final String SCS = "SCS";//转让成功

    private static final String ASC = "asc";//项目期限
    private static final String DESC = "desc";//预期收益

    private static final int DEFAULT = 0;//默认排序
    private static final int ANNUAL_INTEREST_RATE = 1;//年化利率排序
    private static final int EXPECTED_RETURN = 2;//预期收益
    private static final int PROJECT_DURATION = 3;//项目期限


    //private static final String DEFAULT_COMPARE = "TP";//默认排序-投资金额
    private static final String DEFAULT_COMPARE = "BR";//默认排序-投资金额
    private static final String ANNUAL_INTEREST_RATE_COMPARE = "BR";//年化利率排序
    private static final String EXPECTED_RETURN_COMPARE = "RSP";//预期收益
    private static final String PROJECT_DURATION_COMPARE = "BHD";//项目期限
    private  String CURRENT_COMPARE_METHOD = DEFAULT_COMPARE;


    private String status = IPB;
    private String sortColumn = DEFAULT_COMPARE;
    private String sort = ASC;
    private HashMap<String,ArrayList<TranProject[]>> statusList;
    private ImageView sortImg;
    private PopupWindow popupWindow;//排序窗口
    private View popupWindowView;
    private boolean once = true;
    private boolean isLoading = false;

    private String currentCount;
    private boolean isCompleteLoad = false;
    private boolean isFirst = true;

    @Override
    public void initView(View view) {

        navigationBar = view.findViewById(R.id.navigation_bar);
        backLayout = view.findViewById(R.id.back_layout);
        backLayout.setOnClickListener(this);

        navigationBar.setVisibility(View.VISIBLE);

        navigationBar.findViewById(R.id.share).setVisibility(View.INVISIBLE);
        sortImg = (ImageView) navigationBar.findViewById(R.id.screening);
        sortImg.setVisibility(View.VISIBLE);
        sortImg.setOnClickListener(this);
        switchItemLayout = (SwitchItemLayout) navigationBar.findViewById(R.id.switch_project_bar);
        switchItemLayout.setVisibility(View.VISIBLE);
        switchItemLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isFirst) {
                    isFirst = !isFirst;
                    switchItemLayout.setButtonText("转让中(" + HomeActivity.transTotalCount + ")", "已转让");

                } else if (isCompleteLoad) {
                    if (status.equals(IPB)) {
                        switchItemLayout.setLeftButtonText("转让中(" + currentCount + ")");
                    } else {
                        switchItemLayout.setRightButtonText("已转让(" + currentCount + ")");
                    }
                    isCompleteLoad = false;

                }

            }
        });

        switchItemLayout.setOnSlideCompleteListener(new Cst.OnSlideCompleteListener() {
            @Override
            public void onComplete(View view) {
                projectListView.reset();
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                if (status.equals(IPB)) {
                    status = SCS;
                } else {
                    status = IPB;
                }
                if (statusList.get(status).size() > 0) {
                    transProjectListAdapter.getData().clear();
                    transProjectListAdapter.getData().addAll(statusList.get(status));
                    transProjectListAdapter.notifyDataSetChanged();
                } else {
                    transProjectListAdapter.getData().clear();
                    transProjectListAdapter.notifyDataSetChanged();
                    if (status.equals(IPB)) {
                        TransferingPage = 0;
                    } else {
                        TransferedPage = 0;
                    }
                    loadData(CURRENT_COMPARE_METHOD);
                }

            }
        });

        projectListView = (PullToRefresh) view.findViewById(R.id.transform_list);

        projectListView.setOnRefresh(this);
        statusList = new HashMap<String,ArrayList<TranProject[]>>();
        statusList.put(IPB, new ArrayList<TranProject[]>());
        statusList.put(SCS, new ArrayList<TranProject[]>());
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
    }
    private void setProjectListData(){
        if(transProjectListAdapter == null){
            return;
        }
        int page;
        if(status.equals(IPB)){
            page = TransferingPage;
        }else {
            page = TransferedPage;
        }
        if(page == 0){
            statusList.get(status).clear();
        }

        TranProject[] projects = null;
        if(statusList.get(status) != null && statusList.get(status).size() > 0){
            if(statusList.get(status).get(statusList.get(status).size() - 1)[1] == null){
                statusList.get(status).get(statusList.get(status).size() - 1)[1] =  tranProjectArray.get(0);
                tranProjectArray.remove(0);
            }
        }
        for (int i = 0; i < tranProjectArray.size(); i++) {
            if(i % 2 == 0){
                projects = new TranProject[2];
                statusList.get(status).add(projects);
            }

            projects[i % 2] = tranProjectArray.get(i);
        }
        transProjectListAdapter.getData().clear();
        transProjectListAdapter.getData().addAll(statusList.get(status));
        transProjectListAdapter.notifyDataSetChanged();
        if(status.equals(IPB)){
            TransferingPage++;
        }else {
            TransferedPage++;
        }
    }


    @Override
    public int getLayoutResource() {
        return R.layout.transform_list;
    }

    @Override
    public void fillData() {
        transProjectListAdapter = new TransProjectListAdapter(new ArrayList<TranProject[]>(), getActivity());
        projectListView.setBaseAdapter(transProjectListAdapter);

    }

    @Override
    public void onShow(Context context) {
        if(transProjectListAdapter!=null && transProjectListAdapter.getData() != null){
            transProjectListAdapter.getData().clear();
            transProjectListAdapter.notifyDataSetChanged();
        }
        TransferingPage = 0;
        TransferedPage = 0;
        sort = DESC;
        loadData(CURRENT_COMPARE_METHOD);
    }

    private void loadData(String sortColumn){
        if(isLoading || ((BaseActivity)getActivity()).isNetErrorNow){
            return;
        }
        isLoading = true;
        int page;
        if(status.equals(IPB)){
            page = TransferingPage;
        }else {
            page = TransferedPage;
        }
        loadDialog.showAs();
        TransformListRequest transformListRequest = new TransformListRequest(getActivity(), new StringListener() {
            @Override
            public void ResponseListener(String result) {
                TransformResponse transformResponse = new TransformResponse();
                loadDialog.dismiss();
                Log.e("result","result:"+result);
                if(transformResponse.getStatus(result) == 200){

                    if(tranProjectArray!=null && tranProjectArray.size() > 0){
                        tranProjectArray.clear();
                    }
                    tranProjectArray = transformResponse.getObject(result);
                    currentCount = transformResponse.getTransProjectCount(result);
                    isCompleteLoad = true;

                    if(tranProjectArray != null && tranProjectArray.size() > 0){
                        setProjectListData();
                    }
                    projectListView.getHandler().obtainMessage(Cst.COMMON.NO_MORE,true).sendToTarget();
                    isLoading = false;
                }else {
                    projectListView.getHandler().obtainMessage(Cst.COMMON.NO_MORE, false).sendToTarget();
                    if(isAdded()){
                        ((BaseActivity)getActivity()).toast(transformResponse.getMessage(result));
                    }
                    isLoading = false;

                }
            }

            @Override
            public void ErrorListener(VolleyError volleyError) {
                isLoading = false;
                Handler handler = projectListView.getHandler();
                Message msg = handler.obtainMessage();
                msg.what = Cst.COMMON.NO_MORE;
                msg.obj = false;
                handler.sendMessage(msg);
                if(isAdded()){
                    ((BaseActivity)getActivity()).showErrorMsg("请求异常",navigationBar);
                }
                loadDialog.dismiss();
            }
        },page,pageSize,status,sortColumn,sort);
        transformListRequest.stringRequest();

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_layout:
                if(popupWindow != null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                ((HomeActivity)getActivity()).showFragment(R.id.invest);
                break;
            case R.id.screening:
                if(popupWindow != null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                }else {
                    showSortPanel();
                }
                break;
            case R.id.default_sort:
                if(popupWindow != null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                compare(DEFAULT);
                break;
            case R.id.annual_interest_rate:
                if(popupWindow != null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                compare(ANNUAL_INTEREST_RATE);
                break;
            case R.id.expected_return:
                if(popupWindow != null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                compare(EXPECTED_RETURN);
                break;
            case R.id.project_duration:
                if(popupWindow != null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                compare(PROJECT_DURATION);
                break;
        }
    }

    private void compare(int state) {
        if(status.equals(IPB)){
            TransferingPage = 0;
        }else {
            TransferedPage = 0;
        }
        switch (state) {
            case DEFAULT:
                CURRENT_COMPARE_METHOD = DEFAULT_COMPARE;
                sort = DESC;
                loadData(DEFAULT_COMPARE);
                break;
            case ANNUAL_INTEREST_RATE:
                sort = DESC;
                CURRENT_COMPARE_METHOD = ANNUAL_INTEREST_RATE_COMPARE;
                loadData(ANNUAL_INTEREST_RATE_COMPARE);
                break;
            case EXPECTED_RETURN:
                sort = DESC;
                CURRENT_COMPARE_METHOD = EXPECTED_RETURN_COMPARE;
                loadData(EXPECTED_RETURN_COMPARE);
                break;
            case PROJECT_DURATION:
                sort = ASC;
                CURRENT_COMPARE_METHOD = PROJECT_DURATION_COMPARE;
                loadData(PROJECT_DURATION_COMPARE);
                break;
        }

    }

    private int getDuration(String duration,String durationUnit) {
        int unit = 1;
        if(durationUnit.equals("Y")){
            unit = 365;
        }else if(durationUnit.equals("M")){
            unit = 30;
        }
        return Integer.parseInt(duration) * unit;
    }


    private void showSortPanel(){
        if (popupWindow == null){

            popupWindowView = LayoutInflater.from(getActivity()).inflate(R.layout.sort_panel,null);
            popupWindowView.setOnTouchListener(this);

            popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setAnimationStyle(R.style.sort_exist_style);
            initPopupWindowView(popupWindowView);

        }
        popupWindow.showAsDropDown(navigationBar);

    }

    private void initPopupWindowView(View popupWindowView) {
        LinearLayout defaultSortLayout = (LinearLayout) popupWindowView.findViewById(R.id.default_sort);
        LinearLayout annualInterestRateLayout = (LinearLayout) popupWindowView.findViewById(R.id.annual_interest_rate);
        LinearLayout expectedReturnLayout = (LinearLayout) popupWindowView.findViewById(R.id.expected_return);
        LinearLayout projectDurationLayout = (LinearLayout) popupWindowView.findViewById(R.id.project_duration);
        defaultSortLayout.setOnClickListener(this);
        annualInterestRateLayout.setOnClickListener(this);
        expectedReturnLayout.setOnClickListener(this);
        projectDurationLayout.setOnClickListener(this);
    }


    @Override
    public void refresh(PullToRefresh target) {
    }

    @Override
    public void loadMore() {
        loadData(CURRENT_COMPARE_METHOD);
    }

    public PopupWindow getPopupWindow(){
        return popupWindow;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(event.getX() < popupWindowView.findViewById(R.id.popu_window_view).getLeft() ||
                        event.getX() > popupWindowView.findViewById(R.id.popu_window_view).getRight()||
                        event.getY() > popupWindowView.findViewById(R.id.popu_window_view).getBottom()){
                    if (popupWindow.isShowing()){
                        popupWindow.dismiss();
                    }
                }
                return true;
        }
        return false;
    }
}
