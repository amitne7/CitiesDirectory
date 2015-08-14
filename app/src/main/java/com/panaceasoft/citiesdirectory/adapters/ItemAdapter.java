package com.panaceasoft.citiesdirectory.adapters;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.models.PItemData;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Panacea-Soft on 17/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PItemData> mDataset;

    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public static final int VIEW_ITEM = 1;
    public static final int VIEW_PROG = 0;
    private boolean isFooterEnabled = true;


    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        }
    }

    public ItemAdapter(List<PItemData> myDataSet, RecyclerView recyclerView) {
        mDataset = myDataSet;

        if(recyclerView.getLayoutManager()instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }else if(recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager)recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = gridLayoutManager.getItemCount();
                    lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });

        }else if(recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {

            final StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager)recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);


                    if(newState == 1){
                        totalItemCount = staggeredGridLayoutManager.getItemCount();

                        // for staggeredGridLayoutManager
                        int[] arr = new int[totalItemCount];
                        int[] lastVisibleItem2 = staggeredGridLayoutManager.findLastVisibleItemPositions(arr);
                        String string= "";
                        int greatestItem = 0;
                        for(int i = 0; i<lastVisibleItem2.length; i++){
                            if(lastVisibleItem2[i]> greatestItem) {
                                greatestItem = lastVisibleItem2[i];
                            }
                            string += " = " + lastVisibleItem2[i];
                        }
                        if (!loading && totalItemCount <= (greatestItem + visibleThreshold)) {
                            // End has been reached
                            // Do something
                            if (onLoadMoreListener != null) {
                                onLoadMoreListener.onLoadMore();
                            }
                            loading = true;
                        }
                    }

                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = staggeredGridLayoutManager.getItemCount();

                    // for staggeredGridLayoutManager
                    int[] arr = new int[totalItemCount];
                    int[] lastVisibleItem2 = staggeredGridLayoutManager.findLastVisibleItemPositions(arr);
                    String string= "";
                    int greatestItem = 0;
                    for(int i = 0; i<lastVisibleItem2.length; i++){
                        if(lastVisibleItem2[i]> greatestItem) {
                            greatestItem = lastVisibleItem2[i];
                        }
                        string += " = " + lastVisibleItem2[i];
                    }
                    if (!loading && totalItemCount <= (greatestItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }

                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position)!=null? VIEW_ITEM: VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if(viewType==VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_row, parent, false);

            vh = new MyViewHolder(v);
        }else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder){
            ((MyViewHolder)holder).title.setText(mDataset.get(position).name);
            Picasso.with(((MyViewHolder)holder).icon.getContext()).load(Config.APP_IMAGES_URL + mDataset.get(position).images.get(0).path).into(((MyViewHolder) holder).icon);
            ((MyViewHolder)holder).likeCount.setText(mDataset.get(position).like_count);
            ((MyViewHolder)holder).reviewCount.setText(mDataset.get(position).review_count);
        }else{
            // For staggeredGridLayout Manager only
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
            ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded(){
        loading = false;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener{
        void onLoadMore();
    }

    public void updateItemLikeAndReviewCount(int itemID, String likeCount, String reviewCount){
        for(int i = 0; i<mDataset.size(); i++){
            if(mDataset.get(i).id  == itemID){
                mDataset.get(i).like_count = likeCount;
                mDataset.get(i).review_count = reviewCount;
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        ImageView icon;
        TextView likeCount;
        TextView reviewCount;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item_name);
            icon = (ImageView) itemView.findViewById(R.id.item_image);
            likeCount = (TextView) itemView.findViewById(R.id.like_count);
            reviewCount = (TextView) itemView.findViewById(R.id.review_count);

            title.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
            likeCount.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
            reviewCount.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
        }
    }
}


