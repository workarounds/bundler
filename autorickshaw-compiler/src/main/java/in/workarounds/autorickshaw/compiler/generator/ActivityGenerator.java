package in.workarounds.autorickshaw.compiler.generator;

import com.squareup.javapoet.TypeSpec;

import java.util.List;

import in.workarounds.autorickshaw.compiler.Provider;
import in.workarounds.autorickshaw.compiler.model.DestinationModel;
import in.workarounds.autorickshaw.compiler.model.PassengerModel;

/**
 * Created by madki on 16/10/15.
 */
public class ActivityGenerator implements Generator {
    private DestinationModel destinationModel;
    private List<PassengerModel> passengerModels;
    private Provider provider;

    public ActivityGenerator(DestinationModel destinationModel,
                             List<PassengerModel> passengerModels,
                             Provider provider) {
        this.destinationModel = destinationModel;
        this.passengerModels = passengerModels;
        this.provider = provider;
    }

    @Override
    public TypeSpec brewBuilder() {
        return null;
    }

    @Override
    public TypeSpec brewParser() {
        return null;
    }

    @Override
    public TypeSpec brewConstants() {
        return null;
    }
}
