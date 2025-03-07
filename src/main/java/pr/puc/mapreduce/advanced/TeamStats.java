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
import pr.puc.mapreduce.advanced.value.TeamStatsIntermediateWritable;
import pr.puc.mapreduce.advanced.value.TeamStatsWritable;

//The goal of this job is to determine the goal balance and win rate of each team
public class TeamStats extends Configured implements Tool {

  public static void main(String[] args) throws Exception {

    // Executing the jobs
    Integer result = ToolRunner.run(new Configuration(), new TeamStats(), args);
    System.exit(result);
  }

  @Override
  public int run(String[] arg0) throws Exception {

    // Setting the input/output paths
    Path input = new Path("dataset-brasileirao.csv");
    Path output = new Path("output");

    // Instantiatign job and config
    Configuration cfg = this.getConf();
    Job job = Job.getInstance(cfg);

    // Deleting input folder if it exists
    FileSystem fs = FileSystem.get(cfg);
    fs.delete(output, true);

    // Setting input/output file format
    FileInputFormat.setInputPaths(job, input);
    FileOutputFormat.setOutputPath(job, output);

    // Setting input/output file type
    job.setInputFormatClass(TextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);

    // Setting job classes
    job.setJarByClass(TeamStats.class);
    job.setMapperClass(TeamStatsIntermediateMapper.class);
    job.setReducerClass(TeamStatsIntermediateReducer.class);
    job.setCombinerClass(TeamStatsIntermediateCombiner.class);

    // Setting map output types
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(TeamStatsIntermediateWritable.class);

    // Setting reduce output types
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    // Setting number of reducers
    job.setNumReduceTasks(1);

    if (job.waitForCompletion(true)) {

      // Setting the input/output paths
      Path input2 = new Path("output/part-r-00000");
      Path output2 = new Path("result");

      // Instantiatign job and deleting result folder
      Job job2 = Job.getInstance(cfg);
      fs.delete(output2, true);

      // Setting input/output file format
      FileInputFormat.setInputPaths(job2, input2);
      FileOutputFormat.setOutputPath(job2, output2);

      // Setting input/output file type
      job2.setInputFormatClass(TextInputFormat.class);
      job2.setOutputFormatClass(TextOutputFormat.class);

      // Setting job classes
      job2.setJarByClass(TeamStats.class);
      job2.setMapperClass(TeamStatsMapper.class);
      job2.setReducerClass(TeamStatsReducer.class);
      job2.setCombinerClass(TeamStatsCombiner.class);

      // Setting map output types
      job2.setMapOutputKeyClass(Text.class);
      job2.setMapOutputValueClass(TeamStatsWritable.class);

      // Setting reduce output types
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

class TeamStatsMapper extends Mapper<LongWritable, Text, Text, TeamStatsWritable> {
  protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    String[] columns = value.toString().split(" ");

    try {
      String team = columns[0];

      Integer games = Integer.parseInt(columns[1]);
      Integer victories = Integer.parseInt(columns[2]);
      Integer losses = Integer.parseInt(columns[3]);

      Integer victoriesBalance = victories - losses;
      Double winRate = ((double) victories / (double) games) * 100;

      context.write(new Text(team), new TeamStatsWritable(games, victoriesBalance, winRate));
    } catch (Exception e) {
      return;
    }

  }
}

class TeamStatsCombiner extends Reducer<Text, TeamStatsWritable, Text, TeamStatsWritable> {
  protected void reduce(Text key, Iterable<TeamStatsWritable> values, Context context)
      throws InterruptedException, IOException {

    for (TeamStatsWritable value : values) {
      context.write(key, value);
    }

  }
}

class TeamStatsReducer extends Reducer<Text, TeamStatsWritable, Text, Text> {
  protected void reduce(Text key, Iterable<TeamStatsWritable> values, Context context)
      throws InterruptedException, IOException {

    for (TeamStatsWritable value : values) {
      context.write(key, new Text(value.toString()));
    }

  }
}

class TeamStatsIntermediateMapper extends Mapper<LongWritable, Text, Text, TeamStatsIntermediateWritable> {
  protected void map(LongWritable key, Text value, Context context) throws InterruptedException, IOException {
    String[] columns = value.toString().split(",");

    String HomeTeam = columns[4];
    String VisitorTeam = columns[5];

    Integer homeGoals = Integer.parseInt(columns[12]);
    Integer visitorGoals = Integer.parseInt(columns[13]);

    Integer homeWin = homeGoals > visitorGoals ? 1 : 0;
    Integer homeLose = homeGoals < visitorGoals ? 1 : 0;
    Integer VisitorWin = visitorGoals > homeGoals ? 1 : 0;
    Integer VisitorLose = visitorGoals < homeGoals ? 1 : 0;
    Integer draw = homeGoals == visitorGoals ? 1 : 0;

    context.write(new Text(HomeTeam), new TeamStatsIntermediateWritable(homeGoals, 1, homeWin, homeLose, draw));
    context.write(new Text(VisitorTeam),
        new TeamStatsIntermediateWritable(visitorGoals, 1, VisitorWin, VisitorLose, draw));
  }
}

class TeamStatsIntermediateCombiner
    extends Reducer<Text, TeamStatsIntermediateWritable, Text, TeamStatsIntermediateWritable> {
  protected void reduce(Text key, Iterable<TeamStatsIntermediateWritable> values, Context context)
      throws InterruptedException, IOException {

    Integer goals = 0;
    Integer games = 0;
    Integer wins = 0;
    Integer loses = 0;
    Integer draws = 0;

    for (TeamStatsIntermediateWritable value : values) {
      goals += value.getGoals();
      games += value.getGames();
      wins += value.getVictories();
      loses += value.getLoses();
      draws += value.getDraws();
    }

    TeamStatsIntermediateWritable result = new TeamStatsIntermediateWritable(goals, games, wins, loses, draws);

    context.write(key, result);

  }
}

class TeamStatsIntermediateReducer extends Reducer<Text, TeamStatsIntermediateWritable, Text, Text> {
  protected void reduce(Text key, Iterable<TeamStatsIntermediateWritable> values, Context context)
      throws InterruptedException, IOException {

    Integer goals = 0;
    Integer games = 0;
    Integer wins = 0;
    Integer loses = 0;
    Integer draws = 0;

    for (TeamStatsIntermediateWritable value : values) {
      goals += value.getGoals();
      games += value.getGames();
      wins += value.getVictories();
      loses += value.getLoses();
      draws += value.getDraws();
    }

    TeamStatsIntermediateWritable result = new TeamStatsIntermediateWritable(goals, games, wins, loses, draws);

    context.write(key, new Text(result.toString()));

  }
}
