package com.handpay.settings;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class FinalAdapter<T> extends BaseAdapter {

    private List<T> mShowItems = new ArrayList<>();
    private List<String> mHomeTagList = new ArrayList<>();
    private List<T> mTvColor = new ArrayList<>();
    private int mLayoutId = 0;

    private OnAdapterListener mAdapterListener;

    FinalAdapter(List<T> showItems, List<String> homeTagList, int layoutId, OnAdapterListener adapterListener, List<T> mTvColor) {
        mShowItems = showItems;
        mHomeTagList = homeTagList;
        this.mLayoutId = layoutId;
        this.mAdapterListener = adapterListener;
        this.mTvColor = mTvColor;
    }

    @Override
    public int getCount() {
        return mShowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mShowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FinalViewHolder finalViewHolder;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), mLayoutId, null);
            finalViewHolder = new FinalViewHolder(convertView);

            convertView.setTag(finalViewHolder);

        } else {
            finalViewHolder = (FinalViewHolder) convertView.getTag();
        }
        //绑定数据
        bindView(finalViewHolder, mShowItems.get(position), mHomeTagList.get(position), mTvColor.get(position));
        return convertView;
    }

    //这里是可变
    private void bindView(FinalViewHolder finalViewHolder, T content, String tag, T color) {
        //谁用谁来绑定数据
        mAdapterListener.bindView(finalViewHolder, content, tag, color);
    }

    //接口
    public interface OnAdapterListener<T>{
        void bindView(FinalViewHolder finalViewHolder, T content, String tag, T color);
    }

    //接收

    static class FinalViewHolder {

        View mLayoutView;

        FinalViewHolder(View layoutView) {
            //tv = (TextView) view.findViewById(R.id.tv_home_title);
            this.mLayoutView = layoutView;
        }

        //根据id自动查找控件
        // private HashMap<Integer, View> mViewHashMap = new HashMap<>();

        //使用性能更高的
        private SparseArray<View> mViewHashMap = new SparseArray<>();

        View getViewById(int id) {
            View view = mViewHashMap.get(id);
            if (view == null) {
                view = mLayoutView.findViewById(id);
                mViewHashMap.put(id, view);
            }
            return view;
        }

//        //以后可以增加
//        public TextView getTextView(int id) {
//            return (TextView) getViewById(id);
//        }

    }
}
