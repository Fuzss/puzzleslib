package fuzs.puzzleslib.fabric.impl.client.key;

import fuzs.puzzleslib.api.client.key.v1.KeyActivationContext;

public interface ActivationContextKeyMapping {

    void puzzleslib$setKeyActivationContext(KeyActivationContext context);

    KeyActivationContext puzzleslib$getKeyActivationContext();
}
