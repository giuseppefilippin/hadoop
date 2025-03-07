package pr.puc.mapreduce.medium.key;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class TeamsWritable implements WritableComparable<TeamsWritable> {

  private String homeTeam;
  private String visitorTeam;

  public TeamsWritable() {
  }

  public TeamsWritable(String h, String v) {
    homeTeam = h;
    visitorTeam = v;
  }

  @Override
  public String toString() {
    return homeTeam + " x " + visitorTeam;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    homeTeam = in.readUTF();
    visitorTeam = in.readUTF();
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeUTF(homeTeam);
    out.writeUTF(visitorTeam);
  }

  @Override
  public int compareTo(TeamsWritable o) {
    int homeTeamComp = homeTeam.compareTo(o.homeTeam);
    if (homeTeamComp != 0) {
      return homeTeamComp;
    }

    return visitorTeam.compareTo(o.visitorTeam);
  }

}
