package entelect.training.incubator.config;

import entelect.training.incubator.notifications.NotificationMessageListener;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.jms.ConnectionFactory;
import javax.jms.Message;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Disclaimer! In a production system you will never store your credentials in either clear text or in the code.
     * It is done here so that development is both easy to understand and change.
     * The commented code below shows you how to connect to a DB. You will also want to use some kind of password encoding/hashing.
     */

    //    @Autowired
    //    private DataSource securityDataSource;
    //
    //    @Override
    //    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    //        auth.jdbcAuthentication().dataSource(securityDataSource);
    //    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("{noop}the_cake").roles("USER");
        auth.inMemoryAuthentication().withUser("admin").password("{noop}is_a_lie").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable() // !!! Disclaimer: NEVER DISABLE CSRF IN PRODUCTION !!!
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/bookings/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/bookings/**").hasAnyRole("SYSTEM", "ADMIN")
                .anyRequest().denyAll()
                .and()
                .httpBasic();
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);
        jmsTemplate.setDefaultDestinationName("myQueue");
        return jmsTemplate;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL("tcp://localhost:61616");
        connectionFactory.setUserName("admin");
        connectionFactory.setPassword("admin");
        return connectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean
    public JmsListenerContainerFactory<?> notificationContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("1-1");
        return factory;
    }

    public JmsListenerEndpointRegistrar jmsListenerEndpointRegistrar(DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory) {
        JmsListenerEndpointRegistrar registrar = new JmsListenerEndpointRegistrar();
        registrar.setContainerFactory(defaultJmsListenerContainerFactory);
        return registrar;
    }

    @JmsListener(destination = "booking-notification-queue", containerFactory = "notificationContainerFactory")
    public void receiveMessage(Message message) {
        NotificationMessageListener notificationMessageListener = new NotificationMessageListener();
        notificationMessageListener.onMessage(message);
    }
}
