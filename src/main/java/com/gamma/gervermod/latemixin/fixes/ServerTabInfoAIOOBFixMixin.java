package com.gamma.gervermod.latemixin.fixes;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.black_dog20.servertabinfo.client.TpsPage;
import com.black_dog20.servertabinfo.client.objects.IRenderable;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Mixin(TpsPage.class)
public abstract class ServerTabInfoAIOOBFixMixin {

    @Shadow(remap = false)
    private int currentPage;

    @WrapOperation(
        method = "renderTps",
        at = @At(
            value = "INVOKE",
            target = "Lcom/black_dog20/servertabinfo/utility/RenderHelper;getPage(IILjava/util/List;)Ljava/util/List;",
            remap = false),
        remap = false)
    private List<IRenderable> wrapper(int page, int itemPerPage, List<IRenderable> input,
        Operation<List<IRenderable>> original) {
        try {
            return original.call(page, itemPerPage, input);
        } catch (IllegalArgumentException e) {
            this.currentPage = 1; // Reset page back to 1, then try again.
            return original.call(1, itemPerPage, input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
