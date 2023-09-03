package com.example.rdsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
		// 빈 스캔의 경우 해당 클래스가 위치한 패키지 하위를 스캔, common 모듈의 경우 패키지 경로가 다르기 때문에 추가
		scanBasePackages = {"com.example.rdsapi", "com.example.rdscommon"}

)
public class RdsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RdsApiApplication.class, args);
	}

}
