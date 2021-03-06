package com.goodformentertainment.bc.zown.api.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.goodformentertainment.bc.zown.api.IConfiguration;

import net.canarymod.api.entity.Entity;
import net.canarymod.api.world.blocks.BlockType;

public class Configuration implements IConfiguration {
    private final Set<String> ownerPermissions;
    private final Map<String, Boolean> flags;
    private final Set<BlockType> blockBuildExclusions;
    private final Set<BlockType> blockInteractExclusions;
    private final HashSet<Class<? extends Entity>> entityCreateExclusions;
    private final HashSet<Class<? extends Entity>> entityInteractExclusions;
    private final HashSet<String> commandRestrictions;

    public Configuration() {
        ownerPermissions = new HashSet<String>();
        flags = new HashMap<String, Boolean>();
        blockBuildExclusions = new HashSet<BlockType>();
        blockInteractExclusions = new HashSet<BlockType>();
        entityCreateExclusions = new HashSet<Class<? extends Entity>>();
        entityInteractExclusions = new HashSet<Class<? extends Entity>>();
        commandRestrictions = new HashSet<String>();
    }

    @Override
    public boolean addOwnerPermission(final String flag) {
        return ownerPermissions.add(flag);
    }

    @Override
    public boolean removeOwnerPermission(final String flag) {
        return ownerPermissions.remove(flag);
    }

    @Override
    public boolean hasOwnerPermission(final String flag) {
        return ownerPermissions.contains(flag);
    }

    public Collection<String> getOwnerPermissions() {
        return ownerPermissions;
    }

    @Override
    public Boolean getFlag(final String flag) {
        return flags.get(flag);
    }

    @Override
    public Boolean setFlag(final String flag, final Boolean allow) {
        return flags.put(flag, allow);
    }

    @Override
    public boolean hasFlag(final String flag) {
        return flags.containsKey(flag);
    }

    public Map<String, Boolean> getFlags() {
        return flags;
    }

    @Override
    public boolean addBlockBuildExclusion(final BlockType blockType) {
        return blockBuildExclusions.add(blockType);
    }

    @Override
    public boolean removeBlockBuildExclusion(final BlockType blockType) {
        return blockBuildExclusions.remove(blockType);
    }

    @Override
    public boolean hasBlockBuildExclusion(final BlockType blockType) {
        return blockBuildExclusions.contains(blockType);
    }

    public Collection<BlockType> getBlockBuildExclusions() {
        return blockBuildExclusions;
    }

    @Override
    public boolean addBlockInteractExclusion(final BlockType blockType) {
        return blockInteractExclusions.add(blockType);
    }

    @Override
    public boolean removeBlockInteractExclusion(final BlockType blockType) {
        return blockInteractExclusions.remove(blockType);
    }

    @Override
    public boolean hasBlockInteractExclusion(final BlockType blockType) {
        return blockInteractExclusions.contains(blockType);
    }

    public Collection<BlockType> getBlockInteractExclusions() {
        return blockInteractExclusions;
    }

    @Override
    public boolean addEntityCreateExclusion(final Class<? extends Entity> entityClass) {
        return entityCreateExclusions.add(entityClass);
    }

    @Override
    public boolean removeEntityCreateExclusion(final Class<? extends Entity> entityClass) {
        return entityCreateExclusions.remove(entityClass);
    }

    @Override
    public boolean hasEntityCreateExclusion(final Class<? extends Entity> entityClass) {
        // TODO performance?
        boolean hasExclusion = false;
        for (final Class<? extends Entity> c : entityCreateExclusions) {
            if (c.isAssignableFrom(entityClass)) {
                hasExclusion = true;
                break;
            }
        }
        return hasExclusion;
    }

    public Collection<Class<? extends Entity>> getEntityCreateExclusions() {
        return entityCreateExclusions;
    }

    @Override
    public boolean addEntityInteractExclusion(final Class<? extends Entity> entityClass) {
        return entityInteractExclusions.add(entityClass);
    }

    @Override
    public boolean removeEntityInteractExclusion(final Class<? extends Entity> entityClass) {
        return entityInteractExclusions.remove(entityClass);
    }

    @Override
    public boolean hasEntityInteractExclusion(final Class<? extends Entity> entityClass) {
        // TODO performance?
        boolean hasExclusion = false;
        for (final Class<? extends Entity> c : entityInteractExclusions) {
            if (c.isAssignableFrom(entityClass)) {
                hasExclusion = true;
                break;
            }
        }
        return hasExclusion;
    }

    public Collection<Class<? extends Entity>> getEntityInteractExclusions() {
        return entityInteractExclusions;
    }

    @Override
    public boolean addCommandRestriction(final String command) {
        return commandRestrictions.add(command.toLowerCase());
    }

    @Override
    public boolean removeCommandRestriction(final String command) {
        return commandRestrictions.remove(command.toLowerCase());
    }

    @Override
    public boolean hasCommandRestriction(final String command) {
        return commandRestrictions.contains(command.toLowerCase());
    }

    public Collection<String> getCommandRestrictions() {
        return commandRestrictions;
    }

    @Override
    public Configuration clone() {
        final Configuration config = new Configuration();
        config.ownerPermissions.addAll(ownerPermissions);
        config.flags.putAll(flags);
        config.blockBuildExclusions.addAll(blockBuildExclusions);
        config.blockInteractExclusions.addAll(blockInteractExclusions);
        config.entityCreateExclusions.addAll(entityCreateExclusions);
        config.entityInteractExclusions.addAll(entityInteractExclusions);
        config.commandRestrictions.addAll(commandRestrictions);
        return config;
    }

    @Override
    public String toString() {
        return "Configuration: {\n" + "  ownerPermissions: " + ownerPermissions + "\n  flags: "
                + flags + "\n  blockBuildExclusions: " + blockBuildExclusions
                + "\n  blockInteractExclusions: " + blockInteractExclusions
                + "\n  entityCreateExclusions: " + entityCreateExclusions
                + "\n  entityInteractExclusions: " + entityInteractExclusions
                + "\n  commandRestrictions: " + commandRestrictions + "\n}";
    }

    public String getDisplay() {
        final StringBuilder sb = new StringBuilder();

        sb.append("Flags: ");
        sb.append(flags);
        sb.append("\n");

        final Collection<String> placeExclusions = new HashSet<String>();
        for (final BlockType blockType : blockBuildExclusions) {
            placeExclusions.add(blockType.getMachineName());
        }
        for (final Class<? extends Entity> entityClass : entityCreateExclusions) {
            placeExclusions.add(MinecraftMapper.getEntity(entityClass));
        }
        sb.append("Place exceptions: ");
        sb.append(placeExclusions);
        sb.append("\n");

        final Collection<String> interactExclusions = new HashSet<String>();
        for (final BlockType blockType : blockInteractExclusions) {
            interactExclusions.add(blockType.getMachineName());
        }
        for (final Class<? extends Entity> entityClass : entityInteractExclusions) {
            interactExclusions.add(MinecraftMapper.getEntity(entityClass));
        }
        sb.append("Interact exceptions: ");
        sb.append(interactExclusions);
        sb.append("\n");

        sb.append("Command restrictions: ");
        sb.append(commandRestrictions);

        return sb.toString();
    }
}
