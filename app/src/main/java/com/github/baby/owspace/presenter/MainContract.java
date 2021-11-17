package com.github.baby.owspace.presenter;

import com.github.baby.owspace.model.entity.Item;

import java.util.List;

public interface MainContract {

    interface Presenter {
        void getListByPage(int page, int model, String pageId, String deviceId, String createTime);
        void getRecommend(String deviceId);
    }

    interface View {
        void showLoading();
        void dismissLoading();
        void showNoData();
        void showNoMore();
        void updateListUI(List<Item> itemList);
        void showOnFailure();
        void showLunar(String content);
    }

}
