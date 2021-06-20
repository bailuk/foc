package ch.bailu.foc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;



public class FocNameTest {

    
    @Test
    public void testName() {
        Foc foc = new FocName("test");

        assertEquals("test", foc.getName());
        assertEquals(foc.getName(), foc.getPath());

        foc = foc.child("child");
        assertEquals("test/child", foc.getPath());
        assertEquals(foc.getName(), foc.getPath());

        assertEquals(false, foc.exists());
    }
    
}
