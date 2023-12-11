package com.example.diennuoc.model;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.diennuoc.R;
import com.example.diennuoc.databinding.ActivityWaterBillBinding;
import com.example.diennuoc.model.fragment.WaterPriceFragment;
import com.example.diennuoc.viewmodel.WaterBillViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class WaterBillActivity extends AppCompatActivity {
    private static final int LEVEL1 = 4580;
    private static final int LEVEL2 = 5488;
    private static final int LEVEL3 = 6849;
    private ActivityWaterBillBinding activityWaterBillBinding;
    private WaterBillViewModel waterBillViewModel = new WaterBillViewModel();
    private double priceBeforeTax, priceTax, priceTotal;
    private int firstNum, secondNum, totalAmountWater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWaterBillBinding = ActivityWaterBillBinding.inflate(getLayoutInflater());
        setContentView(activityWaterBillBinding.getRoot());
        activityWaterBillBinding.setWaterBillViewModel(waterBillViewModel);

        clickButton();
    }

    private void clickButton() {
        activityWaterBillBinding.ivBack.setOnClickListener(v -> finish());

        activityWaterBillBinding.ivInfor.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.layout_water_bill, new WaterPriceFragment()).addToBackStack(null).commit();
        });

        activityWaterBillBinding.btnSave.setOnClickListener(v -> saveData());

        activityWaterBillBinding.btnCalculate.setOnClickListener(v -> calculatePriceWater());
    }

    private void saveData() {
        // Handler save in this method
    }

    private void calculatePriceWater() {
        priceTotal = calculatePriceTotal(calculateTotalAmountWater());
        priceBeforeTax = priceTotal / 1.05;
        priceTax = priceTotal - priceBeforeTax;

        Locale vn = Locale.forLanguageTag("vi-VN");
        NumberFormat numberFormat = NumberFormat.getInstance(vn);

        waterBillViewModel.setPriceBeforeTax(numberFormat.format((int) Math.round(priceBeforeTax)));
        waterBillViewModel.setPriceTax(numberFormat.format((int) Math.round(priceTax)));
        waterBillViewModel.setPriceTotal(numberFormat.format((int) Math.round(priceTotal)));
    }

    private int calculateTotalAmountWater() {
        String strFirstNum = waterBillViewModel.getFirstNumber();
        String strSecondNum = waterBillViewModel.getSecondNumber();
        String strTotalAmountElectricity = waterBillViewModel.getTotalAmountWater();

        if (strFirstNum != null && !strFirstNum.isEmpty() && strSecondNum != null && !strSecondNum.isEmpty()) {
            firstNum = Integer.parseInt(waterBillViewModel.getFirstNumber());
            secondNum = Integer.parseInt(waterBillViewModel.getSecondNumber());
            if (firstNum <= secondNum) {
                totalAmountWater = secondNum - firstNum;
                waterBillViewModel.setTotalAmountWater(String.valueOf(totalAmountWater));
                return totalAmountWater;
            }
        } else if (strTotalAmountElectricity != null && !strTotalAmountElectricity.isEmpty()) {
            totalAmountWater = Integer.parseInt(waterBillViewModel.getTotalAmountWater());
            if (totalAmountWater >= 0) {
                return totalAmountWater;
            }
        } else {
            Toast.makeText(WaterBillActivity.this,"Dữ liệu nhập vào không chính xác",Toast.LENGTH_SHORT).show();
        }
        return 0;
    }

    private int calculatePriceTotal(int totalAmountWater) {
        int result =0;
        switch (checkLevel(totalAmountWater)) {
            case 1:
                result = LEVEL1*totalAmountWater;
                break;
            case 2:
                result = LEVEL1*10 + LEVEL2*(totalAmountWater-10);
                break;
            case 3:
                result = LEVEL1*10 + LEVEL2*10 + LEVEL3*(totalAmountWater-20);
                break;
        }
        return result;
    }

    private int checkLevel(int totalAmountWater) {
        int level=0;
        for(int i=0; i<3; i++) {
            if(totalAmountWater > 0) {
                totalAmountWater -= 10;
                level++;
            }
        }
        return level;
    } 
}