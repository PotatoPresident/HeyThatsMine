package com.github.fabricservertools.htm.listeners;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockableObject;
import com.github.fabricservertools.htm.events.BlockExplodeCallback;
import com.github.fabricservertools.htm.events.EnderDragonBreakBlockCallback;
import com.github.fabricservertools.htm.events.WorldBreakBlockCallback;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

public class WorldEventListener {
	public static void init() {
		BlockExplodeCallback.EVENT.register(WorldEventListener::onBlockExplode);
		WorldBreakBlockCallback.EVENT.register(WorldEventListener::onBlockBreak);
		EnderDragonBreakBlockCallback.EVENT.register(WorldEventListener::enderDragonBreakBlock);
	}

	private static ActionResult enderDragonBreakBlock(ServerWorld world, BlockPos pos, boolean b) {
		BlockState state = world.getBlockState(pos);
		if (!state.hasBlockEntity()) return ActionResult.PASS;


		BlockEntity blockEntity = world.getBlockEntity(pos);

		if (blockEntity instanceof LockableObject) {
			//noinspection ConstantConditions
			if (InteractionManager.getLock(world, pos).isLocked()) return ActionResult.FAIL;
		}

		return ActionResult.PASS;
	}

	private static ActionResult onBlockBreak(World world, BlockPos pos, boolean drop, @Nullable Entity entity) {
		if (world.isClient) return ActionResult.PASS;

		HTMContainerLock lock = InteractionManager.getLock((ServerWorld) world, pos);

		if (lock == null) return ActionResult.PASS;
		if (!lock.isLocked()) return ActionResult.PASS;

		if (entity instanceof ServerPlayerEntity) {
			if (lock.isOwner((ServerPlayerEntity) entity)) return ActionResult.PASS;
		}

		return ActionResult.FAIL;
	}

	private static ActionResult onBlockExplode(ExplosionBehavior explosionBehavior, Explosion explosion, BlockPos pos, BlockState state) {
		if (!state.hasBlockEntity()) return ActionResult.PASS;

		BlockEntity blockEntity = explosion.getWorld().getBlockEntity(pos);
		if (blockEntity instanceof LockableObject) {
			//noinspection ConstantConditions
			if (InteractionManager.getLock(explosion.getWorld(), pos).isLocked()) return ActionResult.FAIL;
		}

		return ActionResult.PASS;
	}
}
