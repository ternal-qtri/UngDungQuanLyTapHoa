package com.example.apptaphoa.controller;

import java.util.Date;
import java.util.List;

import com.example.apptaphoa.model.DoanhThu;

public interface DoanhThuController {
    void fillDate(Date tuNgay, Date denNgay);
    void findbyTime(String ngay);
    void open();
    void load();
    void fillToTable(List<DoanhThu> list);
}