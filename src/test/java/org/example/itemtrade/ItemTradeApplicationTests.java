package org.example.itemtrade;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(properties = {
    "file.upload-dir=./build/uploads-test",
    "file.chat-dir=./build/uploads-test"
})
class ItemTradeApplicationTests {

  @Test
  void contextLoads() {
  }

}
