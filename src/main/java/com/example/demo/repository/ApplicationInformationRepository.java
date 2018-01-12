package com.example.demo.repository;

/**
 * Created by song on 2017/10/23.
 */

import com.example.demo.model.ApplicationInformation;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin
@org.springframework.stereotype.Repository
public interface ApplicationInformationRepository extends PagingAndSortingRepository<ApplicationInformation,Long> {
    List<ApplicationInformation> findAll();
    ApplicationInformation save(ApplicationInformation applicationInformation);
    int deleteById(long id );
//    spel
}