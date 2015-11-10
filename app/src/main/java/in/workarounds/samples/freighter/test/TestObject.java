package in.workarounds.samples.freighter.test;

import in.workarounds.bundler.annotations.Cargo;
import in.workarounds.bundler.annotations.Freighter;

/**
 * Created by madki on 25/10/15.
 */
@Freighter
public class TestObject {
    @Cargo
    int one;

    public TestObject() {
    }

}
