package advanced.customwritable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;

public class AverageTemperature {

    public static void main(String args[]) throws IOException,
            ClassNotFoundException,
            InterruptedException {
        BasicConfigurator.configure();

        Configuration c = new Configuration();
        String[] files = new GenericOptionsParser(c, args).getRemainingArgs();
        // arquivo de entrada
        Path input = new Path(files[0]);

        // arquivo de saida
        Path output = new Path(files[1]);

        // criacao do job e seu nome
        Job j = new Job(c, "media");

        // registrar as classes
        j.setJarByClass(AverageTemperature.class);
        j.setMapperClass(MapForAverage.class);
        j.setReducerClass(ReduceForAverage.class);
        j.setCombinerClass(CombineForAverage.class);

        // definir os tipos de dados de Chaves e Saída(valor)
        j.setMapOutputKeyClass(Text.class);
        j.setMapOutputValueClass(FireAvgTempWritable.class);//rever
        j.setOutputKeyClass(Text.class);
        j.setOutputValueClass(FloatWritable.class);

        //configurar os jobs de entrada e saida
        FileInputFormat.addInputPath(j, input);
        FileOutputFormat.setOutputPath(j, output);
        boolean b = j.waitForCompletion(true);
        System.exit(b ? 0 : 1);
    }


    public static class MapForAverage extends Mapper<LongWritable, Text, Text, FireAvgTempWritable> {
        public void map(LongWritable key, Text value, Context con)
                throws IOException, InterruptedException {
            String line = value.toString();
            String[] columns = line.split(",");
            if(columns.length>0) {
                FireAvgTempWritable temp = new FireAvgTempWritable(Float.parseFloat(columns[8]), 1);
                con.write(new Text("media"), temp);
            }

        }
    }

    public static class CombineForAverage extends Reducer<Text, FireAvgTempWritable, Text, FireAvgTempWritable>{
        public void reduce(Text key, Iterable<FireAvgTempWritable> values, Context con)
                throws IOException, InterruptedException {
            float sum_temp = 0f;
            int sum_n = 0;
            for (FireAvgTempWritable val : values) {
                sum_temp += val.getTemp();
                sum_n += val.getN();
            }
            FireAvgTempWritable temp = new FireAvgTempWritable(sum_temp, sum_n);
            con.write(key, temp);
        }
    }

    public static class ReduceForAverage extends Reducer<Text, FireAvgTempWritable, Text, FloatWritable> {
        public void reduce(Text key, Iterable<FireAvgTempWritable> values, Context con)
                throws IOException, InterruptedException {
            float sum_temp = 0f;
            int sum_n = 0;
            for (FireAvgTempWritable val : values) {
                sum_temp += val.getTemp();
                sum_n += val.getN();
            }
            float avg_temp = sum_temp / sum_n;
            con.write(key, new FloatWritable(avg_temp));
        }
    }
}