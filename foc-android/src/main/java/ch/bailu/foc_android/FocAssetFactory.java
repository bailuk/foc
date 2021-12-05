package ch.bailu.foc_android;

import android.content.Context;
import android.content.res.AssetManager;

import ch.bailu.foc.Foc;
import ch.bailu.foc.FocFactory;

public class FocAssetFactory implements FocFactory {
    private final AssetManager assets;

    public FocAssetFactory(Context context) {
        assets = context.getAssets();
    }

    @Override
    public Foc toFoc(String string) {
        return new FocAsset(assets, string);
    }
}
