package top.seraphjack.simplelogin.server.storage;

import net.minecraft.nbt.NBTTagCompound;

public class Position {
    private final double x, y, z;

    public Position(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position cast = (Position) o;
            return x == cast.getX() && y == cast.getY() && z == cast.getZ();
        }
        return false;
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setDouble("x", x);
        tag.setDouble("y", y);
        tag.setDouble("z", z);
        return tag;
    }

    public static Position fromNBT(NBTTagCompound nbt) {
        return new Position(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
    }
}
