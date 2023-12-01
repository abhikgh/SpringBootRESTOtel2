package com.example.SpringBootRESTOtel2.controller;

import com.example.SpringBootRESTOtel2.entity.Movie;
import com.example.SpringBootRESTOtel2.service.MovieService;
import io.jaegertracing.internal.JaegerTracer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.opentracing.Span;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/orders/v1")
@Slf4j
public class NavigationController {

    private Logger logger = Logger.getLogger(NavigationController.class.getName());

    @Autowired
    private MovieService movieService;

    @Autowired
    private JaegerTracer jaegerTracer;

    @Autowired
    private MeterRegistry meterRegistry;

    // http://localhost:9151/orders/v1/getMoviesOfDirector/Satyajit Ray
    @GetMapping(value = "/getMoviesOfDirector/{director}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Movie>> getMoviesOfDirector(
            @PathVariable("director") String director) {
        Span span = jaegerTracer.buildSpan("getMoviesOfDirector").start();
        log.info("Inside getMoviesOfDirector controller... ");

        //Counter metric
        Counter counter = Counter.builder("getMoviesOfDirector")
                .description("a number of requests to /getMoviesOfDirector endpoint")
                .register(meterRegistry);
        counter.increment();

        //Timer metric
        Timer.Sample timer = Timer.start(meterRegistry);
        Span getHeroDetailsSpan = jaegerTracer.buildSpan("getMoviesOfDirector-Service").asChildOf(span).start();
        List<Movie> movies = movieService.getMoviesOfDirector(director);
        getHeroDetailsSpan.finish();
        span.finish();
        timer.stop(Timer.builder("getMoviesOfDirector_Timer").register(meterRegistry));
        return ResponseEntity.ok(movies);
    }
}
