package opslog.sql;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SSEController {

	@GetMapping("/stream")
	public SseEmitter stream() {
		SseEmitter emitter = new SseEmitter();
		new Thread(() -> {
			try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/yourdb", "user", "password");
				 Statement stmt = conn.createStatement()) {

				stmt.execute("LISTEN event_channel");
				while (true) {
					ResultSet rs = stmt.executeQuery("SELECT 1");
					if (rs.next()) {
						emitter.send("Event received!");
					}
					Thread.sleep(1000); // Polling delay
				}
			} catch (Exception e) {
				emitter.completeWithError(e);
			}
		}).start();
		return emitter;
	}
}
