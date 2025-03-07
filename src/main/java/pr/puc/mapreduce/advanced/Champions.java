package pr.puc.mapreduce.advanced;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import pr.puc.mapreduce.advanced.key.YearPointsWritable;
import pr.puc.mapreduce.advanced.key.YearTeamWritable;
import pr.puc.mapreduce.advanced.value.TeamGamesWritable;

// The goal of this job is to determine the champion of each year
public class Champions extends Configured implements Tool {

  public static void main(String[] args) throws Exception {

    // Executing the jobs
    Integer result = ToolRunner.run(new Configuration(), new Champions(), args);
    System.exit(result);
  }

  @Override
  public int run(String[] arg0) throws Exception {

    // Setting the input/output paths
    Path input = new Path("dataset-brasileirao.csv");
    Path output = new Path("output");

    // Instantiating cfg and job
    Configuration cfg = this.getConf();
    Job job = Job.getInstance(cfg);

    // Deleting output folder if it exists
    FileSystem fs = FileSystem.get(cfg);
    fs.delete(output, true);

    // Setting input/output file formats
    FileInputFormat.setInputPaths(job, input);
    FileOutputFormat.setOutputPath(job, output);

    // Setting input/output file types
    job.setInputFormatClass(TextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);

    // Setting job classes
    job.setJarByClass(Champions.class);
    job.setMapperClass(ChampionsIntermediateMapper.class);
    job.setReducerClass(ChampionsIntermediateReducer.class);
    job.setCombinerClass(ChampionsIntermediateCombiner.class);

    // Setting map output types
    job.setMapOutputKeyClass(YearTeamWritable.class);
    job.setMapOutputValueClass(TeamGamesWritable.class);

    // Setting reduce output types
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    // Setting number of reducers
    job.setNumReduceTasks(1);

    if (job.waitForCompletion(true)) {

      Path input2 = new Path("output/part-r-00000");
      Path output2 = new Path("result");

      Job job2 = Job.getInstance(cfg);

      // Set input and output paths for the second job
      FileInputFormat.setInputPaths(job2, input2);
      FileOutputFormat.setOutputPath(job2, output2);

      // Delete result directory if it exists
      fs.delete(output2, true);

      // Setting input/output file formats
      job2.setInputFormatClass(TextInputFormat.class);
      job2.setOutputFormatClass(TextOutputFormat.class);

      // Setting job classes
      job2.setJarByClass(Champions.class);
      job2.setMapperClass(ChampionsMapper.class);
      job2.setReducerClass(ChampionsReducer.class);
      job2.setCombinerClass(ChampionsCombiner.class);

      // Set the map output key and value classes
      job2.setMapOutputKeyClass(YearPointsWritable.class);
      job2.setMapOutputValueClass(Text.class);

      // Set the reduce output key and value classe
      job2.setOutputKeyClass(Text.class);
      job2.setOutputValueClass(Text.class);

      // Setting number of reducers
      job2.setNumReduceTasks(1);

      return job2.waitForCompletion(true) ? 0 : 1;

    } else {
      return 1;
    }

  }

}

class ChampionsMapper extends Mapper<LongWritable, Text, YearPointsWritable, Text> {
  protected void map(LongWritable key, Text value, Context context) throws InterruptedException, IOException {
    try {
      String[] columns = value.toString().split(" ");

      String team = columns[0].split(":")[0];
      String year = columns[0].split(":")[1];

      Integer victories = Integer.parseInt(columns[1]);
      Integer draws = Integer.parseInt(columns[3]);
      Integer points = (victories * 3) + draws;

      context.write(new YearPointsWritable(year, points), new Text(team));
    } catch (Exception e) {
      return;
    }
  }

}

class ChampionsReducer extends Reducer<YearPointsWritable, Text, Text, Text> {
  protected void reduce(YearPointsWritable key, Iterable<Text> values, Context context)
      throws InterruptedException, IOException {

    for (Text value : values) {
      context.write(new Text(key.toString()), value);
    }

  }
}

class ChampionsCombiner extends Reducer<YearPointsWritable, Text, YearPointsWritable, Text> {
  protected void reduce(YearPointsWritable key, Iterable<Text> values, Context context)
      throws InterruptedException, IOException {

    for (Text value : values) {
      context.write(key, value);
    }

  }
}

class ChampionsIntermediateMapper extends Mapper<LongWritable, Text, YearTeamWritable, TeamGamesWritable> {
  protected void map(LongWritable key, Text value, Context context) throws InterruptedException, IOException {

    String[] columns = value.toString().split(",");

    String homeTeam = columns[4];
    String visitorTeam = columns[5];

    String date = columns[2];
    String year = date.split("/")[2];

    Integer homeGoals = Integer.parseInt(columns[12]);
    Integer visitorGoals = Integer.parseInt(columns[13]);

    Integer homeWin = homeGoals > visitorGoals ? 1 : 0;
    Integer homeLose = homeGoals < visitorGoals ? 1 : 0;

    Integer draw = homeGoals == visitorGoals ? 1 : 0;

    Integer visitorWin = visitorGoals > homeGoals ? 1 : 0;
    Integer visitorLose = visitorGoals < homeGoals ? 1 : 0;

    context.write(new YearTeamWritable(year, homeTeam), new TeamGamesWritable(1, homeWin, homeLose, draw));
    context.write(new YearTeamWritable(year, visitorTeam), new TeamGamesWritable(1, visitorWin, visitorLose, draw));

  }
}

class ChampionsIntermediateCombiner
    extends Reducer<YearTeamWritable, TeamGamesWritable, YearTeamWritable, TeamGamesWritable> {
  protected void reduce(YearTeamWritable key, Iterable<TeamGamesWritable> values, Context context)
      throws InterruptedException, IOException {

    Integer games = 0;
    Integer wins = 0;
    Integer losses = 0;
    Integer draws = 0;

    for (TeamGamesWritable value : values) {
      games += value.getGames();
      wins += value.getWins();
      losses += value.getLosses();
      draws += value.getDraws();
    }

    TeamGamesWritable result = new TeamGamesWritable(games, wins, losses, draws);

    context.write(key, result);

  }
}

class ChampionsIntermediateReducer extends Reducer<YearTeamWritable, TeamGamesWritable, Text, Text> {
  protected void reduce(YearTeamWritable key, Iterable<TeamGamesWritable> values, Context context)
      throws InterruptedException, IOException {

    Integer games = 0;
    Integer wins = 0;
    Integer losses = 0;
    Integer draws = 0;

    for (TeamGamesWritable value : values) {
      games += value.getGames();
      wins += value.getWins();
      losses += value.getLosses();
      draws += value.getDraws();
    }

    TeamGamesWritable result = new TeamGamesWritable(games, wins, losses, draws);

    context.write(new Text(key.toString()), new Text(result.toString()));

  }
}
