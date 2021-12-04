package ch.bailu.foc;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public abstract class Foc {

    /**
     * Remove file-object. Throws exception on failure
     * @return true if removed
     * @throws IOException
     * @throws SecurityException
     */
    public abstract boolean remove() throws IOException, SecurityException;

    /**
     * Remove file-object. Returns false on failure
     * @return true if removed
     */
    public boolean rm() {
        try {
            return remove();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Remove directory if file-object is a directory
     * @return true if removed
     */
    public boolean rmdir() {
        return isDir() && rm();
    }


    /**
     * Remove directory and all child directories. Continues on failure. Does not remove any non-directories.
     * @return true if all directories are removed. False if failed to remove at least one directory
     */
    public boolean rmdirs() {
        final boolean[] ok = {true};

        foreachDir(new Execute() {
            @Override
            public void execute(Foc child) {
                if (!child.rmdirs()) {
                    ok[0] = false;
                }
            }
        });
        return ok[0] && rmdir();
    }

    /**
     * Try to remove all child file-objects including this file-object. Continues if it fails to remove a file-object.
     * @return true if all file-objects are removed. False if failed to remove at least one file-object.
     */
    public boolean rmRecoursive() {
        final boolean[] ok = {true};

        foreach(new Execute() {
            @Override
            public void execute(Foc child) {
                if (!child.rmRecoursive()) {
                    ok[0] = false;
                }
            }
        });
        return ok[0] && rm();
    }


    /**
     * Create a sub directory.
     * @return True on success. False on failure.
     */
    public abstract boolean mkdir();

    /**
     * Creates all parent directories and this directory
     * @return true if this is an existing directory. False if this is a file-object or if this does not exist.
     */
    public boolean mkdirs() {
        return isDir() || (mkParents() && mkdir());
    }

    /**
     * Creates all parent directories
     * @return true if parent directory exists. False if parent is not a directory or does not exist.
     */
    public boolean mkParents() {
        Foc parent = parent();
        return parent != null && parent.mkdirs();
    }

    /**
     * Check if this file-object has a parent object or if this is a root-object. The parent object
     * can not exist or not.
     * @return true if this object has a logical parent object.
     */
    public boolean hasParent() {
        return parent() != null;
    }

    /**
     * Get logical parent object in path hierarchy.
     * @return return parent or null if there is no parent
     */
    public abstract Foc parent();


    /**
     * Move file-object to target path. Throws exception on failure.
     * @param target New path of this file-object including name of new file-object.
     * @return true on success
     * @throws IOException
     * @throws SecurityException
     */
    public boolean move(Foc target) throws IOException , SecurityException {
        copy(target);
        return  remove();
    }


    /**
     * Move file-object to target path. Returns false on failure
     * @param target New path of this file-object including name of new file-object.
     * @return true on success, false on failure
     */
    public boolean mv(Foc target) {
        try {
            return move(target);
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Copy file-object ot target path. Returns false on failure.
     * @param copy Path of copied file-object including name.
     * @return true on success, false on failure
     */
    public boolean cp(Foc copy) {
        try {
            copy(copy);
            return true;

        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Copy file-object ot target path. Throws exception on failure.
     * @param copy Path of copied file-object including name.
     */
    public void copy(Foc copy) throws IOException, SecurityException {
        OutputStream out = null;
        InputStream in = null;

        try {
            in = openR();
            out = copy.openW();
            copy(in, out);

        } finally {
            Foc.close(in);
            Foc.close(out);
        }
    }


    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int count;
        while ((count = in.read(buffer)) > 0) {
            out.write(buffer,0,count);
        }
    }


    public Foc descendant(String path) {
        Foc descendant = this;

        String[] descendants = path.split("/");

        for (String child : descendants) {
            descendant = descendant.child(child);
        }

        return descendant;
    }


    public abstract Foc child(String name);


    public abstract String getName();

    public abstract String getPath();
    public String getPathName() {
        return getPath();
    }

    public abstract static class Execute {
        public abstract void execute(Foc child);
    }

    public abstract void foreach(Execute e);
    public abstract void foreachFile(Execute e);
    public abstract void foreachDir(Execute e);


    public abstract boolean isDir();
    public abstract boolean isFile();
    public abstract boolean exists();

    public boolean canOnlyRead() {
        return canRead() && ! canWrite();
    }
    public abstract boolean canRead();
    public abstract boolean canWrite();

    public abstract long length();
    public abstract long lastModified();

    public void update() {}

    public abstract InputStream openR() throws IOException, SecurityException;
    public abstract OutputStream openW() throws IOException, SecurityException;

    public static void close(Closeable toClose) {
        try {
            if (toClose != null)
                toClose.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean equals(Object o)  {
        return o instanceof Foc && equals(getPath(), ((Foc) o).getPath());

    }

    private static boolean equals(Object a, Object b) {
        if (a != null && b!= null) {
            return a.equals(b);
        }
        return a == b;
    }

    @Override
    public String toString() {
        return getPath();
    }


    @Override
    public int hashCode() {
        return getPath().hashCode();
    }

}
