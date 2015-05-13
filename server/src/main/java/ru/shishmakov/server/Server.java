package ru.shishmakov.server;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.shishmakov.server.core.Game;
import ru.shishmakov.server.core.ServerConfig;

import java.lang.invoke.MethodHandles;

/**
 * Main class to launch the {@link Game}.
 *
 * @author Dmitriy Shishmakov
 */
public class Server {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());

    public static void main(final String[] args) throws InterruptedException {
        Game game = null;
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(ServerConfig.class);
            context.refresh();
            context.registerShutdownHook();
            game = context.getBean(Game.class);
            game.checkDbConnection();
            game.start();
        } catch (Exception e) {
            logger.error("The server failure: " + e.getMessage(), e);
        }finally {
            if(game != null){
                game.stop();
            }
        }
    }
}
