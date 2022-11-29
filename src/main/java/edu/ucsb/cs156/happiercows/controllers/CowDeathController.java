package edu.ucsb.cs156.happiercows.controllers;

import edu.ucsb.cs156.happiercows.errors.EntityNotFoundException;
import edu.ucsb.cs156.happiercows.entities.CowDeath;
import edu.ucsb.cs156.happiercows.entities.UserCommons;
import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.CowDeathRepository;
import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "Cow Deaths")
@RequestMapping("/api/cowdeath")
@RestController
@Slf4j

public class CowDeathController extends ApiController {
    @Autowired
    CommonsRepository commonsRepository;

    @Autowired
    UserCommonsRepository userCommonsRepository;

    @Autowired
    CowDeathRepository cowDeathRepository;

    @ApiOperation(value = "Create a new CowDeath as admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/post")
    public CowDeath postCowDeath_admin(
            @ApiParam("commonsId") @RequestParam long commonsId,
            @ApiParam("ZonedDateTime") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ZonedDateTime,
            @ApiParam("cowsKilled") @RequestParam Integer cowsKilled,
            @ApiParam("avgHealth") @RequestParam Long avgHealth) {
        
        Long userId = getCurrentUser().getUser().getId();

        CowDeath createdCowDeath = CowDeath.builder()
            .commonsId(commonsId)
            .userId(userId)
            .ZonedDateTime(ZonedDateTime)
            .cowsKilled(cowsKilled)
            .avgHealth(avgHealth)
            .build();
        
        CowDeath savedCowDeath = cowDeathRepository.save(createdCowDeath);
        return savedCowDeath;
    }

    @ApiOperation(value = "Get all cow deaths belonging to a commons as an admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/all/bycommons")
    public Iterable<CowDeath> allCowDeathsByCommonsId_admin(
            @ApiParam("commonsId") @RequestParam Long commonsId) {
        Iterable<CowDeath> cowDeaths = cowDeathRepository.findAllByCommonsId(commonsId);
        return cowDeaths;
    }

    
    @ApiOperation(value = "Get all cow deaths belonging to a user commons as a user via CommonsID")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all/byusercommons")
    public Iterable<CowDeath> allCowDeathsByCommonsId(
            @ApiParam("commonsId") @RequestParam Long commonsId) {
        
        Long userId = getCurrentUser().getUser().getId();
        UserCommons userCommons = userCommonsRepository.findByCommonsIdAndUserId(commonsId, userId)
            .orElseThrow(() -> new EntityNotFoundException(UserCommons.class, "commonsId", commonsId, "userId", userId));

        if (userId != userCommons.getUserId())
            throw new EntityNotFoundException(UserCommons.class, userCommons.getId());

        Iterable<CowDeath> cowDeaths = cowDeathRepository.findAllByCommonsId(userCommons.getId());

        return cowDeaths;
    }

}