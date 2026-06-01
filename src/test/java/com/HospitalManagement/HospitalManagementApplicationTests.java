package com.HospitalManagement;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Disabled because database is not available for full context loading in unit test runs")
class HospitalManagementApplicationTests {

	@Test
	void contextLoads() {
	}

}
