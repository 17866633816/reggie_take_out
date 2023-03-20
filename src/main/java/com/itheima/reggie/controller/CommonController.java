package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传与下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${upload.path}")
    private String filePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info(file.toString());
        //1.获取上传文件的后缀
        String fileName1 = file.getOriginalFilename();
        String suffix = fileName1.substring(fileName1.lastIndexOf("."));

        //2.利用UUID生成一个随机的文件名
        String fileName2 = UUID.randomUUID().toString()+suffix;

        //3.判断文件夹是否存在，不存在则创建一个，防止报错
        File dir = new File(filePath);
        if (!dir.exists()){
            dir.mkdirs();
        }

        //4.将文件转存到本地磁盘上
        try {
            file.transferTo(new File(filePath+fileName2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return R.success(fileName2);
    }

    /**
     * 文件下载
     * @param response
     * @param name
     * @return
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response,String name){

        try {
            //1.从文件系统中取出文件
            FileInputStream fis = new FileInputStream(new File(filePath+name));

            //2.将文件发送到客户端
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            byte[] bytes = new byte[1024];
            int len=0;
            while ((len=fis.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //3.关闭资源
            fis.close();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
