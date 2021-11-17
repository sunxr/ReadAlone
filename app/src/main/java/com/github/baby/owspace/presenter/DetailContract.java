package com.github.baby.owspace.presenter;

import com.github.baby.owspace.model.entity.DetailEntity;

public interface DetailContract {

    interface Presenter {
        void getDetail(String itemId);
    }

    interface View {
        void showLoading();
        void dismissLoading();
        void updateListUI(DetailEntity detailEntity);
        void showOnFailure();
    }

}
