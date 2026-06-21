package com.example.apptaphoa.controller;

import com.example.apptaphoa.model.HoaDon;
import java.util.Date;
import java.util.List;

public interface HoaDonController {
    void open();
    void fillToTable(List<HoaDon> list);
    void findById(int maHD);
    void filterByDate(Date tuNgay, Date denNgay);
    void load();
}
