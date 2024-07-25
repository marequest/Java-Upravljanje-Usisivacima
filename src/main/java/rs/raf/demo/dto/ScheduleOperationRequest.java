package rs.raf.demo.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class ScheduleOperationRequest {
    private String operation;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date scheduledTime;
}
