package org.mozilla.vrbrowser.ui.keyboards;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vive.browser.wave.R;
import org.mozilla.vrbrowser.input.CustomKeyboard;
import org.mozilla.vrbrowser.ui.widgets.WidgetPlacement;
import org.mozilla.vrbrowser.utils.StringUtils;

import java.util.Locale;

public class SwedishKeyboard extends BaseKeyboard {
    private CustomKeyboard mKeyboard;
    private CustomKeyboard mSymbolsKeyboard;
    private Locale mLocale;

    public SwedishKeyboard(Context aContext) {
        super(aContext);
        mLocale = new Locale("sv", "SE");
    }

    @NonNull
    @Override
    public CustomKeyboard getAlphabeticKeyboard() {
        if (mKeyboard == null) {
            mKeyboard = new CustomKeyboard(mContext.getApplicationContext(), R.xml.keyboard_qwerty_swedish);
        }
        return mKeyboard;
    }

    @Nullable
    @Override
    public CustomKeyboard getSymbolsKeyboard() {
        if (mSymbolsKeyboard == null) {
            mSymbolsKeyboard = new CustomKeyboard(mContext.getApplicationContext(), R.xml.keyboard_symbols_swedish);
        }
        return mSymbolsKeyboard;
    }

    @Override
    public float getAlphabeticKeyboardWidth() {
        return WidgetPlacement.dpDimension(mContext, R.dimen.keyboard_alphabetic_width_swedish);
    }

    @Nullable
    @Override
    public CandidatesResult getCandidates(String aText) {
        return null;
    }

    @Override
    public String getKeyboardTitle() {
        return StringUtils.getStringByLocale(mContext, R.string.settings_language_swedish, getLocale());
    }

    @Override
    public Locale getLocale() {
        return mLocale;
    }

    @Override
    public String getSpaceKeyText(String aComposingText) {
        return StringUtils.getStringByLocale(mContext, R.string.settings_language_swedish, getLocale());
    }

    @Override
    public String[] getDomains(String... domains) {
        return super.getDomains(".se");
    }
}
