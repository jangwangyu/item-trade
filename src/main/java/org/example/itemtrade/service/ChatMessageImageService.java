package org.example.itemtrade.service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
@Transactional
@Service
public class ChatMessageImageService {
  @Value("${file.chat-dir}")
  private String chatDir;

  public String save(MultipartFile file) {
    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
    Path path = Paths.get(chatDir, fileName); // ,로 자동 / 처리

    File directory = new File(chatDir);
    if (!directory.exists()) {
      directory.mkdirs(); // 없으면 생성
    }

    try {
      file.transferTo(path);
    } catch (Exception e) {
      throw new RuntimeException("파일 저장에 실패했습니다.", e);
    }

    return "/uploads/chat/" + fileName;
  }

}
