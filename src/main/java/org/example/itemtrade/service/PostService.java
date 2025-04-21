package org.example.itemtrade.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.Oauth2.CustomOAuth2User;
import org.example.itemtrade.dto.request.ItemPostCreateRequest;
import org.example.itemtrade.dto.request.ItemPostUpdateRequest;
import org.example.itemtrade.dto.response.ItemPostResponse;
import org.example.itemtrade.repository.ItemPostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

  private final ItemPostRepository postRepository;

  // 전체조회
  public List<ItemPostResponse> getAllPosts(String category, Integer minPrice, Integer maxPrice) {
    // 모든 게시글을 조회하여 반환
    return postRepository.findAll().stream()
        .filter(post -> post.getCategory() != null) // 카테고리가 null이 아닌 게시글만 필터링
        .filter(post -> category == null || post.getCategory().name().equals(category))
        .filter(post -> minPrice == null || post.getPrice() >= minPrice)
        .filter(post -> maxPrice == null || post.getPrice() <= maxPrice)
        .map(ItemPostResponse::from).toList();
  }

  // 특정 게시글 조회
  public ItemPostResponse getPostById(Long postId) {
    // 특정 게시글을 조회하여 반환
    return postRepository.findById(postId)
        .map(ItemPostResponse::from)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."));
  }

  // 게시글 작성
  public ItemPostResponse createPost(ItemPostCreateRequest request, CustomOAuth2User user, MultipartFile image)
      throws IOException {
    String uploadDir = "C:/Users/dkfdj/IdeaProjects/item-trade/src/main/resources/uploads/";
    // 1. 디렉토리 확인 및 생성
    File directory = new File(uploadDir);
    if (!directory.exists()) {
      directory.mkdirs(); // 폴더 없으면 생성
    }

    // 2. 파일 이름 생성
    String originalFilename = image.getOriginalFilename();
    String ext = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf("."));
    String storedFilename = UUID.randomUUID() + ext;

    // 3. 실제 저장 경로
    File saveFile = new File(uploadDir + storedFilename);
    image.transferTo(saveFile); // 여기서 오류났던 것

    // 4. DTO -> Entity
    request.setImagePath("/uploads/" + storedFilename); // DB 저장용 URL
    ItemPost post = request.of(user.getMember());
    postRepository.save(post);
    // 5. Entity -> DTO
    return ItemPostResponse.from(post);
  }

  // 수정
  public void updatePost(Long postId, ItemPostUpdateRequest request, Member currentUser, MultipartFile image) throws IOException {
    ItemPost post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

    if (!post.getSeller().getId().equals(currentUser.getId())) {
      throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
    }

    // 이미지 업로드 처리
    if (image != null && !image.isEmpty()) {
      String uploadDir = "C:/Users/dkfdj/IdeaProjects/item-trade/src/main/resources/uploads/";
      String originalFilename = image.getOriginalFilename();
      String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
      String storedFilename = UUID.randomUUID() + ext;
      File file = new File(uploadDir + storedFilename);
      image.transferTo(file);
      post.setImagePath("/uploads/" + storedFilename);
    }

    // 게시글 정보 업데이트
    post.update(request);
  }
  public ItemPostResponse getPostForEdit(Long postId) {
    return postRepository.findById(postId)
        .map(ItemPostResponse::from)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."));
  }

  // 특정 게시글 삭제
  public void deletePost(Long postId, Member user) {
    ItemPost post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

    if(!post.getSeller().getId().equals(user.getMember().getId())) {
      throw new IllegalArgumentException("게시글 작성자만 삭제할 수 있습니다.");
    }

    // 이미지 파일 삭제
    String imagePath = post.getImagePath();
    if (imagePath != null) {
      Path path = Paths.get("C:/Users/dkfdj/IdeaProjects/item-trade/src/main/resources" + imagePath);
      try {
        Files.deleteIfExists(path);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    postRepository.delete(post);
  }

}
