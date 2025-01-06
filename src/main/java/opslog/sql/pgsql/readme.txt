src/main/java/com/example/sseapp/
Application.java
SSEController.java

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//This would be set up in the Opslog application

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

The SSE controller will work with most SQL databases as long as they support some form of notification mechanism similar to PostgreSQL’s LISTEN and NOTIFY. Here are a few databases you can consider:

PostgreSQL: Uses LISTEN and NOTIFY commands to push notifications.

MySQL: Can use triggers combined with external messaging systems (like Redis or RabbitMQ) to simulate real-time notifications.

Microsoft SQL Server: Supports Service Broker for messaging, which can be used for similar purposes.

Oracle Database: Can use Advanced Queuing (AQ) for message-based notifications.

SQLite: Generally used for lightweight applications, so direct real-time notifications might be limited.


Here is the configuration information for the OpsLog and any other requirements. The current development environment is located at https://replit.com/. It’s probably not possible but If development could be moved onto the floor that might speed things up. It will also make it easier to sustain. If that’s the case I can give you a full list of dependencies and requirements. Also, if you need the git let me know.

Language: 
-	Java 21/OpenJDK 21 (LTS to 2028/2031)
-	JavaFX 21 
-	If you need a full list of dependencies, I can send you the current pom.xml

Database:
-	PostgreSQL 17.0
o	Version 17 supports a built in NOTIFY and LISTEN for real-time 
notifications. So, no need for 3rd party notification management. 
This will decrease maintenance. 
o	Supported OS: Linux and Windows
o	https://www.postgresql.org/download/
-	pgAdmin 4: 
o	DB administration for PostgreSQL
o	https://www.pgadmin.org
