package com.mpa.config;

import com.mpa.constants.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcReactRoutingConfiguration implements WebMvcConfigurer {

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
      registry.addViewController("/{spring:\\w+}")
            .setViewName(Constants.VIEW_NAME);
      registry.addViewController("/**/{spring:\\w+}")
            .setViewName(Constants.VIEW_NAME);
      registry.addViewController("/{spring:\\w+}/**{spring:?!(\\.js|\\.css)$}")
            .setViewName(Constants.VIEW_NAME);
  }


}