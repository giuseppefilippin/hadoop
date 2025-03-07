package pr.puc.mapreduce.medium.value;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class CoachWinsWritable implements Writable {

  private Integer homeCoachWin;
  private Integer visitoCoachWin;

  public CoachWinsWritable() {
  }

  public CoachWinsWritable(Integer h, Integer v) {
    homeCoachWin = h;
    visitoCoachWin = v;
  }

  public Integer getHomeCoachWin() {
    return homeCoachWin;
  }

  public Integer getVisitorCoachWin() {
    return visitoCoachWin;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    homeCoachWin = in.readInt();
    visitoCoachWin = in.readInt();
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeInt(homeCoachWin);
    out.writeInt(visitoCoachWin);
  }

}
