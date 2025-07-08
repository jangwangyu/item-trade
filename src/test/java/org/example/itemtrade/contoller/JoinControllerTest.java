package org.example.itemtrade.contoller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.itemtrade.domain.Member;
import org.example.itemtrade.dto.request.MemberJoinRequest;
import org.example.itemtrade.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest(
    properties = {
        "file.upload-dir=./build/uploads-test",
        "file.chat-dir=./build/uploads-test"
    })
@AutoConfigureMockMvc
@Transactional
class JoinControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MemberRepository memberRepository;
  @Test
  void 회원가입_post()throws Exception {
    // Given
    MemberJoinRequest request = MemberJoinRequest.builder()
        .email("test@test.com")
        .password("password123")
        .confirmPassword("password123")
        .nickname("tester")
        .build();
    // When
    mockMvc.perform(
            post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk());
    // Then
    Member member = memberRepository.findByEmail("test@test.com")
        .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    assertThat(member.getNickName()).isEqualTo("tester");
    assertThat(member.getEmail()).isEqualTo("test@test.com");
    assertThat(member.getPassword()).isNotEqualTo("password123"); // 비밀번호는 암호화되어 저장됨
  }

}