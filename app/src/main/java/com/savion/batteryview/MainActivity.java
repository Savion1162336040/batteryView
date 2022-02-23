package com.savion.batteryview;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.savion.battery.BatteryView;
import com.savion.battery.NumberUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class MainActivity extends AppCompatActivity {

    BatteryView batteryView;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button updatePower = findViewById(R.id.update_power);
        SwitchCompat switchCompat = findViewById(R.id.toggle_charge);
        editText = findViewById(R.id.power);
        batteryView = findViewById(R.id.battery);
        updatePower.setOnClickListener(view -> {
            int power = NumberUtil.strToInt(editText.getText().toString(), 0);
            batteryView.updatePower(power);
        });
        switchCompat.setOnCheckedChangeListener((compoundButton, b) -> batteryView.updateIsCharge(b));
    }
}