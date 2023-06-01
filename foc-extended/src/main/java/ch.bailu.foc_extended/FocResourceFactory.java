package ch.bailu.foc_extended;

import ch.bailu.foc.Foc;
import ch.bailu.foc.FocFactory;

/**
 * Factory class for foc adapter for Java resources
 */
public class FocResourceFactory implements FocFactory {
    @Override
    public Foc toFoc(String string) {
        return new FocResource(string);
    }
}
