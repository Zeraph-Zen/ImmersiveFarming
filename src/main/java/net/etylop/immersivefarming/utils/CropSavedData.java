package net.etylop.immersivefarming.utils;

import com.google.gson.Gson;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;

public class CropSavedData extends SavedData {
    public static final String DATA_IDENTIFIER = "crop_tracker";
    private static final Gson helper = new Gson();
    private final HashMap<String, String> cropTracker = new HashMap<>();

    private String getCropHash(Level level, BlockPos pos) {
        return level.dimension().toString() + pos.toString();
    }

    public boolean testCrop(Level level, BlockPos pos) {
        String cropName = cropTracker.get(getCropHash(level, pos));
        if (cropName==null)
            return false;
        return !cropName.equals(level.getBlockState(pos).getBlock().toString());
    }

    public void insertCrop(Level level, BlockPos pos) {
        cropTracker.put(getCropHash(level, pos), level.getBlockState(pos).getBlock().toString());
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putString(DATA_IDENTIFIER, helper.toJson(cropTracker));
        return compoundTag;
    }

    public static CropSavedData load(CompoundTag nbt) {
        CropSavedData data = new CropSavedData();
        //data.seasonCycleTicks = Mth.clamp(nbt.getInt("SeasonCycleTicks"), 0, SeasonTime.ZERO.getCycleDuration());
        return data;
    }

    public static CropSavedData create() {
        return new CropSavedData();
    }

}
