package pr.puc.mapreduce.advanced.key;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class YearPointsWritable implements WritableComparable<YearPointsWritable> {

  private String year;
  private Integer points;

  public YearPointsWritable() {
  }

  public YearPointsWritable(String year, Integer points) {
    this.year = year;
    this.points = points;
  }

  @Override
  public String toString() {
    return year + "-" + points;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    this.year = in.readUTF();
    this.points = in.readInt();
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeUTF(this.year);
    out.writeInt(this.points);
  }

  @Override
  public int compareTo(YearPointsWritable o) {
    int yearComparison = this.year.compareTo(o.year);
    if (yearComparison != 0) {
      return yearComparison;
    }

    return o.points.compareTo(this.points);
  }

}
