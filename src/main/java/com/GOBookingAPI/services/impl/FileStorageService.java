package com.GOBookingAPI.services.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

import com.GOBookingAPI.config.FileStorageProperties;
import com.GOBookingAPI.enums.DriverInfoImg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.GOBookingAPI.exceptions.FileStorageException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            String dateTimeNowStr = LocalDateTime.now().toString().replaceAll("-", "").replace(":", "").replace(".", "");
            fileName = dateTimeNowStr + "_" + fileName.trim().replaceAll(" ", "");
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public String storeFile(MultipartFile file, DriverInfoImg driverInfoImg, String phone, int i) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
//            String dateTimeNowStr = LocalDateTime.now().toString().replaceAll("-", "").replace(":", "").replace(".", "");
//            fileName = dateTimeNowStr +"_"+ fileName.trim().replaceAll(" ", "");
            System.out.println(getFileExtension(fileName));
            String fileExtension = getFileExtension(fileName);
            fileName = driverInfoImg.name() + "_" + i +"_" + phone + fileExtension;
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return phone + fileExtension;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileStorageException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileStorageException("File not found " + fileName, ex);
        }
    }

    public String createImgUrl(MultipartFile file) {
        String fileName = this.storeFile(file);
        String imgUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("api")
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();
        return imgUrl;
    }

    public String createImgUrl(MultipartFile file, String phone) {
        String fileName = this.storeFile(file);
        String imgUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("api")
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();
        return imgUrl;
    }

    public String createRootImgUrl(MultipartFile file, DriverInfoImg driverInfoImg, String phone, int i) {
        String fileName = this.storeFile(file, driverInfoImg, phone, i);
        String imgUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("api")
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();
        return fileName;
    }

    private static String getFileExtension(String fileName) {
        // Tìm vị trí cuối cùng của dấu chấm (.)
        int lastDotIndex = fileName.lastIndexOf(".");

        // Kiểm tra xem có dấu chấm nào không
        if (lastDotIndex != -1 && lastDotIndex < fileName.length() - 1) {
            // Lấy chuỗi sau dấu chấm
            return fileName.substring(lastDotIndex);
        } else {
            // Nếu không có dấu chấm, hoặc dấu chấm là ký tự cuối cùng, trả về chuỗi rỗng hoặc xử lý phù hợp
            return "";
        }
    }
}