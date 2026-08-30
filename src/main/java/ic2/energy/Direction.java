

package ic2.energy;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.World;

public enum Direction {
    XP(0),
    XN(1),
    YP(2),
    YN(3),
    ZP(4),
    ZN(5);

    private final int dir;
    private static final Direction[] directions;

    private Direction(int dir) {
        this.dir = dir;
    }

    public int toSideValue() {
        return this.dir;
    }

    public Direction getInverse() {
        return switch (this.dir) {
            case 0 -> XN;
            case 1 -> XP;
            case 2 -> YN;
            case 3 -> YP;
            case 4 -> ZN;
            case 5 -> ZP;
            default -> this;
        };
    }

    public int getXOffset() {
        return switch (this.dir) {
            case 0 -> 1;
            case 1 -> -1;
            default -> 0;
        };
    }

    public int getYOffset() {
        return switch (this.dir) {
            case 2 -> 1;
            case 3 -> -1;
            default -> 0;
        };
    }

    public int getZOffset() {
        return switch (this.dir) {
            case 4 -> 1;
            case 5 -> -1;
            default -> 0;
        };
    }

    public static Direction fromSideValue(int side) {
        return side >= 0 && side < directions.length ? directions[side] : XP;
    }

    public static Direction fromBta(net.minecraft.core.util.helper.Direction d) {
        if (d == null) {
            return XP;
        }
        return switch (d) {
            case EAST -> XP;
            case WEST -> XN;
            case UP -> YP;
            case DOWN -> YN;
            case SOUTH -> ZP;
            case NORTH -> ZN;
            default -> XP;
        };
    }

    public net.minecraft.core.util.helper.Direction toBta() {
        return switch (this.dir) {
            case 0 -> net.minecraft.core.util.helper.Direction.EAST;
            case 1 -> net.minecraft.core.util.helper.Direction.WEST;
            case 2 -> net.minecraft.core.util.helper.Direction.UP;
            case 3 -> net.minecraft.core.util.helper.Direction.DOWN;
            case 4 -> net.minecraft.core.util.helper.Direction.SOUTH;
            case 5 -> net.minecraft.core.util.helper.Direction.NORTH;
            default -> net.minecraft.core.util.helper.Direction.EAST;
        };
    }

    public TileEntity applyToTileEntity(World world, TileEntity tileEntity) {
        int x = tileEntity.tilePos.x() + this.getXOffset();
        int y = tileEntity.tilePos.y() + this.getYOffset();
        int z = tileEntity.tilePos.z() + this.getZOffset();
        return world.getTileEntity(x, y, z);
    }

    static {
        directions = Direction.values();
    }
}

