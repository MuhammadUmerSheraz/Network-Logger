package com.umer.networklogger

data class GetAppSettingResponse(
    var result: Boolean,
    var info: AppsettingResponse
)

data class AppsettingResponse(
    var id: String,
    var nearby_notfication_period: String,
    var tash_pay_fee: String,
    var search_radius_mile: String,
    var subscribe_price: String,
    var overwash_redeem: String,
    var discount_percent: String,
    var non_profit_fee: String,

    var fundraise_status: String,
    var fundraise_price: String,
    var fundraise_start_date: String,
    var fundraise_end_date: String,
    var cooperate_discount: String,

    )