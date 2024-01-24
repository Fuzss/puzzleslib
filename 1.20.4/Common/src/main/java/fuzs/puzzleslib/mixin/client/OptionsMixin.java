package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.tutorial.TutorialSteps;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(Options.class)
abstract class OptionsMixin {
    @Shadow
    @Final
    private File optionsFile;
    @Shadow
    @Final
    private OptionInstance<Integer> renderDistance;
    @Shadow
    @Final
    private OptionInstance<Integer> framerateLimit;
    @Shadow
    @Final
    private OptionInstance<Boolean> narratorHotkey;
    @Shadow
    public boolean advancedItemTooltips;
    @Shadow
    public TutorialSteps tutorialStep;
    @Shadow
    public boolean joinedFirstServer;
    @Shadow
    public boolean hideBundleTutorial;
    @Shadow
    @Final
    private OptionInstance<Boolean> operatorItemsTab;
    @Shadow
    @Final
    private OptionInstance<Boolean> entityShadows;
    @Shadow
    @Final
    private OptionInstance<Boolean> realmsNotifications;
    @Shadow
    @Final
    private OptionInstance<Boolean> showSubtitles;
    @Shadow
    @Final
    private OptionInstance<Integer> guiScale;
    @Shadow
    public boolean onboardAccessibility;

    @Inject(method = "load", at = @At("HEAD"))
    public void load(CallbackInfo callback) {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment() || this.optionsFile.exists()) return;
        this.renderDistance.set(16);
        this.framerateLimit.set(60);
        this.narratorHotkey.set(false);
        this.advancedItemTooltips = true;
        this.tutorialStep = TutorialSteps.NONE;
        this.joinedFirstServer = true;
        this.hideBundleTutorial = true;
        this.operatorItemsTab.set(true);
        this.entityShadows.set(false);
        this.realmsNotifications.set(false);
        this.showSubtitles.set(true);
        this.guiScale.set(5);
        this.onboardAccessibility = false;
    }
}
