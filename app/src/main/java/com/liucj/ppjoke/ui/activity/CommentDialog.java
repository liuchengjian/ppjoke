package com.liucj.ppjoke.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.UserManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;

import com.google.android.material.button.MaterialButton;
import com.liucj.libcommon.AppGlobals;
import com.liucj.libcommon.dialog.LoadingDialog;
import com.liucj.libcommon.utils.FileUploadManager;
import com.liucj.libcommon.utils.FileUtils;
import com.liucj.libcommon.utils.PixUtils;
import com.liucj.libcommon.view.ViewHelper;
import com.liucj.libnetwork.ApiResponse;
import com.liucj.libnetwork.ApiService;
import com.liucj.libnetwork.JsonCallBack;
import com.liucj.ppjoke.R;
import com.liucj.ppjoke.model.Comment;
import com.liucj.ppjoke.ui.view.PPEditTextView;
import com.liucj.ppjoke.ui.view.PPImageView;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class CommentDialog extends DialogFragment implements View.OnClickListener {
    private AppCompatImageView commentVideo;
    private AppCompatImageView commentDelete;
    private MaterialButton commentSend;
    private PPEditTextView inputView;
    private PPImageView commentCover;
    private AppCompatImageView commentIconVideo;
    private FrameLayout commentExtLayout;
    private static final String KEY_ITEM_ID = "key_item_id";
    private String filePath;
    private int width, height;
    private boolean isVideo = false;
    private String coverUrl;
    private String fileUrl;
    private long itemId;
    private commentAddListener mListener;
    private LoadingDialog loadingDialog;

    public static CommentDialog newInstance(long itemId) {
        Bundle args = new Bundle();
        args.putLong(KEY_ITEM_ID, itemId);
        CommentDialog fragment = new CommentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        window.setWindowAnimations(0);

        getDialog().setCanceledOnTouchOutside(true);
        View rootView = inflater.inflate(R.layout.layout_comment_dialog, container, false);
        //这步是必须的
        window.setBackgroundDrawableResource(R.color.transparent);
        //必要，设置 padding，这一步也是必须的，内容不能填充全部宽度和高度
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);

        commentVideo = rootView.findViewById(R.id.comment_video);
        commentDelete = rootView.findViewById(R.id.comment_delete);
        commentSend = rootView.findViewById(R.id.comment_send);
        inputView = rootView.findViewById(R.id.input_view);
        commentExtLayout = rootView.findViewById(R.id.comment_ext_layout);
        commentCover = rootView.findViewById(R.id.comment_cover);
        commentIconVideo = rootView.findViewById(R.id.comment_icon_video);
//        commentExtLayout.setVisibility(View.VISIBLE);
        commentCover.setImageUrl(filePath);
        if (isVideo) {
            commentIconVideo.setVisibility(View.VISIBLE);
        } else {
            commentIconVideo.setVisibility(View.GONE);
        }

        commentVideo.setOnClickListener(this);
        commentDelete.setOnClickListener(this);
        commentSend.setOnClickListener(this);
        this.itemId = getArguments().getLong(KEY_ITEM_ID);
        ViewHelper.setViewOutline(rootView, PixUtils.dp2px(10), ViewHelper.RADIUS_TOP);
        rootView.post(() -> showSoftInputMethod(window));
        dismissWhenPressBack();
        return rootView;
    }

    private void showSoftInputMethod(Window window) {
        inputView.setFocusable(true);
        inputView.setFocusableInTouchMode(true);
        inputView.requestFocus();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }

    private void dismissWhenPressBack() {
        inputView.setOnBackKeyEventListener(() -> {
            inputView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            }, 200);
            return true;
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment_send:
                publishComment();
                break;
            case R.id.comment_video:
                CaptureActivity.startActivityForResult(getActivity());
                break;
            case R.id.comment_delete:
                filePath = null;
                isVideo = false;
                width = 0;
                height = 0;
                commentCover.setImageDrawable(null);
                commentExtLayout.setVisibility(View.GONE);
                commentVideo.setEnabled(true);
                commentVideo.setAlpha(255);
                break;


        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        dismissLoadingDialog();
        filePath = null;
        fileUrl = null;
        coverUrl = null;
        isVideo = false;
        width = 0;
        height = 0;
    }

    private void publishComment() {
        if (TextUtils.isEmpty(inputView.getText())) {
            showToast("请输入您的评论");
            return;
        }

        if (isVideo && !TextUtils.isEmpty(filePath)) {
            FileUtils.generateVideoCover(filePath).observe(this, new Observer<String>() {
                @Override
                public void onChanged(String coverPath) {
                    uploadFile(coverPath, filePath);
                }
            });
        } else if (!TextUtils.isEmpty(filePath)) {
            uploadFile(null, filePath);
        } else {
            publish();
        }
    }

    private void publish() {
        if (TextUtils.isEmpty(inputView.getText())) {
            return;
        }
        String commentText = inputView.getText().toString();
        ApiService.post("/comment/addComment")
                .addParam("userId", "1631678065")
                .addParam("itemId", itemId)
                .addParam("commentText", commentText)
                .addParam("image_url", isVideo ? coverUrl : fileUrl)
                .addParam("video_url", isVideo ? fileUrl : null)
                .addParam("width", width)
                .addParam("height", height)
                .execute(new JsonCallBack<Comment>() {
                    @Override
                    public void onSuccess(ApiResponse<Comment> response) {
                        onCommentSuccess(response.body);
                    }

                    @Override
                    public void onError(ApiResponse<Comment> response) {
                        showToast("评论失败:" + response.message);
                    }
                });
    }


    @SuppressLint("RestrictedApi")
    private void uploadFile(String coverPath, String filePath) {
        //AtomicInteger, CountDownLatch, CyclicBarrier
        showLoadingDialog();
        AtomicInteger count = new AtomicInteger(1);
        if (!TextUtils.isEmpty(coverPath)) {
            count.set(2);
            ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    int remain = count.decrementAndGet();
                    coverUrl = FileUploadManager.upload(coverPath);
                    if (remain <= 0) {
                        if (!TextUtils.isEmpty(fileUrl) && !TextUtils.isEmpty(coverUrl)) {
                            publish();
                        } else {
                            dismissLoadingDialog();
                            showToast(getString(R.string.file_upload_failed));
                        }
                    }
                }
            });
        }
        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                int remain = count.decrementAndGet();
                fileUrl = FileUploadManager.upload(filePath);
                if (remain <= 0) {
                    if (!TextUtils.isEmpty(fileUrl) || !TextUtils.isEmpty(coverPath) && !TextUtils.isEmpty(coverUrl)) {
                        publish();
                    } else {
                        dismissLoadingDialog();
                        showToast(getString(R.string.file_upload_failed));
                    }
                }
            }
        });

    }

    private void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getContext());
            loadingDialog.setLoadingText(getString(R.string.upload_text));
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.setCancelable(false);
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    @SuppressLint("RestrictedApi")
    private void dismissLoadingDialog() {
        if (loadingDialog != null) {
            //dismissLoadingDialog  的调用可能会出现在异步线程调用
            if (Looper.myLooper() == Looper.getMainLooper()) {
                ArchTaskExecutor.getMainThreadExecutor().execute(() -> {
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                });
            } else if (loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private void onCommentSuccess(Comment body) {
        showToast("评论发布成功");
        ArchTaskExecutor.getMainThreadExecutor().execute(() -> {
            if (mListener != null) {
                mListener.onAddComment(body);
            }
            dismiss();
        });
    }


    @SuppressLint("RestrictedApi")
    private void showToast(String s) {
        //showToast几个可能会出现在异步线程调用
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(AppGlobals.getApplication(), s, Toast.LENGTH_SHORT).show();
        } else {
            ArchTaskExecutor.getMainThreadExecutor().execute(() -> Toast.makeText(AppGlobals.getApplication(), s, Toast.LENGTH_SHORT).show());
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQ_CAPTURE && resultCode == Activity.RESULT_OK) {
            filePath = data.getStringExtra(CaptureActivity.RESULT_FILE_PATH);
            width = data.getIntExtra(CaptureActivity.RESULT_FILE_WIDTH, 0);
            height = data.getIntExtra(CaptureActivity.RESULT_FILE_HEIGHT, 0);
            isVideo = data.getBooleanExtra(CaptureActivity.RESULT_FILE_TYPE, false);

            if (!TextUtils.isEmpty(filePath)) {
                commentExtLayout.setVisibility(View.VISIBLE);
                commentCover.setImageUrl(filePath);
                if (isVideo) {
                    commentIconVideo.setVisibility(View.VISIBLE);
                }
            }

            commentVideo.setEnabled(false);
            commentVideo.setAlpha(80);
        }
    }

    public interface commentAddListener {
        void onAddComment(Comment comment);
    }

    public void setCommentAddListener(commentAddListener listener) {

        mListener = listener;
    }
}
