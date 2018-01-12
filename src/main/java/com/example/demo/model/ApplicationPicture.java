package com.example.demo.model; /**
 * Copyright 2018 bejson.com
 */
import lombok.Data;

import javax.persistence.*;

/**
 * Auto-generated: 2018-01-09 14:5:54
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Entity
@Data
public class ApplicationPicture {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
     long id;
     String regulations;//章程
     String license;//执照
     String credential;//保密资质
    @OneToOne
    @JoinColumn(name = "id")
    ApplicationInformation applicationInformation;

}