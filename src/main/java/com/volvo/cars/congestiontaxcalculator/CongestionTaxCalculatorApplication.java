package com.volvo.cars.congestiontaxcalculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CongestionTaxCalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CongestionTaxCalculatorApplication.class, args);
//	    CostTiming ct = new CostTiming();
//	    ct.setStartTime("07:00");
//	    ct.setEndTime("08:00");
//	    ct.setPrice(8);
//	    
//	    System.out.println(ct.isTimeBetween("07:01"));
	}

}
