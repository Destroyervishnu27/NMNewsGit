package com.nmnews.nmnewsagency.adapter;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nmnews.nmnewsagency.R;
import com.nmnews.nmnewsagency.activity.OwnVideoDetailActivity;
import com.nmnews.nmnewsagency.modelclass.GetUserOwnNewsModel;
import com.bumptech.glide.Glide;

import java.util.List;

import vimeoextractor.OnVimeoExtractionListener;
import vimeoextractor.VimeoExtractor;
import vimeoextractor.VimeoVideo;

public class GetUserOwnNewsAdapter extends RecyclerView.Adapter<GetUserOwnNewsAdapter.MyViewHolder> {
    private List<GetUserOwnNewsModel.DataBeanX.DataBean.PagedRecordBean> moviesList;
    Context context;

    public GetUserOwnNewsAdapter(Context context, List<GetUserOwnNewsModel.DataBeanX.DataBean.PagedRecordBean> moviesList) {
        this.moviesList = moviesList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView img_hashtag;
        LinearLayout lin_additem;
        private TextView tv_Videostatus;

        public MyViewHolder(View view) {
            super(view);
           img_hashtag = (ImageView) view.findViewById(R.id.img_hashtag);
            lin_additem = (LinearLayout) view.findViewById(R.id.lin_additem);
            tv_Videostatus = view.findViewById(R.id.tv_Videostatus);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hashtag_detail, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GetUserOwnNewsModel.DataBeanX.DataBean.PagedRecordBean movie = moviesList.get(position);
        getThumbnail(movie.getVideoId(),holder.img_hashtag);
        holder.lin_additem.setTag(position);
        holder.lin_additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos= (int) v.getTag();
                Intent intent = new Intent(context, OwnVideoDetailActivity.class);
                intent.putExtra("newsid", moviesList.get(pos).getNewsId());
                intent.putExtra("self", "self");
                intent.putExtra("type","own");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }
        });

        if (movie.getStatusName()!=null && movie.getStatusName().toString().equalsIgnoreCase("Published"))
        {

            holder.tv_Videostatus.setVisibility(View.VISIBLE);
            holder.tv_Videostatus.setBackgroundColor(context.getResources().getColor(R.color.locbutonbag));
            holder.tv_Videostatus.setTextColor(context.getResources().getColor(R.color.white));
            holder.tv_Videostatus.setText(movie.getStatusName().toString());
        }else
        {
            holder.tv_Videostatus.setVisibility(View.VISIBLE);
            holder.tv_Videostatus.setBackgroundColor(context.getResources().getColor(R.color.alert));
            holder.tv_Videostatus.setTextColor(context.getResources().getColor(R.color.white));
            holder.tv_Videostatus.setText(movie.getStatusName().toString());
        }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
    public void getThumbnail(String vimeoUrl,ImageView imageView) {
        // Log.e("vimeothumb====","https://vimeo.com/"+vimeoUrl+".xml");
        VimeoExtractor.getInstance().fetchVideoWithURL("https://vimeo.com/api/v2/video/" + vimeoUrl, null, new OnVimeoExtractionListener() {
            @Override
            public void onSuccess(VimeoVideo video) {
                String hdStream = video.getThumbs().get("640");
                Log.e("vimeothumb====", hdStream);

                try{
                    ContextCompat.getMainExecutor(getApplicationContext()).execute(new Runnable() {
                        @Override public void run() {
                            Glide.with(getApplicationContext())
                                    .load(hdStream)
                                    .into(imageView);                        }
                    });
                }catch (Exception e)
                {

                }


            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("error", throwable.getMessage());

            }
        });
    }
}
