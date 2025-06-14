package org.example.itemtrade.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.itemtrade.domain.ItemImage;
import org.example.itemtrade.domain.ItemPost;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.domain.MemberBlock;
import org.example.itemtrade.dto.request.ItemPostCreateRequest;
import org.example.itemtrade.dto.request.ItemPostUpdateRequest;
import org.example.itemtrade.dto.response.ItemPostResponse;
import org.example.itemtrade.enums.Category;
import org.example.itemtrade.enums.TradeStatus;
import org.example.itemtrade.repository.ItemPostRepository;
import org.example.itemtrade.repository.MemberBlockRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  private final MemberBlockRepository memberBlockRepository;

  // 전체조회
  public Page<ItemPostResponse> getAllPosts(String categoryName, Integer minPrice, Integer maxPrice, Member currentUser,
      Pageable pageable) {

    List<Member> blocked = (currentUser != null) ? memberBlockRepository.findAllByBlocker(currentUser).stream().map(MemberBlock::getBlocked).toList() : Collections.emptyList();

    Category category = (categoryName != null) ? Category.valueOf(categoryName) : null;

    return postRepository.findAllPosts(category, minPrice, maxPrice, blocked, pageable).map(ItemPostResponse::from);
  }

  // 특정 게시글 조회
  public ItemPostResponse getPostById(Long postId) {
    // 특정 게시글을 조회하여 반환
    return postRepository.findById(postId)
        .map(ItemPostResponse::from)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."));
  }

  // 게시글 작성
  public ItemPostResponse createPost(ItemPostCreateRequest request, Member member, List<MultipartFile> images)
      throws IOException {
    // 객체 생성
    ItemPost post = ItemPost.builder()
        .title(request.getTitle())
        .description(request.getDescription())
        .price(request.getPrice())
        .category(request.getCategory())
        .seller(member)
        .isSold(false)
        .build();

    // 2. 이미지 저장 및 연관관계 설정
    String uploadDir = "C:/Users/dkfdj/IdeaProjects/item-trade/src/main/resources/uploads/post/";
    File directory = new File(uploadDir);
    if (!directory.exists()) {
      directory.mkdirs();
    }

    for (MultipartFile image : images) {
      if(!image.isEmpty()) {
        // 2. 파일 이름 생성
        String originalFilename = image.getOriginalFilename();
        String ext = Objects.requireNonNull(originalFilename)
            .substring(originalFilename.lastIndexOf("."));
        String storedFilename = UUID.randomUUID() + ext;

        // 3. 실제 저장 경로
        File saveFile = new File(uploadDir + storedFilename);
        image.transferTo(saveFile);

        // 4. DTO -> Entity
        String imagePath = "/uploads/post/" + storedFilename;
        ItemImage itemImage = ItemImage.of(imagePath);
        post.addImage(itemImage);// DB 저장용 URL
      }
    }
    post.setStatus(TradeStatus.TRADE);
    postRepository.save(post);

    // 5. Entity -> DTO
    return ItemPostResponse.from(post);
  }

  // 수정
  public void updatePost(Long postId, ItemPostUpdateRequest request ,Member member, List<MultipartFile> images, List<Long> deletedItemImageIds) throws IOException {
    ItemPost post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

    if (!post.getSeller().getId().equals(member.getId())) {
      throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
    }

    // 삭제 요청된 이미지 처리
    if (deletedItemImageIds != null) {
      post.getImages().removeIf(image -> {
        if (deletedItemImageIds.contains(image.getId())) {
          deletePhysicalImage(image.getImagePath());
          return true; // 삭제할 이미지
        }
        return false; // 유지할 이미지
      });
    }

    // 새 이미지 추가
    if (images != null) {
      for (MultipartFile image : images) {
        if (!image.isEmpty()) {
          String imagePath = saveImage(image);
          ItemImage itemImage = ItemImage.of(imagePath);
          post.addImage(itemImage);
        }
      }
    }
    // 게시글 정보 업데이트
    post.update(request);
  }

  public String saveImage(MultipartFile image) throws IOException {
    String uploadDir = "C:/Users/dkfdj/IdeaProjects/item-trade/src/main/resources/uploads/post/";
    File directory = new File(uploadDir);
    if (!directory.exists()) {
      directory.mkdirs(); // 없으면 생성
    }
    String ext = Objects.requireNonNull(image.getOriginalFilename())
        .substring(image.getOriginalFilename().lastIndexOf("."));
    String storedFilename = UUID.randomUUID() + ext;
    File saveFile = new File(uploadDir + storedFilename);
    image.transferTo(saveFile);
    return "/uploads/post/" + storedFilename; // DB에 저장할 경로
  }

  public void deletePhysicalImage(String imagePath) {
    try {
      Path path = Paths.get("C:/.../resources" + imagePath);
      Files.deleteIfExists(path);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public ItemPostResponse getPostForEdit(Long postId) {
    return postRepository.findById(postId)
        .map(ItemPostResponse::from)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."));
  }

  // 특정 게시글 삭제
  public void deletePost(Long postId ,Member member) {
    ItemPost post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

    if(!post.getSeller().getId().equals(member.getId())) {
      throw new IllegalArgumentException("게시글 작성자만 삭제할 수 있습니다.");
    }

    // 이미지 파일 삭제
    for(ItemImage image : post.getImages()) {
      String imagePath = image.getImagePath();
      Path path = Paths.get(
          "C:/Users/dkfdj/IdeaProjects/item-trade/src/main/resources" + imagePath);
      try {
        Files.deleteIfExists(path);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    postRepository.delete(post);
  }

}
