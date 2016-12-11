package com.jacktheogre.lightswitch.tools;

/**
 * Created by luna on 01.11.16.
 */
public class Assets {

    public static AssetLoader assetLoader;

    public static AssetLoader getAssetLoader() {
        if(assetLoader == null) {
            assetLoader = new AssetLoader();
            assetLoader.load();
        }
        return assetLoader;
    }
}
