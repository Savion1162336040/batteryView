package com.savion.battery;
/**
 * @author savion
 * @date 2022/2/23
 * @desc 电池充电状态
**/
public enum ChargeMode {
    /**
     * @author savion
     * @date 2022/2/23
     * @desc 未在充电
     **/
    NONE,
    /**
     * @author savion
     * @date 2022/2/23
     * @desc 未识别的充电方式
     **/
    UNKNOW,
    /**
     * @author savion
     * @date 2022/2/23
     * @desc USB充电
     **/
    USB,
    /**
     * @author savion
     * @date 2022/2/23
     * @desc 充电器充电
     **/
    AC,
    /**
     * @author savion
     * @date 2022/2/23
     * @desc 无线充电
     **/
    WIRELESS;

    /**
     * @author savion
     * @date 2022/2/23
     * @desc 是否正在充电
     **/
    public boolean isCharging() {
        return this != NONE;
    }
}
