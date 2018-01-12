package com.example.demo.controller;


import com.example.demo.model.ApplicationInformation;
import com.example.demo.model.ApplicationInformationDTO;
import com.example.demo.model.ApplicationPicture;
import com.example.demo.model.SecretStaffMng;
import com.example.demo.repository.ApplicationInformationRepository;
import com.example.demo.util.AESUtil;
import com.example.demo.util.IOUtil;
import com.example.demo.util.RSAUtil;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.interfaces.RSAPrivateKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static com.example.demo.controller.RestUploadController.dateToStamp;
import static com.example.demo.util.AESUtil.AES_CBC_Decrypt;
import static com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility.getBytes;


/**
 * Created by huang on 2018/1/9.
 */
@Controller
@RequestMapping("/api/v1")
public class ApplicationController {
    class State{
        public String state;
        public String url;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
    @Autowired
    ApplicationInformationRepository applicationInformationRepository;

    private int k = 1; // 定义递归次数变量
    @Value("${web.upload-temp}")
    String UPLOADED_FOLDER = "";
    @PostMapping("/import")
    public ResponseEntity<?> importData(@RequestParam("file") MultipartFile uploadfile, HttpServletRequest request) throws Exception {
        String res=request.getRealPath("res");
        String unName = uploadfile.getName().substring(0, uploadfile.getName().indexOf("_"));
        String[] address = new String[10];
        String path=request.getServletContext().getContextPath();
        System.out.println("地址:"+ ClassUtils.getDefaultClassLoader().getResource("").getPath());
        System.out.println("上传地址:"+ UPLOADED_FOLDER);
        address = saveUploadedFiles(Arrays.asList(uploadfile));

        //创建临时文件夹 temp
        File file = new File(UPLOADED_FOLDER+"temp");
        file.mkdir();


        // 获得zip信息 解压到upload temp 下
        ZipFile zipFile = new ZipFile(UPLOADED_FOLDER+"temp/"+uploadfile.getName());
        Enumeration<ZipEntry> enu = (Enumeration<ZipEntry>) zipFile.entries();
        while (enu.hasMoreElements()){
            ZipEntry zipElement = (ZipEntry) enu.nextElement();
            InputStream read = zipFile.getInputStream(zipElement);
            String fileName = zipElement.getName();
            if (fileName != null && fileName.indexOf(".") != -1) {// 是否为文件
                unZipFile(zipElement, read, UPLOADED_FOLDER+"temp/");
            }
        }


        String ziptemp=request.getRealPath("ziptemp");
        String temp=request.getRealPath("temp");


        //获取私钥
        RSAPrivateKey privateKey = RSAUtil.getPrivateKey(res+"/private.pem");
        //读取已经公钥加密的AES密钥文件--记录AES加密时候产生的随机数
        String encodedSecrete = IOUtil.getContent(res+"/key.secrete");
        //解密该随机数
        String secrete = RSAUtil.decrypt(encodedSecrete, privateKey);
        //读取加密过的申请文件
        File unZip = new File(UPLOADED_FOLDER+"temp/files.kgj");
        byte[] encodedFileBytes = getBytes(UPLOADED_FOLDER+"temp/files.kgj");
        //文件转码和解密,输入上面解析出的随机数
        byte[] decoded = AES_CBC_Decrypt(encodedFileBytes, AESUtil.genSkc(secrete), false);
        FileOutputStream fileOutputStream = new FileOutputStream(new File(UPLOADED_FOLDER+unName+".zip"));
        fileOutputStream.write(decoded);
        fileOutputStream.flush();


        //创建临时文件夹 temp
        File file1 = new File(UPLOADED_FOLDER+"temp/untemp");
        if(!file1.exists()){//判断文件夹是否创建，没有创建则创建新文件夹
            file1.mkdirs();
        }

        // 获得zip信息 解压到upload temp 下
        ZipFile unZipFile = new ZipFile(UPLOADED_FOLDER+unName+".zip");
        Enumeration<ZipEntry> enu1 = (Enumeration<ZipEntry>) unZipFile.entries();
        while (enu1.hasMoreElements()){
            ZipEntry zipElement1 = (ZipEntry) enu1.nextElement();
            InputStream read1 = unZipFile.getInputStream(zipElement1);
            String fileName1 = zipElement1.getName();
            if (fileName1 != null && fileName1.indexOf(".") != -1) {// 是否为文件
                unZipFile(zipElement1, read1, UPLOADED_FOLDER+"temp/");
            }
            if(fileName1.indexOf(".")>0 && ".json".equals(fileName1.substring(fileName1.indexOf("."), fileName1.length()))){
                File jsonFile=new File(UPLOADED_FOLDER+"temp/"+fileName1 );
                String jsonStr= FileUtils.readFileToString(jsonFile,"UTF-8").replaceAll(" +","");
                JSONObject jsonObject = JSONObject.fromObject(jsonStr);
                ApplicationInformationDTO applicationInformationDTO = (ApplicationInformationDTO) JSONObject.toBean(jsonObject,ApplicationInformationDTO.class);
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

                //创建存储图片的文件夹
                File pictureFile = new File(UPLOADED_FOLDER+"picture");
                if(!pictureFile.exists()){//判断文件夹是否创建，没有创建则创建新文件夹
                    pictureFile.mkdirs();
                }
                //查找图片文件
                long time = new Date().getTime();
                ApplicationPicture applicationPicture = new ApplicationPicture();
                if(fileName1.indexOf(".")>0 && "保密资质".equals(fileName1.substring(0, 3))){
                    File credential=new File(UPLOADED_FOLDER+"temp/"+fileName1 );

                    credential.renameTo(new File(pictureFile+"/"+String.valueOf(time)+fileName1.substring(0, 3)));
                    applicationPicture.setCredential("/picture/"+String.valueOf(time)+fileName1.substring(0, 3));
                }

                if(fileName1.indexOf(".")>0 && "公司章程".equals(fileName1.substring(0, 3))){
                    File credential=new File(UPLOADED_FOLDER+"temp/"+fileName1 );
                    credential.renameTo(new File(pictureFile+"/"+String.valueOf(time)+fileName1.substring(0, 3)));
                    applicationPicture.setRegulations("/picture/"+String.valueOf(time)+fileName1.substring(0, 3));
                }
                if(fileName1.indexOf(".")>0 && "营业执照".equals(fileName1.substring(0, 3))){
                    File credential=new File(UPLOADED_FOLDER+"temp/"+fileName1 );
                    credential.renameTo(new File(pictureFile+"/"+String.valueOf(time)+fileName1.substring(0, 3)));
                    applicationPicture.setLicense("/picture/"+String.valueOf(time)+fileName1.substring(0, 3));
                }
                applicationInformation.setApplicationPicture(applicationPicture);

                //创建word存放地点
                File wordFile = new File(UPLOADED_FOLDER+"word");
                if(!wordFile.exists()){//判断文件夹是否创建，没有创建则创建新文件夹
                    wordFile.mkdirs();
                }
                if(fileName1.indexOf(".")>0 && "申请书".equals(fileName1.substring(0, 2))){
                    File credential=new File(UPLOADED_FOLDER+"temp/"+fileName1 );
                    File newWordFile = new File(wordFile + "/" + String.valueOf(time) + fileName1.substring(0, 3));
                    credential.renameTo(new File(wordFile+"/"+String.valueOf(time)+fileName1.substring(0, 3)));
                    applicationInformation.setWord(newWordFile.getPath());
                }

                applicationInformationRepository.save(applicationInformation);
            }
        }
        //删除临时文件夹 temp
        deleteDir(file);
        State state = new State();
        state.setState("success");
        state.setUrl(address[0]);
        return new ResponseEntity(state, new HttpHeaders(), HttpStatus.OK);
    }
    @GetMapping("applications")
    public Page<ApplicationInformation> query(int pageNumber, int pageSize){
        PageRequest request = this.buildPageRequest(pageNumber,pageSize);
        Page<ApplicationInformation> sourceCodes= this.applicationInformationRepository.findAll(request);
        return sourceCodes;
    }
    @DeleteMapping("applications/{id}")
    public void delete(@PathVariable int id) {
        applicationInformationRepository.deleteById(id);
    }
    @PutMapping("applications/{id}")
    public void update(@PathVariable ApplicationInformationDTO applicationInformationDTO) {
        ApplicationInformation applicationInformation = new ApplicationInformation();
        BeanUtils.copyProperties(applicationInformationDTO, applicationInformation);//浅拷贝
        applicationInformation.setWorkSituation(listToString(applicationInformationDTO.getWorkSituation()));
        applicationInformation.setTightMng(listToString(applicationInformationDTO.getTightMng()));
        applicationInformation.setTheImportSecretMng(listToString(applicationInformationDTO.getTheImportSecretMng()));
        applicationInformation.setSysAndEquiAndStorageMng(listToString(applicationInformationDTO.getSysAndEquiAndStorageMng()));
        applicationInformation.setCollaborationMng(listToString(applicationInformationDTO.getCollaborationMng()));
        applicationInformation.setWorkingFundsMng(listToString(applicationInformationDTO.getWorkingFundsMng()));
        applicationInformation.setTestMng(applicationInformationDTO.getTestMng());
        applicationInformation.setMeetingMng((applicationInformationDTO.getMeetingMng()));
        applicationInformation.setSecretCommittee(listTwoToString(applicationInformationDTO.getSecretCommittee()));
        applicationInformation.setMtcsol(listTwoToString(applicationInformationDTO.getMtcsol()));
        //图片和word
        SecretStaffMng secretStaffMng = new SecretStaffMng();
        secretStaffMng.setContent(applicationInformationDTO.getSecretStaffMng().getContent());
        secretStaffMng.setCounts(listToString(applicationInformationDTO.getSecretStaffMng().getCounts()));
        applicationInformation.setSecretStaffMng(secretStaffMng);
        applicationInformation.setApplicationPicture(applicationInformationDTO.getApplicationPicture());
    }
//    @PostMapping("/applications/user/login")
//    public String login(){
//
//    }



    private String[] saveUploadedFiles(List<MultipartFile> files) throws IOException, ParseException {
        String[] fileAddress = new String[files.size()];
        int index = 0;
        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                continue; //next pls
            }
            byte[] bytes = file.getBytes();
            Date currentTime = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = format.format(currentTime);
            String fileName=file.getName();
            String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
            Path path = Paths.get(UPLOADED_FOLDER + fileName);
            Files.write(path, bytes);
            fileAddress[index] = "/upload/" + dateToStamp(date)+prefix;
            index++;
        }
        return fileAddress;
    }

    private void zip(String zipFileName, File inputFile) throws Exception {
        System.out.println("压缩中...");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
                zipFileName));
        BufferedOutputStream bo = new BufferedOutputStream(out);
        zip(out, inputFile, inputFile.getName(), bo);
        bo.close();
        out.close(); // 输出流关闭
        System.out.println("压缩完成");
    }

    private void zip(ZipOutputStream out, File f, String base, BufferedOutputStream bo) throws Exception { // 方法重载
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            if (fl.length == 0) {
                out.putNextEntry(new ZipEntry(base + "/")); // 创建zip压缩进入点base
                System.out.println(base + "/");
            }
            for (int i = 0; i < fl.length; i++) {
                zip(out, fl[i], base + "/" + fl[i].getName(), bo); // 递归遍历子文件夹
            }
            System.out.println("第" + k + "次递归");
            k++;
        } else {
            out.putNextEntry(new ZipEntry(base)); // 创建zip压缩进入点base
            System.out.println(base);
            FileInputStream in = new FileInputStream(f);
            BufferedInputStream bi = new BufferedInputStream(in);
            int b;
            while ((b = bi.read()) != -1) {
                bo.write(b); // 将字节流写入当前zip目录
            }
            bi.close();
            in.close(); // 输入流关闭
        }
    }

    /**
     *
     * @Description: TODO(找到文件并读取解压到指定目录)
     * @param
     * @return void 返回类型
     * @throws
     */
    public void unZipFile(ZipEntry ze, InputStream read,String saveRootDirectory) throws FileNotFoundException, IOException {
        // 如果只读取图片，自行判断就OK.
        long date = new Date().getTime();
        String time = date+"";
        String fileName= ze.getName();
        // 判断文件是否符合要求或者是指定的某一类型
//      if (fileName.equals("WebRoot/WEB-INF/web.xml")) {
        // 指定要解压出来的文件格式（这些格式可抽取放置在集合或String数组通过参数传递进来，方法更通用）
        File file = new File(saveRootDirectory + fileName);
        if (!file.exists()) {
            File rootDirectoryFile = new File(file.getParent());
            // 创建目录
            if (!rootDirectoryFile.exists()) {
                boolean ifSuccess = rootDirectoryFile.mkdirs();
                if (ifSuccess) {
                    System.out.println("文件夹创建成功!");
                } else {
                    System.out.println("文件创建失败!");
                }
            }
            // 创建文件
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 写入文件
        BufferedOutputStream write = new BufferedOutputStream(new FileOutputStream(file));
        int cha = 0;
        while ((cha = read.read()) != -1) {
            write.write(cha);
        }
        // 要注意IO流关闭的先后顺序
        write.flush();
        write.close();
        read.close();
        // }
//      }
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

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    };
    public Page<ApplicationInformation> getSourceCode(int pageNumber, int pageSize){
        PageRequest request = this.buildPageRequest(pageNumber,pageSize);
        Page<ApplicationInformation> sourceCodes= this.applicationInformationRepository.findAll(request);
        return sourceCodes;
    }
    //构建PageRequest
    private PageRequest buildPageRequest(int pageNumber, int pagzSize) {
        return new PageRequest(pageNumber - 1, pagzSize, null);
    }


}
