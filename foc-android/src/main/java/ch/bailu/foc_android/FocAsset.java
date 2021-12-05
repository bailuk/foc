package ch.bailu.foc_android;

import android.content.res.AssetManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ch.bailu.foc.Foc;

public class FocAsset extends Foc {

    private final String asset;
    private final AssetManager manager;

    private String[] children = new String[0];
    private final Check isDirectory = new Check();
    private final Check isFile = new Check();

    public FocAsset(AssetManager manager, String assetPath) {
        this.manager = manager;
        this.asset = assetPath;
    }


    @Override
    public long lastModified() {
        return System.currentTimeMillis();
    }


    @Override
    public boolean remove() {
        return false;
    }

    @Override
    public boolean mkdir() {
        return false;
    }

    @Override
    public Foc parent() {
        String path = new File(asset).getParent();
        if (path != null) {
            return new FocAsset(manager, path);
        }
        return null;
    }

    @Override
    public Foc child(String name) {
        return new FocAsset(manager, new File(asset, name).getPath());
    }

    @Override
    public String getName() {
        return new File(asset).getName();
    }

    @Override
    public String getPath() {
        return asset;
    }

    @Override
    public long length() {
        return 0;
    }

    @Override
    public void foreach(OnHaveFoc onHaveFoc) {
        checkAndLoadDirectory();
        for (String child : children) {
            onHaveFoc.run(child(child));
        }
    }

    @Override
    public void foreachFile(OnHaveFoc onHaveFoc) {
        foreach(child -> {
            if (child.isFile()) {
               onHaveFoc.run(child);
            }
        });
    }

    @Override
    public void foreachDir(OnHaveFoc onHaveFoc) {
        foreach(child -> {
            if (child.isDir()) {
               onHaveFoc.run(child);
            }
        });
    }

    @Override
    public boolean isDir() {
        checkAndLoadDirectory();
        return isDirectory.get();
    }

    @Override
    public boolean isFile() {
        checkIsFile();
        return isFile.get();
    }

    @Override
    public boolean exists() {
        return isDir() || isFile();
    }

    @Override
    public boolean canRead() {
        return exists();
    }

    @Override
    public boolean canWrite() {
        return false;
    }

    @Override
    public InputStream openR() throws IOException {
        return manager.open(asset);
    }

    @Override
    public OutputStream openW() throws IOException {
        throw new IOException();
    }

    private void checkIsFile() {
        if (!isFile.isSet()) {
            InputStream toClose = null;
            try {
                toClose = openR();
                isFile.set(true);
                isDirectory.set(false);
            } catch (Exception e) {
                isFile.set(false);
            } finally {
                close(toClose);
            }
        }
    }

    private void checkAndLoadDirectory() {
        checkIsFile();
        if (!isDirectory.isSet()) {
            try {
                children = manager.list(asset);
                isDirectory.set(true);
            } catch (IOException e) {
                children = new String[0];
                isDirectory.set(false);
            }
        }
    }

    private static class Check {
        private Boolean check = null;

        public void set(boolean b) {
            if (check == null) {
                check = b;
            }
        }

        public boolean get() {
            return (check != null && check.booleanValue());
        }

        public  boolean isSet() {
            return check != null;
        }
    }
}
