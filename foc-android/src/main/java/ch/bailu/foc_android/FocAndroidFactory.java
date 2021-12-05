package ch.bailu.foc_android;

import android.content.Context;

import ch.bailu.foc.Foc;
import ch.bailu.foc.FocFactory;

public class FocAndroidFactory implements FocFactory {
    private final Context context;

    public FocAndroidFactory(Context context) {
        this.context = context;
    }

    @Override
    public Foc toFoc(String string) {
        return FocAndroid.factory(context, string);
    }
}
