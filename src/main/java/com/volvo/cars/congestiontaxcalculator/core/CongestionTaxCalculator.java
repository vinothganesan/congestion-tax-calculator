package com.volvo.cars.congestiontaxcalculator.core;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.volvo.cars.congestiontaxcalculator.props.CostTiming;
import com.volvo.cars.congestiontaxcalculator.props.HYear;
import com.volvo.cars.congestiontaxcalculator.props.Hmonth;
import com.volvo.cars.congestiontaxcalculator.props.HolidayList;
import com.volvo.cars.congestiontaxcalculator.props.TaxExempt;
import com.volvo.cars.congestiontaxcalculator.props.TaxPrice;
import com.volvo.cars.congestiontaxcalculator.props.Vehicle;

@Service
public class CongestionTaxCalculator {

    @Autowired
    TaxExempt taxExempt;

    @Autowired
    TaxPrice taxPrice;

    @Autowired
    HolidayList holidayList;

    @Value("${singlecharge.time}")
    private int singleChargeTime;

    public int getTax(Vehicle vehicle, Date[] dates) {

        int totalFee = 0;

        for (int i = 0; i < dates.length; i++) {
            int tollFee = 0;
            if (i == 0 || dateDiff(dates[i - 1], dates[i])) {
                tollFee = getTollFee(dates[i], vehicle.name());
                totalFee += tollFee;
            }
        }

        if (totalFee > singleChargeTime)
            totalFee = singleChargeTime;
        return totalFee;
    }

    private boolean dateDiff(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.MILLISECONDS.toMinutes(diff) > singleChargeTime;
    }

    private boolean IsTollFreeVehicle(String vehicleType) {
        if (vehicleType == null)
            return false;
        return taxExempt.getVehicles().contains(vehicleType);
    }

    public int getTollFee(Date date, String vehicle) {
        if (IsTollFreeDate(date) || IsTollFreeVehicle(vehicle))
            return 0;
        String hour = date.getHours() + ":" + date.getMinutes();
        return taxPrice.getTimings().stream().filter(obj -> obj.isTimeBetween(hour)).findFirst()
                .orElse(new CostTiming()).getPrice();
    }

    private Boolean IsTollFreeDate(Date date) {
    	
        Calendar calendar = Calendar.getInstance();
        
        calendar.setTime(date);
        
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY)
            return true;

        for (HYear hYear : holidayList.getYears()) {
            if (hYear.getYear() == year) {
                for (Hmonth hmonth : hYear.getMonths()) {
                    if (hmonth.getMonth() == month)
                        if (hmonth.getDates().isEmpty())
                            return true;
                        else if (hmonth.getDates().contains(dayOfMonth) || hmonth.getDates().contains(dayOfMonth + 1))
                            return true;
                }
            }
        }
        return false;
    }
}
