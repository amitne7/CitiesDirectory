package com.panaceasoft.citiesdirectory.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.activities.DetailOneActivity;
import com.panaceasoft.citiesdirectory.adapters.MyAdapter;
import com.panaceasoft.citiesdirectory.listeners.ClickListener;
import com.panaceasoft.citiesdirectory.listeners.RecyclerTouchListener;
import com.panaceasoft.citiesdirectory.models.CustomRowInformation;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class ContactUsFragment extends Fragment {


    private RecyclerView mRecyclerView;
    //private GridLayoutManager mLayoutManager;
    private StaggeredGridLayoutManager mLayoutManager;
    private MyAdapter mAdapter;

    private List<CustomRowInformation> myDataset = new ArrayList<>();;
    private CustomRowInformation info;
    private Handler handler;
    public ContactUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);

        handler = new Handler();


        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        //mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(myDataset, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);


        /* For Empty View
        TextView emptyView = (TextView) view.findViewById(R.id.empty_view);
        if(myDataset.isEmpty()){
            mRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }else{
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }/*/

        // Just Remove only for blank testing
        for (int i = 0; i < 15; i++) {

            info = new CustomRowInformation();
            //info.setTitle("Item" + (myDataset.size() + 1));
            if(i == 0) {
                info.setTitle("Item Long Long Long Long" + (myDataset.size() + 1));
            } else if(i == 1) {
                info.setTitle("Item Long Long Long Long Item Long Long Long LongItem Long Long Long Long" + (myDataset.size() + 1));
            } else if(i == 4) {
                info.setTitle("Item Long Long Long Long Item Long Long Long LongItem Long Long Long Long" + (myDataset.size() + 1));
            } else {
                info.setTitle("Item " + (myDataset.size() + 1));
            }

            myDataset.add(info);
            mAdapter.notifyItemInserted(myDataset.size());
        }


        mAdapter.setOnLoadMoreListener(new MyAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add progress item
                myDataset.add(null);
                mAdapter.notifyItemInserted(myDataset.size() - 1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //remove progress item
                        myDataset.remove(myDataset.size() - 1);
                        mAdapter.notifyItemRemoved(myDataset.size());
                        //add items one by one
                        for (int i = 0; i < 16; i++) {

                            info = new CustomRowInformation();

                            info.setTitle("Item " + (myDataset.size() + 1)  );
                            myDataset.add(info);
                            mAdapter.notifyItemInserted(myDataset.size());
                        }
                        mAdapter.setLoaded();
                        //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                    }
                }, 2000);
                System.out.println("load");
            }
        });

        // for grid layout manager
        /*
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(mAdapter.getItemViewType(position)){
                    case MyAdapter.VIEW_ITEM:
                        return 1;
                    case MyAdapter.VIEW_PROG:
                        return 2; //number of columns of the grid
                    default:
                        return -1;
                }
            }
        });
        */

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(getActivity(), " Position "+ position, Toast.LENGTH_SHORT).show();
                //mDrawerLayout.closeDrawer(GravityCompat.START);
                onItemClicked(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }

    public void onItemClicked( int position){

        switch(position){
            case 0:
                startActivity(new Intent(getActivity(), DetailOneActivity.class));
                break;
            default:
                startActivity(new Intent(getActivity(), DetailOneActivity.class));
                Toast.makeText(getActivity(), " Position is " + position, Toast.LENGTH_SHORT).show();
                break;
        }


    }

}
