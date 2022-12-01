package edu.ucsb.cs156.happiercows.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UpdateCowHealthJobFactory  {

    @Autowired 
    CommonsRepository commonsRepository;
    
    @Autowired
    UserCommonsRepository userCommonsRepository;
  
    public UpdateCowHealthJob create() {
        log.info("commonsRepository = " + commonsRepository);
        log.info("userCommonsRepository = " + userCommonsRepository);
        return new UpdateCowHealthJob( commonsRepository, userCommonsRepository);
    }

}
