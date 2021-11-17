package com.github.baby.owspace.view.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.baby.owspace.R;
import com.github.baby.owspace.app.GlideApp;
import com.github.baby.owspace.app.OwspaceApplication;
import com.github.baby.owspace.di.components.DaggerDetailComponent;
import com.github.baby.owspace.di.modules.DetailModule;
import com.github.baby.owspace.model.entity.DetailEntity;
import com.github.baby.owspace.model.entity.Item;
import com.github.baby.owspace.player.IPlayback;
import com.github.baby.owspace.player.PlayState;
import com.github.baby.owspace.player.PlaybackService;
import com.github.baby.owspace.presenter.DetailContract;
import com.github.baby.owspace.presenter.DetailPresenter;
import com.github.baby.owspace.util.AppUtil;
import com.github.baby.owspace.util.TimeUtils;
import com.github.baby.owspace.util.tool.AnalysizeHTML;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AudioDetailActivity extends BaseActivity implements DetailContract.View, ObservableScrollViewCallbacks, IPlayback.Callback {

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.news_top_img_under_line)
    View newsTopImgUnderLine;
    @BindView(R.id.news_top_type)
    TextView newsTopType;
    @BindView(R.id.news_top_date)
    TextView newsTopDate;
    @BindView(R.id.news_top_title)
    TextView newsTopTitle;
    @BindView(R.id.news_top_author)
    TextView newsTopAuthor;
    @BindView(R.id.news_top_lead)
    TextView newsTopLead;
    @BindView(R.id.news_top_lead_line)
    View newsTopLeadLine;
    @BindView(R.id.news_top)
    LinearLayout newsTop;
    @BindView(R.id.news_parse_web)
    LinearLayout newsParseWeb;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.scrollView)
    ObservableScrollView scrollView;
    @BindView(R.id.favorite)
    ImageView favorite;
    @BindView(R.id.write)
    ImageView write;
    @BindView(R.id.share)
    ImageView share;
    @BindView(R.id.toolBar)
    Toolbar toolBar;

    @Inject
    DetailPresenter presenter;
    @BindView(R.id.button_play_last)
    AppCompatImageView buttonPlayLast;
    @BindView(R.id.button_play_toggle)
    AppCompatImageView buttonPlayToggle;
    @BindView(R.id.button_play_next)
    AppCompatImageView buttonPlayNext;
    @BindView(R.id.text_view_progress)
    TextView textViewProgress;
    @BindView(R.id.seek_bar)
    AppCompatSeekBar seekBar;
    @BindView(R.id.text_view_duration)
    TextView textViewDuration;
    private int mParallaxImageHeight;

    private PlaybackService playbackService;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            playbackService = ((PlaybackService.LocalBinder) iBinder).getService();
            register();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            unRegister();
            playbackService = null;
        }
    };

    private void register() {
        playbackService.registerCallback(this);
    }

    private void unRegister() {
        if (playbackService != null) {
            playbackService.unregisterCallback(this);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        ButterKnife.bind(this);
        initView();
        initPresenter();
        bindPlaybackService();
    }

    private void initView() {
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        toolBar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.primary)));
        scrollView.setScrollViewCallbacks(this);
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playbackService.seekTo(getSeekDuration(seekBar.getProgress()));
            }
        });
    }

    private int getSeekDuration(int progress) {
        return (int) (getCurrentSongDuration() * ((float) progress / seekBar.getMax()));
    }

    private int getCurrentSongDuration() {
        int duration=0;
        if (playbackService != null){
            duration = playbackService.getDuration();
        }
        return duration;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();
        Item item = bundle.getParcelable("item");
        if (item != null) {
            GlideApp.with(this).load(item.getThumbnail()).centerCrop().into(image);
            newsTopImgUnderLine.setVisibility(View.VISIBLE);
            newsTopType.setText("音 频");
            newsTopDate.setText(item.getUpdate_time());
            newsTopTitle.setText(item.getTitle());
            newsTopAuthor.setText(item.getAuthor());
            newsTopLead.setText(item.getLead());
            newsTopLead.setLineSpacing(1.5f, 1.8f);
            presenter.getDetail(item.getId());
        }
    }

    private void initPresenter() {
        DaggerDetailComponent.builder()
                .netComponent(OwspaceApplication.get(this).getNetComponent())
                .detailModule(new DetailModule(this))
                .build()
                .inject(this);
    }

    private void bindPlaybackService() {
        this.bindService(new Intent(this, PlaybackService.class), connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    String song;

    @Override
    public void updateListUI(DetailEntity detailEntity) {
        song = detailEntity.getFm();
        if (detailEntity.getParseXML() == 1) {
            int i = detailEntity.getLead().trim().length();
            AnalysizeHTML analysisHTML = new AnalysizeHTML();
            analysisHTML.loadHtml(this, detailEntity.getContent(), analysisHTML.HTML_STRING, newsParseWeb, i);
            newsTopType.setText("音 频");
        }
        else {
            initWebViewSetting();
            newsParseWeb.setVisibility(View.GONE);
            image.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            newsTop.setVisibility(View.GONE);
            webView.loadUrl(addParams2WezeitUrl(detailEntity.getHtml5(), false));
        }
    }

    private void initWebViewSetting() {
        WebSettings localWebSettings = this.webView.getSettings();
        localWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        localWebSettings.setJavaScriptEnabled(true);
        localWebSettings.setSupportZoom(true);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        localWebSettings.setUseWideViewPort(true);
        localWebSettings.setLoadWithOverviewMode(true);
    }

    public String addParams2WezeitUrl(String url, boolean paramBoolean) {
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append(url);
        localStringBuffer.append("?client=android");
        localStringBuffer.append("&device_id=" + AppUtil.getDeviceId(this));
        localStringBuffer.append("&version=" + "1.3.0");
        if (paramBoolean)
            localStringBuffer.append("&show_video=0");
        else {
            localStringBuffer.append("&show_video=1");
        }
        return localStringBuffer.toString();
    }

    @Override
    protected void onDestroy() {
        unRegister();
        handleProgress.removeCallbacks(runnable);
        super.onDestroy();
    }

    Handler handleProgress = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (playbackService.isPlaying()) {
                if (isFinishing()) {
                    return;
                }
                int progress = (int) (seekBar.getMax()
                        * ((float) playbackService.getProgress() / (float) playbackService.getDuration()));
                updateProgressTextWithProgress(playbackService.getProgress());
                if (progress >= 0 && progress <= seekBar.getMax()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        seekBar.setProgress(progress, true);
                    }
                    else {
                        seekBar.setProgress(progress);
                    }
                }
            }
        }
    };

    private void updateProgressTextWithProgress(int progress) {
        textViewProgress.setText(TimeUtils.formatDuration(progress));
    }

    @Override
    public void showOnFailure() {

    }

    @Override
    public void onScrollChanged(int i, boolean b, boolean b1) {
        int baseColor = getResources().getColor(R.color.primary);
        float alpha = Math.min(1, (float) i / mParallaxImageHeight);
        toolBar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @OnClick(R.id.button_play_toggle)
    public void onClick() {
        if (playbackService == null || song == null) {
            Log.d("sunxinrong", "playBackService == null");
            return;
        }
        if (playbackService.isPlaying()) {
            if (song.equals(playbackService.getSong())) {
                playbackService.pause();
                buttonPlayToggle.setImageResource(R.drawable.ic_play);
            }
            else {
                playbackService.play(song);
                buttonPlayToggle.setImageResource(R.drawable.ic_pause);
            }
        }
        else {
            if (song.equals(playbackService.getSong())) {
                playbackService.play();
            }
            else {
                playbackService.play(song);
            }
            buttonPlayToggle.setImageResource(R.drawable.ic_pause);
        }
    }

    Timer timer = null;

    @Override
    public void onComplete(PlayState state) {
        cancelTimer();
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
    }

    @Override
    public void onPlayStatusChanged(PlayState status) {
        switch (status) {
            case INIT:
                break;
            case PREPARE:
                break;
            case PLAYING:
                updateDuration();
                playTimer();
                buttonPlayToggle.setImageResource(R.drawable.ic_pause);
                break;
            case ERROR:
                break;
            case COMPLETE:
                cancelTimer();
                buttonPlayToggle.setImageResource(R.drawable.ic_play);
                seekBar.setProgress(0);
                break;
        }
    }

    private void updateDuration() {
        textViewDuration.setText(TimeUtils.formatDuration(playbackService.getDuration()));
    }

    private void playTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (playbackService == null) {
                    return;
                }
                if (playbackService.isPlaying()) {
                    handleProgress.post(runnable);
                }
            }
        }, 0, 1000);
    }

    @Override
    public void onPosition(int position) {

    }
}
