package pr.puc.mapreduce.medium;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
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
import pr.puc.mapreduce.medium.value.GoalsWritable;

// The goal of this job is to determine the average goals that is made in each arena
public class AverageGoalsPerArena extends Configured implements Tool {
  public static void main(String[] args) throws Exception {

    // Executing the job
    int result = ToolRunner.run(new Configuration(), new AverageGoalsPerArena(), args);
    System.exit(result);
  }

  @Override
  public int run(String[] arg0) throws Exception {

    // Setting up input/output paths
    Path input = new Path("dataset-brasileirao.csv");
    Path output = new Path("output/");

    // Instantiating cfg and job
    Configuration cfg = this.getConf();
    Job job = Job.getInstance(cfg);

    // Deleting the output folder if it exists
    FileSystem fs = FileSystem.get(cfg);
    fs.delete(output, true);

    // Setting up input/output file formats
    FileInputFormat.setInputPaths(job, input);
    FileOutputFormat.setOutputPath(job, output);

    // Setting up input/output file types
    job.setInputFormatClass(TextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);

    // Setting job classes
    job.setJarByClass(AverageGoalsPerArena.class);
    job.setMapperClass(AverageGoalsPerArenaMapper.class);
    job.setReducerClass(AverageGoalsPerArenaReducer.class);
    job.setCombinerClass(AverageGoalsPerArenaCombiner.class);

    // Setting map output types
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(GoalsWritable.class);

    // Setting reduce output types
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    // Setting number of reducers
    job.setNumReduceTasks(1);

    if (job.waitForCompletion(true)) {
      return 0;
    } else {
      return 1;
    }

  }

}

class AverageGoalsPerArenaMapper extends Mapper<LongWritable, Text, Text, GoalsWritable> {
  protected void map(LongWritable key, Text value, Context context) throws InterruptedException, IOException {

    String[] line = value.toString().split(",");

    String stadium = line[11];
    Integer homeGoals = Integer.parseInt(line[12]);
    Integer visitorGoals = Integer.parseInt(line[13]);

    context.write(new Text(stadium), new GoalsWritable(homeGoals, visitorGoals, 1));

  }
}

class AverageGoalsPerArenaCombiner extends Reducer<Text, GoalsWritable, Text, GoalsWritable> {
  protected void reduce(Text key, Iterable<GoalsWritable> values, Context context)
      throws IOException, InterruptedException {

    Integer home = 0;
    Integer visitor = 0;
    Integer partial = 0;

    for (GoalsWritable value : values) {
      home += value.getHomeGoal();
      visitor += value.getVisitorGoals();
      partial += value.getPartial();
    }

    context.write(key, new GoalsWritable(home, visitor, partial));
  }
}

class AverageGoalsPerArenaReducer extends Reducer<Text, GoalsWritable, Text, FloatWritable> {
  protected void reduce(Text key, Iterable<GoalsWritable> values, Context context)
      throws InterruptedException, IOException {

    Float occurances = 0f;
    Float total = 0f;

    for (GoalsWritable value : values) {
      total += value.getTotal();
      occurances += value.getPartial();
    }

    context.write(key, new FloatWritable(total / occurances));
  }
}
