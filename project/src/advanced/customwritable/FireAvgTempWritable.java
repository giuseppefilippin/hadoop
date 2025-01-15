package advanced.customwritable;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.checkerframework.checker.units.qual.Temperature;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class FireAvgTempWritable implements Writable{
    private float temp;
    private int n;

    public FireAvgTempWritable() {}

    public FireAvgTempWritable(float temp, int n) {
        this.temp = temp;
        this.n = n;
    }

    public float getTemp() { return temp; }
    public void setTemp(float temp) { this.temp = temp; }

    public int getN() { return n; }
    public void setN(int n) { this.n = n; }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeFloat(temp);
        dataOutput.writeInt(n);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.temp = dataInput.readFloat();
        this.n = dataInput.readInt();
    }
}
