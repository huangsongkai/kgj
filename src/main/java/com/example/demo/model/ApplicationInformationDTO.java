package com.example.demo.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by huang on 2018/1/11.
 */
@Data
public class ApplicationInformationDTO {
    private String newsMng;
    private String MeetingMng;
    private String companyName;
    private String creditCode;
    private String companyType;
    private String legalBody;
    private int companyPersonCount;
    private int secretPersonCount;
    private String regAddress;
    private String officeAddress;
    private String mailingAddress;
    private String postalCode;
    private long phone;
    private Date companyCreateTime;
    private String isShangshi;
    private String regMoney;
    private String fixedAssets;
    private String equityStructure;
    private String foreignRelations;
    private String punishments;
    private String summaryOfCompany;
    private String applyReason;
    private String mianLegalBody;
    private String chargeOfSecretLeader;
    private String otherLeader;
    private String secretDepartment;
    private String secretPerson;
    private String centralizingMng;
    private List<List<String>> secretCommittee;
    private List<String> workSituation;
    private List<List<String>> mtcsol;
    private String secretCommit;
    private String secretSituation;
    private String basicSystem;
    private String specialSystem;
    private List<String> tightMng;
    private SecretStaffMngDTO secretStaffMng;
    private String countriesSys;
    private String denseProductMng;
    private List<String> theImportSecretMng;
    private List<String> sysAndEquiAndStorageMng;
    private String TestMng;
    private List<String> collaborationMng;
    private String foreignNationals;
    private String secretCheck;
    private String kpAndRewardsAndPunishments;
    private String workFileMng;
    private List<String> workingFundsMng;
    ApplicationPicture applicationPicture;
    String Word;

}
