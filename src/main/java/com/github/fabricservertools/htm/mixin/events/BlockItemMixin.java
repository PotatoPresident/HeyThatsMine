package com.github.fabricservertools.htm.mixin.events;

import com.github.fabricservertools.htm.events.PlayerPlaceBlockCallback;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item {
	public BlockItemMixin(Settings settings) {
		super(settings);
	}

	/**
	 * Log item placement hook
	 */
	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemPlacementContext;getBlockPos()Lnet/minecraft/util/math/BlockPos;"
			),
			method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
			cancellable = true
	)
	public void HTMPlaceEventTrigger(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> info) {
		if (context.getPlayer() == null) return;

		ActionResult result = PlayerPlaceBlockCallback.EVENT.invoker().place(context.getPlayer(), context);

		if (result != ActionResult.PASS) {
			info.setReturnValue(ActionResult.FAIL);
		}
	}
}