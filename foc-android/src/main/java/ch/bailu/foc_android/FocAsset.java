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
    private Boolean isDirectory = null;
    private Boolean isFile = null;

    public FocAsset(AssetManager m, String a) {
        manager = m;
        asset = a;
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
    public void foreach(Execute e) {
        checkAndLoadDirectory();
        for (String child : children) {
            e.execute(child(child));
        }
    }

    @Override
    public void foreachFile(Execute e) {
        foreach(new Execute() {
            @Override
            public void execute(Foc child) {
                if (child.isFile()) {
                    e.execute(child);
                }
            }
        });
    }

    @Override
    public void foreachDir(Execute e) {
        foreach(new Execute() {
            @Override
            public void execute(Foc child) {
                if (child.isDir()) {
                    e.execute(child);
                }
            }
        });
    }

    @Override
    public boolean isDir() {
        checkAndLoadDirectory();
        return isDirectory.booleanValue();
    }

    @Override
    public boolean isFile() {
        checkIsFile();
        return isFile.booleanValue();
    }

    @Override
    public boolean exists() {
        return isDir() || isFile();
    }

    @Override
    public boolean canRead() {
        return isFile();
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
        if (isFile == null) {
            InputStream toClose = null;
            try {
                toClose = openR();
                isFile = Boolean.TRUE;
                isDirectory = Boolean.FALSE;
            } catch (Exception e) {
                isFile = Boolean.FALSE;
            } finally {
                close(toClose);
            }
        }
    }

    private void checkAndLoadDirectory() {
        if (isDirectory == null) {
            try {
                children = manager.list(asset);
                isDirectory = Boolean.TRUE;
                isFile = Boolean.FALSE;
            } catch (IOException e) {
                children = new String[0];
                isDirectory = Boolean.FALSE;
            }
        }
    }
}
