package ch.bailu.foc_extended;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import ch.bailu.foc.Foc;

class FocResourceTest {

    @Test
    void testResources() throws IOException {
        Foc resources = new FocResourceFactory().toFoc("test-resources");
        Foc file = resources.child("test.txt");

        assertEquals("test.txt", file.getName());
        assertTrue(file.isFile());
        assertTrue(file.canRead());
        assertFalse(file.canWrite());
        assertFalse(file.rm());

        assertTrue(resources.isDir());

        var stream = file.openR();
        var text = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        assertEquals("Test\n", text);
        stream.close();

        var parent = file.parent();
        assertEquals("test-resources", parent.getPath());

        var child = parent.child("test.txt");
        var descendant = parent.descendant("test.txt");

        assertEquals(child, descendant);

        assertEquals("test-resources", resources.getPath());
        AtomicInteger count = new AtomicInteger();
        resources.foreach((item)-> {
            System.out.println("count");
            count.getAndIncrement();
        });

        assertEquals(2, count.get());

        Foc file2 = resources.descendant("test-child/test2.txt");
        assertEquals("test-resources/test-child/test2.txt", file2.getPath());
        assertTrue(file.isFile());
        assertTrue(file.canRead());
        assertFalse(file.canWrite());
        assertFalse(file.rm());

        var stream2 = file2.openR();
        var text2 = new String(stream2.readAllBytes(), StandardCharsets.UTF_8);
        assertEquals("Test2\n", text2);
        stream2.close();
    }
}
