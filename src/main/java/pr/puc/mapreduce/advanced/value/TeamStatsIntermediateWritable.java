package pr.puc.mapreduce.advanced.value;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class TeamStatsIntermediateWritable implements Writable {

  private Integer goals;
  private Integer games;
  private Integer victories;
  private Integer loses;
  private Integer draws;

  public TeamStatsIntermediateWritable() {

  }

  public TeamStatsIntermediateWritable(Integer goals, Integer games, Integer victories, Integer loses, Integer draws) {
    this.goals = goals;
    this.games = games;
    this.victories = victories;
    this.loses = loses;
    this.draws = draws;
  }

  public Integer getGoals() {
    return this.goals;
  }

  public Integer getGames() {
    return this.games;
  }

  public Integer getVictories() {
    return this.victories;
  }

  public Integer getLoses() {
    return this.loses;
  }

  public Integer getDraws() {
    return this.draws;
  }

  @Override
  public String toString() {
    return goals + " " + games + " " + victories + " " + loses + " " + draws;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    goals = in.readInt();
    games = in.readInt();
    victories = in.readInt();
    loses = in.readInt();
    draws = in.readInt();
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeInt(goals);
    out.writeInt(games);
    out.writeInt(victories);
    out.writeInt(loses);
    out.writeInt(draws);
  }

}
