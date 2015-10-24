package in.workarounds.freighter.compiler.generator;

import java.util.List;

import in.workarounds.freighter.compiler.Provider;
import in.workarounds.freighter.compiler.model.CargoModel;
import in.workarounds.freighter.compiler.model.FreighterModel;

/**
 * Created by madki on 24/10/15.
 */
public class FragmentWriter extends Writer {

    protected FragmentWriter(Provider provider, FreighterModel freighterModel, List<CargoModel> cargoList) {
        super(provider, freighterModel, cargoList);
    }
}
