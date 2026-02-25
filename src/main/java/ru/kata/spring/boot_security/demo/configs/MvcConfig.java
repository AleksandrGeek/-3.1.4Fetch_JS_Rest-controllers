package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/*
Это конфигурация MVC (Model-View-Controller)
Она регистрирует простой контроллер: при запросе /user отображается шаблон user.html
Это альтернатива созданию отдельного контроллера для простых страниц
Вопрос: Зачем это нужно, если можно создать контроллер? (Ответ: для уменьшения boilerplate-кода)
 */

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/admin").setViewName("admin");
        registry.addViewController("/user").setViewName("user");


    }
}
