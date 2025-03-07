package pr.puc.mapreduce.medium.value;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class DerbyStatsWritable implements Writable {

  private Integer goals;
  private Integer games;

  public DerbyStatsWritable() {
  }

  public DerbyStatsWritable(Integer goals, Integer games) {
    this.goals = goals;
    this.games = games;
  }

  public Integer getGoals() {
    return this.goals;
  }

  public Integer getGames() {
    return this.games;
  }

  @Override
  public String toString() {
    return "Total goals: " + goals + "; Total games: " + games + "; Average goals per game: "
        + (float) goals / (float) games;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    goals = in.readInt();
    games = in.readInt();
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeInt(goals);
    out.writeInt(games);
  }

}
