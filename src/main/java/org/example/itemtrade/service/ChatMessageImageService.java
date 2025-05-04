package org.example.itemtrade.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ChatMessageImageService {
  private static final String UPLOAD_DIR = "C:/Users/dkfdj/IdeaProjects/item-trade/src/main/resources/uploads/chat/";

  public String save(MultipartFile file) {
    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
    Path path = Paths.get(UPLOAD_DIR + fileName);

    try {
      file.transferTo(path);
    } catch (Exception e) {
      throw new RuntimeException("파일 저장에 실패했습니다.", e);
    }

    return "/uploads/chat/" + fileName;
  }

}
