package com.example.demo.model;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

//申请信息
@Entity
@Data
public class ApplicationInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
     long id;
     String MeetingMng;
     String TestMng;
     String applyReason;
     String basicSystem;
     String centralizingMng;
     String chargeOfSecretLeader;
     String collaborationMng;
     Date companyCreateTime;
     String companyName;
     int companyPersonCount;
     String companyType;
     String countriesSys;
     String creditCode;
     String denseProductMng;
     String equityStructure;
     String fixedAssets;
     String foreignNationals;
     String foreignRelations;
     String isShangshi;
     String kpAndRewardsAndPunishments;
     String legalBody;
     String mailingAddress;
     String mianLegalBody;
     //TODO List<String[]> mtcsol
     String mtcsol;
     String newsMng;
     String officeAddress;
     String otherLeader;
     long phone;
     String postalCode;
     String punishments;
     String regAddress;
     String regMoney;
     String secretCheck;
     String secretCommit;
     String secretCommittee;
     String secretDepartment;
     String secretPerson;
     int secretPersonCount;
     String secretSituation;
    @OneToOne(mappedBy = "applicationInformation",cascade = CascadeType.ALL)
     SecretStaffMng secretStaffMng;
     String specialSystem;
     String summaryOfCompany;
     String sysAndEquiAndStorageMng;
     String theImportSecretMng;
     String  tightMng;
     String workFileMng;
     String  workSituation;
     String workingFundsMng;
     String word;
    @OneToOne(mappedBy = "applicationInformation",cascade = CascadeType.ALL)
     ApplicationPicture applicationPicture;

    /**
     * 创建时间
     */
    @Column(columnDefinition = "TIMESTAMP")
    Date createTime;





}