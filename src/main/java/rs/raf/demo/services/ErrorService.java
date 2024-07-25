package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import rs.raf.demo.model.ErrorMessage;
import rs.raf.demo.repositories.ErrorMessageRepository;

import java.util.List;

@Service
public class ErrorService {

    @Autowired
    private ErrorMessageRepository errorMessageRepository;

    public List<ErrorMessage> getErrorMessages() {
        return errorMessageRepository.findAll();
    }
}
