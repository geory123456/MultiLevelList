package com.simon.multilist.core;

import android.view.View;

import com.simon.multilist.demo.bean.BaseNode;
import com.simon.multilist.demo.bean.BaseParentNode;
import com.simon.multilist.core.tree.INode;
import com.simon.multilist.demo.DataConverter;

import java.util.List;

/**
 * 扩展自多类型Adapter的多级Adapter
 * Created by simon on 17-5-12.
 */

public abstract class MultiLevelAdapter<T extends BaseParentNode> extends MultiAdapter implements OnMultiLevelItemClickListener {

    private T dataRoot;

    public MultiLevelAdapter() {
        this(null);
    }

    public MultiLevelAdapter(T dataRoot) {
        super();
        this.dataRoot = dataRoot;
        setDataList(DataConverter.convert(dataRoot));
        registerViewHolderCreators();
        if(getViewHolderCreatorCount() == 0) {
            throw new IllegalStateException("register ViewHolderCreator please");
        }
    }

    public void setDataRoot(T dataRoot) {
        this.dataRoot = dataRoot;
        setDataList(DataConverter.convert(dataRoot));
    }


    /**
     * sub class register ViewHolderCreators here
     * */
    protected abstract void registerViewHolderCreators();

    @Override
    public abstract void onClickChild(BaseNode child);

    @Override
    public void onClickParent(BaseParentNode parent) {
        List<INode> dataList = getDataList();
        int index = dataList.indexOf(parent);
        if(parent.isOpen()) {
            List<INode> closeList = parent.close();
            dataList.removeAll(closeList);
            notifyItemRangeRemoved(index + 1, closeList.size());
        } else {
            List<INode> list = parent.open();
            dataList.addAll(index + 1,list);
            notifyItemRangeInserted(index + 1, list.size());

        }
    }


    public static abstract class MultiLevelViewHolder<T extends BaseNode> extends MultiAdapter.BaseHolder<T> {

        private View itemView;

        public MultiLevelViewHolder(View itemView, int type, OnMultiLevelItemClickListener listener) {
            super(itemView, type);
            this.itemView = itemView;
            setOnClickListener(listener);
        }

        public void setOnClickListener(final OnMultiLevelItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    T data = getData();
                    if(listener != null && data != null) {
                        if(data instanceof BaseParentNode) {
                            listener.onClickParent(((BaseParentNode) data));
                        } else {
                            listener.onClickChild(data);
                        }
                    }
                }
            });
        }

        @Override
        public abstract void bindViewHolder(T data);

    }


}