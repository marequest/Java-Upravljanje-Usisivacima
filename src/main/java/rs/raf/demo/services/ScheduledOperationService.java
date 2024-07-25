package rs.raf.demo.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rs.raf.demo.model.ErrorMessage;
import rs.raf.demo.model.ScheduleOperation;
import rs.raf.demo.model.Usisivac;
import rs.raf.demo.repositories.ErrorMessageRepository;
import rs.raf.demo.repositories.ScheduledOperationRepository;
import rs.raf.demo.repositories.UsisivacRepository;

import java.util.Date;
import java.util.List;

@Service
public class ScheduledOperationService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledOperationService.class);
    @Autowired
    private ScheduledOperationRepository scheduledOperationRepository;

    @Autowired
    private UsisivacRepository usisivacRepository;

    @Autowired
    private UsisivacService usisivacService;

    @Autowired
    private ErrorMessageRepository errorMessageRepository;

    public ScheduleOperation scheduleOperation(Long usisivacId, ScheduleOperation.Operation operation, Date scheduledTime) {
        ScheduleOperation scheduledOperation = new ScheduleOperation();
        scheduledOperation.setUsisivacId(usisivacId);
        scheduledOperation.setOperation(operation);
        scheduledOperation.setScheduledTime(scheduledTime);
        return scheduledOperationRepository.save(scheduledOperation);
    }

    @Scheduled(fixedRate = 10000)
    public void executeScheduledOperations() {
        System.out.println("Executing scheduled operations");
        List<ScheduleOperation> operations = scheduledOperationRepository.findByScheduledTimeBefore(new Date());
        for (ScheduleOperation operation : operations) {
            try {
                Usisivac usisivac = usisivacRepository.findById(operation.getUsisivacId()).orElseThrow(() -> new RuntimeException("Usisivac not found"));
                switch (operation.getOperation()) {
                    case START:
                        usisivacService.startUsisivac(usisivac.getId());
                        break;
                    case STOP:
                        usisivacService.stopUsisivac(usisivac.getId());
                        break;
                    case DISCHARGE:
                        usisivacService.dischargeUsisivac(usisivac.getId());
                        break;
                }
            } catch (Exception e) {
                System.out.println("Couldn't execute scheduled operation!");
                ErrorMessage errorMessage = new ErrorMessage();
                errorMessage.setDate(new Date());
                errorMessage.setUsisivacId(operation.getUsisivacId());
                errorMessage.setOperation(ErrorMessage.Operation.valueOf(operation.getOperation().name()));
                errorMessage.setMessage(e.getMessage());
                errorMessageRepository.save(errorMessage);
            }
            scheduledOperationRepository.delete(operation);
        }
    }
}
