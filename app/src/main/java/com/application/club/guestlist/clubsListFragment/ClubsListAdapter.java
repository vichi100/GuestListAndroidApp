package com.application.club.guestlist.clubsListFragment;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.club.guestlist.clubdetails.ClubDetailsListActivity;
import com.application.club.guestlist.videoMode.Feed;
import com.application.club.guestlist.videoMode.Video;
import com.application.club.guestlist.videoUI.BaseAdapter;
import com.application.club.guestlist.videoUI.CameraAnimation;
import com.application.club.guestlist.videoUI.FeedAdapter;
import com.application.club.guestlist.videoUI.VideoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hoanganhtuan95ptit.autoplayvideorecyclerview.VideoHolder;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.application.club.guestlist.R;
import com.application.club.guestlist.utils.Constants;
import com.application.club.guestlist.utils.UtillMethods;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClubsListAdapter extends BaseAdapter<Feed> {

	Context context;
	List<Video> rowItem;
	private final Activity mActivity;

	private static int screenWight = 0;

	private static final int VIDEO_M1 = 2;
	private static final int VIDEO_M2 = 3;



	public ClubsListAdapter(Activity activity, List<Video> clubRowItemList) {

		super(activity);
		context = activity;
		mActivity=activity;
		this.rowItem = clubRowItemList;
		screenWight = getScreenWight();
	}

	private int getScreenWight() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics.widthPixels;
	}




	@Override
	public int getItemViewType(int position) {
		if(list != null && list.size() > 0){
			Feed feed = list.get(position);
			if (feed.getInfo() instanceof Video) {
				if (feed.getModel() == Feed.Model.M1) return VIDEO_M1;
				return VIDEO_M2;
			} else {
				if (feed.getModel() == Feed.Model.M1) return VIDEO_M1;
				return VIDEO_M2;
			}
		}
		return VIDEO_M2;
	}

	@Override
	public int getItemCount() {
		return rowItem.size();
	}

//	@Override
//	public long getItemId(int position) {
//
//		return rowItem.indexOf(getItem(position));
//	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		Feed feed = list.get(position);
		if (holder instanceof Video169Holder) {
			onBindVideo169Holder((Video169Holder) holder, feed);
		} else if (holder instanceof FeedAdapter.Video169Holder) {
			onBindVideo169Holder((Video169Holder) holder, feed);
		}
	}

	private void onBindVideo169Holder(final DemoVideoHolder holder, Feed feed) {
		//Picasso.with(activity).setLoggingEnabled(true);
//		Picasso.with(activity)
//				.load(Constants.HTTP_URL+feed.getInfo().getImageURL())
//				.resize(screenWight, screenWight * 9 / 16)
//				.centerCrop()
//				.into(holder.ivInfo);
		Glide.with(activity)
				.load(Constants.HTTP_URL+feed.getInfo().getImageURL())
				.override(screenWight, screenWight * 9 / 16)
				//.centerCrop()
				.diskCacheStrategy(DiskCacheStrategy.RESULT)
				//.skipMemoryCache(true)
				.fitCenter()
				//.placeholder(R.drawable.circular_progress_bar)
				.into(holder.ivInfo);
		holder.vvInfo.setVideo((Video) feed.getInfo());
		holder.clubMainName.setText(feed.getInfo().getClubname());
		holder.location.setText(feed.getInfo().getLocation());
		final String latLong = feed.getInfo().getLatlong();
		holder.naviIcon.setOnClickListener(new View.OnClickListener() {
			// Start new list activity
			public void onClick(View v) {
				//https://stackoverflow.com/questions/2662531/launching-google-maps-directions-via-an-intent-on-android
				//http://android-pratap.blogspot.in/2014/09/launching-google-maps-directions-via.html
				//https://gist.github.com/laaptu/3ad031740deb99b9bd60


                //https://stackoverflow.com/questions/2662531/launching-google-maps-directions-via-an-intent-on-android
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("www.google.com").appendPath("maps").appendPath("dir").appendPath("").appendQueryParameter("api", "1")
                        .appendQueryParameter("destination", latLong);
                String url = builder.build().toString();
                Log.d("Directions", url);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
			}
		});

	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;
		switch (viewType) {

			case VIDEO_M1:
				view = activity.getLayoutInflater().inflate(R.layout.item_video, parent, false);
				return new Video169Holder(view);
			default:
				view = activity.getLayoutInflater().inflate(R.layout.item_video, parent, false);
				return new Video169Holder(view);
		}
	}

//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//
//		if (convertView == null) {
//			LayoutInflater mInflater = (LayoutInflater) context
//					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//			convertView = mInflater.inflate(R.layout.club_list_adapter_item, null);
//		}
//        Video clubRowItemObj = rowItem.get(position);
//        String imgURL = Constants.HTTP_URL+clubRowItemObj.getImageURL();
//
//
//        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.clubMainImage);
//        Picasso.with(context).load(imgURL)
//				//.networkPolicy(NetworkPolicy.OFFLINE)
//				.into(imgIcon);
//		TextView clubMainName = (TextView) convertView.findViewById(R.id.clubMainName);
//        clubMainName.setText(UtillMethods.toCamelCase(clubRowItemObj.getClubname()));
//        TextView location = (TextView) convertView.findViewById(R.id.location);
//        location.setText(UtillMethods.toCamelCase(clubRowItemObj.getLocation()));
//
//        final String latLong = clubRowItemObj.getLatlong();
//
//		ImageView naviIcon = (ImageView) convertView.findViewById(R.id.navi);
//
//		naviIcon.setOnClickListener(new View.OnClickListener() {
//			// Start new list activity
//			public void onClick(View v) {
//				//https://stackoverflow.com/questions/2662531/launching-google-maps-directions-via-an-intent-on-android
//				//http://android-pratap.blogspot.in/2014/09/launching-google-maps-directions-via.html
//				//https://gist.github.com/laaptu/3ad031740deb99b9bd60
//
//
//                //https://stackoverflow.com/questions/2662531/launching-google-maps-directions-via-an-intent-on-android
//                Uri.Builder builder = new Uri.Builder();
//                builder.scheme("https")
//                        .authority("www.google.com").appendPath("maps").appendPath("dir").appendPath("").appendQueryParameter("api", "1")
//                        .appendQueryParameter("destination", latLong);
//                String url = builder.build().toString();
//                Log.d("Directions", url);
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                context.startActivity(i);
//			}
//		});
//
//		return convertView;
//
//	}

	public  class DemoVideoHolder extends VideoHolder {
		AudioManager manager;

		@BindView(R.id.vvInfo)
		VideoView vvInfo;
		@BindView(R.id.ivInfo)
		ImageView ivInfo;
		@BindView(R.id.ivCameraAnimation)
		CameraAnimation ivCameraAnimation;
		@BindView(R.id.navi)
		ImageView naviIcon;
		@BindView(R.id.location)
		TextView location;
		@BindView(R.id.clubMainName)
		TextView clubMainName;
//		@BindView(R.id.speaker)
//		ImageView speaker;
//		@BindView(R.id.soundon)
//		ImageView soundon;

		boolean isMute = false;

		//https://codentrick.com/recyclerview-example-part-3-android-recyclerview-onclicklistener/

		public DemoVideoHolder(final View itemView) {
			super(itemView);
			manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			manager.setStreamMute(AudioManager.STREAM_MUSIC, true);


			ButterKnife.bind(this, itemView);
//			if(isMute){
//				soundon.setVisibility(View.INVISIBLE);
//				soundoff.setVisibility(View.VISIBLE);
//			}else {
//				soundon.setVisibility(View.VISIBLE);
//				soundoff.setVisibility(View.INVISIBLE);
//				context.
//			}
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					int position = getAdapterPosition();
                    Video clubRowItemObj = rowItem.get(position);
                    String clubId = clubRowItemObj.getClubid();
                    String clubName = clubRowItemObj.getClubname();
                    String imageUrl = clubRowItemObj.getImageURL();
                    Intent intent = new Intent(context, ClubDetailsListActivity.class);
                    intent.putExtra(Constants.CLUB_ID, clubId);
                    intent.putExtra(Constants.IMAGE_URL, imageUrl);
                    intent.putExtra(Constants.CLUB_NAME, clubName);
                    stopVideo();
                    context.startActivity(intent);
				}
			});

			itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override public boolean onLongClick(View v) {
					if(!isMute){
						manager.setStreamMute(AudioManager.STREAM_MUSIC, true);
						isMute = true;
					}else {
						manager.setStreamMute(AudioManager.STREAM_MUSIC, false);
						isMute = false;
					}
					return true;
				}
			});
//			speaker.setOnClickListener(new View.OnClickListener() {
//				@Override public void onClick(View v) {
//					if(!isMute){
//						manager.setStreamMute(AudioManager.STREAM_MUSIC, true);
//						isMute = true;
//						new Thread(new Runnable() {
//							public void run() {
//								mActivity.runOnUiThread(new Runnable() {
//									@Override
//									public void run() {
//										speaker.setImageResource(R.drawable.soundoff);
//									}
//								});
//
//							}
//						}).start();
//					}else {
//						manager.setStreamMute(AudioManager.STREAM_MUSIC, false);
//						isMute = false;
//						new Thread(new Runnable() {
//							public void run() {
//
//								mActivity.runOnUiThread(new Runnable() {
//									@Override
//									public void run() {
//										speaker.setImageResource(R.drawable.soundon);
//									}
//								});
//
//							}
//						}).start();
//					}
//
//				}
//			});
//			soundoff.setOnClickListener(new View.OnClickListener() {
//				@Override public void onClick(View v) {
//					manager.setStreamMute(AudioManager.STREAM_MUSIC, false);
//					soundon.setVisibility(View.VISIBLE);
//					soundoff.setVisibility(View.INVISIBLE);
//					isMute = false;
//				}
//			});
		}

		@Override
		public View getVideoLayout() {
			return vvInfo;
		}

		@Override
		public void playVideo() {
			ivInfo.setVisibility(View.VISIBLE);
			ivCameraAnimation.start();
			vvInfo.play(new VideoView.OnPreparedListener() {
				@Override
				public void onPrepared() {
					ivInfo.setVisibility(View.GONE);
					ivCameraAnimation.stop();
				}
			});
		}

		@Override
		public void stopVideo() {
			ivInfo.setVisibility(View.VISIBLE);
			ivCameraAnimation.stop();
			vvInfo.stop();
		}


	}

	public  class Video169Holder extends DemoVideoHolder {

		public Video169Holder(View itemView) {
			super(itemView);
			FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) vvInfo.getLayoutParams();
			layoutParams.width = screenWight;
			layoutParams.height = screenWight * 9 / 16;
			vvInfo.setLayoutParams(layoutParams);
		}
	}

}
