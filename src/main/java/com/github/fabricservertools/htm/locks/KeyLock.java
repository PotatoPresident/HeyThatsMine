package com.github.fabricservertools.htm.locks;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.api.Lock;
import com.github.fabricservertools.htm.api.LockType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class KeyLock implements Lock {
	private ItemStack key;

	@Override
	public boolean canOpen(ServerPlayerEntity player, HTMContainerLock lock) {
		if (lock.isTrusted(player.getUuid())) return true;
		if (Utility.getGlobalTrustState(player.server).isTrusted(lock.getOwner(), player.getUuid()))
			return true;

		ItemStack itemStack = player.getMainHandStack();
		return ItemStack.canCombine(itemStack, key);
	}

	@Override
	public void onLockSet(ServerPlayerEntity player, HTMContainerLock lock) {
		key = player.getMainHandStack().copy();
		player.sendMessage(Text.translatable("text.htm.key_set", key.toHoverableText()), false);
	}

	@Override
	public void onInfo(ServerPlayerEntity player, HTMContainerLock lock) {
		player.sendMessage(Text.translatable("text.htm.key", key.toHoverableText()), false);
	}

	@Override
	public NbtCompound toTag() {
		NbtCompound tag = new NbtCompound();
		key.writeNbt(tag);
		return tag;
	}

	@Override
	public void fromTag(NbtCompound tag) {
		key = ItemStack.fromNbt(tag);
	}

	@Override
	public LockType<?> getType() {
		return LockType.KEY_LOCK;
	}
}
