package com.example.consumerapp;

import com.example.consumerapp.service.TestAspectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AppApplicationTests {

	@Autowired
	private TestAspectService testAspectService;

	@Test
	void test() {
		testAspectService.testLoggingAspect();
	}

	@Test
	void test1() {
		testAspectService.testTransactionAspect();
	}


}
