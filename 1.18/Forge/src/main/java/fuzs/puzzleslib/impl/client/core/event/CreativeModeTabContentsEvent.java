package fuzs.puzzleslib.impl.client.core.event;

import fuzs.puzzleslib.api.item.v2.DisplayItemsOutput;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public class CreativeModeTabContentsEvent extends Event implements IModBusEvent {
    private final CreativeModeTab tab;
    private final DisplayItemsOutput output;

    public CreativeModeTabContentsEvent(CreativeModeTab tab, DisplayItemsOutput output) {
        this.tab = tab;
        this.output = output;
    }

    public CreativeModeTab getTab() {
        return this.tab;
    }

    public DisplayItemsOutput getOutput() {
        return this.output;
    }
}
