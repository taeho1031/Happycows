package edu.ucsb.cs156.happiercows.jobs;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import edu.ucsb.cs156.happiercows.entities.Commons;
import edu.ucsb.cs156.happiercows.entities.CowDeath;
import edu.ucsb.cs156.happiercows.entities.User;
import edu.ucsb.cs156.happiercows.entities.UserCommons;
import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.CowDeathRepository;
import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;
import edu.ucsb.cs156.happiercows.services.jobs.JobContext;
import edu.ucsb.cs156.happiercows.services.jobs.JobContextConsumer;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class UpdateCowHealthJob implements JobContextConsumer {

    private CommonsRepository commonsRepository;
    private UserCommonsRepository userCommonsRepository;

    @Override
    public void accept(JobContext ctx) throws Exception {
        ctx.log("Starting to update cow health.");
        
        Iterable<Commons> commonsList = commonsRepository.findAll();

        for(Commons commons : commonsList){
            
            int carryingCapacity = commons.getCarryingCapacity();
            int totalNumCows = commonsRepository.getNumCows(commons.getId()).get();
            double degradationRate = commons.getDegradationRate();
            double adjHealthValue = 0;

            if(totalNumCows <= carryingCapacity){
                adjHealthValue = degradationRate;
            } else {
                adjHealthValue = - Math.min((totalNumCows - carryingCapacity) * degradationRate, 100L);
            }

            for (int i = 0; i < commons.getUsers().size(); ++i){
                User user = commons.getUsers().get(i);
                Optional<UserCommons> userCommonsOptional = userCommonsRepository.findByCommonsIdAndUserId(commons.getId(), user.getId());
                if(userCommonsOptional.isEmpty()){
                    continue;
                }
                UserCommons userCommons = userCommonsOptional.get();
                double newAvgHealth = Math.min(userCommons.getAvgCowHealth() + adjHealthValue, 100);
                userCommons.setAvgCowHealth(newAvgHealth);
                userCommonsRepository.save(userCommons);
            }
        }

        ctx.log("Cows Health has been updated!");

    }
}
