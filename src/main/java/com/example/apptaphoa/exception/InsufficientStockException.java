package com.example.apptaphoa.exception;

/** Không đủ tồn kho để bán hoặc để trừ kho. */
public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(String tenSanPham) {
        super("Sản phẩm \"" + tenSanPham + "\" không đủ số lượng tồn kho");
    }
}
