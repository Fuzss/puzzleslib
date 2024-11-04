package fuzs.puzzleslib.neoforge.impl.attachment;

import fuzs.puzzleslib.impl.attachment.AttachmentTypeAdapter;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredHolder;

public record NeoForgeAttachmentTypeAdapter<T extends IAttachmentHolder, A>(DeferredHolder<AttachmentType<?>, AttachmentType<A>> attachmentType) implements AttachmentTypeAdapter<T, A> {

    @Override
    public ResourceLocation resourceLocation() {
        return this.attachmentType.getKey().location();
    }

    @Override
    public boolean hasData(T holder) {
        return holder.hasData(this.attachmentType);
    }

    @Override
    public A getData(T holder) {
        return holder.getData(this.attachmentType);
    }

    @Override
    public A setData(T holder, A value) {
        return holder.setData(this.attachmentType, value);
    }

    @Override
    public A removeData(T holder) {
        return holder.removeData(this.attachmentType);
    }
}
