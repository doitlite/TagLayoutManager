package com.doitlite.taglayoutmanager.example.demo1;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.doitlite.taglayoutmanager.example.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Description:
 * Date: 2017-04-06 13:23
 * Author: chenzc
 */
public class TagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String SELECTED_SUFFIX = " ╳";

    private static final int INVALID_POSITION = -1;

    private static final int TYPE_TAG = 0;
    private static final int TYPE_INPUT = 1;

    private Context mContext;
    private List<String> mList;
    private OnTagClickListener mListener;
    private LayoutInflater mInflater;

    private String mPreInputStr = "";
    private String mCurInputStr = "";
    private int mSelectedPosition = INVALID_POSITION;
    private Handler mHandler;

    public TagAdapter(@NonNull Context context, @NonNull List<String> list, @NonNull OnTagClickListener listener) {
        this.mContext = context;
        this.mList = list;
        this.mListener = listener;
        this.mInflater = LayoutInflater.from(context);
        this.mHandler = new Handler();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_TAG:
                viewHolder = new TagViewHolder(mInflater.inflate(R.layout.item_tag, parent, false));
                break;
            case TYPE_INPUT:
                viewHolder = new InputViewHolder(mInflater.inflate(R.layout.item_tag_input, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case TYPE_TAG:
                ((TagViewHolder) holder).mTvTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int tempPos = INVALID_POSITION;
                        if (mSelectedPosition != INVALID_POSITION) {
                            if (mSelectedPosition == position) {
                                mListener.onRemoved(mList.get(mSelectedPosition));
                                removeItem(mSelectedPosition);
                                mSelectedPosition = INVALID_POSITION;
                                return;
                            } else {
                                tempPos = mSelectedPosition;
                            }
                        }

                        mSelectedPosition = position;
                        notifyItemChanged(tempPos);
                        notifyItemChanged(mSelectedPosition);
                    }
                });
                if (position == mSelectedPosition) {
                    ((TagViewHolder) holder).mTvTag.setBackgroundResource(R.drawable.bg_tag_selected);
                    ((TagViewHolder) holder).mTvTag.setSelected(true);
                    ((TagViewHolder) holder).mTvTag.setText(mList.get(position) + SELECTED_SUFFIX);
                } else {
                    ((TagViewHolder) holder).mTvTag.setBackgroundResource(R.drawable.bg_tag);
                    ((TagViewHolder) holder).mTvTag.setSelected(false);
                    ((TagViewHolder) holder).mTvTag.setText(mList.get(position));
                }
                break;
            case TYPE_INPUT:
                requestInputFocus(((InputViewHolder) holder).mEtInput);

                ((InputViewHolder) holder).mEtInput.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                        if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                return false;
                            }

                            if (keyCode == KeyEvent.KEYCODE_DEL) {
                                boolean isDeleteComplete = false;
                                if(mPreInputStr.length() == ((InputViewHolder) holder).mEtInput.getText().toString().length()
                                        && isEmpty(((InputViewHolder) holder).mEtInput.getText().toString())) {
                                    isDeleteComplete = true;
                                }

                                mPreInputStr = ((InputViewHolder) holder).mEtInput.getText().toString();

                                if (isDeleteComplete) {
                                    if (mSelectedPosition == INVALID_POSITION) {
                                        mSelectedPosition = mList.size() - 1;
                                        notifyItemChanged(mSelectedPosition);
                                        return true;
                                    } else if (mSelectedPosition != mList.size() - 1) {
                                        int tempPos = mSelectedPosition;
                                        mSelectedPosition = mList.size() - 1;
                                        notifyItemChanged(tempPos);
                                        notifyItemChanged(mSelectedPosition);
                                        return true;
                                    }

                                    if (mSelectedPosition != INVALID_POSITION && mList.size() > 0) {
                                        mSelectedPosition = INVALID_POSITION;
                                        mListener.onRemoved(mList.get(mList.size() - 1));
                                        removeItem(mList.size() - 1);
                                        mListener.onDataSetChange();
                                        return true;
                                    }
                                } else {
                                    clearSelectedItem();
                                }
                            }
                        }
                        return false;
                    }
                });

                if (((InputViewHolder) holder).mEtInput.getTag() instanceof TextWatcher) {
                    ((InputViewHolder) holder).mEtInput.removeTextChangedListener((TextWatcher)((InputViewHolder) holder).mEtInput.getTag());
                }
                if (mCurInputStr != null) {
                    ((InputViewHolder) holder).mEtInput.setText(mCurInputStr);
                    ((InputViewHolder) holder).mEtInput.setSelection(mCurInputStr.length());
                }
                TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        mCurInputStr = stringFilter(((InputViewHolder) holder).mEtInput.getText().toString());

                        if (mCurInputStr.length() > 18) {
                            Toast.makeText(mContext, "超过18个字符限制", Toast.LENGTH_SHORT).show();
                            mCurInputStr = mCurInputStr.substring(0, 18);
                        }

                        if (!isEmpty(mCurInputStr)) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mSelectedPosition != INVALID_POSITION) {
                                        int tempPos = mSelectedPosition;
                                        mSelectedPosition = INVALID_POSITION;
                                        notifyItemChanged(tempPos);
                                    }
                                    notifyItemChanged(getItemCount() - 1);
                                    mListener.onDataSetChange();
                                }
                            });
                        }
                    }
                };
                ((InputViewHolder) holder).mEtInput.addTextChangedListener(textWatcher);
                ((InputViewHolder) holder).mEtInput.setTag(textWatcher);
                ((InputViewHolder) holder).mEtInput.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clearSelectedItem();
                        showInputMethod(((InputViewHolder) holder).mEtInput);
                    }
                });

                ((InputViewHolder) holder).mEtInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        mCurInputStr = ((InputViewHolder) holder).mEtInput.getText().toString();
                        inputDone();
                        return true;
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_INPUT;
        } else {
            return TYPE_TAG;
        }
    }

    private boolean isEmpty(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        return false;
    }

    private String stringFilter(String str) throws PatternSyntaxException {
        String regEx = "[#/\\:*?<>|\"\n\t]"; //要过滤掉的字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

    private void showInputMethod(final View view) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager manager = ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE));
                if (manager != null) {
                    manager.restartInput(view);
                }
            }
        });
    }

    private void requestInputFocus(final View view) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                view.requestFocus();
                view.requestFocusFromTouch();
            }
        });
    }

    private void removeItem(int position){
        if (position < 0 || position >= mList.size()) {
            return;
        }
        mList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);
    }

    private void clearSelectedItem() {
        if (mSelectedPosition != INVALID_POSITION) {
            int tempPos = mSelectedPosition;
            mSelectedPosition = INVALID_POSITION;
            notifyItemChanged(tempPos);
        }
    }

    public void inputDone() {
        if (!isEmpty(mCurInputStr)) {
            mList.add(mCurInputStr);
            mCurInputStr = "";
            mSelectedPosition = INVALID_POSITION;
            notifyItemInserted(mList.size() - 1);
            notifyItemChanged(getItemCount() - 1);
            mListener.onDataSetChange();
        }
    }

    class TagViewHolder extends RecyclerView.ViewHolder {

        public TextView mTvTag;

        public TagViewHolder(View view) {
            super(view);
            mTvTag = (TextView) view.findViewById(R.id.tv_tag);
        }

    }

    class InputViewHolder extends RecyclerView.ViewHolder {

        public EditText mEtInput;

        public InputViewHolder(View view) {
            super(view);
            mEtInput = (EditText) view.findViewById(R.id.et_input);
        }

    }

    public interface OnTagClickListener {
        public void onRemoved(String removedTag);
        public void onDataSetChange();
    }

}
