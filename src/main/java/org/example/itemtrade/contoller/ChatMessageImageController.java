package org.example.itemtrade.contoller;

import lombok.RequiredArgsConstructor;
import org.example.itemtrade.service.ChatMessageImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class ChatMessageImageController {

  private final ChatMessageImageService chatMessageImageService;

  // 이미지 업로드
  @PostMapping("/api/images/upload")
  public ResponseEntity<String> uploadImage(@RequestParam("file")MultipartFile file) {
    // 이미지 파일을 업로드하고 URL을 반환
    String image = chatMessageImageService.save(file);
    return ResponseEntity.ok(image);
  }

}
