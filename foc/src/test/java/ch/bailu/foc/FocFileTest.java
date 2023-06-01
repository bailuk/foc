package ch.bailu.foc;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;


public class FocFileTest {


    @TempDir
    File tempDir;

    @Test
    public void testDirectory()  {
        Foc tempDir = new FocFile(this.tempDir);

        Foc dir = tempDir.child("test");
        dir.rmdirs();

        assertTrue(dir.mkdirs());
        assertTrue(dir.exists());
        assertTrue(dir.isDir());
        assertFalse(dir.isFile());

        assertEquals("test", dir.getName());

        assertTrue(dir.rmdir());
        assertFalse(dir.exists());
        assertFalse(dir.isDir());
        assertFalse(dir.isFile());

        dir = dir.child("test2").child("test3");
        assertEquals("test3", dir.getName());
        assertTrue(dir.mkdirs());
        assertTrue(dir.exists());
        assertTrue(dir.isDir());

        dir = dir.parent();
        assertEquals("test2", dir.getName());
        assertTrue(dir.exists());
        assertTrue(dir.isDir());

        dir = dir.parent();
        assertEquals("test", dir.getName());
        assertTrue(dir.exists());
        assertTrue(dir.isDir());

        dir.rmdirs();
        assertFalse(dir.exists());
    }


    @Test
    public void testIO() throws IOException {
        Foc tempDir = new FocFile(this.tempDir);

        Foc file = tempDir.child("test.txt");
        write(file, "test");
        assertEquals("test", read(file));
    }


    private void write(Foc file, String content) throws IOException {
        try (PrintWriter writer = new PrintWriter(file.openW())) {
            writer.print(content);
        }

    }


    private String read(Foc file) throws IOException {
        try (Scanner scanner = new Scanner(file.openR())) {
            return scanner.nextLine();
        }
    }
}
