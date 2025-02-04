package com.alamin.ecommerce.home;

import com.alamin.ecommerce.product.Product;
import com.alamin.ecommerce.product.ProductImage;
import com.alamin.ecommerce.product.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class FileController {

    private ProductService productService;

    @PostMapping("/admin/upload")
    public Map<String, String> uploadFile(@RequestBody MultipartFile file){
        if(file.getSize() > 5000 * 1024)
            throw new RuntimeException("file size exceed allowed size");

        String name = saveFile(file);
        System.out.println("file saved successful with name: " + name);
        Map<String, String> res = new HashMap<>();
        res.put("url",  name);
        res.put("result", "success");
        System.out.println("File name: " + name);
        return res;
    }

    @PostMapping("/admin/products/upload")
    public Map<String, String> uploadFile(@RequestBody MultipartFile file,
                                          @RequestParam(required = false) Long id){
        if(file.getSize() > 5000 * 1024)
            throw new RuntimeException("file size exceed allowed size");

        Product product = productService.getProductById(id).orElseThrow();
        Map<String, String> res = uploadFile(file);
        product.getProductImages().add(new ProductImage(res.get("url"), id));
        productService.save(product);
        return res;
    }

    @GetMapping("/admin/images/{name}")
    public void getImage(@PathVariable String name, HttpServletResponse response, UriComponentsBuilder ucb) {
        File file = new File("C:\\uploads\\" + name);
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try (FileInputStream inputStream = new FileInputStream(file)) {
            String mimeType = "application/octet-stream";
            response.setContentType(mimeType);
            response.setContentLength((int) file.length());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public static String saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            System.out.println("File is empty. Please upload a valid file.");
            return null;
        }
        String fileName = file.getOriginalFilename();
        String uploadDir = "C:\\uploads\\";
        String fileExtension = "";
        if (fileName != null && fileName.contains(".")) {
            fileExtension = fileName.substring(fileName.lastIndexOf("."));
        }

        fileName = UUID.randomUUID().toString().replace("-", "") + fileExtension;
        try {
            File destinationFile = new File(uploadDir + fileName);
            file.transferTo(destinationFile);
            System.out.println("File saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save the file.");
        }
        return fileName;
    }
}
