package pr.puc.mapreduce.medium.value;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class GoalsWritable implements Writable {

  private Integer homeGoals;
  private Integer visitorGoals;
  private Integer partial;

  public GoalsWritable() {
  }

  public GoalsWritable(Integer home, Integer visitor, Integer n) {
    this.homeGoals = home;
    this.visitorGoals = visitor;
    this.partial = n;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    this.homeGoals = in.readInt();
    this.visitorGoals = in.readInt();
    this.partial = in.readInt();
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeInt(this.homeGoals);
    out.writeInt(this.visitorGoals);
    out.writeInt(this.partial);
  }

  public Integer getTotal() {
    return this.homeGoals + this.visitorGoals;
  }

  public Integer getHomeGoal() {
    return this.homeGoals;
  }

  public Integer getVisitorGoals() {
    return this.visitorGoals;
  }

  public Integer getPartial() {
    return this.partial;
  }

}
