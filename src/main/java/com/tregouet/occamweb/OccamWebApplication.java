package com.tregouet.occamweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.tregouet.occam.Occam;
import com.tregouet.occam.alg.OverallAbstractFactory;
import com.tregouet.occam.alg.OverallStrategy;

@SpringBootApplication
public class OccamWebApplication {

	public static final OverallStrategy STRATEGY = Occam.STRATEGY;

	public static void initialize() {
		OverallAbstractFactory.INSTANCE.apply(Occam.STRATEGY);
		OverallAbstractFactory.INSTANCE.apply(STRATEGY);
	}

	public static void main(final String[] args) {
		initialize();
		SpringApplication.run(OccamWebApplication.class, args);
	}

}
