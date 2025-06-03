package s.ma.project.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE_NAME = "feedback.retrain.queue";
    public static final String EXCHANGE_NAME = "feedback.exchange";
    public static final String ROUTING_KEY = "feedback.retrain";

    @Bean
    public Queue feedbackQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange feedbackExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding feedbackBinding(Queue feedbackQueue, DirectExchange feedbackExchange) {
        return BindingBuilder.bind(feedbackQueue)
                             .to(feedbackExchange)
                             .with(ROUTING_KEY);
    }
}
