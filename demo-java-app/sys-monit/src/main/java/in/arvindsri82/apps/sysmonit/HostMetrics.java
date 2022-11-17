package in.arvindsri82.apps.sysmonit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HostMetrics {

    private static final Logger logger = LoggerFactory.getLogger(HostMetrics.class);

    @GetMapping(path = "/host")
    public Map<String, Object> hostInfo() {

        long jvmStartTimeInMillis = ManagementFactory.getRuntimeMXBean().getStartTime();
        var jvmStartTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(jvmStartTimeInMillis), ZoneId.of("GMT+05:30"));

        var uptimeInSeconds = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;

        var uptimeMins = uptimeInSeconds / 60;
        var uptimeSecs = uptimeInSeconds % 60;

        var metrics = new HashMap<String, Object>();
        metrics.put("JVM Start Time", jvmStartTime);
        metrics.put("Uptime", uptimeMins + " mins " + uptimeSecs + " seconds");

        logger.debug("Metrics collected : {}",metrics);
        return metrics;
    }

}
