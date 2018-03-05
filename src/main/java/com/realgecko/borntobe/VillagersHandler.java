package com.realgecko.borntobe;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class VillagersHandler {

    final Random random = new Random();
    private List<VillagerProfession> professions;
    private boolean noCareers = true;

    public VillagersHandler() {
        professions = new ArrayList<VillagerProfession>();
        for (Map.Entry<ResourceLocation, VillagerProfession> profession : ForgeRegistries.VILLAGER_PROFESSIONS.getEntries())
            if (!profession.getKey().toString().contains("minecraft:"))
                professions.add(profession.getValue());

        if (professions.size() > 0)
            noCareers = false;
    }

    @SubscribeEvent
    public void handleEntityJoinWorld(EntityJoinWorldEvent e) {
        if (noCareers || e.getWorld().isRemote) return;
        if (!(e.getEntity() instanceof EntityVillager)) return;
        if (e.getEntity().hasCustomName()) return;

        EntityVillager villager = (EntityVillager) e.getEntity();
        NBTTagCompound compound = new NBTTagCompound();
        villager.writeEntityToNBT(compound);
        int careerId = compound.getInteger("Career") - 1;

        if ((villager.getProfessionForge().getRegistryName().toString().equals("minecraft:farmer")))
            if (random.nextInt(100) <= Config.instance.chance) {
                VillagerProfession profession = professions.get(random.nextInt(professions.size() - 1));

                villager.setProfession(profession);
                villager.getProfessionForge().getRandomCareer(random);

                // Fucking mess!!!
                villager.writeEntityToNBT(compound);
                careerId = compound.getInteger("Career") - 1;
                compound.setTag("Offers", new NBTTagCompound());
                villager.readEntityFromNBT(compound);

                List<EntityVillager.ITradeList> trades = profession.getCareer(careerId).getTrades(0);

                for (EntityVillager.ITradeList trade : trades) {
                    trade.addMerchantRecipe(villager, villager.getRecipes(null), random);
                }
            }

        // As this method is called whenever entity is loaded we'll give villager a custom name
        // Otherwise no farmers will exist in the world :D
        VillagerCareer career = villager.getProfessionForge().getCareer(careerId);
        TextComponentTranslation name = new TextComponentTranslation("entity.Villager." + career.getName());
        villager.setCustomNameTag(name.getUnformattedComponentText());
    }
}
