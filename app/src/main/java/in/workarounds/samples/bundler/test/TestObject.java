package in.workarounds.samples.bundler.test;

import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;

/**
 * Created by madki on 25/10/15.
 */
@RequireBundler
public class TestObject {
    @Arg
    int one;

    public TestObject() {
    }

}
