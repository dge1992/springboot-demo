package com.github.elliot.flinkdemo.watermark;

import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;

import java.time.Duration;

import static org.apache.flink.util.Preconditions.checkArgument;
import static org.apache.flink.util.Preconditions.checkNotNull;

/**
 * 自定义水位线生成器
 * 断点式
 * @param <T>
 */
public class CustomerBreakPointWatermarks<T> implements WatermarkGenerator<T> {

    private long maxTimestamp;

    private final long outOfOrdernessMillis;

    public CustomerBreakPointWatermarks(Duration maxOutOfOrderness) {
        checkNotNull(maxOutOfOrderness, "maxOutOfOrderness");
        checkArgument(!maxOutOfOrderness.isNegative(), "maxOutOfOrderness cannot be negative");

        this.outOfOrdernessMillis = maxOutOfOrderness.toMillis();

        // start so that our lowest watermark would be Long.MIN_VALUE.
        this.maxTimestamp = Long.MIN_VALUE + outOfOrdernessMillis + 1;
    }

    // ------------------------------------------------------------------------

    @Override
    public void onEvent(T event, long eventTimestamp, WatermarkOutput output) {
        maxTimestamp = Math.max(maxTimestamp, eventTimestamp);
        output.emitWatermark(new Watermark(maxTimestamp - outOfOrdernessMillis - 1));
        System.out.println("maxTimestamp:" + maxTimestamp);
    }

    @Override
    public void onPeriodicEmit(WatermarkOutput output) {

    }
}
