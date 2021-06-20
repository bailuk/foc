package ch.bailu.foc;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


public class FocFileTest {

    
    @TempDir
    File tempDir;

    @Test
    public void testDirectory()  {
        Foc tempDir = new FocFile(this.tempDir);

        Foc dir = tempDir.child("test");
        dir.rmdirs();

        assertEquals(true, dir.mkdirs());
        assertEquals(true, dir.exists());
        assertEquals(true, dir.isDir());
        assertEquals(false, dir.isFile());

        assertEquals("test", dir.getName());

        assertEquals(true, dir.rmdir());
        assertEquals(false, dir.exists());
        assertEquals(false, dir.isDir());
        assertEquals(false, dir.isFile());      
     
        dir = dir.child("test2").child("test3");
        assertEquals("test3", dir.getName());
        assertEquals(true, dir.mkdirs());
        assertEquals(true, dir.exists());
        assertEquals(true, dir.isDir());
        
        dir = dir.parent();
        assertEquals("test2", dir.getName());
        assertEquals(true, dir.exists());
        assertEquals(true, dir.isDir());

        dir = dir.parent();
        assertEquals("test", dir.getName());
        assertEquals(true, dir.exists());
        assertEquals(true, dir.isDir());

        dir.rmdirs();
        assertEquals(false, dir.exists());
    }


    @Test
    public void testIO() throws IOException {
        Foc tempDir = new FocFile(this.tempDir);

        Foc file = tempDir.child("test.txt");
        write(file, "test");
        assertEquals("test", read(file));
    }


    private void write(Foc file, String content) throws IOException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file.openW());
            writer.print(content);
        } finally {
            if (writer != null) writer.close();
        } 

    }


    private String read(Foc file) throws IOException {
        Scanner scanner = null;

        try {
            scanner = new Scanner(file.openR());
            return scanner.nextLine();
        } finally {
            if (scanner != null) scanner.close();
        }
    }
}
