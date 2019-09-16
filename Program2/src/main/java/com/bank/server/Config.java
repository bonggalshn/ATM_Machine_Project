package com.bank.server;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"rpc"})
@Configuration
public class Config {
    @Profile("client")
    private static class ClientConfig {

        @Bean
        public DirectExchange exchange() {
            return new DirectExchange("mainExchange");
        }

        @Bean
        public RecClient client() {
            return new RecClient();
        }

    }

    @Profile("server")
    private static class ServerConfig {

        @Bean
        public Queue queue() {
            return new Queue("mainQueue");
        }

        @Bean
        public DirectExchange exchange() {
            return new DirectExchange("mainExchange");
        }

        @Bean
        public Binding binding(DirectExchange exchange, Queue queue) {
            return BindingBuilder.bind(queue).to(exchange).with("main_route");
        }

        @Bean
        public Server server() {
            return new Server();
        }

    }
}
