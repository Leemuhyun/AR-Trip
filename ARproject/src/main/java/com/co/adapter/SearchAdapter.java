package com.co.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.co.R;
import com.co.activity.ScrollingActivity;
import com.co.util.ItemArray;

import java.util.ArrayList;

/**
 * Created by angus on 2017-05-18.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private ArrayList<ItemArray> mDataset;
    Context context;
    SwipeRefreshLayout refreshLayout;

    // 뷰홀더는 리사이클뷰에 안에 우리가 제어할 오브젝트를 초기화(등록)해주는 작업을함
    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView mImageView;
        TextView sTitle;
        TextView sdist;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardview);
            mImageView = (ImageView) view.findViewById(R.id.imageView);
            sTitle = (TextView) view.findViewById(R.id.stitle);
            sdist = (TextView) view.findViewById(R.id.dist);
            Log.d("LMH", "홀더");
        }

    }

    // 생성자에서는 필요한 정보를 가지고옴
    public SearchAdapter( Context context, SwipeRefreshLayout refreshLayout) {
        mDataset = new ArrayList<>();
        this.context = context;
        this.refreshLayout = refreshLayout;
        Log.d("LMH", "생성자");
    }

    // 리사이클뷰 하위에있는 카드뷰 레이아웃을 등록시킴
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new ViewHolder(v);
    }

    // 여기서 실제로 화면에 보여지는 것들을 초기화해줌
    // 따로 for문 안써도 리스트안에 있는 정보를 다 화면에 뿌려줌
    // 그리고 이부분에서 클릭이벤트 사용함
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        int mdist = 0;

        Log.d("LMH", "연결");
        if (mDataset.get(position).getsImage() != null) {
            Glide.with(context).load(mDataset.get(position).getsImage()).into(holder.mImageView);
        } else {
            Glide.with(context).load(R.drawable.no_image).into(holder.mImageView);
        }
        if(mDataset.get(position).getsDistanse() != null) {
            mdist = Integer.parseInt(mDataset.get(position).getsDistanse());
            if (mdist < 1000) {
                holder.sdist.setText(mdist+"m");
            } else if (mdist >= 1000) {
                float fDistance = (float) mdist / 1000;
                fDistance = (float) Math.round(fDistance * 10) / 10;
                holder.sdist.setText(fDistance+"Km");
            }
        }
        holder.sTitle.setText(mDataset.get(position).getsName());
        //클릭이벤트 사용
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ScrollingActivity.class);
                intent.putExtra("ID",mDataset.get(position).getsContentId());
                intent.putExtra("IMAGE",mDataset.get(position).getsImage());
                v.getContext().startActivity(intent);
            }
        });

        // 새로고침중 표시 없앰
        refreshLayout.setRefreshing(false);
    }

    @Override
    public int getItemCount() {
        if(mDataset == null){
            return 0;
        }
        return mDataset.size();
    }

    // http통신이 완료되면 불러오는 메소드
    public void add(ArrayList<ItemArray> a) {
        mDataset = a;
        notifyDataSetChanged();
    }

}
