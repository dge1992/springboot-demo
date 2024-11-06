package com.github.elliot.flinkdemo.window;

import com.github.elliot.flinkdemo.bean.WaterSensor;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * 全窗口函数
 */
public class WindowProcessDemo {

    public static void main(String[] args) throws Exception {
        LocalStreamEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        SingleOutputStreamOperator<WaterSensor> streamOperator = env.socketTextStream("10.211.55.4", 7777)
                .flatMap(new FlatMapFunction<String, WaterSensor>() {
                    @Override
                    public void flatMap(String value, Collector<WaterSensor> out) throws Exception {
                        String[] split = value.split(",");
                        WaterSensor waterSensor = new WaterSensor();
                        waterSensor.setId(split[0]);
                        waterSensor.setSt(Long.valueOf(split[1]));
                        waterSensor.setVc(Integer.valueOf(split[2]));
                        out.collect(waterSensor);
                    }
                });
        KeyedStream<WaterSensor, String> waterSensorStringKeyedStream = streamOperator.keyBy(e -> e.getId());
        WindowedStream<WaterSensor, String, TimeWindow> window =
                waterSensorStringKeyedStream.window(TumblingProcessingTimeWindows.of(Time.seconds(20)));
        SingleOutputStreamOperator<String> process = window.process(new ProcessWindowFunction<WaterSensor, String, String, TimeWindow>() {
            @Override
            public void process(String s, ProcessWindowFunction<WaterSensor, String, String,
                    TimeWindow>.Context context, Iterable<WaterSensor> elements, Collector<String> out) throws Exception {
                long start = context.window().getStart();
                long end = context.window().getEnd();
                String startStr = DateFormatUtils.format(start, "yyyy-MM-dd HH:mm:ss.SSS");
                String endStr = DateFormatUtils.format(end, "yyyy-MM-dd HH:mm:ss.SSS");
                long count = elements.spliterator().estimateSize();
                out.collect("key:" + s + " 时间段[" + startStr + "-" + endStr + "]" + "的个数是:" + count + "数据集为:" + elements.toString());
            }
        });
        process.print();
        env.execute();
    }
}
