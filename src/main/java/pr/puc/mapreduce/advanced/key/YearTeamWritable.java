package pr.puc.mapreduce.advanced.key;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class YearTeamWritable implements WritableComparable<YearTeamWritable> {

  private String year;
  private String team;

  public YearTeamWritable() {
  }

  public YearTeamWritable(String year, String team) {
    this.year = year;
    this.team = team;
  }

  public String getYear() {
    return this.year;
  }

  public String getTeam() {
    return this.team;
  }

  @Override
  public String toString() {
    return team + ":" + year;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    this.year = in.readUTF();
    this.team = in.readUTF();
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeUTF(this.year);
    out.writeUTF(this.team);
  }

  @Override
  public int compareTo(YearTeamWritable o) {
    int yearComparison = this.year.compareTo(o.year);
    if (yearComparison != 0) {
      return yearComparison;
    }
    return this.team.compareTo(o.team);
  }

}
