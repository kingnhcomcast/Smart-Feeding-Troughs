package io.drahlek.smartfeedingtroughs;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

	public static final String MOD_ID = "smartfeedingtroughs";
	public static final String MOD_NAME = "SmartFeedingTroughs";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

	public static final String GROUP = "io.drahlek.smartfeedingtroughs";

	public static String describeEntity(Entity entity) {
		return "%s[type=%s, id=%s, name=%s]".formatted(
				entity.getClass().getSimpleName(),
				BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()),
				entity.getId(),
				entity.getName().getString()
		);
	}

	public static String describeBlockEntity(BlockEntity blockEntity) {
		return "%s[type=%s, pos=%s]".formatted(
				blockEntity.getClass().getSimpleName(),
				BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()),
				blockEntity.getBlockPos()
		);
	}
}
