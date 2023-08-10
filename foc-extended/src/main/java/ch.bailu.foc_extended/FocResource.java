package ch.bailu.foc_extended;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ch.bailu.foc.Foc;
import ch.bailu.foc.util.Check;

/**
 * Foc adapter for Java resources
 */
public class FocResource extends Foc {
    private final String resource;

    private List<String> children = new ArrayList<>(0);
    private final Check isDirectory = new Check();
    private final Check isFile = new Check();

    public FocResource(String resourcePath) {
        this.resource = resourcePath;
    }

    @Override
    public boolean remove() throws IOException, SecurityException {
        return false;
    }

    @Override
    public boolean mkdir() {
        return false;
    }

    @Override
    public Foc parent() {
        String path = new File(resource).getParent();
        if (path != null) {
            return new FocResource(path);
        }
        return null;
    }

    @Override
    public Foc child(String name) {
        return new FocResource(new File(resource, name).getPath());
    }

    @Override
    public String getName() {
        return new File(resource).getName();
    }

    @Override
    public String getPath() {
        return resource;
    }

    @Override
    public void foreach(Foc.OnHaveFoc onHaveFoc) {
        checkAndLoadDirectory();
        for (String child : children) {
            onHaveFoc.run(child(child));
        }
    }

    @Override
    public void foreachFile(Foc.OnHaveFoc onHaveFoc) {
        foreach(child -> {
            if (child.isFile()) {
                onHaveFoc.run(child);
            }
        });
    }

    @Override
    public void foreachDir(Foc.OnHaveFoc onHaveFoc) {
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
    public long length() {
        return 0;
    }

    @Override
    public long lastModified() {
        return 0;
    }

    @Override
    public InputStream openR() throws IOException {
        var result =  this.getClass().getClassLoader().getResourceAsStream(resource);
        if (result == null) {
            throw new IOException();
        }
        return result;
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
            } catch (Exception e) {
                isFile.set(false);
            } finally {
                close(toClose);
            }
        }
    }

    private void checkIsDirectory() {
        if (!isDirectory.isSet()) {
            try {
                children = listFilesFromPath();
                isDirectory.set(true);
            } catch (IOException | URISyntaxException e) {
                try {
                    children = listFilesFromStream();
                    isDirectory.set(true);
                } catch (IOException e2) {
                    children = new ArrayList<>(0);
                    isDirectory.set(false);
                }
            }
        }
    }

    private void checkAndLoadDirectory() {
        checkIsFile();
        checkIsDirectory();
    }

    private List<String> listFilesFromPath() throws IOException, URISyntaxException {
        ArrayList<String> result = new ArrayList<>();

        Path dirPath = Paths.get(getResourceURI(resource));
        Files.list(dirPath).forEach(p -> result.add(p.toString()));
        return result;
    }


    private List<String> listFilesFromStream() throws IOException {
        List<String> result = new ArrayList<>();

        try (var in = new BufferedReader(new InputStreamReader(openR()))) {
            String resource;

            while ((resource = in.readLine()) != null) {
                result.add(resource);
            }

            return result;
        }
    }

    private URI getResourceURI(String resource) throws IOException, URISyntaxException {
        var res = FocResource.class.getResource(resource);
        if (res == null) {
            throw new IOException("Failed to get URI from '" + resource + "'");
        }
        return res.toURI();
    }
}
