package com.tregouet.occamweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.tregouet.occam.alg.OverallAbstractFactory;
import com.tregouet.occam.alg.OverallStrategy;

@SpringBootApplication
public class OccamWebApplication {

	public static final OverallStrategy strategy = OverallStrategy.OVERALL_STRATEGY_3;

	public static void initialize() {
		OverallAbstractFactory.INSTANCE.apply(strategy);
	}

	public static void main(final String[] args) {
		initialize();
		SpringApplication.run(OccamWebApplication.class, args);
	}

}
