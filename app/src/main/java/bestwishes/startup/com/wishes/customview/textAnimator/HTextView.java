package bestwishes.startup.com.wishes.customview.textAnimator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;

import bestwishes.startup.com.wishes.R;

/**
 * Animate TextView
 */
public class HTextView extends android.support.v7.widget.AppCompatTextView {

    private IHText mIHText = new LineText();
    private AttributeSet attrs;
    private int defStyle;
    private int animateType;

    public HTextView(Context context) {
        super(context);
        init(null, 0);
    }

    public HTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public HTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        this.attrs = attrs;
        this.defStyle = defStyle;

        // Get the attributes array
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.HTextView);
        animateType = typedArray.getInt(R.styleable.HTextView_animateType, 0);
        final String fontAsset = typedArray.getString(R.styleable.HTextView_fontAsset);

        if (!this.isInEditMode()) {
            // Set custom typeface
            if (fontAsset != null && !fontAsset.trim().isEmpty()) {
                setTypeface(ResourcesCompat.getFont(getContext(), R.font.toolbar_font));
            }
        }


        mIHText = new LineText();
        typedArray.recycle();
        initHText(attrs, defStyle);
    }

    private void initHText(AttributeSet attrs, int defStyle) {
        mIHText.init(this, attrs, defStyle);
    }

    public void animateText(CharSequence text) {
        mIHText.animateText(text);
    }

    public void animateText(CharSequence text, int colors[]) {
        mIHText.animateText(text);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mIHText.onDraw(canvas);
    }

    public void reset(CharSequence text) {
        mIHText.reset(text);
    }

    public void setAnimateType() {
        mIHText = new LineText();
        initHText(attrs, defStyle);
    }


    @Override
    public void setTextColor(@ColorInt int color) {
        //Check for RainbowText. Do not alter color if on that type due to paint conflicts
        if (animateType != 8) {
            super.setTextColor(color);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState state = new SavedState(superState);
        state.animateType = animateType;
        return state;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(state);
        animateType = ss.animateType;
    }

    public static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int animateType;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            animateType = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(animateType);
        }

        @Override
        public int describeContents() {
            return 0;
        }
    }

}
