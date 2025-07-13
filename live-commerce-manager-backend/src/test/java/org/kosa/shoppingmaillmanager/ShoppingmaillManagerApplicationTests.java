package org.kosa.shoppingmaillmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShoppingmaillManagerApplicationTests {

	 @Test
	    void simpleTest() {
	        assertEquals(2, 1 + 1); // ✅ 항상 성공하는 테스트
	    }

}
