package com.sixbexchange.mvp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.TypeReference;
import com.fivefivelike.mybaselibrary.base.BasePullFragment;
import com.fivefivelike.mybaselibrary.utils.GsonUtil;
import com.fivefivelike.mybaselibrary.utils.callback.DefaultClickLinsener;
import com.sixbexchange.R;
import com.sixbexchange.adapter.WalletCoinAdapter;
import com.sixbexchange.entity.bean.WalletCoinBean;
import com.sixbexchange.mvp.databinder.BaseFragmentPullBinder;
import com.sixbexchange.mvp.delegate.BaseFragentPullDelegate;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
* 钱包资产列表
* @author gqf
* @Description
* @Date  2019/4/3 0003 11:15
* @Param
* @return
**/

public class ExchWalletFragment extends BasePullFragment<BaseFragentPullDelegate, BaseFragmentPullBinder> {

    @Override
    protected Class<BaseFragentPullDelegate> getDelegateClass() {
        return BaseFragentPullDelegate.class;
    }

    @Override
    public BaseFragmentPullBinder getDataBinder(BaseFragentPullDelegate viewDelegate) {
        return new BaseFragmentPullBinder(viewDelegate);
    }


    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
    }

    public static ExchWalletFragment newInstance(
            String typeStr,
            int position) {
        ExchWalletFragment newFragment = new ExchWalletFragment();
        Bundle bundle = new Bundle();
        bundle.putString("typeStr", typeStr);
        bundle.putInt("position", position);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    String typeStr = "";
    int position = 0;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("typeStr", typeStr);
        outState.putInt("position", position);
    }

    WalletCoinAdapter adapter;


    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (savedInstanceState != null) {
            typeStr = savedInstanceState.getString("typeStr", "");
            position = savedInstanceState.getInt("position");
        } else {
            typeStr = this.getArguments().getString("typeStr", "");
            position = this.getArguments().getInt("position");
        }
        initList(new ArrayList<WalletCoinBean>());
        onRefresh();
    }

    private void initList(List<WalletCoinBean> data) {
        if (adapter == null) {
            adapter = new WalletCoinAdapter(getActivity(), data);
            adapter.setDefaultClickLinsener(new DefaultClickLinsener() {
                @Override
                public void onClick(View view, int p, Object item) {
                    if (getParentFragment().getParentFragment() instanceof MainFragment) {
                        if (view.getId() == R.id.tv_withdraw) {
                            ((MainFragment) getParentFragment().getParentFragment()).startBrotherFragment(
                                    WithdrawCoinFragment.newInstance(adapter.getDatas().get(p).getCoin(),position));
                        } else if (view.getId() == R.id.tv_transfer) {
                            ((MainFragment) getParentFragment().getParentFragment()).startBrotherFragment(
                                    RechargeFragment.newInstance(adapter.getDatas().get(p).getCoin(), 1,position));
                        } else if (view.getId() == R.id.tv_recharge) {
                            ((MainFragment) getParentFragment().getParentFragment()).startBrotherFragment(
                                    RechargeFragment.newInstance(adapter.getDatas().get(p).getCoin(), 0,position));
                        }
                    }
                }
            });
            adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {


                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                    return false;
                }
            });
            viewDelegate.setIsPullDown(false);
            viewDelegate.setCanToTop(false);
            viewDelegate.setIsLoadMore(false);
            initRecycleViewPull(adapter, new LinearLayoutManager(getActivity()));
        } else {
            getDataBack(adapter.getDatas(), data, adapter);
        }
    }

    @Override
    protected void onServiceSuccess(String data, String info, int status, int requestCode) {
        switch (requestCode) {
            case 0x123:
                List<String> list = GsonUtil.getInstance().toList(
                        data, String.class
                );
                Map<String, String> map = GsonUtil.getInstance().toMap(list.get(position),
                        new TypeReference<Map<String, String>>() {
                        });
                map.remove("name");
                map.remove("position");
                List<WalletCoinBean> walletCoinBeans = new ArrayList<>();
                for (String key : map.keySet()) {
                    WalletCoinBean walletCoinBean = GsonUtil.getInstance().toObj(map.get(key), WalletCoinBean.class);
                    walletCoinBean.setCoin(key);
                    walletCoinBeans.add(walletCoinBean);
                }
                initList(walletCoinBeans);
                break;
        }
    }

    @Override
    protected void refreshData() {
        addRequest(binder.getAccountDetail(this));
    }
}