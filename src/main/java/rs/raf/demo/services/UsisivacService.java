package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import rs.raf.demo.controllers.WebSocketController;
import rs.raf.demo.model.ErrorMessage;
import rs.raf.demo.model.Usisivac;
import rs.raf.demo.model.User;
import rs.raf.demo.repositories.ErrorMessageRepository;
import rs.raf.demo.repositories.UsisivacRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import java.util.concurrent.Executor;
import java.util.stream.Collectors;


@Service
public class UsisivacService {

    @Qualifier("myTaskExecutor")
    @Autowired
    private Executor taskExecutor;

    @Autowired
    private UsisivacRepository usisivacRepository;

    @Autowired
    private ErrorMessageRepository errorMessageRepository;

    @Autowired
    private WebSocketController webSocketController;

    public List<Usisivac> getAllActiveUsisivaciForUser(User user) {
        List<Usisivac> usisivaci = usisivacRepository.findByAddedByAndActiveTrue(user);
        System.out.println("Found usisivaci for user: " + user.getUsername());
        return usisivaci;
    }

    public List<Usisivac> searchVacuums(String name, List<String> statusStrings, Date dateFrom, Date dateTo, User user) {
        List<Usisivac.Status> status = null;
        if (statusStrings != null && !statusStrings.isEmpty()) {
            status = statusStrings.stream()
                    .map(String::toUpperCase)
                    .map(Usisivac.Status::valueOf)
                    .collect(Collectors.toList());
        }
        return usisivacRepository.findByCriteria(name, status, dateFrom, dateTo, user);
    }

    public Usisivac addUsisivac(Usisivac usisivac) {
        usisivac.setCreatedDate(new Date());
        return usisivacRepository.save(usisivac);
    }

    public void removeUsisivac(Long id) {
        usisivacRepository.deleteById(id);
    }

    public Usisivac startUsisivac(Long id) {
        try{
            Optional<Usisivac> optionalUsisivac = usisivacRepository.findById(id);
            if (optionalUsisivac.isPresent()) {
                Usisivac usisivac = optionalUsisivac.get();
                if (usisivac.getStatus() != Usisivac.Status.OFF) {
                    throw new RuntimeException("Start operation already in progress");
                }
                usisivac.setStatus(Usisivac.Status.IN_PROGRESS);
                usisivacRepository.save(usisivac);
                webSocketController.sendStatusUpdate("Vacuum " + usisivac.getId() + " in progress.");

                taskExecutor.execute(() -> {
                    try {
                        Thread.sleep(15000); // Simulacija vremena potrebnog za start
                        usisivac.setStatus(Usisivac.Status.ON);
                        usisivacRepository.save(usisivac);
                        webSocketController.sendStatusUpdate("Vacuum " + usisivac.getId() + " started.");
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                        throw new RuntimeException("Error finishing start operation");
                    }
                });
                return usisivac;
            } else {
                throw new RuntimeException("Usisivac " + id + "not found");
            }
        } catch (Exception e) {
            handleException(e, id, ErrorMessage.Operation.START);
            throw e;
        }
    }

    public Usisivac stopUsisivac(Long id) {
        try {
            Optional<Usisivac> optionalUsisivac = usisivacRepository.findById(id);
            if (optionalUsisivac.isPresent()) {
                Usisivac usisivac = optionalUsisivac.get();
                if (usisivac.getStatus() != Usisivac.Status.ON) {
                    throw new RuntimeException("Stop operation already in progress");
                }
                usisivac.setStatus(Usisivac.Status.IN_PROGRESS);
                usisivacRepository.save(usisivac);
                webSocketController.sendStatusUpdate("Vacuum " + usisivac.getId() + " in progress.");

                taskExecutor.execute(() -> {
                    try {
                        Thread.sleep(15000); // Simulacija vremena potrebnog za stop
                        usisivac.setStatus(Usisivac.Status.OFF);
                        usisivac.setCycleCount(usisivac.getCycleCount() + 1);
                        if (usisivac.getCycleCount() >= 3) {
                            automaticDischarge(usisivac);
                        } else {
                            usisivacRepository.save(usisivac);
                            webSocketController.sendStatusUpdate("Vacuum " + usisivac.getId() + " stopped.");
                        }
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                        throw new RuntimeException("Error finishing stop operation");
                    }
                });
                return usisivac;
            } else {
                throw new RuntimeException("Usisivac " + id + "not found");
            }
        } catch (Exception e) {
            handleException(e, id, ErrorMessage.Operation.STOP);
            throw e;
        }
    }

    public Usisivac dischargeUsisivac(Long id) {
        try {
            Optional<Usisivac> optionalUsisivac = usisivacRepository.findById(id);
            if (optionalUsisivac.isPresent()) {
                Usisivac usisivac = optionalUsisivac.get();
                if (usisivac.getStatus() != Usisivac.Status.OFF) {
                    throw new RuntimeException("Usisivac must be OFF to discharge");
                }
                usisivac.setStatus(Usisivac.Status.IN_PROGRESS);
                usisivacRepository.save(usisivac);
                webSocketController.sendStatusUpdate("Vacuum " + usisivac.getId() + " in progress.");

                taskExecutor.execute(() -> {
                    try {
                        Thread.sleep(15000);
                        usisivac.setStatus(Usisivac.Status.DISCHARGING);
                        usisivacRepository.save(usisivac);
                        webSocketController.sendStatusUpdate("Vacuum " + usisivac.getId() + " discharging.");

                        Thread.sleep(15000);
                        usisivac.setStatus(Usisivac.Status.OFF);
                        usisivac.setCycleCount(0);
                        usisivacRepository.save(usisivac);
                        webSocketController.sendStatusUpdate("Vacuum " + usisivac.getId() + " stopped.");
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                        throw new RuntimeException("Error finishing discharge operation");
                    }
                });
                return usisivac;
            } else {
                throw new RuntimeException("Usisivac " + id + "not found");
            }
        } catch (Exception e) {
            handleException(e, id, ErrorMessage.Operation.DISCHARGE);
            throw e;
        }
    }

    private void automaticDischarge(Usisivac usisivac) {
        try {
            usisivac.setStatus(Usisivac.Status.IN_PROGRESS);
            usisivacRepository.save(usisivac);
            webSocketController.sendStatusUpdate("Vacuum " + usisivac.getId() + " in progress.");

            taskExecutor.execute(() -> {
                try {
                    Thread.sleep(15000); // Pola vremena za prelazak u DISCHARGING
                    usisivac.setStatus(Usisivac.Status.DISCHARGING);
                    usisivacRepository.save(usisivac);
                    webSocketController.sendStatusUpdate("Vacuum " + usisivac.getId() + " automatically discharging.");

                    Thread.sleep(15000); // Druga polovina vremena za prelazak u OFF
                    usisivac.setStatus(Usisivac.Status.OFF);
                    usisivac.setCycleCount(0); // Resetovanje brojača ciklusa nakon pražnjenja
                    usisivacRepository.save(usisivac);
                    webSocketController.sendStatusUpdate("Vacuum " + usisivac.getId() + " stopped.");
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                    throw new RuntimeException("Error finishing automatic discharge operation");
                }
            });
        } catch (Exception e) {
            handleException(e, usisivac.getId(), ErrorMessage.Operation.DISCHARGE);
            throw e;
        }
    }

    private void handleException(Exception e, Long usisivacId, ErrorMessage.Operation operation) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setDate(new Date());
        errorMessage.setUsisivacId(usisivacId);
        errorMessage.setOperation(ErrorMessage.Operation.START);
        errorMessage.setMessage(e.getMessage());
        Optional<Usisivac> optionalUsisivac = usisivacRepository.findById(usisivacId);
        if (optionalUsisivac.isPresent()) {
            User user = optionalUsisivac.get().getAddedBy();
            errorMessage.setUser(user);
        }
        errorMessageRepository.save(errorMessage);
    }
}
