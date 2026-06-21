package com.example.apptaphoa.dao;

import java.util.List;
import com.example.apptaphoa.model.ChiTietHoaDon;

public interface ChiTietHoaDonDAOItf {
    void insert(ChiTietHoaDon entity);
    List<ChiTietHoaDon> findByMaHD(int maHD);
}
