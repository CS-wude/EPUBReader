package com.wude.epubreader;

import nl.siegmann.epublib.epub.EpubReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EpubController {

    private final Path uploadDir = Paths.get("G:\\book").toAbsolutePath();

    public EpubController() throws IOException {
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    @PostMapping("/uploadEpub")
    public String uploadEpub(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "请选择一个EPUB文件进行上传。";
        }

        try {
            // Save the file to the uploads directory
            Path filePath = uploadDir.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // Parse with epublib (optional, can be done later)
            EpubReader epubReader = new EpubReader();
            epubReader.readEpub(file.getInputStream()); // This might need to be from the saved file now

            return "EPUB 文件 " + file.getOriginalFilename() + " 上传并解析成功！";

        } catch (IOException e) {
            e.printStackTrace();
            return "上传和解析 EPUB 文件失败: " + e.getMessage();
        }
    }

    @GetMapping("/listEpubs")
    public List<String> listEpubs() throws IOException {
        return Files.list(uploadDir)
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    @GetMapping("/epub/{filename:.+}")
    public ResponseEntity<Resource> serveEpubFile(@PathVariable String filename) throws MalformedURLException {
        Path filePath = uploadDir.resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() || resource.isReadable()) {
            String contentType = "application/epub+zip";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            throw new RuntimeException("Could not read file: " + filename);
        }
    }
} 