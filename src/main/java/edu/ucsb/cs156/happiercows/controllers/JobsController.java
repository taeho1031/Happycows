package edu.ucsb.cs156.happiercows.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ucsb.cs156.happiercows.entities.jobs.Job;
import edu.ucsb.cs156.happiercows.repositories.jobs.JobsRepository;
import edu.ucsb.cs156.happiercows.services.jobs.JobService;



@Slf4j
@Api(description = "Jobs")
@RequestMapping("/api/jobs")
@RestController
public class JobsController extends ApiController {
    @Autowired
    private JobsRepository jobsRepository;

    @Autowired
    private JobService jobService;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all jobs")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public Iterable<Job> allJobs() {
        Iterable<Job> jobs = jobsRepository.findAll();
        return jobs;
    }

    @ApiOperation(value = "Launch Test Job (click fail if you want to test exception handling)")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/launch/testjob")
    public Job launchTestJob(
        @ApiParam("fail") @RequestParam Boolean fail, 
        @ApiParam("sleepMs") @RequestParam Integer sleepMs
    ) {

        return jobService.runAsJob(ctx -> {
            ctx.log("Hello World! from test job!");
            Thread.sleep(sleepMs);
            if (fail) {
                throw new Exception("Fail!");
            }
            ctx.log("Goodbye from test job!");
          });
    }

    @ApiOperation(value = "Launch Job to Update Cow Health (click fail if you want to test exception handling)")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/launch/updatehealthjob")
    public Job launchUpdateCowHealthJob(
    ) {

        return jobService.runAsJob(ctx -> {
            ctx.log("Starting to update cow health");
            ctx.log("This is where the code to update cow health will go.");
            ctx.log("Cows health is updated!");
          });
    }

    @ApiOperation(value = "Launch Job for Instructor Report (click fail if you want to test exception handling)")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/launch/instructorreportjob")
    public Job launchInstructorReportJob(
    ) {

        return jobService.runAsJob(ctx -> {
            ctx.log("Starting instructor report");
            ctx.log("This is where the code for instructor report.");
            ctx.log("Instructor report completed!");
          });
    }


}
