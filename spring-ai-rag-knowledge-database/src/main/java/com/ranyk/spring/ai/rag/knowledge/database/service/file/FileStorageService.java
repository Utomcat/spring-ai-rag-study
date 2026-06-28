package com.ranyk.spring.ai.rag.knowledge.database.service.file;

import cn.hutool.core.util.IdUtil;
import com.ranyk.spring.ai.rag.knowledge.database.base.domain.dto.StoredFile;
import com.ranyk.spring.ai.rag.knowledge.database.common.exception.FileException;
import com.ranyk.spring.ai.rag.knowledge.database.config.properties.FileProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * CLASS_NAME: FileStorageService.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 文件存储业务逻辑类
 * @date: 2026-06-28
 */
@Slf4j
@Service
public class FileStorageService {

    /**
     * 文件属性配置类对象
     */
    private final FileProperties fileProperties;

    private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ROOT);

    /**
     * 构造方法 - 由 Spring IOC 容器在创建当前 Bean 对象实例时,自动注入 {@link FileProperties} 对象
     *
     * @param fileProperties 文件属性配置类对象
     */
    @Autowired
    public FileStorageService(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }

    /**
     * 上传文件 - 上传单个文件
     *
     * @param file 文件对象
     * @return 文件存储信息
     */
    public StoredFile upload(MultipartFile file) {
        try {
            // 获取当前日期的 yyyyMMdd 格式字符串
            String yyyyMmDd = LocalDate.now().format(YYYY_MM_DD);
            // 获取文件的原始名称
            String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
            // 获取文件的扩展名
            int dot = original.lastIndexOf('.');
            String ext = dot > 0 ? original.substring(dot) : "";
            // 生成新的文件名
            String name = IdUtil.simpleUUID() + ext.toLowerCase(Locale.ROOT);
            // 获取文件上传的目录
            Path dir = Paths.get(fileProperties.getUpload().getRoot(), yyyyMmDd);
            // 创建目录
            Files.createDirectories(dir);
            // 获取文件的完整路径
            Path target = dir.resolve(name);
            // 将文件保存到目标路径
            file.transferTo(target);
            // 获取文件的相对路径
            String relative = yyyyMmDd + "/" + name;
            // 返回文件的相对路径和完整路径
            return new StoredFile(name, relative, ext, file.getSize(), target);
        } catch (Exception e) {
            throw new FileException("file.upload.error", new String[]{file.getOriginalFilename(), e.getMessage()});
        }
    }

    /**
     * 上传文件 - 上传多个文件
     *
     * @param files 文件对象列表
     * @return 文件存储信息列表
     */
    public List<StoredFile> batchUpload(List<MultipartFile> files) {
        return files.stream().map(file -> {
            try {
                return upload(file);
            } catch (Exception e) {
                throw new FileException("file.upload.error", new String[]{file.getOriginalFilename(), e.getMessage()});
            }
        }).toList();
    }
}
