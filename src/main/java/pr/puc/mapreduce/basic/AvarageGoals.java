package pr.puc.mapreduce.basic;

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

// The goal of this job is to determine the avarage goals per match of the championship
public class AvarageGoals extends Configured implements Tool {
//drive
  public static void main(String[] args) throws Exception {
    // Executes the Job
    int res = ToolRunner.run(new Configuration(), new AvarageGoals(), args);
    System.exit(res);

  }

  @Override
  public int run(String[] arg0) throws Exception {

    // Input and output paths
    Path input = new Path("dataset-brasileirao.csv");
    Path output = new Path("output/");

    // Instantiating cfg and job
    Configuration cfg = this.getConf();
    Job job = Job.getInstance(cfg);

    // Deleting the output folder if it exists
    FileSystem fs = FileSystem.get(cfg);
    fs.delete(output, true);

    // Setting the input and output file format
    FileInputFormat.setInputPaths(job, input);
    FileOutputFormat.setOutputPath(job, output);

    // Setting the input/output type
    job.setInputFormatClass(TextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);

    // Setting The classes
    job.setJarByClass(AvarageGoals.class);
    job.setMapperClass(AvarageGoalsMapper.class);
    job.setReducerClass(AvarageGoalsReducer.class);

    // Setting the map output types
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(IntWritable.class);

    // Setting the reduce output types
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(FloatWritable.class);

    // Setting the number or reducers
    job.setNumReduceTasks(1);

    if (job.waitForCompletion(true)) {
      return 0;
    } else {
      return 1;
    }

  }

}

class AvarageGoalsMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

  protected void map(LongWritable key, Text value, Context context) throws InterruptedException, IOException {

    String line = value.toString();

    Integer homeTeamGoals = Integer.valueOf(line.split(",")[12]);
    Integer visitorTeamGoals = Integer.valueOf(line.split(",")[13]);

    context.write(new Text("Goals"), new IntWritable(homeTeamGoals + visitorTeamGoals));

  }

}

class AvarageGoalsReducer extends Reducer<Text, IntWritable, Text, FloatWritable> {

  protected void reduce(Text key, Iterable<IntWritable> values, Context context)
      throws InterruptedException, IOException {

    Float total = 0.0f;
    Float occurances = 0.0f;

    for (IntWritable value : values) {
      total += value.get();
      occurances += 1;
    }

    context.write(new Text("Avarage goals per match: "), new FloatWritable(total / occurances));

  }

}
