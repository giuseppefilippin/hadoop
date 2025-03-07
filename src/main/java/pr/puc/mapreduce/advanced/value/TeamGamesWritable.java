package pr.puc.mapreduce.advanced.value;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class TeamGamesWritable implements Writable {

  private Integer games;
  private Integer wins;
  private Integer losses;
  private Integer draws;

  public TeamGamesWritable() {
  }

  public TeamGamesWritable(Integer games, Integer wins, Integer losses, Integer draws) {
    this.games = games;
    this.wins = wins;
    this.losses = losses;
    this.draws = draws;
  }

  public Integer getGames() {
    return this.games;
  }

  public Integer getWins() {
    return this.wins;
  }

  public Integer getLosses() {
    return this.losses;
  }

  public Integer getDraws() {
    return this.draws;
  }

  @Override
  public String toString() {
    return games + " " + wins + " " + losses + " " + draws;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    games = in.readInt();
    wins = in.readInt();
    losses = in.readInt();
    draws = in.readInt();
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeInt(games);
    out.writeInt(wins);
    out.writeInt(losses);
    out.writeInt(draws);
  }
}
