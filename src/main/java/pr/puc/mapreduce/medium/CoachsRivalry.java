package pr.puc.mapreduce.medium;

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
import pr.puc.mapreduce.medium.key.CoachsWritable;
import pr.puc.mapreduce.medium.value.CoachWinsWritable;

// The goal of this job is to determine the amout of times that a pair of coachs faced each other and who won more
public class CoachsRivalry extends Configured implements Tool {

  public static void main(String[] args) throws Exception {
    // Executing the job
    int result = ToolRunner.run(new Configuration(), new CoachsRivalry(), args);

    System.exit(result);
  }

  @Override
  public int run(String[] arg0) throws Exception {

    // Setting input/output paths
    Path input = new Path("dataset-brasileirao.csv");
    Path output = new Path("output/");

    // Instantiating cfg and job
    Configuration cfg = this.getConf();
    Job job = Job.getInstance(cfg);

    // Deleting the output folder if it exists
    FileSystem fs = FileSystem.get(cfg);
    fs.delete(output, true);

    // Setting the input/output file formats
    FileInputFormat.setInputPaths(job, input);
    FileOutputFormat.setOutputPath(job, output);

    // Setting the input/output file types
    job.setInputFormatClass(TextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);

    // Setting the job classes
    job.setJarByClass(CoachsRivalry.class);
    job.setMapperClass(CoachsRivalryMapper.class);
    job.setReducerClass(CoachsRivalryReducer.class);
    job.setCombinerClass(CoachsRivalrtCombiner.class);

    // Setting the map output types
    job.setMapOutputKeyClass(CoachsWritable.class);
    job.setMapOutputValueClass(CoachWinsWritable.class);

    // Setting the reduce output types
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    // Setting the number of reducers
    job.setNumReduceTasks(1);

    if (job.waitForCompletion(true)) {
      return 0;
    } else {
      return 1;
    }

  }

}

class CoachsRivalryMapper extends Mapper<LongWritable, Text, CoachsWritable, CoachWinsWritable> {
  protected void map(LongWritable key, Text value, Context context) throws InterruptedException, IOException {

    String line = value.toString();
    String[] columns = line.split(",");

    String homeCoach = columns[8];
    String visitorCoach = columns[9];

    String homeTeam = columns[4];
    String winner = columns[10];

    if (!homeCoach.equals(" ") && !visitorCoach.equals(" ")) {
      if (winner.equals(homeTeam)) {
        context.write(new CoachsWritable(homeCoach, visitorCoach), new CoachWinsWritable(1, 0));
      } else {
        context.write(new CoachsWritable(homeCoach, visitorCoach), new CoachWinsWritable(0, 1));
      }
    }

  }
}

class CoachsRivalrtCombiner extends Reducer<CoachsWritable, CoachWinsWritable, CoachsWritable, CoachWinsWritable> {
  protected void reduce(CoachsWritable key, Iterable<CoachWinsWritable> values, Context context)
      throws InterruptedException, IOException {

    Integer home = 0;
    Integer visitor = 0;

    for (CoachWinsWritable value : values) {
      home += value.getHomeCoachWin();
      visitor += value.getVisitorCoachWin();
    }

    context.write(key, new CoachWinsWritable(home, visitor));

  }
}

class CoachsRivalryReducer extends Reducer<CoachsWritable, CoachWinsWritable, Text, Text> {
  protected void reduce(CoachsWritable key, Iterable<CoachWinsWritable> values, Context context)
      throws IOException, InterruptedException {

    Integer home = 0;
    Integer visitor = 0;

    for (CoachWinsWritable value : values) {
      home += value.getHomeCoachWin();
      visitor += value.getVisitorCoachWin();
    }

    context.write(new Text(key.toString()), new Text(home + " " + visitor));

  }
}
