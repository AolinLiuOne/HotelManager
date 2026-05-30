package com.hotely5d.controller;

import com.hotely5d.entity.model.Result;
import com.hotely5d.entity.model.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {   //公用文件上传服务
    @Value("${upload.sufix}")
    private String uploadSufix;
    @Value("${upload.maxsize}")
    private long uploadMaxSize;
    @Value("${upload.realPath}")
    private String realPath;//文件保存位置
    @Value("${upload.urlPrefix}")
    private String urlPrefix;//图片url前缀

    private Logger log = LoggerFactory.getLogger(FileController.class);


    /**
     * 图片统一上传类
     * @param photo
     * @return
     */
    @RequestMapping(value="/uploadPhoto",method= RequestMethod.POST)
    @ResponseBody
    public Result uploadPhoto(@RequestParam(name="photo",required=true) MultipartFile photo){
        return  uploadPhotoMultipartFile(photo);
    }

    public Result uploadPhotoMultipartFile(MultipartFile photo){
        //判断文件类型是否是图片
        String originalFilename = photo.getOriginalFilename();
        //获取文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."),originalFilename.length());
        if(!uploadSufix.contains(suffix.toLowerCase())){
            return new Result(StatusCode.ERROR,false,"图片格式不合法");
        }
        if(photo.getSize()/1024 > uploadMaxSize){
            return new Result(StatusCode.ERROR,false,"图片大小不合法");
        }
        //准备保存文件
        File filePath = new File(realPath);
        if(!filePath.exists()){
            //若不存在文件夹，则创建一个文件夹
            filePath.mkdir();
        }
        //判断当天日期的文件夹是否存在，若不存在，则创建
        if(!filePath.exists()){
            //若不存在文件夹，则创建一个文件夹
            filePath.mkdir();
        }
        String filename = UUID.randomUUID().toString().replace("-","") + suffix;
        try {
            new File(realPath+"/"+filename).getParentFile().mkdirs();
            photo.transferTo(new File(realPath+"/"+filename));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("图片上传成功，保存位置：" + realPath + filename);
        Map map = new HashMap();
        map.put("filename",filename);
        map.put("url",urlPrefix+filename);
        return new Result(StatusCode.OK,true,"上传成功",map);
    }
}

