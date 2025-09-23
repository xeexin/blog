package velog.clone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Velog 클론 블로그 애플리케이션의 메인 클래스
 * Spring Boot 애플리케이션을 시작하는 진입점을 제공합니다.
 */
@SpringBootApplication
public class CloneApplication {

	/**
	 * 애플리케이션의 메인 메서드
	 * Spring Boot 애플리케이션을 시작합니다.
	 *
	 * @param args 명령행 인자
	 */
	public static void main(String[] args) {
		SpringApplication.run(CloneApplication.class, args);
	}

}
