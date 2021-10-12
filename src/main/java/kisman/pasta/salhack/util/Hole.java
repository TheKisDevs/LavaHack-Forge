package kisman.pasta.salhack.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class Hole extends Vec3i {
    private BlockPos blockPos;
    private boolean tall;
    private HoleTypes HoleType;

    public enum HoleTypes {
        None,
        Normal,
        Obsidian,
        Bedrock,
    }

    public Hole(int x, int y, int z, final BlockPos pos, HoleTypes type) {
        super(x, y, z);
        blockPos = pos;
        setHoleType(type);
    }

    public Hole(int x, int y, int z, final BlockPos pos, HoleTypes type, boolean tall) {
        super(x, y, z);
        blockPos = pos;
        this.tall = true;
        setHoleType(type);
    }

    public boolean isTall() {
        return tall;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    /**
     * @return the holeType
     */
    public HoleTypes getHoleType() {
        return HoleType;
    }

    /**
     * @param holeType the holeType to set
     */
    public void setHoleType(HoleTypes holeType) {
        HoleType = holeType;
    }
}
