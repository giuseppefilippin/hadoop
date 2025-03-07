package pr.puc.mapreduce.advanced.value;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class TeamStatsWritable implements Writable {

  private Integer games;
  private Integer victoriesBalance;
  private Double winRate;

  public TeamStatsWritable() {
  }

  public TeamStatsWritable(Integer games, Integer victoriesBalance, Double winRate) {
    this.games = games;
    this.victoriesBalance = victoriesBalance;
    this.winRate = winRate;
  }

  public Integer getGames() {
    return this.games;
  }

  public Integer getVictoriesBalance() {
    return this.victoriesBalance;
  }

  public Double getWinRate() {
    return this.winRate;
  }

  @Override
  public String toString() {
    return "Total Games: " + this.games + "; Victories balance: " + this.victoriesBalance + "; Win rate: "
        + this.winRate + "%";
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    games = in.readInt();
    victoriesBalance = in.readInt();
    winRate = in.readDouble();
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeInt(games);
    out.writeInt(victoriesBalance);
    out.writeDouble(winRate);
  }

}
