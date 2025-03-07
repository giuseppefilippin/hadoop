package pr.puc.mapreduce.medium.key;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class CoachsWritable implements WritableComparable<CoachsWritable> {

  private String homeCoach;
  private String visitorCoach;

  public CoachsWritable() {
  }

  public CoachsWritable(String h, String v) {
    homeCoach = h;
    visitorCoach = v;
  }

  @Override
  public String toString() {
    return homeCoach + " x " + visitorCoach;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    this.homeCoach = in.readUTF();
    this.visitorCoach = in.readUTF();
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeUTF(this.homeCoach);
    out.writeUTF(this.visitorCoach);
  }

  @Override
  public int compareTo(CoachsWritable o) {
    int homeCoachComp = this.homeCoach.compareTo(o.homeCoach);
    if (homeCoachComp != 0) {
      return homeCoachComp;
    }

    return this.visitorCoach.compareTo(o.visitorCoach);
  }
}
