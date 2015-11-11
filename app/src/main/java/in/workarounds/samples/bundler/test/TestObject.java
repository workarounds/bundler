package in.workarounds.samples.bundler.test;

import in.workarounds.bundler.annotations.BundlerArg;
import in.workarounds.bundler.annotations.RequireBundler;

/**
 * Created by madki on 25/10/15.
 */
@RequireBundler
public class TestObject {
    @BundlerArg
    int one;

    public TestObject() {
    }

}
