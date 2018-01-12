package com.example.demo;

import com.cputech.modules.usermsg.model.SysRole;
import com.cputech.modules.usermsg.repository.SysRoleRepository;
import com.cputech.modules.usermsg.repository.SysUserRepository;
import com.cputech.modules.usermsg.service.CustomUserDetailsService;
import com.example.demo.model.ApplicationInformation;
import com.example.demo.model.ApplicationInformationDTO;
import com.example.demo.model.SecretStaffMng;
import com.example.demo.repository.ApplicationInformationRepository;
import com.example.demo.repository.RecordRepository;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by song on 2017/9/26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@AutoConfigureMockMvc
@SpringBootTest(classes = {DemoApplication.class},webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SysUserRepositoryTest {
    @Autowired
    CustomUserDetailsService customUserDetailsService;
    @Autowired
    SysRoleRepository sysRoleRepository;
    @Autowired
    SysUserRepository sysUserRepository;
    @Autowired
    RecordRepository recordRepository;
    @Autowired
    ApplicationInformationRepository applicationInformationRepository;
    @Test
    public void addRoles() throws Exception {
        SysRole sysRole = new SysRole();
        sysRole.setName("ROLE_ADMIN");
        sysRoleRepository.save(sysRole);
        sysRole = new SysRole();
        sysRole.setName("ROLE_USER");
        sysRoleRepository.save(sysRole);
    }
    @Test
    public void createAdminAndUser() throws Exception {
//        SysUser user = new SysUser();
//        user.setUsername("admin");
//        user.setPassword("123456");
//        List<SysRole> sysRoleList = new ArrayList<>();
//        sysRoleList.add(sysRoleRepository.findOne(1L));
//        sysRoleList.add(sysRoleRepository.findOne(2L));
//        user.setRoles(sysRoleList);
//        customUserDetailsService.create(user);
    }

    @Test
    public void createUsers() throws Exception {
//        SysUser user = new SysUser();
//        user.setUsername("宗建峰");
//        user.setPassword("15146082288");
//        List<SysRole> sysRoleList = new ArrayList<>();
//        sysRoleList.add(sysRoleRepository.findOne(2L));
//        user.setRoles(sysRoleList);
//        customUserDetailsService.create(user);
    }

    @Test
    public void createRecords() throws Exception {
//        Record record = new Record();
//        record.setId(1L);
//        record.setDescription("1111");
//        record.setOwner((SysUser) customUserDetailsService.loadUserByUsername("swl"));
//        recordRepository.save(record);
//
//        record = new Record();
//        record.setId(2L);
//        record.setDescription("2222");
//        record.setOwner((SysUser) customUserDetailsService.loadUserByUsername("swl2"));
//        recordRepository.save(record);

    }
    @Test
    public void readExcel() throws Exception {
//        File file = new File("/Users/huang/Desktop/上传名录.xlsx");
//        FileInputStream in = new FileInputStream(file);
//        List<List<Object>> bankListByExcel = ReadExcel.getBankListByExcel(in,"上传名录.xlsx");
//        in.close();
//        for (int i = 0; i < bankListByExcel.size(); i++) {
//            List<Object> lo = bankListByExcel.get(i);
//            System.out.println("联系人:"+lo.get(2).toString().trim()+"电话:"+lo.get(3).toString().trim());
//            //进行加密
//            SysUser user = new SysUser();
//            user.setUsername(lo.get(2).toString().trim());
//            user.setPassword(lo.get(3).toString().trim());
//            List<SysRole> sysRoleList = new ArrayList<>();
//            sysRoleList.add(sysRoleRepository.findOne(2L));
//            user.setRoles(sysRoleList);
//            customUserDetailsService.create(user);
//        }
//        in.close();
    }
   @Test
    public void tesReadJsonFile() throws IOException {
       File jsonFile=new File("/Users/huang/Downloads/1515488142000_国防科工局/填报文件/1515488142000_国防科工局.json");
       String jsonStr= FileUtils.readFileToString(jsonFile,"UTF-8").replaceAll(" +","");
       JSONObject jsonObject = JSONObject.fromObject(jsonStr);
       ApplicationInformationDTO applicationInformationDTO = (ApplicationInformationDTO) JSONObject.toBean(jsonObject,ApplicationInformationDTO.class);
       applicationInformationDTO.getApplyReason();
       for (List<String> list : applicationInformationDTO.getSecretCommittee()) {
           System.out.println("数组"+list.get(0));
           System.out.println("数组数量"+list.size());

       }
       System.out.println("==================TESTMNG"+jsonObject.get("TestMng"));
       System.out.println("==================getMeetingMng"+jsonObject.get("TestMng"));

       ApplicationInformation applicationInformation = new ApplicationInformation();
       BeanUtils.copyProperties(applicationInformationDTO, applicationInformation);//浅拷贝
       applicationInformation.setWorkSituation(listToString(applicationInformationDTO.getWorkSituation()));
       applicationInformation.setTightMng(listToString(applicationInformationDTO.getTightMng()));
       applicationInformation.setTheImportSecretMng(listToString(applicationInformationDTO.getTheImportSecretMng()));
       applicationInformation.setSysAndEquiAndStorageMng(listToString(applicationInformationDTO.getSysAndEquiAndStorageMng()));
       applicationInformation.setCollaborationMng(listToString(applicationInformationDTO.getCollaborationMng()));
       applicationInformation.setWorkingFundsMng(listToString(applicationInformationDTO.getWorkingFundsMng()));
       applicationInformation.setTestMng((String) jsonObject.get("TestMng"));
       applicationInformation.setMeetingMng((String) jsonObject.get("MeetingMng"));
       applicationInformation.setSecretCommittee(listTwoToString(applicationInformationDTO.getSecretCommittee()));
       applicationInformation.setMtcsol(listTwoToString(applicationInformationDTO.getMtcsol()));
       SecretStaffMng secretStaffMng = new SecretStaffMng();
       secretStaffMng.setContent(applicationInformationDTO.getSecretStaffMng().getContent());
       secretStaffMng.setCounts(listToString(applicationInformationDTO.getSecretStaffMng().getCounts()));
       applicationInformation.setSecretStaffMng(secretStaffMng);
       applicationInformationRepository.save(applicationInformation);
   }
    public String listToString(List list){
        String rSting = "";
       for (int j = 0; j < list.size(); j++) {
           String str =  list.get(j)+"";
            if (list.size()-1 == j){
                rSting+= str;
            }else{
                rSting+= str+",";
            }
        }
        list.forEach(System.out::println);
        return rSting;
    }

    public String listTwoToString(List<List<String>> list){
        String rSting = "";
        for (int j = 0; j < list.size(); j++) {
            String str = listToString(list.get(j));
            if (list.size()-1 == j){
                rSting+= str;
            }else{
                rSting+= str+"|";
            }
        }
        list.forEach(System.out::println);
        return rSting;
    }

}